# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

RDEPENDS:packagegroup-base-bluetooth:append:sokol-flex = "${@bb.utils.contains('DISTRO_FEATURES', 'pulseaudio', 'pulseaudio pulseaudio-server', '', d)}"

RDEPENDS:packagegroup-base-nfs:append:sokol-flex = " nfs-utils-client"
