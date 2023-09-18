# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# We want a package manager in our base images
IMAGE_FEATURES += "package-management"

# We want libgcc to always be available, even if nothing needs it, as its size
# is minimal, and it's often needed by third party (or QA) binaries
IMAGE_INSTALL:append:sokol-flex = " libgcc"

LICENSE = "MIT"

inherit core-image image-buildinfo rootfs-disable-tty1-login

IMAGE_FEATURES:append:sokol-flex = " ${@bb.utils.contains('COMBINED_FEATURES', 'alsa', ' tools-audio', '', d)}"
IMAGE_INSTALL:append:sokol-flex = " util-linux-mkfs connman"
IMAGE_INSTALL:append:sokol-flex = " ${@bb.utils.contains_any('IMAGE_FEATURES', ['multimedia', 'graphics'], '${MACHINE_HWCODECS}', '', d)}"

# Allow our BSPs to disable the login on tty1 via MACHINE_FEATURES
IMAGE_FEATURES:append:sokol-flex = " ${@bb.utils.contains('MACHINE_FEATURES', 'disable-tty1-login', 'disable-tty1-login', '', d)}"
