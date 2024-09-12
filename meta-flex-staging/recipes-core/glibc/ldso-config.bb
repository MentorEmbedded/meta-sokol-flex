SUMMARY = "ld.so configuration file for the current tuning library paths"
DESCRIPTION = "${SUMMARY}"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"
INHIBIT_DEFAULT_DEPS = "1"

deltask do_fetch
deltask do_unpack
deltask do_patch
deltask do_configure
deltask do_compile

do_install () {
    install -d "${D}${sysconfdir}/ld.so.conf.d"
    conffile=${D}${sysconfdir}/ld.so.conf.d/${DEFAULTTUNE}.conf
    echo '${base_libdir}' >>"$conffile"
    echo '${libdir}' >>"$conffile"
}
