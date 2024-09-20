# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

RDEPENDS:packagegroup-base-bluetooth:append:flex-os = "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio pulseaudio-server', '', d)}"

RDEPENDS:packagegroup-base-nfs:append:flex-os = " nfs-utils-client"
