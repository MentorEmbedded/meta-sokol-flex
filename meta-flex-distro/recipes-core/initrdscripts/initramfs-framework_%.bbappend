# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:flex-os := "${THISDIR}/${PN}:"

RDEPENDS:initramfs-module-lvm:append:flex-os = " lvm2"
RRECOMMENDS:${PN}-base:append:flex-os = " initramfs-module-lvm"
