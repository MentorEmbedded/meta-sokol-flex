SUMMARY = "VirGL virtual OpenGL renderer"
DESCRIPTION = "Virgil is a research project to investigate the possibility of \
creating a virtual 3D GPU for use inside qemu virtual machines, that allows \
the guest operating system to use the capabilities of the host GPU to \
accelerate 3D rendering."
HOMEPAGE = "https://virgil3d.github.io/"

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=c81c08eeefd9418fca8f88309a76db10"

DEPENDS = "libdrm libepoxy python3-pyyaml-native virtual/egl virtual/libgbm"
SRCREV = "ca50e008863837e094747a69974dde3ae148aeaa"
SRC_URI = "git://gitlab.freedesktop.org/virgl/virglrenderer.git;branch=main;protocol=https;tag=${PV} \
           file://0001-meson.build-use-python3-directly-for-python.patch \
           file://986b5fc57b07c06b5e0b3a3694d06898ebc80163.patch \
           file://support_yv12_in_r8.patch \
           file://0001-vrend-Support-tiled-gbm-allocation.patch \
           file://0002-vkr-keep-internal-queue-sync-fences-non-exportable.patch \
           file://0003-vrend-sample-external-only-dma-bufs-through-a-shadow.patch \
           file://0004-egl-Make-vrend-gbm-and-native-context-by-default-use.patch \
           file://0005-vkr-prefer-the-physical-device-sharing-the-GPU-with-.patch \
           file://0006-vrend-egl-fix-DRM-node-lookup-of-the-EGL-display-dev.patch \
           file://0007-vrend-don-t-back-scanout-resources-with-GBM-if-their.patch \
           file://0008-vrend-replace-the-Mesa-GL_VERSION-gate-of-the-GBM-la.patch \
           "

inherit meson pkgconfig features_check

PACKAGECONFIG ?= "${@bb.utils.contains('DISTRO_FEATURES', 'vulkan', 'venus', '', d)}"

PACKAGECONFIG[venus] = "-Dvenus=true,-Dvenus=false,vulkan-loader vulkan-headers"
PACKAGECONFIG[va] = "-Dvideo=true,-Dvideo=false,libva"
PACKAGECONFIG[minigbm_allocation] = "-Dminigbm_allocation=true,-Dminigbm_allocation=false"
PACKAGECONFIG[tests] = "-Dtests=true,-Dtests=false,libcheck"

BBCLASSEXTEND = "native nativesdk"

REQUIRED_DISTRO_FEATURES = "opengl"
