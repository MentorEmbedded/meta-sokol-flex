# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# We need openssl support for nativesdk-curl to ensure we can clone https
# repositories with nativesdk-git.
DEPENDS:append:class-nativesdk:sokol-flex = " nativesdk-openssl"
PACKAGECONFIG:append:class-nativesdk:sokol-flex = " ssl"
