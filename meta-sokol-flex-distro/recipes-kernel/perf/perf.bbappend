# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# Sokol Flex OS supports BSP kernel versions which upstream doesn't care about.
# Remove -I/usr/local/include from the default INCLUDES
EXTRA_OEMAKE:append:sokol-flex = " 'INCLUDES=-I. $(CONFIG_INCLUDES)'"
