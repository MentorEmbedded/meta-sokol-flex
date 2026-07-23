# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SUMMARY = "Add the KERNEL_ variables to the SDK environment."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Needed to get the vars defined
inherit kernel-arch

do_install () {
    install -d "${D}/environment-setup.d"
    # Strip build-system -ffile-prefix-map entries from KERNEL_CC;
    # they reference TMPDIR and have no meaning in the installed SDK
    KERNEL_CC_CLEAN="$(echo "${KERNEL_CC}" | sed 's/-ffile-prefix-map=[^ ]*//g')"
    KERNEL_LD_CLEAN="$(echo "${KERNEL_LD}" | sed 's/-ffile-prefix-map=[^ ]*//g')"
    KERNEL_AR_CLEAN="$(echo "${KERNEL_AR}" | sed 's/-ffile-prefix-map=[^ ]*//g')"
    cat <<END >"${D}/environment-setup.d/kernel.sh"
KERNEL_CC="${KERNEL_CC_CLEAN}"
KERNEL_LD="${KERNEL_LD_CLEAN}"
KERNEL_AR="${KERNEL_AR_CLEAN}"
END
}

FILES:${PN} += "/environment-setup.d/*"
