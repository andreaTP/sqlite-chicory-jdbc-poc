package com.github.andreaTP.sqlite.chicory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.StandardCopyOption;

import static java.nio.file.Files.copy;

public class Demo {

    public static void main(String[] args) {
        System.out.println("Getting started with SQLite JDBC!");

        var sqlite = new LibSQLite();

        var testDB = new File("core/src/test/resources/Chinook_Sqlite.sqlite");
        var testDBPath = "/" + testDB.getName();
        try (FileInputStream fis = new FileInputStream(testDB)) {
            copy(fis, sqlite.fs().getPath(testDBPath), StandardCopyOption.REPLACE_EXISTING);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var dbPtrPtr = sqlite.malloc(LibSQLite.PTR_SIZE);
        var dbPtr = sqlite.memory().readInt(dbPtrPtr);
        var testDBPathPtr = sqlite.malloc(testDBPath.length());
        sqlite.memory().writeCString(testDBPathPtr, testDBPath);

        sqlite.open(testDBPathPtr, dbPtr);

        // exec

        // read results in place:
        // https://stackoverflow.com/a/31168999/7898052
        // sqlite3_stmt *stmt;
        //const char *sql = "SELECT ID, Name FROM User";
        //    int rc = sqlite3_prepare_v2(db, sql, -1, &stmt, NULL);
        //    if (rc != SQLITE_OK) {
        //        print("error: ", sqlite3_errmsg(db));
        //        return;
        //    }
        //    while ((rc = sqlite3_step(stmt)) == SQLITE_ROW) {
        //        int id           = sqlite3_column_int (stmt, 0);
        //const char *name = sqlite3_column_text(stmt, 1);
        //        // ...
        //    }
        //    if (rc != SQLITE_DONE) {
        //        print("error: ", sqlite3_errmsg(db));
        //    }
        //    sqlite3_finalize(stmt);

        System.out.println("End of demo code");
    }
}
