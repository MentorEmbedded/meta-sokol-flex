# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:flex-os := "${THISDIR}:"

dirs755:append:flex-os = "\
    ${sysconfdir}/alternatives \
    ${localstatedir}/lib/alternatives \
"
