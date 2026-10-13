# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SYSTEMD_LOGLEVEL ?= "info"
SYSTEMD_LOGLEVEL:innexis-linux ?= "emerg"

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}/${PN}:"
SRC_URI:append:innexis-linux = " file://0001-profile-skip-osc-context-hooks-over-ssh.patch"

do_compile:append:innexis-linux () {
    printf '[Manager]\n' >loglevel.conf
    printf 'LogLevel=${SYSTEMD_LOGLEVEL}\n' >>loglevel.conf
}

do_install:append:innexis-linux () {
    install -d "${D}${nonarch_libdir}/systemd/system.conf.d"
    install -m 0644 loglevel.conf "${D}${nonarch_libdir}/systemd/system.conf.d/"
}
