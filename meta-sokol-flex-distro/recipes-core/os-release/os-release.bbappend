# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

OS_RELEASE_FIELDS:sokol-flex = "PRETTY_NAME NAME VERSION_ID VERSION VERSION_CODENAME ID HOME_URL SUPPORT_URL BUG_REPORT_URL"

ID:sokol-flex = "flex-os"
NAME:sokol-flex = "Flex OS"
VERSION:sokol-flex = "${DISTRO_VERSION}${@' (%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
VERSION_ID:sokol-flex = "${DISTRO_VERSION}"
VERSION_CODENAME:sokol-flex = "${@'(%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
PRETTY_NAME:sokol-flex = "${DISTRO_NAME} ${VERSION}"
