# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

inherit sdk_multilib_hook

# In most cases, it's better to use environment-setup.d for this, but there
# are cases where it's useful to add to the main env setup scripts
EXTRA_SDK_VARS ?= ""
EXTRA_EXPORTED_SDK_VARS ?= ""
EXTRA_SDK_LINES ?= ""

SDK_POSTPROCESS_MULTILIB_COMMAND += "add_sdk_extra_vars"

add_sdk_extra_vars () {
    if [ -e "${SDK_ENV_SETUP_SCRIPT}" ]; then
        cat <<END >>"${SDK_ENV_SETUP_SCRIPT}"
${@sdk_extra_var_lines(d)}
END
    fi
}

def sdk_extra_var_lines(d):
    lines = []
    for var in d.getVar('EXTRA_SDK_VARS', True).split():
        try:
            var, value = var.rsplit('=', 1)
        except ValueError:
            value = None

        try:
            var, shvar = var.rsplit(':', 1)
        except ValueError:
            shvar = var

        if value is None:
            value = d.getVar(var) or ""

        lines.append('%s="%s"' % (shvar, value))

    for var in d.getVar('EXTRA_EXPORTED_SDK_VARS', True).split():
        try:
            var, value = var.rsplit('=', 1)
        except ValueError:
            value = None

        try:
            var, shvar = var.rsplit(':', 1)
        except ValueError:
            shvar = var

        if value is None:
            value = d.getVar(var) or ""
        lines.append('export %s="%s"' % (shvar, value))

    extra_lines = d.getVar('EXTRA_SDK_LINES', True).replace('\\n', '\n').split('\n')
    lines.extend(extra_lines)
    return '\n'.join(lines)
