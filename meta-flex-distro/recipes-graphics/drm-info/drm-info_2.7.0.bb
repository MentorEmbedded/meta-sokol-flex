DESCRIPTION = "drm_info is a command-line utility to display information about DRM devices."
HOMEPAGE = "https://gitlab.freedesktop.org/emersion/drm_info"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=32fd56d355bd6a61017655d8da26b67c"

SRC_URI = "git://gitlab.freedesktop.org/emersion/drm_info.git;protocol=https;branch=master"
SRCREV = "c1f5ca4cf750b26eb26c1d9d5c2ef057acbcfefc"

S = "${WORKDIR}/git"

DEPENDS = "libdrm json-c"

inherit meson pkgconfig

EXTRA_OEMESON = ""

RPROVIDES:pn-drm_info = "drm_info"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/drm_info ${D}${bindir}/
}
