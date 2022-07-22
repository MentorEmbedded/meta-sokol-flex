# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SRC_URI:prepend:sokol-flex = "git://github.com/MentorEmbedded/kbd;branch=2.4;protocol=https "
SRC_URI:remove:sokol-flex := "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz"
SRCREV:sokol-flex = "a95b34e0f4c78be7e3c137613b3d8c161ab322ba"
S:sokol-flex = "${WORKDIR}/git"

DEPENDS:append:sokol-flex = " bison-native"

# Don't exclude autopoint
EXTRA_AUTORECONF:sokol-flex = ""
