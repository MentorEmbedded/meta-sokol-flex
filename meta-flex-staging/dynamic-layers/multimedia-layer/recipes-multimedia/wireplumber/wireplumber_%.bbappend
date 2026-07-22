FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " \
    file://65-default-volume.conf \
"

do_install:append() {
    install -d ${D}${datadir}/wireplumber/wireplumber.conf.d
    install -m 0644 ${WORKDIR}/65-default-volume.conf \
        ${D}${datadir}/wireplumber/wireplumber.conf.d/65-default-volume.conf
}

SYSTEMD_SERVICE:${PN} += "wireplumber.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"
