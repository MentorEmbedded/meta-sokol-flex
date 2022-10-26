# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# We want a package manager in our base images
IMAGE_FEATURES += "package-management"

# We want libgcc to always be available, even if nothing needs it, as its size
# is minimal, and it's often needed by third party (or QA) binaries
IMAGE_INSTALL:append:sokol-flex = " libgcc"

LICENSE = "MIT"

inherit core-image

IMAGE_FEATURES:append:sokol-flex = " splash ${@bb.utils.contains('COMBINED_FEATURES', 'alsa', ' tools-audio', '', d)}"
IMAGE_INSTALL:append:sokol-flex = " util-linux-mkfs connman"
IMAGE_INSTALL:append:sokol-flex = " ${@bb.utils.contains_any('IMAGE_FEATURES', ['multimedia', 'graphics'], '${MACHINE_HWCODECS}', '', d)}"
