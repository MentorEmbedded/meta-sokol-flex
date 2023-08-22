
def meson_dynamic_linker(d):
    host_arch = d.getVar('HOST_ARCH')
    if 'x86_64' in host_arch:
        loader = 'ld-linux-x86-64.so.2'
    elif 'i686' in host_arch:
        loader = 'ld-linux.so.2'
    elif 'aarch64' in host_arch:
        loader = 'ld-linux-aarch64.so.1'
    elif 'ppc64le' in host_arch:
        loader = 'ld64.so.2'
    elif 'loongarch64' in host_arch:
        loader = 'ld-linux-loongarch-lp64d.so.1'
    else:
        loader = 'ld-linux-UNKNOWN.so.1'
    return loader

do_write_config:append:class-target() {
    # Write out a qemu wrapper that will be used as exe_wrapper so that meson
    # can run target helper binaries through that.
    # qemu_binary="${@qemu_wrapper_cmdline(d, '$STAGING_DIR_HOST', ['$STAGING_DIR_HOST/${libdir}','$STAGING_DIR_HOST/${base_libdir}'])}"
    cat > ${WORKDIR}/meson-qemuwrapper << EOF
#!/bin/sh
# Use a modules directory which doesn't exist so we don't load random things
# which may then get deleted (or their dependencies) and potentially segfault
export GIO_MODULE_DIR=${STAGING_LIBDIR}/gio/modules-dummy

# meson sets this wrongly (only to libs in build-dir), qemu_wrapper_cmdline() and GIR_EXTRA_LIBS_PATH take care of it properly
unset LD_LIBRARY_PATH

$qemu_binary $STAGING_DIR_HOST/${libdir}/${@meson_dynamic_linker(d)} "\$@"
EOF
    chmod +x ${WORKDIR}/meson-qemuwrapper
}
