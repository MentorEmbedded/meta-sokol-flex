# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:sokol-flex := "${THISDIR}/busybox:"

# fancy-head.cfg is enabled so we have head -c, which we need for our tracing
# scripts with lttng
SRC_URI:append:sokol-flex = "\
    file://setsid.cfg \
    file://fancy-head.cfg \
	file://pidof.cfg \
	file://top.cfg \
"
