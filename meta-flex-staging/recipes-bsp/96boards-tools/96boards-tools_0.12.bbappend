# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/${PN}:"
SRC_URI:append:feature-flex-staging = " file://0001-resize-helper-make-parted-not-prompt-for-user-interv.patch"
