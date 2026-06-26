# RELEASE-4.21.1
SRCREV ?= "f1a1e629d0cc4729d10e86104ce157732bcabaca"

XEN_REL ?= "4.21.1"
XEN_BRANCH ?= "stable-4.21"

SRC_URI = " \
    git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH} \
    file://0001-python-pygrub-pass-DISTUTILS-xen-4.20.patch \
    "

LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+stable"

S = "${WORKDIR}/git"

DEFAULT_PREFERENCE ??= "-1"

require xen.inc
require xen-tools.inc
