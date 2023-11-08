# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}/weston-init:"

SRC_URI += "file://weston-init.sh"

do_install:append () {
    # Set WAYLAND_DISPLAY by default for console or ssh users
    install -d ${D}${sysconfdir}/profile.d/
    install ${WORKDIR}/weston-init.sh ${D}${sysconfdir}/profile.d/weston-init.sh
}

FILES:${PN} += "${sysconfdir}/profile.d/weston-init.sh"
