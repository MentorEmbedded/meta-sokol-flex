# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/files:"

SRC_URI:append:feature-flex-staging = " file://0001-initrdscripts-init-live.sh-Fixed-mounts-fail-to-move.patch"
