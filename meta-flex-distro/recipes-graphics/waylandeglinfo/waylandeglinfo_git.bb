# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SUMMARY = "Small utility to print the EGL config / info on wayland"
HOMEPAGE = "https://github.com/clopez/waylandeglinfo"
BUGTRACKER = "https://github.com/clopez/waylandeglinfo/issues"

SRCREV = "b295363ab4e858fe4cfb32ee4e71a177e939b691"
SRC_URI = " \
    git://github.com/clopez/waylandeglinfo.git;protocol=https;branch=master \
    file://0001-waylandes2info-Try-higher-GL-ES-profiles.patch \
"



LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=1c45a60ed9e3db41ec069e422044577e"
DEPENDS = "virtual/egl wayland"

inherit cmake pkgconfig

# Fix cmake 4.3 dropping <3.5 support
do_configure:prepend() {
    sed -i "s/cmake_minimum_required(VERSION 3.1)/cmake_minimum_required(VERSION 3.5)/" ${S}/CMakeLists.txt
}
