# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend := "${THISDIR}/weston-init:"

SRC_URI += "file://weston-init.sh \
            file://weston-virtio-gpu-wrapper.sh"

FILES:${PN} += "${systemd_system_unitdir}/weston.service.d/10-virtio-gpu.conf"

do_install:append () {
    # Set WAYLAND_DISPLAY by default for console or ssh users
    install -d ${D}${sysconfdir}/profile.d/
    install ${UNPACKDIR}/weston-init.sh ${D}${sysconfdir}/profile.d/weston-init.sh

    # Prefer virtio-gpu DRM device over VKMS.
    # Weston find_primary_gpu() picks the first udev-enumerated DRM
    # device, which is VKMS (card0) in wrynose. Install a wrapper
    # that uses udevadm to discover the virtio-pci DRM card and pass
    # it to weston via --drm-device, falling back to card0 when
    # virtio-gPU is not present.
    install -d ${D}${bindir}
    install -m 0755 ${UNPACKDIR}/weston-virtio-gpu-wrapper.sh ${D}${bindir}/weston-virtio-gpu-wrapper

    install -d ${D}${systemd_system_unitdir}/weston.service.d
    cat > ${D}${systemd_system_unitdir}/weston.service.d/10-virtio-gpu.conf << CFOEOF
[Service]
ExecStart=
ExecStart=${bindir}/weston-virtio-gpu-wrapper
CFOEOF
}

RDEPENDS:${PN} += "udev"
