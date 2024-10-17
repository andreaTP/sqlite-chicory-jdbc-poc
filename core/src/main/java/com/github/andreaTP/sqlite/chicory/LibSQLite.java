package com.github.andreaTP.sqlite.chicory;

import com.dylibso.chicory.log.Logger;
import com.dylibso.chicory.log.SystemLogger;
import com.dylibso.chicory.runtime.ExternalValues;
import com.dylibso.chicory.runtime.Instance;
import com.dylibso.chicory.runtime.Memory;
import com.dylibso.chicory.wasi.WasiOptions;
import com.dylibso.chicory.wasi.WasiPreview1;
import com.dylibso.chicory.wasm.Module;
import com.dylibso.chicory.wasm.Parser;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.nio.file.FileSystem;

public class LibSQLite implements AutoCloseable {

    public final static int PTR_SIZE = 4;

    private static final Logger logger = new SystemLogger();
    private final Module module;
    private final Instance instance;
    private final FileSystem fs;

    public LibSQLite() {
        this.module = Parser.parse(Demo.class.getResourceAsStream("/libsqlite.wasm"));
        this.fs = Jimfs.newFileSystem(
                        Configuration.unix().toBuilder()
                                .setAttributeViews("unix")
                                .build());

        var wasiOpts = WasiOptions.builder()
                .withDirectory("/", fs.getPath("/"))
                .build();

        try (var wasi =
                     WasiPreview1.builder().withLogger(logger).withOpts(wasiOpts).build()) {
            ExternalValues imports = new ExternalValues(wasi.toHostFunctions());

            this.instance =
                    Instance.builder(this.module)
                            .withExternalValues(imports)
                            // a call_indirect size is failing, debug!
                            // .withMachineFactory(SQLiteModuleMachineFactory::create)
                            .build();
        }
    }

    public FileSystem fs() {
        return fs;
    }

    public Memory memory() {
        return instance.memory();
    }

    public int malloc(int size) {
        return (int) instance.export("sqlite3_malloc").apply(size)[0];
    }

    public void free(int ptr) {
        instance.export("sqlite3_free").apply(ptr);
    }

    public int open(int pathPtr, int dbPtr) {
        return (int) instance.export("sqlite3_open").apply(pathPtr, dbPtr)[0];
    }


    public void exec(int dbPtr, int sqlPtr) {
        instance.export("sqlite3_exec").apply(dbPtr, sqlPtr);
    }

    public int errmsg(int dbPtr) {
        return (int) instance.export("sqlite3_errmsg").apply(dbPtr)[0];
    }

    @Override
    public void close() throws Exception {
        if (fs != null && fs.isOpen()) {
            fs.close();
        }
    }
}
