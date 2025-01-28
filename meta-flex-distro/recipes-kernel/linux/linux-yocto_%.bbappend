# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: GPL-2.0
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}/${PN}:"
SRC_URI:append:innexis-linux = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'tracing', 'file://ftrace.cfg ', '', d)} \
"
