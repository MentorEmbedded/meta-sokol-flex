# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:sokol-flex := "${THISDIR}/${PN}:"

RDEPENDS:initramfs-module-lvm:append:sokol-flex = " lvm2"
RRECOMMENDS:${PN}-base:append:sokol-flex = " initramfs-module-lvm"
