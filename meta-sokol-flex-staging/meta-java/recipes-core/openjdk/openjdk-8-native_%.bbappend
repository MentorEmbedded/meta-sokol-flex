# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/${BPN}:"

SRC_URI:append:feature-sokol-flex-staging = " file://0001-PR3597-Potential-bogus-Wformat-overflow-warning-with.patch"

