# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/${BPN}:"

SRC_URI:append:feature-sokol-flex-staging = "\
    file://plug_fix_rate_converter_config.patch \
    file://fix_dshare_status.patch \
"
