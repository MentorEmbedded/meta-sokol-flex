# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"

SRC_URI:append:feature-sokol-flex-staging = " file://0001-ebgpart-fix-conflict-with-__unused-in-system-headers.patch"
