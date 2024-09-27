# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# Flex OS Flex does not support systemtap. Systemtap brings boost which takes lots of resources. So we do not need it.
SYSTEMTAP:flex-os = ""

# Add perfetto to tools-profile for tracing
RDEPENDS:${PN}:append:flex-os = " perfetto libperfetto"
