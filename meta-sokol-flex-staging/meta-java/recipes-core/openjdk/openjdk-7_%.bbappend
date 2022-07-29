# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/files:"

ICEDTEAPATCHES:append:feature-sokol-flex-staging = "\
    file://icedtea-flags-to-compile-with-GCC-6.patch;apply=no \
    file://icedtea-specify-overloaded-variant-of-fmod.patch;apply=no \
"

DISTRIBUTION_PATCHES:append:feature-sokol-flex-staging = "\
    patches/icedtea-flags-to-compile-with-GCC-6.patch \
    patches/icedtea-specify-overloaded-variant-of-fmod.patch \
"

FILES:${JDKPN}-jdk:append:feature-sokol-flex-staging = " ${JDK_HOME}/tapset "

EXTRA_OEMAKE:append:feature-sokol-flex-staging = " LDFLAGS_HASH_STYLE='${LDFLAGS}'"

INSANE_SKIP:${JDKPN}-vm-zero:append:feature-sokol-flex-staging = " textrel"

python () {
    if 'feature-sokol-flex-staging' in d.getVar('OVERRIDES').split(':'):
        d.setVarFlag('DISTRIBUTION_PATCHES', 'export', 1)
}
