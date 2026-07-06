SUMMARY = "Mouse, Keyboard and Screen for QEMU via D-Bus"
DESCRIPTION = "This library provides a Mouse, Keyboard, and Screen interface to QEMU using D-Bus device support."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libmks"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

SRC_URI = "\
    git://gitlab.gnome.org/GNOME/libmks.git;branch=main;protocol=https \
    file://0001-paintable-Fix-rejection-of-ARGB32-scanouts.patch \
    file://0002-framebuffer-Support-ABGR8888-and-XBGR8888-scanouts.patch \
    file://0003-framebuffer-Draw-the-framebuffer-as-a-single-texture.patch \
    file://0004-screen-Do-not-require-both-keyboard-and-mouse.patch \
    file://0005-display-Disable-GtkGraphicsOffload.patch \
    file://0006-tools-Open-a-window-for-every-display-head.patch \
    file://0007-tools-Add-no-auto-resize.patch \
    file://0008-tools-Add-monitor-fullscreen-and-list-monitors.patch \
    file://0009-tools-Exit-when-the-VM-goes-away.patch \
    file://0010-tools-Default-to-the-GL-renderer.patch \
"

SRCREV = "90f722234b903591b86f70620040f202e51bdbc3"

PV = "0.1.6+git"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

inherit meson pkgconfig features_check gobject-introspection gettext vala

DEPENDS = " \
    glib-2.0 \
    gtk4 \
    libdrm \
    virtual/egl \
    virtual/libgles2 \
"

EXTRA_OEMESON = " \
    -Dintrospection=enabled \
    -Dinstall-tools=true \
    -Dtests=false \
"

GIR_MESON_ENABLE_FLAG = "enabled"
GIR_MESON_DISABLE_FLAG = "disabled"

PACKAGES =+ " \
    ${PN}-gir \
"

FILES:${PN} += " \
    ${libdir}/libmks*.so.* \
"

FILES:${PN}-dev += " \
    ${includedir}/* \
    ${libdir}/pkgconfig/*.pc \
    ${libdir}/libmks*.so \
"

FILES:${PN}-gir = " \
    ${datadir}/gir-1.0/*.gir \
    ${libdir}/girepository-1.0/*.typelib \
"

RDEPENDS:${PN}-dev += "${PN}"
