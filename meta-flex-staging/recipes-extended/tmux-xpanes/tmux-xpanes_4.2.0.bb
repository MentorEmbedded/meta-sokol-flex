SUMMARY = "Ultimate terminal divider powered by tmux"
HOMEPAGE = "https://github.com/greymd/tmux-xpanes"
SECTION = "console/utils"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=390f257ea6ec21da91b1c8f7160fac7e"

RDEPENDS:${PN} = " \
    bash \
    tmux \
"

# Fetch the latest stable release (v4.2.0)
SRC_URI = "git://github.com/greymd/tmux-xpanes.git;protocol=https;branch=master"
SRCREV = "2199593a2601a5bf86cfeb01afc00e2f91d39b6a"

# xpanes is a bash script, no compilation is needed
do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/bin/xpanes ${D}${bindir}/xpanes
}
