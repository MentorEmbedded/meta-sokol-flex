# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/${PN}:"

SRC_URI:append:feature-sokol-flex-staging = " file://0001-Use-an-array-for-efi-ld-to-allow-for-ld-arguments.patch"

LDFLAGS:remove:feature-sokol-flex-staging := "${@ " ".join(d.getVar('LD').split()[1:])} "
EXTRA_OEMESON:append:feature-sokol-flex-staging = ' "-Defi-ld=${@meson_array("LD", d)}"'
