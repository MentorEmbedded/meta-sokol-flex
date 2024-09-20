EXTRA_OEMAKE:append:mingw32:feature-flex-staging = "\
    -fwin32/Makefile.gcc \
    SHARED_MODE=1 \
    \
    'PREFIX=${TARGET_PREFIX}' \
    'CC=${CC}' \
    'CFLAGS=${CFLAGS}' \
    'AS=${CC}' \
    'LD=${CC}' \
    'LDFLAGS=${CFLAGS} ${LDFLAGS}' \
    'AR=${AR}' \
    'RC=${RC}' \
    'STRIP=${STRIP}' \
    \
    'prefix=${prefix}' \
    'exec_prefix=${exec_prefix}' \
    'BINARY_PATH=${bindir}' \
    'LIBRARY_PATH=${libdir}' \
    'INCLUDE_PATH=${includedir}' \
    'INSTALL=install' \
"

do_configure:mingw32:feature-flex-staging () {
    :
}

do_compile:mingw32:feature-flex-staging () {
	oe_runmake libz.a zlib1.dll libz.dll.a minigzip.exe minigzip_d.exe
}
