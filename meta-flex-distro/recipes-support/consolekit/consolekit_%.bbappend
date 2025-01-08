# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}/${PN}:"

SRC_URI:append:innexis-linux = " \
    file://0001-Ensure-filesystems-are-still-mounted-when-consolekit.patch \
"
