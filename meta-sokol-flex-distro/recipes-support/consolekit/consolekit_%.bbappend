# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:sokol-flex := "${THISDIR}/${PN}:"

SRC_URI:append:sokol-flex = " \
    file://0001-Ensure-filesystems-are-still-mounted-when-consolekit.patch \
"
