# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/${PN}:"

# Fix for https://github.com/matiasb/python-unidiff/pull/97
SRC_URI:append:feature-sokol-flex-staging = " file://97.patch"
