#!/bin/sh
# Scan for a virtio-gpu DRM card and launch weston with it.
# Uses udevadm for stable device discovery across kernel versions.
# Falls back to card0 if no virtio-gPU is present.

for card in /dev/dri/card[0-9]; do
    if udevadm info --attribute-walk --name="$card" 2>/dev/null | grep -q "DRIVERS==\"virtio-pci\""; then
        n=$(basename "$card")
        exec /usr/bin/weston --drm-device="$n" --modules=systemd-notify.so
    fi
done

exec /usr/bin/weston --drm-device=card0 --modules=systemd-notify.so
