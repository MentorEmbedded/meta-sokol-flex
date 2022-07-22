# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}/qtbase:"
SRC_URI:append:feature-sokol-flex-staging = " file://Fix-note-alignment.patch"
