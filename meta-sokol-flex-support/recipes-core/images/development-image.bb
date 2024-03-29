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

require recipes-core/images/flex-image.inc
