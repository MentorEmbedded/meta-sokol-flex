# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SUMMARY = "A development/debugging image that fully supports the target \
device hardware."

IMAGE_FEATURES:append = " \
    codebench-debug \
    debug-tweaks \
    package-management \
    ssh-server-openssh \
"

IMAGE_FEATURES:append:feature-tracing = " tools-profile"

# We want libgcc to always be available, even if nothing needs it, as its size
# is minimal, and it's often needed by third party (or QA) binaries
IMAGE_INSTALL:append:flex-os = " libgcc"

LICENSE = "MIT"

inherit core-image image-buildinfo

# Remove kernel image installation in the RFS by default
PACKAGE_EXCLUDE = "kernel-image-*"
