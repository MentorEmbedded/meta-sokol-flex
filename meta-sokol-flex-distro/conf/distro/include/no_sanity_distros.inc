python no_sanity_distros () {
    if bb.utils.contains('DISTRO_FEATURES', 'selftest', True, False, d):
        d.delVar('SANITY_TESTED_DISTROS')
}
addhandler no_sanity_distros
