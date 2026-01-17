# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}/${PN}:"

SRC_URI:append:innexis-linux = " file://0001-cube-CMakeLists.txt-locate-wayland-scanner-native-bi.patch"

PACKAGECONFIG[wayland] = "-DBUILD_WSI_WAYLAND_SUPPORT=ON, -DBUILD_WSI_WAYLAND_SUPPORT=OFF, wayland wayland-native wayland-protocols"
EXTRA_OECMAKE:append:innexis-linux = " -DBUILD_CUBE=ON -DCUBE_WSI_SELECTION=WAYLAND"
