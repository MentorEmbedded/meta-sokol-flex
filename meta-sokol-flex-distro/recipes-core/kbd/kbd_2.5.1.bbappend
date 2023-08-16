# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SRC_URI:prepend:sokol-flex = "git://github.com/MentorEmbedded/kbd;branch=2.5;protocol=https "
SRC_URI:remove:sokol-flex := "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz"
SRCREV:sokol-flex = "76a726463757933231cbf1df54f54181e9080edc"
S:sokol-flex = "${WORKDIR}/git"

DEPENDS:append:sokol-flex = " bison-native"

# Don't exclude autopoint
EXTRA_AUTORECONF:sokol-flex = ""
