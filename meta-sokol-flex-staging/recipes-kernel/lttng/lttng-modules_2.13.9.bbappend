# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/lttng-modules:"

SRC_URI:append:feature-sokol-flex-staging = "\
    file://0001-fix-mm-page_alloc-fix-tracepoint-mm_page_alloc_zone_.patch \
"
