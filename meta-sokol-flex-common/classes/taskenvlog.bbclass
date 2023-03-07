# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# Log the values of specified variables when starting tasks as debug messages
TASK_ENV_LOG_VARS ?= ""

python log_task_env() {
    import re

    # Warning: copied from ConfHandler
    flagpattern = re.compile(r'^(?P<var>[a-zA-Z0-9\-_+.${}/~:]+?)(\[(?P<flag>[a-zA-Z0-9\-_+.]+)\])?$', re.X)

    for k in d.getVar('TASK_ENV_LOG_VARS').split():
        m = flagpattern.match(k)
        if not m:
            bb.warn('Unexpected value in TASK_ENV_LOG_VARS: %s' % k)
            continue

        if m.group('flag'):
            v = d.getVarFlag(m.group('var'), m.group('flag'), expand=True)
        else:
            v = d.getVar(k)

        bb.debug(1, '%s %s: Environment: %s="%s"' % (e._package, e._task, k, v or ''))
}
log_task_env[eventmask] = "bb.build.TaskStarted"
addhandler log_task_env
