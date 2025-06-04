# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/files:"

SRC_URI:append:feature-flex-staging = " file://0001-cfg-docker.cfg-enable-CONFIG_BPF_SYSCALL.patch \
                                        file://0002-cfg-docker.scc-include-bridge.scc-for-docker-bridge-.patch \
                                        file://0003-cfg-docker.cfg-enable-pre-req-of-DM_THIN_PROVISIONIN.patch \
"
