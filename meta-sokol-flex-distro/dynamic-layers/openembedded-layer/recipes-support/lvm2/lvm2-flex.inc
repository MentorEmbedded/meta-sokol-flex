FILESEXTRAPATHS:prepend := "${THISDIR}/lvm2:"

SRC_URI:append = " file://lvm2-flex.service"
SRC_URI:append = " file://09-dm_hack.rules"

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
    install -m 0755 -d ${D}${sysconfdir}/udev/rules.d/
    install -m 0644 ${WORKDIR}/09-dm_hack.rules ${D}${sysconfdir}/udev/rules.d/
}
