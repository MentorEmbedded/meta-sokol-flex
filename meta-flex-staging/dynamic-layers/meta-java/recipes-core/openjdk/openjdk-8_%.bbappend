# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/${PN}:"

SRC_URI:append:feature-flex-staging = " file://0001-PR3597-Potential-bogus-Wformat-overflow-warning-with.patch"

