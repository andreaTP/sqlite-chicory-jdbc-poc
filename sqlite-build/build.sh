#! /bin/bash

# TODO: improve this script to be reproducible on different machines

# Download WASI-SDK
# wget https://github.com/WebAssembly/wasi-sdk/releases/download/wasi-sdk-24/wasi-sdk-24.0-x86_64-linux.tar.gz
# tar -xvf wasi-sdk-24.0-x86_64-linux.tar.gz
# rm wasi-sdk-24.0-x86_64-linux.tar.gz

# wget https://www.sqlite.org/2024/sqlite-amalgamation-3460100.zip
# unzip sqlite-amalgamation-3460100.zip
# rm sqlite-amalgamation-3460100.zip

export WASI_SDK_PATH=${PWD}/wasi-sdk-24.0-x86_64-linux

# TODO: check if wasm-opt improve performance/size
(
    cd sqlite-amalgamation-3460100
    ${WASI_SDK_PATH}/bin/clang --sysroot=${WASI_SDK_PATH}/share/wasi-sysroot \
        --target=wasm32-wasi \
        -o libsqlite.wasm \
        sqlite3.c \
        -Wl,--allow-undefined \
        -Wl,--export-all \
        -Wl,--no-entry \
        -mexec-model=reactor \
        -Oz
)
