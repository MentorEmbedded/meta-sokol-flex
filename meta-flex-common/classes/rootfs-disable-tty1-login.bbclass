# Disable the login on tty1 if the 'disable-tty1-login' image feature is set

ROOTFS_POSTPROCESS_COMMAND += '${@bb.utils.contains("IMAGE_FEATURES", "disable-tty1-login", "disable_tty1_login", "",d)}'

FEATURE_PACKAGES_disable-tty1-login = ""

disable_tty1_login() {
    if [ -e ${IMAGE_ROOTFS}${root_prefix}/lib/systemd/systemd ]; then
        systemctl --root="${IMAGE_ROOTFS}" mask getty@tty1.service                                                               
    fi
}
