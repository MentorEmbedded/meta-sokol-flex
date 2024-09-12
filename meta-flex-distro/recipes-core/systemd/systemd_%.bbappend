# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SYSTEMD_LOGLEVEL ?= "info"
SYSTEMD_LOGLEVEL:flex-os ?= "emerg"

do_compile:append:flex-os () {
    printf '[Manager]\n' >loglevel.conf
    printf 'LogLevel=${SYSTEMD_LOGLEVEL}\n' >>loglevel.conf
}

do_install:append:flex-os () {
    install -d "${D}${nonarch_libdir}/systemd/system.conf.d"
    install -m 0644 loglevel.conf "${D}${nonarch_libdir}/systemd/system.conf.d/"
}
