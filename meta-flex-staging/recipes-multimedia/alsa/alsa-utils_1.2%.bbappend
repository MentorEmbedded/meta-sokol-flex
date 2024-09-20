# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/${PN}:"

# Interrupt streaming via CTRL-C
SRC_URI:append:feature-flex-staging = " file://0001-alsa-utils-interrupt-streaming-via-signal.patch"
