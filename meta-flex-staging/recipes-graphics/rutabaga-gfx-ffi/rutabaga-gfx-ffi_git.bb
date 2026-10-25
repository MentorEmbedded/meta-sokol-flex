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
SRCREV = "db76468124945af9c8adf2ea8e88dcf8ca06b47d"

require rutabaga-gfx-ffi-crates.inc

DEPENDS = "gfxstream virglrenderer"

RDEPENDS:${PN}-dev = "${PN}"

COMPATIBLE_HOST = "(arm|aarch64).*-linux"

EXTRA_OECARGO_PATHS += "${S}"
CARGO_BUILD_FLAGS += "--features=gfxstream,virgl_renderer,vulkano"
CARGO_SRC_DIR = "ffi"

INHIBIT_PACKAGE_STRIP = "1"

# rutabaga has no Cargo.lock upstream and the cargo class builds with --frozen, so a Cargo.lock
# has to exist in ${S}. It is generated with network access and kept in the layer as
# rutabaga-gfx-ffi/Cargo.lock (an empty placeholder is shipped until it is generated):
#
#   bitbake -c generate_lockfile rutabaga-gfx-ffi   # rewrites rutabaga-gfx-ffi/Cargo.lock
#   bitbake -c update_crates rutabaga-gfx-ffi       # regenerates rutabaga-gfx-ffi-crates.inc
#   bitbake rutabaga-gfx-ffi
#
# Repeat the first two commands whenever SRCREV changes. The normal cargo configuration cannot be
# used to resolve: it replaces crates.io by an offline directory with only the crates listed in
# the crates.inc, which is exactly what is being regenerated.
SRC_URI += "file://Cargo.lock"

python do_patch:append() {
    import shutil
    shutil.copy(d.expand('${UNPACKDIR}/Cargo.lock'), d.expand('${S}/Cargo.lock'))
}

do_configure:prepend() {
    if [ ! -s ${S}/Cargo.lock ]; then
        bbfatal "No Cargo.lock: run 'bitbake -c generate_lockfile ${PN}' and then 'bitbake -c update_crates ${PN}'"
    fi
}

# Manual task, never part of a normal build. Uses the cargo of the build system, so the lock file
# format is guaranteed to be readable by the cargo that later builds the recipe.
do_generate_lockfile[depends] += "ca-certificates-native:do_populate_sysroot"
do_generate_lockfile[nostamp] = "1"
do_generate_lockfile[network] = "1"
do_generate_lockfile() {
    cd ${S}
    rm -f Cargo.lock

    # Own cargo home: no source replacement, resolve against the real crates.io index.
    export CARGO_HOME="${WORKDIR}/cargo_home_lockgen"
    rm -rf "${CARGO_HOME}"
    export CARGO_HTTP_CAINFO="${STAGING_ETCDIR_NATIVE}/ssl/certs/ca-certificates.crt"
    # Prefer crate versions that the rustc of this build system can compile (needs cargo >= 1.84,
    # ignored by older versions).
    export CARGO_RESOLVER_INCOMPATIBLE_RUST_VERSIONS="fallback"

    ${CARGO} generate-lockfile

    install -d ${THISDIR}/${BPN}
    install -m 0644 Cargo.lock ${THISDIR}/${BPN}/Cargo.lock
    bbnote "Wrote ${THISDIR}/${BPN}/Cargo.lock - now run: bitbake -c update_crates ${PN}"
}
addtask generate_lockfile after do_prepare_recipe_sysroot do_patch

LIB_NAME = "librutabaga_gfx_ffi.so"
RUTABAGA_VERSION = "0.1.85"
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
