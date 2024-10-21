# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SUMMARY = "A development/debugging image that fully supports the target \
device hardware."

IMAGE_FEATURES = "${EXTRA_IMAGE_FEATURES}"
EXTRA_IMAGE_FEATURES = "debug-tweaks codebench-debug ssh-server-openssh"
IMAGE_FEATURES:append:feature-tracing = " tools-profile"

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

# Remove kernel image installation in the RFS by default
PACKAGE_EXCLUDE = "kernel-image-*"
