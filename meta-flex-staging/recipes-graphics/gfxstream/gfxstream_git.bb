SUMMARY = "Graphics Streaming Kit -- runtime library and development files"
DESCRIPTION = "Graphics Streaming Kit helps serialize and forward graphics API calls for virtualization and IPC."
HOMEPAGE = "https://android.googlesource.com/platform/hardware/google/gfxstream"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

SRC_URI = "git://android.googlesource.com/platform/hardware/google/gfxstream;branch=emu-main-dev"
SRCREV = "abd306f3f35ab9946066084671ed263008531d19"
SRC_URI += "\
    file://0001-host-remove-hard-dependency-on-x11.patch \
"

DEPENDS = "libdrm glm virtual/egl virtual/libgles2"

inherit meson pkgconfig

EXTRA_OEMESON += "-Dglx=disabled"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"

FILES:${PN} = "${libdir}/libgfxstream_backend.so*"
FILES:${PN}-dev = "${libdir}/libgfxstream_backend.so ${includedir}/gfxstream/*.h ${libdir}/pkgconfig/*.pc"
RDEPENDS:${PN}-dev = "${PN}"
