# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:flex-os := "${THISDIR}/${PN}:"
SRC_URI:prepend:flex-os = "file://0001-ftrace-Avoid-crashing-if-format-doesn-t-match-expect.patch "
