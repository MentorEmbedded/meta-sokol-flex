# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/files:"
SRC_URI:append:feature-sokol-flex-staging = " file://0001-connman-implement-network-interface-management-techn.patch"
