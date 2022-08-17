# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/${PN}:"
SRC_URI:append:feature-sokol-flex-staging = " file://0001-udev-extraconf-mount.sh-add-LABELs-to-mountpoints.patch \
             file://0001-udev-extraconf-mount.sh-define-mount-prefix-using-a-.patch \
             file://0002-udev-extraconf-mount.sh-save-mount-name-in-our-tmp-f.patch \
             file://0003-udev-extraconf-mount.sh-only-mount-devices-on-hotplu.patch \
             file://0001-udev-extraconf-mount.sh-ignore-lvm-in-automount.patch"

RDEPENDS:${PN}:append:feature-sokol-flex-staging = " util-linux-blkid ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'util-linux-lsblk', '', d)}"

FILES:${PN}:append:feature-sokol-flex-staging = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${sysconfdir}/systemd/system/systemd-udevd.service', '', d)}"

pkg_postinst:${PN} () {
	if [ -e $D${systemd_unitdir}/system/systemd-udevd.service ]; then
		sed -i "/\[Service\]/aMountFlags=shared" $D${systemd_unitdir}/system/systemd-udevd.service
	fi
}

pkg_postrm:${PN} () {
	if [ -e $D${systemd_unitdir}/system/systemd-udevd.service ]; then
		sed -i "/MountFlags=shared/d" $D${systemd_unitdir}/system/systemd-udevd.service
	fi
}
