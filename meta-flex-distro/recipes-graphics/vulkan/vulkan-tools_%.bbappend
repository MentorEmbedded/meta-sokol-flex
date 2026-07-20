# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG[wayland] = "-DBUILD_WSI_WAYLAND_SUPPORT=ON, -DBUILD_WSI_WAYLAND_SUPPORT=OFF, wayland wayland-native wayland-protocols"
EXTRA_OECMAKE:append = " -DBUILD_CUBE=ON -DCUBE_WSI_SELECTION=WAYLAND"

do_configure:prepend() {
    sed -i "s|pkg_get_variable(WAYLAND_SCANNER_EXECUTABLE wayland-scanner wayland_scanner)|set(WAYLAND_SCANNER_EXECUTABLE ${RECIPE_SYSROOT_NATIVE}/usr/bin/wayland-scanner)|g" ${S}/cube/CMakeLists.txt
}
