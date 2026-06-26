# 4.22.0
SRCREV ?= "d45d5687f1441495f4ee20d5e9940066c5fa5beb"

XEN_REL ?= "4.22.0"
XEN_BRANCH ?= "stable-4.22"

SRC_URI = " \
    git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    "

LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+stable"

DEFAULT_PREFERENCE ??= "-1"

require xen.inc
require xen-hypervisor.inc
