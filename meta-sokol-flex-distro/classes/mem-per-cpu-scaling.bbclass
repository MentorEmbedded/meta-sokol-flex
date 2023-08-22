# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------
#
# Warn if memory-per-cpu-core is low, and set BB_NUMBER_THREADS_SCALED and
# PARALLEL_MAKE_SCALED with scaled down values.
#
# Inspired by https://elinux.org/images/d/d4/Goulart.pdf.

# Set to 0 to opt out of the warning/error message
MEM_PER_CPU_MESSAGE ?= "1"

# Configured using the MEM_PER_CPU_SCALING_THRESHOLDS variable:
#
# MEM_PER_CPU_THRESHOLD,THREADS,JOBS,ACTION
#
# If mem per cpu is less than MEM_PER_CPU_THRESHOLD, set BB_NUMBER_THREADS_SCALED to
# THREADS, PARALLEL_MAKE_SCALED to JOBS, and execute specified ACTION if set. If THREADS
# or JOBS are empty, they are left unmodified.
MEM_PER_CPU_SCALING_THRESHOLDS ?= "\
    4,cpus,cpus//2,WARN \
    2,cpus//2,cpus//2,WARN \
"

# Default values to avoid errors before the ConfigParsed event is fired
BB_NUMBER_THREADS_SCALED = "${@int(oe.utils.cpu_count())}"
PARALLEL_MAKE_SCALED = "-j ${@oe.utils.cpu_count()}"

# This function postpones warnings to reduce duplication, and postpones failures
# to avoid breaking bitbake -e.
python mem_per_cpu_scaling() {
    """Set bitbake threads and make jobs based upon available memory per CPU core."""

    current_threads = d.getVar('BB_NUMBER_THREADS', False)
    threads_are_scaled = current_threads == '${BB_NUMBER_THREADS_SCALED}'
    if not threads_are_scaled:
        current_threads = int(d.expand(current_threads))

    current_jobs = d.getVar('PARALLEL_MAKE', False)
    jobs_are_scaled = current_jobs == '${PARALLEL_MAKE_SCALED}'
    if not jobs_are_scaled:
        current_jobs = int(d.expand(current_jobs).split()[1])

    try:
        matched, mem_per_cpu, warning = matching_rule(d)
    except ValueError as exc:
        d.setVar('MEM_PER_CPU_SCALING_ERROR', str(exc))
        return
    else:
        if warning:
            d.setVar('MEM_PER_CPU_SCALING_WARNING', warning)

        if not matched:
            return
        else:
            threshold, threads, jobs, action = matched
    
    suggested = []
    if threads and (threads_are_scaled or current_threads > threads):
        if not threads_are_scaled:
            suggested.append('reduce BB_NUMBER_THREADS to %d or less' % threads)
        d.setVar('BB_NUMBER_THREADS_SCALED', str(threads))

    if jobs and (jobs_are_scaled or current_jobs > jobs):
        if not jobs_are_scaled:
            suggested.append('reduce PARALLEL_MAKE to %d or less' % jobs)
        d.setVar('PARALLEL_MAKE_SCALED', '-j %s' % jobs)

    if action and suggested and d.getVar('MEM_PER_CPU_MESSAGE') == '1':
        if action == 'FATAL':
            message = 'Very low memory per CPU core (~%s Gb) in this system' % int(mem_per_cpu)
            if suggested:
                message += ', please %s in local.conf, or set MEM_PER_CPU_MESSAGE="0" to disable this message.' % ' and '.join(suggested)
            d.setVar('MEM_PER_CPU_SCALING_ERROR', message)
            return
        elif action == 'WARN':
            message = 'Low memory per CPU core (~%s Gb) in this system.' % int(mem_per_cpu)
            if suggested:
                message += ' You may wish to %s in local.conf, or set MEM_PER_CPU_MESSAGE="0" to disable this message.' % ' and '.join(suggested)
            d.setVar('MEM_PER_CPU_SCALING_WARNING', message)
}
mem_per_cpu_scaling[eventmask] = "bb.event.ConfigParsed"
addhandler mem_per_cpu_scaling


def matching_rule(d):
    warning, error = None, None
    cpus = oe.utils.cpu_count()
    try:
        mem_total = mem_total_gigs()
    except Exception:
        warning = 'Failed to determine system total memory for threads and jobs heuristic.'
        return None, mem_per_cpu, warning

    mem_per_cpu = mem_total / cpus

    for entry in reversed(d.getVar('MEM_PER_CPU_SCALING_THRESHOLDS').split()):
        split = entry.split(',')
        try:
            threshold, threads, jobs, action = split
        except ValueError:
            raise ValueError('Invalid entry in MEM_PER_CPU_SCALING_THRESHOLDS: %s. Expected: MEM_PER_CPU_MINIMUM,THREADS,JOBS,ACTION' % entry)

        if mem_per_cpu < (float(threshold) + 0.1):
            try:
                threads = max(int(eval(threads)), 1)
            except Exception as exc:
                raise ValueError('Failed to evaluate threads (%s) in MEM_PER_CPU_SCALING_THRESHOLDS entry %s: %s' % (
                            threads, entry, exc))

            try:
                jobs = max(int(eval(jobs)), 1)
            except Exception as exc:
                raise ValueError('Failed to evaluate jobs (%s) in MEM_PER_CPU_SCALING_THRESHOLDS entry %s: %s' % (
                            jobs, entry, exc))

            return [threshold, threads, jobs, action], mem_per_cpu, None

    return None, mem_per_cpu, None

def mem_total_gigs():
    if os.path.exists('/proc/meminfo'):
        with open('/proc/meminfo', 'rb') as f:
            splitlines = [l.split()[:2] for l in f.readlines()]
            data = dict((k.strip()[:-1].decode('utf-8'), int(v.strip())) for k, v in splitlines)
            total = data.get('MemTotal')
            if total:
                return total / 1024 / 1024

python mem_per_cpu_scaling_sanity() {
    """Inform the user about issues with their system resources."""
    fatal = d.getVar('MEM_PER_CPU_SCALING_ERROR')
    if fatal:
        bb.fatal(fatal)

    warn = d.getVar('MEM_PER_CPU_SCALING_WARNING')
    if warn:
        bb.warn(warn)
}
mem_per_cpu_scaling_sanity[eventmask] = "bb.event.SanityCheck bb.build.BuildStarted"
addhandler mem_per_cpu_scaling_sanity
