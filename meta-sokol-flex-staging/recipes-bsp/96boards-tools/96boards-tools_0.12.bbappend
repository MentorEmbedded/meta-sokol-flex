# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/${PN}:"
SRC_URI:append:feature-sokol-flex-staging = " file://0001-resize-helper-make-parted-not-prompt-for-user-interv.patch"
