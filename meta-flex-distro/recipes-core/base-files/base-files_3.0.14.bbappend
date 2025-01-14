# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:innexis-linux := "${THISDIR}:"

dirs755:append:innexis-linux = "\
    ${sysconfdir}/alternatives \
    ${localstatedir}/lib/alternatives \
"
