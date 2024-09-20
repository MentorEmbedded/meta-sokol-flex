# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# Flex OS supports BSP kernel versions which upstream doesn't care about.
# Remove -I/usr/local/include from the default INCLUDES
EXTRA_OEMAKE:append:flex-os = " 'INCLUDES=-I. $(CONFIG_INCLUDES)'"
