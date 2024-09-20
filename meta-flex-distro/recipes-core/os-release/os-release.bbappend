# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

OS_RELEASE_FIELDS:flex-os = "PRETTY_NAME NAME VERSION_ID VERSION VERSION_CODENAME ID HOME_URL SUPPORT_URL BUG_REPORT_URL"

ID:flex-os = "flex-os"
NAME:flex-os = "Flex OS"
VERSION:flex-os = "${DISTRO_VERSION}${@' (%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
VERSION_ID:flex-os = "${DISTRO_VERSION}"
VERSION_CODENAME:flex-os = "${@'(%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
PRETTY_NAME:flex-os = "${DISTRO_NAME} ${VERSION}"
