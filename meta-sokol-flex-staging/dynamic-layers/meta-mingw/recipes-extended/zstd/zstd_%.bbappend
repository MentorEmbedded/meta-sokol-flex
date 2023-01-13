# Zstd's makefile creates these even when we're buliding a dll, so remove them
do_install:append:mingw32:feature-sokol-flex-staging () {
    rm -f ${D}${libdir}/libzstd.so ${D}${libdir}/libzstd.so.1
}
