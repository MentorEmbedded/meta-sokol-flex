# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:flex-os := "${THISDIR}/${PN}:"

SRC_URI:append:flex-os = " \
    file://0001-Ensure-filesystems-are-still-mounted-when-consolekit.patch \
"
