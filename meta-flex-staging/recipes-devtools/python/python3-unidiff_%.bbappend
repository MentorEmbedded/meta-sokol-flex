# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/${PN}:"

# Fix for https://github.com/matiasb/python-unidiff/pull/97
SRC_URI:append:feature-flex-staging = " file://97.patch"
