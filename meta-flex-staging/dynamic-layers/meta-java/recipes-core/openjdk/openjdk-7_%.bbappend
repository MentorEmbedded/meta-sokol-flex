# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/files:"

ICEDTEAPATCHES:append:feature-flex-staging = "\
    file://icedtea-flags-to-compile-with-GCC-6.patch;apply=no \
    file://icedtea-specify-overloaded-variant-of-fmod.patch;apply=no \
"

DISTRIBUTION_PATCHES:append:feature-flex-staging = "\
    patches/icedtea-flags-to-compile-with-GCC-6.patch \
    patches/icedtea-specify-overloaded-variant-of-fmod.patch \
"

FILES:${JDKPN}-jdk:append:feature-flex-staging = " ${JDK_HOME}/tapset "

EXTRA_OEMAKE:append:feature-flex-staging = " LDFLAGS_HASH_STYLE='${LDFLAGS}'"

INSANE_SKIP:${JDKPN}-vm-zero:append:feature-flex-staging = " textrel"

python () {
    if 'feature-flex-staging' in d.getVar('OVERRIDES').split(':'):
        d.setVarFlag('DISTRIBUTION_PATCHES', 'export', 1)
}
