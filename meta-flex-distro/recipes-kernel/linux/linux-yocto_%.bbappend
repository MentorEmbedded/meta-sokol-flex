# This material contains trade secrets or otherwise confidential information owned by Siemens Industry Software Inc.
# or its affiliates (collectively, "Siemens"), or its licensors. Access to and use of this information is strictly limited
# as set forth in the Customer's applicable agreements with Siemens.
# ---------------------------------------------------------------------------------------------------------------------
# Unpublished work. Copyright 2024 Siemens
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}/${PN}:"
SRC_URI:append:innexis-linux = " \
	${@bb.utils.contains('DISTRO_FEATURES', 'tracing', 'file://ftrace.cfg ', '', d)} \
"