# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}:"

dirs755:append:sokol-flex = "\
    ${sysconfdir}/alternatives \
    ${localstatedir}/lib/alternatives \
"
