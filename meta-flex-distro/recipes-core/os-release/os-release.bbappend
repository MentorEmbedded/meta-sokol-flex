# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

OS_RELEASE_FIELDS:innexis-linux = "PRETTY_NAME NAME VERSION_ID VERSION VERSION_CODENAME ID HOME_URL SUPPORT_URL BUG_REPORT_URL"

ID:innexis-linux = "innexis-linux"
NAME:innexis-linux = "Innexis Linux"
VERSION:innexis-linux = "${DISTRO_VERSION}${@' (%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
VERSION_ID:innexis-linux = "${DISTRO_VERSION}"
VERSION_CODENAME:innexis-linux = "${@'(%s)' % DISTRO_CODENAME if 'DISTRO_CODENAME' in d else ''}"
PRETTY_NAME:innexis-linux = "${DISTRO_NAME} ${VERSION}"
