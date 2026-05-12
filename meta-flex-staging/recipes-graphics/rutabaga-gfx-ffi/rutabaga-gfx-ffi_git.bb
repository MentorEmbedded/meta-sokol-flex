SUMMARY = "rutabaga_ffi"
DESCRIPTION = "Handling virtio-gpu protocols with C API"
HOMEPAGE = "https://github.com/magma-gpu/rutabaga_gfx"

LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=03dbda889fc4ff3d6d2981a1504ea88b"

inherit cargo
inherit cargo-update-recipe-crates
inherit pkgconfig

SRC_URI = "git://github.com/magma-gpu/rutabaga_gfx.git;branch=main;protocol=https"
SRC_URI += "\
    file://rutabaga_gfx_ffi.pc.in \
    file://unstable.patch \
"
SRCREV = "e89c714fdb627148beeaa69a0ce745b38d1e3fb5"

require rutabaga-gfx-ffi-crates.inc

DEPENDS = "gfxstream virglrenderer"

RDEPENDS:${PN}-dev = "${PN}"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"

EXTRA_OECARGO_PATHS += "${S}"
CARGO_BUILD_FLAGS += "--features=gfxstream,virgl_renderer,vulkano"
CARGO_SRC_DIR = "ffi"

INHIBIT_PACKAGE_STRIP = "1"

# rutabaga has no Cargo.lock file, so we need to generate one to make the rest of the tooling happy...
# Note:
# 1. rutabaga 0.1.76 crate need to be patched to sync with upstream using Cargo.toml [patch.crates-io]
# 2. However, OE doesn't allow network access except for do_fetch stage. Therefore, this step will fail
# in any case when any crate dependancies are overriden.
# 3. Therefore, we workaround by generating cargo.lock file offline via $bitbake -c devshell
#
do_configure:append() {
    # Ensure rust features are passed and Cargo.lock gets generated if missing
    if [ ! -e ${S}/Cargo.lock ]; then
        cd ${S}
        ${CARGO} generate-lockfile --offline
    fi
}

LIB_NAME = "librutabaga_gfx_ffi.so"
RUTABAGA_VERSION = "0.1.76"
RUTABAGA_VERSION_MAJOR = "0"

do_install() {
    install -d ${D}${libdir}
    install -m755 ${B}/target/${CARGO_TARGET_SUBDIR}/deps/${LIB_NAME}  ${D}${libdir}/
    mv ${D}${libdir}/${LIB_NAME} ${D}${libdir}/${LIB_NAME}.${RUTABAGA_VERSION}

    install -d ${D}${libdir}/pkgconfig
    sed -e 's:@libdir@:${libdir}:' \
        -e 's:@includedir@:${includedir}:' \
        -e 's:@version@:${RUTABAGA_VERSION}:' \
      ${UNPACKDIR}/rutabaga_gfx_ffi.pc.in > ${D}${libdir}/pkgconfig/rutabaga_gfx_ffi.pc

    ln -sf ${LIB_NAME}.${RUTABAGA_VERSION} ${D}${libdir}/${LIB_NAME}.${RUTABAGA_VERSION_MAJOR}
    ln -sf ${LIB_NAME}.${RUTABAGA_VERSION} ${D}${libdir}/${LIB_NAME}

    install -d ${D}${includedir}/rutabaga_gfx
    install -m644 ${S}/${CARGO_SRC_DIR}/src/include/rutabaga_gfx_ffi.h ${D}${includedir}/rutabaga_gfx/
}

BBCLASSEXTEND = "nativesdk"
