SUMMARY = "Android developer library for emulators"
DESCRIPTION = "Utility library for common functions used in the Android Emulator. External projects (gfxstream, QEMU) may use to perform C++ functions."
HOMEPAGE = "https://android.googlesource.com/platform/hardware/google/aemu"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ff39be111c2cce0426721beaa1211c63"

SRC_URI = "git://android.googlesource.com/platform/hardware/google/aemu;branch=main"
SRCREV = "3c1ced8a369417db591eb7cd083af5bb2c317975"
SRC_URI += "file://fixup-refs-to-cuda-headers.patch"

S = "${WORKDIR}/git"

inherit cmake pkgconfig

COMPATIBLE_HOST = "(arm|aarch64).*-linux"

FILES:${PN} = "${libdir}/libaemu*.so.*"
FILES:${PN}-dev = "${libdir}/libaemu*.so ${includedir} ${libdir}/pkgconfig/*.pc"

RDEPENDS:${PN}-dev = "${PN}"

EXTRA_OECMAKE = " \
    -DAEMU_COMMON_GEN_PKGCONFIG=ON \
    -DAEMU_COMMON_BUILD_CONFIG=gfxstream \
    -DBUILD_SHARED_LIBS=ON \
    -DENABLE_VKCEREAL_TESTS=Off \
"
