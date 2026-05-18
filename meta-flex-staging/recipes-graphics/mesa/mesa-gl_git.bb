# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

require mesa.inc

SUMMARY += " (OpenGL only, no EGL/GLES)"

PROVIDES = "virtual/libgl virtual/mesa"

TARGET_CFLAGS = "-I${STAGING_INCDIR}/drm"

PACKAGECONFIG = "expat opengl gallium ${@bb.utils.filter('DISTRO_FEATURES', 'x11', d)} xmlconfig zlib"

# removed libclc from following as it depends on clang and we dont need it for now
PACKAGECONFIG:append:x86 = " gallium-llvm intel amd nouveau svga"
PACKAGECONFIG:append:x86-64 = " gallium-llvm intel amd nouveau svga"
PACKAGECONFIG:append:i686 = " gallium-llvm intel amd nouveau svga"
