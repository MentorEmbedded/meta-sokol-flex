SRC_URI:append = " \
	file://0001-virgl-Support-tiled-gbm-modifiers.patch \
	file://0002-wsi-fall-back-to-a-buffer-blit-when-no-winsys-modifi.patch \
	file://0003-venus-stop-spoofing-VK_EXT_physical_device_drm-on-Nv.patch \
	file://0001-venus-fix-NULL-deref-on-zero-handleType-in-image-for.patch \
	file://0002-venus-only-reject-legacy-tiled-external-images-for-s.patch \
	file://0001-vulkan-device-select-select-device-by-deviceUUID.patch \
	file://0002-vulkan-device-select-print-deviceUUID-in-the-device-.patch \
"

EXTRA_OEMESON:append = "${@bb.utils.contains('PACKAGECONFIG', 'vulkan', ' -Dvulkan-layers=device-select', '', d)}"
FILES:mesa-vulkan-drivers += "${libdir}/libVkLayer_MESA_device_select.so"
