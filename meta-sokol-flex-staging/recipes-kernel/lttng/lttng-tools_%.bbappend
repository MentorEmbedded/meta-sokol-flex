# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/lttng-tools:"

SRC_URI:append:feature-sokol-flex-staging = " file://0001-Ensure-that-the-consumerd-configure-arguments-are-us.patch"

python () {
    if not d.getVar('MULTILIBS'):
        return

    variants = (d.getVar("MULTILIB_VARIANTS") or "").split()

    # The 32-bit build refers to the 64, and vice versa
    mlprefix = d.getVar('MLPREFIX')
    if 'lib64' in variants:
        if mlprefix:
            other_variant = ''
            other = '32'
        else:
            other_variant = 'lib64'
            other = '64'
    elif 'lib32' in variants:
        if mlprefix:
            other_variant = ''
            other = '64'
        else:
            other_variant = 'lib32'
            other = '32'
    else:
        return

    other_data = get_multilib_datastore(other_variant, d)
    other_libdir = other_data.getVar('libdir')
    consumerd = os.path.join(other_libdir, 'lttng', 'libexec', 'lttng-consumerd')
    d.appendVar('EXTRA_OECONF', ' --with-consumerd%s-libdir=%s' % (other, other_libdir))
    d.appendVar('EXTRA_OECONF', ' --with-consumerd%s-bin=%s' % (other, consumerd))
}

# Split off components which should be per-multilib
PACKAGE_BEFORE_PN:prepend:feature-sokol-flex-staging = "${PN}-consumerd liblttng-ctl "

RDEPENDS:${PN}:append:feature-sokol-flex-staging = " ${PN}-consumerd ${MLPREFIX}liblttng-ctl"

FILES:${PN}-consumerd = "${libdir}/lttng/libexec/lttng-consumerd"
# Since files are installed into ${libdir}/lttng/libexec we match
# the libexec insane test so skip it.
INSANE_SKIP:${PN}-consumerd = "dev-so"

FILES:${MLPREFIX}liblttng-ctl = "${libdir}/liblttng-ctl.so.*"
