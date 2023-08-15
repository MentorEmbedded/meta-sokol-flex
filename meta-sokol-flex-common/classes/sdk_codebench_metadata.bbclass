# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# Write additiional metadata for CodeBench to the SDK when codebench-metadata is
# in SDKIMAGE_FEATURES.

inherit sdk_multilib_hook sdk_extra_vars codebench-environment-setup-d-hack

OVERRIDES =. "${@bb.utils.contains('SDKIMAGE_FEATURES', 'codebench-metadata', 'sdk-codebench-metadata:', '', d)}"

SDK_POSTPROCESS_MULTILIB_COMMAND:prepend:sdk-codebench-metadata = "adjust_sdk_script_codebench; write_cb_mbs_options;"

TOOLCHAIN_HOST_TASK:append:sdk-codebench-metadata = " ${@bb.utils.contains('BBFILE_COLLECTIONS', 'sokol-flex-support', 'nativesdk-relocate-makefile', '', d)}"
TOOLCHAIN_TARGET_TASK:append:sdk-codebench-metadata = " ${@bb.utils.contains('BBFILE_COLLECTIONS', 'sokol-flex-support', 'codebench-makefile', '', d)}"

SOURCERY_VERSION ?= ""
CODEBENCH_SDK_VARS += "\
    MACHINE \
    DISTRO \
    DISTRO_NAME \
    DISTRO_VERSION \
    SDK_IDENTIFIER \
    SDK_TITLE \
    SDK_VERSION \
    gdb_serverpath=${bindir}/gdbserver \
    TOOLCHAIN_PATH \
    TOOLCHAIN_GDB_PATH \
"

TOOLCHAIN_PATH = "${SDKPATHNATIVE}${bindir_nativesdk}/${TARGET_SYS}"
TOOLCHAIN_GDB_PATH = "${TOOLCHAIN_PATH}/${TARGET_PREFIX}gdb"

SDKPATHTOOLCHAIN ?= "${SDKPATH}/toolchain"
EXTERNAL_TOOLCHAIN_RELBIN = "${@os.path.relpath(d.getVar('EXTERNAL_TOOLCHAIN_BIN'), d.getVar('EXTERNAL_TOOLCHAIN'))}"
TOOLCHAIN_PATH:tcmode-external-oe-sdk = "${SDKPATHTOOLCHAIN}/${EXTERNAL_TOOLCHAIN_RELBIN}"

CODEBENCH_SDK_VARS:append:tcmode-external-sourcery = "\
    SOURCERY_VERSION \
    TOOLCHAIN_VERSION=${@d.getVar('SOURCERY_VERSION').split('-', 1)[0]} \
"
EXTRA_SDK_VARS:append:sdk-codebench-metadata = " ${CODEBENCH_SDK_VARS}"
EXTRA_SDK_LINES:append:sdk-codebench-metadata = 'PATH=\$TOOLCHAIN_PATH:\$PATH\n'

CB_MBS_OPTIONS ?= ""
CB_MBS_OPTIONS_FEATURES_MAP ?= ""
CB_MBS_OPTIONS_FLAGS_MAP ?= ""
CB_MBS_OPTIONS_FLAGS_VALUE_MAP ?= ""

CB_MBS_OPTIONS_CC_FLAGS_MAP ?= ""
CB_MBS_OPTIONS_CC_FLAGS_MAP[-g] = "compiler*option.debugging.level=debugging.level.default"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-g0] = "compiler*option.debugging.level=debugging.level.none"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-g1] = "compiler*option.debugging.level=debugging.level.minimal"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-g3] = "compiler*option.debugging.level=debugging.level.max"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-O0] = "compiler*option.optimization.level=optimization.level.none"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-O1] = "compiler*option.optimization.level=optimization.level.optimize"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-O2] = "compiler*option.optimization.level=optimization.level.more"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-O3] = "compiler*option.optimization.level=optimization.level.most"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-Os] = "compiler*option.optimization.level=optimization.level.size"
CB_MBS_OPTIONS_CC_FLAGS_MAP[-Og] = "compiler*option.optimization.level=optimization.level.debug"

CB_MBS_IGNORED_EXTRA_FLAGS ?= ""
CB_MBS_IGNORED_EXTRA_FLAGS += "${@'-mcpu=* -march=* -mtune=*' if d.getVarFlag('CB_MBS_OPTIONS', 'general.cpu', expand=True) else ''}"
CB_MBS_IGNORED_EXTRA_FLAGS += "${@'-mfpu=*' if d.getVarFlag('CB_MBS_OPTIONS', 'general.fpu', expand=True) else ''}"
CB_MBS_IGNORED_EXTRA_FLAGS[doc] = "Arguments we don't want to include in the extra options, as they're already handled elsewhere."

CB_MBS_IGNORED_FLAGS ?= ""
CB_MBS_IGNORED_FLAGS[doc] = "General flag arguments we don't want to include."

adjust_sdk_script_codebench () {
    # Determine the script's location relative to itself rather than hardcoding it
    script="${SDK_ENV_SETUP_SCRIPT}"
    if [ -e "$script" ]; then
        cat >"${script}.new" <<END
if [ -n "\$BASH_SOURCE" ] || [ -n "\$ZSH_NAME" ]; then
    if [ -n "\$BASH_SOURCE" ]; then
        scriptdir="\$(cd "\$(dirname "\$BASH_SOURCE")" && pwd)"
    elif [ -n "\$ZSH_NAME" ]; then
        scriptdir="\$(cd "\$(dirname "\$0")" && pwd)"
    fi
else
    if [ ! -d "${SDKPATH}" ]; then
        echo >&2 "Warning: Unable to determine SDK install path from environment setup script location, using default of ${SDKPATH}."
    fi
    scriptdir="${SDKPATH}"
fi
END
        sed -e "s#${SDKPATH}#\$scriptdir#g" "$script" >>"${script}.new"
        mv "${script}.new" "${script}"
    fi
}

SDK_CB_OPTIONS = "${SDK_OUTPUT}/${SDKPATH}/cb-mbs-options-${REAL_MULTIMACH_TARGET_SYS}"

python write_cb_mbs_options() {
    optionsfile = d.getVar('SDK_CB_OPTIONS')

    # We don't care about flags like debugging, optimization. TUNE_CCARGS is
    # already covered.
    l = d.createCopy()
    l.setVar('TARGET_CFLAGS', '')
    l.setVar('TARGET_CXXFLAGS', '')
    l.setVar('TARGET_LDFLAGS', '')
    options = get_cb_options(l)
    bb.utils.mkdirhier(os.path.dirname(optionsfile))
    with open(optionsfile, 'w') as f:
        f.writelines('%s=%s\n' % (k, d.expand(v)) for k, v in sorted(options.items()))
}
write_cb_mbs_options[vardeps] += "${@' '.join('CB_MBS_OPTIONS[%s]' % f for f in (d.getVarFlags('CB_MBS_OPTIONS') or []))}"

def get_cb_options(d):
    """Set default CodeBench metadata values based upon BitBake build flags."""
    import shlex
    import subprocess
    from fnmatch import fnmatchcase

    options = d.getVarFlags('CB_MBS_OPTIONS') or {}

    l = d.createCopy()
    l.finalize()
    l.setVar('DEBUG_PREFIX_MAP', '')
    l.setVar('STAGING_DIR_TARGET', '$SDKTARGETSYSROOT')

    features = d.getVar('TUNE_FEATURES').split()
    for feature, settings in (d.getVarFlags('CB_MBS_OPTIONS_FEATURES_MAP') or {}).items():
        if feature.startswith('-'):
            enabled = feature[1:] not in features
        else:
            enabled = feature in features
        if enabled:
            for setting in settings.split():
                skey, svalue = setting.split('=', 1)
                if setting and not options.get(skey):
                    options[skey] = svalue

    cflags = shlex.split(l.getVar('TARGET_CFLAGS'))
    cxxflags = shlex.split(l.getVar('TARGET_CXXFLAGS'))
    ldflags = shlex.split(l.getVar('TARGET_LDFLAGS'))

    ccargs = shlex.split(l.getVar('TARGET_CC_ARCH'))
    cflags.extend(ccargs)
    cxxflags.extend(ccargs)
    ldflags.extend(ccargs)

    ignored = d.getVar('CB_MBS_IGNORED_FLAGS').split()
    cflags = [a for a in cflags if not any(fnmatchcase(a, p) for p in ignored)]
    cxxflags = [a for a in cxxflags if not any(fnmatchcase(a, p) for p in ignored)]
    ldflags = [a for a in ldflags if not any(fnmatchcase(a, p) for p in ignored)]

    check_cflags, check_cxxflags = list(cflags), list(cxxflags)
    for flag, settings in (d.getVarFlags('CB_MBS_OPTIONS_FLAGS_MAP') or {}).items():
        for setting in settings.split():
            skey, svalue = setting.split('=', 1)
            if flag in check_cflags:
                if flag in cflags:
                    cflags.remove(flag)
                if setting and not options.get(skey):
                    options[skey] = svalue

            if flag in check_cxxflags:
                if flag in cxxflags:
                    cxxflags.remove(flag)
                if setting and not options.get(skey):
                    options[skey] = svalue

    for flag, settings in (d.getVarFlags('CB_MBS_OPTIONS_FLAGS_VALUE_MAP') or {}).items():
        for setting in settings.split():
            skey, svalue = setting.split('=', 1)
            for check_flag in check_cflags:
                if check_flag.startswith(flag + '='):
                    _, check_value = check_flag.split('=', 1)
                    if check_flag in cflags:
                        cflags.remove(check_flag)
                    if setting and not options.get(skey):
                        options[skey] = svalue + check_value

            for check_flag in check_cxxflags:
                if check_flag.startswith(flag + '='):
                    _, check_value = check_flag.split('=', 1)
                    if check_flag in cxxflags:
                        cxxflags.remove(check_flag)
                    if setting and not options.get(skey):
                        options[skey] = svalue + check_value

    for flag, settings in (d.getVarFlags('CB_MBS_OPTIONS_CC_FLAGS_MAP') or {}).items():
        for setting in settings.split():
            if flag in check_cflags:
                if flag in cflags:
                    cflags.remove(flag)
                if setting:
                    skey, svalue = setting.split('=', 1)
                    skey = 'gnu.c.' + skey
                    svalue = 'gnu.c.' + svalue
                    if not options.get(skey):
                        options[skey] = svalue

            if flag in check_cxxflags:
                if flag in cxxflags:
                    cxxflags.remove(flag)
                if setting:
                    skey, svalue = setting.split('=', 1)
                    skey = 'gnu.cpp.' + skey
                    svalue = 'gnu.cpp.compiler.' + svalue
                    if not options.get(skey):
                        options[skey] = svalue

    ignored = d.getVar('CB_MBS_IGNORED_EXTRA_FLAGS').split()
    cflags = [a for a in cflags if not any(fnmatchcase(a, p) for p in ignored)]
    cxxflags = [a for a in cxxflags if not any(fnmatchcase(a, p) for p in ignored)]
    ldflags = [a for a in ldflags if not any(fnmatchcase(a, p) for p in ignored)]

    if cflags:
        options['gnu.c.compiler*option.misc.other'] = subprocess.list2cmdline(cflags)
    if cxxflags:
        options['gnu.cpp.compiler*option.other.other'] = subprocess.list2cmdline(cxxflags)
    if ldflags:
        options['gnu.c.link*option.ldflags'] = subprocess.list2cmdline(ldflags)
        options['gnu.cpp.link*option.flags'] = subprocess.list2cmdline(ldflags)

    for option, value in list(options.items()):
        value = d.expand(value)
        if not value:
            del options[option]
        else:
            # Force immediate expansion, so we use target overrides regardless
            # of the context in which the flags are used
            options[option] = value

    return options
