SUMMARY = "Graphics Streaming Kit -- runtime library and development files"
DESCRIPTION = "Graphics Streaming Kit helps serialize and forward graphics API calls for virtualization and IPC."
HOMEPAGE = "https://android.googlesource.com/platform/hardware/google/gfxstream"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8c18bc94001e6e45c4727758ff462348"

SRC_URI = "git://android.googlesource.com/platform/hardware/google/gfxstream;branch=main"
SRCREV = "d047a57228332d995d36600792fa9ccc26cf8ae6"
SRC_URI += "\
    file://0001-fix-qemu-runtime-error.patch \
    file://0001-host-gl-include-GL-glx.h-only-when-GLX-is-enabled.patch \
"
S = "${WORKDIR}/git"

DEPENDS = "aemu libdrm glm virtual/egl virtual/libgles2"

inherit meson pkgconfig

EXTRA_OEMESON += "-Dglx=disabled"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"

FILES:${PN} = "${libdir}/libgfxstream_backend.so*"
FILES:${PN}-dev = "${libdir}/libgfxstream_backend.so ${includedir}/gfxstream/*.h ${libdir}/pkgconfig/*.pc"
RDEPENDS:${PN}-dev = "${PN}"
