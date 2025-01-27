# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

EXTRA_OECMAKE:append:class-target = " -DLLVM_HOST_TRIPLE=${TARGET_SYS}"
EXTRA_OECMAKE:append:class-nativesdk = " -DLLVM_HOST_TRIPLE=${SDK_SYS}"
