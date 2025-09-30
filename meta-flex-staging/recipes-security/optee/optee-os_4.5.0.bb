require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRCREV = "0919de0f7c79ad35ad3c8ace5f823ad1344b4716"
SRC_URI += " \
    file://0003-optee-enable-clang-support.patch \
   "
