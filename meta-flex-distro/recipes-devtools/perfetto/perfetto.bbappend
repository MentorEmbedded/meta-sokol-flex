# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}/${PN}:"

# Remove build path references from debug symbols and avoid QA warnings
# TODO: remove when upstream is updated
TUNE_CCARGS:append = " ${DEBUG_PREFIX_MAP}"
