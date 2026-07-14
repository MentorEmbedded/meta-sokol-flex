# RELEASE-4.21.1
SRCREV ?= "f1a1e629d0cc4729d10e86104ce157732bcabaca"

XEN_REL ?= "4.21.1"
XEN_BRANCH ?= "stable-4.21"

SRC_URI = " \
    git://xenbits.xen.org/xen.git;branch=${XEN_BRANCH} \
    file://0001-menuconfig-mconf-cfg-Allow-specification-of-ncurses-location.patch \
    file://0001-xen-mm-remove-aliasing-of-PGC_need_scrub-over-PGC_al.patch \
    file://0002-xen-mm-allow-deferred-scrub-of-physmap-populate-allo.patch \
    file://0003-xen-mm-don-t-unconditionally-clear-PGC_need_scrub-in.patch \
    file://0004-xen-mm-do-not-assign-pages-to-a-domain-until-they-ar.patch \
    file://0005-xen-mm-Fix-off-by-one-preventing-tail-merge-in-reser.patch \
    "

LIC_FILES_CHKSUM ?= "file://COPYING;md5=d1a1e216f80b6d8da95fec897d0dbec9"

PV = "${XEN_REL}+stable"

S = "${WORKDIR}/git"

DEFAULT_PREFERENCE ??= "-1"

require xen.inc
require xen-hypervisor.inc
