# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SRC_URI:prepend:mel = "git://github.com/MentorEmbedded/kbd;branch=2.5;protocol=https "
SRC_URI:remove:mel := "${KERNELORG_MIRROR}/linux/utils/${BPN}/${BP}.tar.xz"
SRCREV:mel = "76a726463757933231cbf1df54f54181e9080edc"
S:mel = "${WORKDIR}/git"

DEPENDS:append:mel = " bison-native"

# Don't exclude autopoint
EXTRA_AUTORECONF:mel = ""
