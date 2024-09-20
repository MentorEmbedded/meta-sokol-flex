# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/qtbase:"
SRC_URI:append:feature-flex-staging = " file://Fix-note-alignment.patch"
