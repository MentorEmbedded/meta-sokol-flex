# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " file://lvm2-flex.service"

PACKAGES += "${PN}-flex"
RDEPENDS:${PN} += "${PN}-flex"
SYSTEMD_PACKAGES += "${PN}-flex"
SYSTEMD_SERVICE:${PN}-flex = "lvm2-flex.service"
SYSTEMD_AUTO_ENABLE:${PN}-flex = "enable"
RREPLACES:${PN}-flex += "${PN}-mel"
RCONFLICTS:${PN}-flex += "${PN}-mel"

do_install:append() {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -m 0644 ${WORKDIR}/${SYSTEMD_SERVICE:${PN}-flex} ${D}${systemd_system_unitdir}/
    fi
}
