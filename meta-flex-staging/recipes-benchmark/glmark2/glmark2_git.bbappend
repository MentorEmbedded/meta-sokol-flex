PACKAGECONFIG[gbm-gl] = ",,virtual/libgl libdrm virtual/libgbm"
PACKAGECONFIG[gbm-gles2] = ",,virtual/libgles2 libdrm virtual/libgbm"

# Redefine flavors options to include new gbm PACKAGECONFIGs
python __anonymous() {
    packageconfig = (d.getVar("PACKAGECONFIG") or "").split()
    flavors = []
    if "x11-gles2" in packageconfig:
        flavors.append("x11-glesv2")
    if "x11-gl" in packageconfig:
        flavors.append("x11-gl")
    if "wayland-gles2" in packageconfig:
        flavors.append("wayland-glesv2")
    if "wayland-gl" in packageconfig:
        flavors.append("wayland-gl")
    if "drm-gles2" in packageconfig:
        flavors.append("drm-glesv2")
    if "drm-gl" in packageconfig:
        flavors.append("drm-gl")
    if "gbm-gles2" in packageconfig:
        flavors.append("gbm-glesv2")
    if "gbm-gl" in packageconfig:
        flavors.append("gbm-gl")
    if "dispmanx" in packageconfig:
        flavors = ["dispmanx-glesv2"]
    if flavors:
        d.appendVar("EXTRA_OEMESON", " -Dflavors=%s" % ",".join(flavors))
}
