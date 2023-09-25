# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

require recipes-kernel/lttng/lttng-ust_2.13.5.bb

SRC_URI[sha256sum] = "e7e04596dd73ac7aa99e27cd000f949dbb0fed51bd29099f9b08a25c1df0ced5"

# Reset PE due to PV bump, as it's no longer necessary
PE = ""

# Pull in original patch files from oe-core
ORIG_FILE := "${@bb.utils.which(d.getVar('BBPATH'), 'recipes-kernel/lttng/lttng-ust_2.13.5.bb')}"
ORIG_FILE_DIRNAME = "${@os.path.dirname(d.getVar('ORIG_FILE'))}"
FILESPATH .= ":${ORIG_FILE_DIRNAME}/lttng-ust"
