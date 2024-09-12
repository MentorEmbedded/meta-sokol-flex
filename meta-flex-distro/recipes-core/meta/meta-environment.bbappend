# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SDK_MULTILIB_VARIANTS ?= "${MULTILIB_VARIANTS}"
python set_multilib_variants () {
    variants = d.getVar('SDK_MULTILIB_VARIANTS', True)
    if variants:
        d.setVar('MULTILIB_VARIANTS', variants)
}
SET_MULTILIB_VARIANTS = ""
SET_MULTILIB_VARIANTS:flex-os = "set_multilib_variants"
do_generate_content[prefuncs] += "${SET_MULTILIB_VARIANTS}"
