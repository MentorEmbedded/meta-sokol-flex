# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/lttng-tools:"

SRC_URI:append:feature-sokol-flex-staging = " file://0001-Ensure-that-the-consumerd-configure-arguments-are-us.patch"

python () {
    if not d.getVar('MULTILIBS'):
        return

    variants = (d.getVar("MULTILIB_VARIANTS") or "").split()
    if 'lib32' in variants:
        thirty_two = get_multilib_datastore('lib32', d)
        sixty_four = d
    elif 'lib64' in variants:
        thirty_two = d
        sixty_four = get_multilib_datastore('lib64', d)
    else:
        return

    lib64path = sixty_four.getVar('libdir')
    d.appendVar('EXTRA_OECONF', ' --with-consumerd64-libdir=' + lib64path)
    d.appendVar('EXTRA_OECONF', ' --with-consumerd64-bin=' + os.path.join(lib64path, 'lttng', 'libexec', 'lttng-consumerd'))

    lib32path = thirty_two.getVar('libdir')
    d.appendVar('EXTRA_OECONF', ' --with-consumerd32-libdir=' + lib32path)
    d.appendVar('EXTRA_OECONF', ' --with-consumerd32-bin=' + os.path.join(lib32path, 'lttng', 'libexec', 'lttng-consumerd'))
}

# Split off components which should be per-multilib
PACKAGE_BEFORE_PN:prepend:feature-sokol-flex-staging = "${PN}-consumerd liblttng-ctl "

RDEPENDS:${PN}:append:feature-sokol-flex-staging = " ${PN}-consumerd ${MLPREFIX}liblttng-ctl"

FILES:${PN}-consumerd = "${libdir}/lttng/libexec/lttng-consumerd"
# Since files are installed into ${libdir}/lttng/libexec we match
# the libexec insane test so skip it.
INSANE_SKIP:${PN}-consumerd = "dev-so"

FILES:${MLPREFIX}liblttng-ctl = "${libdir}/liblttng-ctl.so.*"
