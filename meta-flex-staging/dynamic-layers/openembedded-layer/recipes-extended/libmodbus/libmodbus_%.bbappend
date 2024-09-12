# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-flex-staging := "${THISDIR}/libmodbus:"
SRC_URI:append:feature-flex-staging = " \
    file://run-ptest \
    file://local-tcp-test \
"

inherit ptest

do_install_ptest:feature-flex-staging() {
        install -d ${D}${PTEST_PATH}/tests/
        install -m 0755 ${WORKDIR}/*-test ${B}/tests/.libs/* ${D}${PTEST_PATH}/tests/
        sed -i -e 's#@PTEST_PATH@#${PTEST_PATH}#g' ${D}${PTEST_PATH}/run-ptest
}
