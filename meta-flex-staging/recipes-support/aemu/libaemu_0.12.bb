# SPDX-License-Identifier: Apache-2.0

SUMMARY = "Android Emulator Common Libraries"
DESCRIPTION = "Libraries required for Android Emulator graphics stream (gfxstream)"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ff39be111c2cce0426721beaa1211c63"

SRC_URI = "git://android.googlesource.com/platform/hardware/google/aemu;branch=main"
SRCREV = "dd8b929c247ce9872c775e0e5ddc4300011d0e82"

DEPENDS = "cmake-native"

REQUIRED_DISTRO_FEATURES = "vulkan"

S = "${WORKDIR}/git"

# Extra cmake options for libaemu
EXTRA_OECMAKE = "\
    -DAEMU_COMMON_GEN_PKGCONFIG=ON \
    -DAEMU_COMMON_BUILD_CONFIG=gfxstream \
    -DENABLE_VKCEREAL_TESTS=OFF \
"

inherit cmake pkgconfig features_check

FILES:${PN} = "${libdir}/*"
FILES:${PN}-dev = "${includedir} ${libdir}/cmake ${libdir}/pkgconfig"

PROVIDES = "aemu_base"
RPROVIDES:${PN} += "aemu_base"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"
