# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/files:"

SRC_URI:append:feature-flex-staging = " file://0001-ui-sdl2-fix-mouse-co-ordinates-for-scaled-guest-surf.patch"
