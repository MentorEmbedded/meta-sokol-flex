# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}:"

SRC_URI:append:sokol-flex = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '\
                   file://psplash-quit.service \
                   file://psplash-start.service \
                   file://psplash-final.service \
                   ', '', d)}"

# Change background color and splash image with no progress bar
SRC_URI:append:sokol-flex = " file://0001-psplash-config-enable-fullscreen-image.patch \
		  file://0001-plash-colors.h-color-change.patch \
		 "
EXTRA_OECONF:append:sokol-flex = " --disable-progress-bar"

# # Update to latest version of psplash
# SRCREV:sokol-flex = "5b3c1cc28f5abdc2c33830150b48b278cc4f7bca"

SPLASH_IMAGES:sokol-flex = "file://mel.png;outsuffix=default"

SYSTEMD_INHERIT = ""
SYSTEMD_INHERIT:sokol-flex = "systemd"
inherit ${SYSTEMD_INHERIT}

SYSTEMD_SERVICE:${PN}:sokol-flex = "psplash-start.service psplash-quit.service psplash-final.service"
SYSTEMD_AUTO_ENABLE ?= "enable"

do_install:append:sokol-flex() {
        if ${@bb.utils.contains('DISTRO_FEATURES','systemd','true','false',d)}; then
                install -d ${D}${systemd_unitdir}/system/
                install -m 0644 ${WORKDIR}/psplash-quit.service ${D}${systemd_unitdir}/system
                install -m 0644 ${WORKDIR}/psplash-start.service ${D}${systemd_unitdir}/system
                install -m 0644 ${WORKDIR}/psplash-final.service ${D}${systemd_unitdir}/system
        fi
        rm -f ${D}${systemd_unitdir}/system/psplash-systemd.service
}
