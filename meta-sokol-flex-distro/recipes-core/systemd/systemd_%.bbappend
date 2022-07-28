# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SYSTEMD_LOGLEVEL ?= "info"
SYSTEMD_LOGLEVEL:sokol-flex ?= "emerg"

do_compile:append:sokol-flex () {
    printf '[Manager]\n' >loglevel.conf
    printf 'LogLevel=${SYSTEMD_LOGLEVEL}\n' >>loglevel.conf
}

do_install:append:sokol-flex () {
    install -d "${D}${nonarch_libdir}/systemd/system.conf.d"
    install -m 0644 loglevel.conf "${D}${nonarch_libdir}/systemd/system.conf.d/"
}
