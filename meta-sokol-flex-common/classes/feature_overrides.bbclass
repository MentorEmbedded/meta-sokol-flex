# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# This class eases conditional additions to OVERRIDES, which makes it easier
# to create yocto-compatible layers. This is similar to distrooverrides.bbclass,
# but is more generic.
#
# Example usage, in conf/layer.conf:
#
#   INHERIT:append = " feature_overrides"
#   FEATUREOVERRIDES .= "${@bb.utils.contains('DISTRO_FEATURES', 'some-feature', ':feature-some-feature', '', d)}"
#   FEATUREOVERRIDES .= "${@bb.utils.contains('MACHINE_FEATURES', 'some-bsp-feature', ':feature-some-bsp-feature', '', d)}"

def unique_everseen(iterable):
    "List unique elements, preserving order. Remember all elements ever seen."
    # unique_everseen('AAAABBBCCDAABBB') --> A B C D
    # unique_everseen('ABBCcAD', str.lower) --> A B C D
    import itertools
    seen = set()
    seen_add = seen.add
    for element in itertools.filterfalse(seen.__contains__, iterable):
        seen_add(element)
        yield element

FEATURE_OVERRIDES_PREPEND = "${@':'.join(unique_everseen(filter(None, d.getVar('FEATUREOVERRIDES').split(':'))))}"
OVERRIDES:prepend = "${@d.getVar('FEATURE_OVERRIDES_PREPEND') + ':' if d.getVar('FEATURE_OVERRIDES_PREPEND') else ''}"
