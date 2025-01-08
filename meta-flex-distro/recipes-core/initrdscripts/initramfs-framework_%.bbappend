# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}/${PN}:"

RDEPENDS:initramfs-module-lvm:append:innexis-linux = " lvm2"
RRECOMMENDS:${PN}-base:append:innexis-linux = " initramfs-module-lvm"
