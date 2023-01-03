# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SUMMARY = "SDK Makefile Fragment for CodeBench"
LICENSE = "MIT"
INHIBIT_DEFAULT_DEPS = "1"

S = "${WORKDIR}"

SDK_PRE_BUILD_TARGET ?= ""
SDK_POST_BUILD_TARGET ?= ""

do_compile () {
    cat <<END >environment-setup.mk
PATH:=@SDKPATH@/sysroots/${SDK_SYS}${prefix_nativesdk}/bin:\$(PATH)
-include @SDKPATH@/sysroots/${MULTIMACH_TARGET_SYS}/environment-setup.d/*.mk
-include @SDKPATH@/sysroots/${SDK_SYS}/environment-setup.d/*.mk
END

    {
        echo 'SDK_CUSTOM_MAKEFILE="sysroots/${MULTIMACH_TARGET_SYS}/environment-setup.mk"'
        if [ -n "${SDK_PRE_BUILD_TARGET}" ]; then
            echo 'PRE_BUILD_TARGET="${SDK_PRE_BUILD_TARGET}"'
        fi
        if [ -n "${SDK_POST_BUILD_TARGET}" ]; then
            echo 'POST_BUILD_TARGET="${SDK_POST_BUILD_TARGET}"'
        fi
    } >${BPN}.sh
}

do_install () {
    install -d ${D}/environment-setup.d
    install -m 0644 environment-setup.mk ${D}/
    install -m 0644 ${BPN}.sh ${D}/environment-setup.d/
}

FILES:${PN} += "/environment-setup.mk /environment-setup.d"
