# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:sokol-flex := "${THISDIR}:"

dirs755:append:sokol-flex = "\
    ${sysconfdir}/alternatives \
    ${localstatedir}/lib/alternatives \
"
