# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:flex-os := "${THISDIR}/${PN}:"
SRC_URI:prepend:flex-os = "file://0001-ftrace-Avoid-crashing-if-format-doesn-t-match-expect.patch "

# Remove build path references from debug symbols and avoid QA warnings
# TODO: remove when upstream is updated
TUNE_CCARGS:append = " ${DEBUG_PREFIX_MAP}"
