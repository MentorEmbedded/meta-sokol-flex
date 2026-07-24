# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: BSD-3-Clause
# ---------------------------------------------------------------------------------------------------------------------

DESCRIPTION = "Open Asset Import Library is a portable Open Source library to import \
               various well-known 3D model formats in a uniform manner."
HOMEPAGE = "http://www.assimp.org/"
SECTION = "devel"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d9d5275cab4fb13ae624d42ce64865de"

DEPENDS += "zlib"

SRC_URI = " \
    git://github.com/assimp/assimp.git;protocol=https;branch=master \
    file://0001-CMakeLists.txt-Make-paths-in-assimpTargets.cmake-gen.patch \
"

UPSTREAM_CHECK_GITTAGREGEX = "v(?P<pver>(\d+(\.\d+)+))"

SRCREV = "6a08c39e3a91ef385e76515cfad86aca4bfd57ff"


inherit cmake

EXTRA_OECMAKE = " \
	-DASSIMP_WARNINGS_AS_ERRORS=OFF \
	-DCMAKE_CXX_FLAGS=-Wno-error=array-bounds \
    -D ASSIMP_HUNTER_ENABLED=OFF \
    -D ASSIMP_DOUBLE_PRECISION=OFF \
    -D ASSIMP_OPT_BUILD_PACKAGES=OFF \
    -D ASSIMP_BUILD_ASSIMP_TOOLS=OFF \
    -D ASSIMP_BUILD_SAMPLES=OFF \
    -D ASSIMP_BUILD_ZLIB=OFF \
    -D ASSIMP_BUILD_TESTS=OFF \
    -D ASSIMP_IGNORE_GIT_HASH=OFF \
    -D ASSIMP_LIB_INSTALL_DIR=${baselib} \
"
INSANE_SKIP:${PN} += "buildpaths"
