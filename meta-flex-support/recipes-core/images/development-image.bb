# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SUMMARY = "A development/debugging image that fully supports the target \
device hardware."

IMAGE_FEATURES = "${IMAGE_FEATURES_DEVELOPMENT} ${EXTRA_IMAGE_FEATURES}"
IMAGE_FEATURES_DEVELOPMENT ?= "debug-tweaks"
IMAGE_FEATURES_DEVELOPMENT:append:feature-tracing = " tools-profile"
IMAGE_FEATURES_DISABLED_DEVELOPMENT ?= ""
IMAGE_FEATURES:remove = "${IMAGE_FEATURES_DISABLED_DEVELOPMENT}"

# We want a package manager in our base images
IMAGE_FEATURES += "package-management"

# We want libgcc to always be available, even if nothing needs it, as its size
# is minimal, and it's often needed by third party (or QA) binaries
IMAGE_INSTALL:append:flex-os = " libgcc"

LICENSE = "MIT"

inherit core-image image-buildinfo rootfs-disable-tty1-login

IMAGE_INSTALL:append:flex-os = " util-linux-mkfs"

# Allow our BSPs to disable the login on tty1 via MACHINE_FEATURES
IMAGE_FEATURES:append:flex-os = " ${@bb.utils.contains('MACHINE_FEATURES', 'disable-tty1-login', 'disable-tty1-login', '', d)}"
