# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

#Don't use sysvinit based network configuration. We have connman for that purpose
SYSVINIT_SCRIPTS:sokol-flex := "${@oe.utils.str_filter_out('init-ifupdown', SYSVINIT_SCRIPTS, d)}"
