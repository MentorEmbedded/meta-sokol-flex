# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# Run SDK postprocess commands for each multilib variant
SDK_POSTPROCESS_MULTILIB_COMMAND ?= ""

# For convenience, in the commands
REAL_MULTIMACH_TARGET_SYS ?= "${TUNE_PKGARCH}${TARGET_VENDOR}-${TARGET_OS}"
SDK_ENV_SETUP_SCRIPT ?= "${SDK_OUTPUT}/${SDKPATH}/environment-setup-${REAL_MULTIMACH_TARGET_SYS}"

SDK_POSTPROCESS_COMMAND:prepend = "sdk_postprocess_per_multilib "

python sdk_postprocess_per_multilib () {
    # Handle multilibs in the SDK environment, siteconfig, etc files...
    localdata = bb.data.createCopy(d)

    # make sure we only use the WORKDIR value from 'd', or it can change
    localdata.setVar('WORKDIR', d.getVar('WORKDIR'))

    # make sure we only use the SDKTARGETSYSROOT value from 'd'
    localdata.setVar('SDKTARGETSYSROOT', d.getVar('SDKTARGETSYSROOT'))
    localdata.setVar('libdir', d.getVar('target_libdir', False))

    commands = [cmd.strip() for cmd in d.getVar('SDK_POSTPROCESS_MULTILIB_COMMAND').split()]
    variants = d.getVar("MULTILIB_VARIANTS") or ""
    for variant in [''] + variants.split():
        if variant:
            # Load overrides from 'd' to avoid having to reset the value...
            overrides = d.getVar("OVERRIDES", False) + ":virtclass-multilib-" + variant
            localdata.setVar("OVERRIDES", overrides)
            localdata.setVar("MLPREFIX", variant + "-")

        for command in commands:
            if command and command in localdata:
                bb.build.exec_func(command, localdata)
}
