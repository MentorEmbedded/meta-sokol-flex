# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SRC_URI:prepend:sokol-flex = "git://github.com/MentorEmbedded/kbd;branch=2.6;protocol=https "
SRC_URI:remove:sokol-flex := "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz"
SRCREV:sokol-flex = "a0a5164d57b15a01f2da8a1368025e53905fc5da"
S:sokol-flex = "${WORKDIR}/git"

DEPENDS:append:sokol-flex = " bison-native"

# Don't exclude autopoint
EXTRA_AUTORECONF:sokol-flex = ""
