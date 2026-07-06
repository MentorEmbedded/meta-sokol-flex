SUMMARY = "Mouse, Keyboard and Screen for QEMU via D-Bus"
DESCRIPTION = "This library provides a Mouse, Keyboard, and Screen interface to QEMU using D-Bus device support."
HOMEPAGE = "https://gitlab.gnome.org/GNOME/libmks"
LICENSE = "LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=4b54a1fd55a448865a0b32d41598759d"

SRC_URI = "\
    git://gitlab.gnome.org/GNOME/libmks.git;branch=main;protocol=https \
"

SRCREV = "f8d9be2e56046dbeecaf54d654fa4fcdf401aada"

PV = "0.1.5"

REQUIRED_DISTRO_FEATURES = "gobject-introspection-data"

inherit meson pkgconfig gobject-introspection gettext vala

DEPENDS = " \
    glib-2.0 \
    libxml2 \
    libdrm \
    virtual/libgbm \
    virtual/egl \
    virtual/libgles2 \
    pixman \
    vte \
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
