python () {
    if not d.getVar('MULTILIBS').strip():
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
