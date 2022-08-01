# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SUMMARY = "Useful bits an pieces to make 96Boards more standard across the board"
HOMEPAGE = "https://github.com/96boards/96boards-tools"
SECTION = "devel"

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c"

SRCREV = "a999d655417bb19ce8a476ff8c811e957748b661"
SRC_URI = "git://github.com/96boards/96boards-tools;branch=master;protocol=https\
           file://resize-helper.sh.in"

S = "${WORKDIR}/git"

inherit systemd allarch update-rc.d

do_install () {
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0755 ${S}/*.rules ${D}${sysconfdir}/udev/rules.d/

    install -d ${D}${sbindir}
    install -m 0755 ${S}/resize-helper ${D}${sbindir}

    if ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}; then
        install -d ${D}${systemd_unitdir}/system
        install -m 0644 ${S}/resize-helper.service ${D}${systemd_unitdir}/system
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'sysvinit', 'true', 'false', d)}; then
        install -d ${D}${sysconfdir}/init.d
        install -m 0755 ${WORKDIR}/resize-helper.sh.in ${D}${sysconfdir}/init.d/resize-helper.sh
        sed -i -e "s:@bindir@:${bindir}:; s:@sbindir@:${sbindir}:; s:@sysconfdir@:${sysconfdir}:" \
            ${D}${sysconfdir}/init.d/resize-helper.sh
    fi
}

SYSTEMD_SERVICE:${PN} = "resize-helper.service"
RDEPENDS:${PN} += "e2fsprogs-resize2fs gptfdisk parted util-linux udev"

INITSCRIPT_NAME = "resize-helper.sh"
INITSCRIPT_PARAMS = "start 22 5 3 ."
