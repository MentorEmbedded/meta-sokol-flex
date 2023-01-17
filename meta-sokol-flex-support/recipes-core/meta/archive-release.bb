# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

DESCRIPTION = "Archive the artifacts for a ${DISTRO_NAME} release"
LICENSE = "MIT"
INHIBIT_DEFAULT_DEPS = "1"
PACKAGE_ARCH = "${MACHINE_ARCH}"
EXCLUDE_FROM_WORLD = "1"

inherit image_types image-artifact-names nopackages layerdirs

DEPLOY_DIR_RELEASE ?= "${DEPLOY_DIR}/release-artifacts"
RELEASE_ARTIFACTS ?= "layers bitbake images downloads"
RELEASE_ARTIFACTS[doc] = "List of artifacts to include (available: layers, bitbake, images, downloads"
RELEASE_IMAGE ?= "core-image-base"
RELEASE_IMAGE[doc] = "The image to build and archive in this release"
BINARY_ARTIFACTS_COMPRESSION ?= ""
BINARY_ARTIFACTS_COMPRESSION[doc] = "Compression type for images and downloads artifacts.\
 Available: '.bz2' and '.gz'. No compression if empty"

ARCHIVE_RELEASE_VERSION ?= "${DISTRO_VERSION}"
MANIFEST_NAME ?= "${DISTRO}-${ARCHIVE_RELEASE_VERSION}-${MACHINE}"
EXTRA_MANIFEST_NAME ?= "${DISTRO}-${ARCHIVE_RELEASE_VERSION}"
SCRIPTS_VERSION ?= "1"
SCRIPTS_ARTIFACT_NAME ?= "${DISTRO}-scripts-${DISTRO_VERSION}.${SCRIPTS_VERSION}"

# Don't allow git to chdir up past our workspace to avoid redistributing the wrong repository
export GIT_CEILING_DIRECTORIES = "${WORKDIR}:${FLEXDIR}:${TOPDIR}:${HOME}"

# `layers` artifact configuration {{{1
SUBLAYERS_INDIVIDUAL_ONLY ?= ""
SUBLAYERS_INDIVIDUAL_ONLY_TOPLEVEL ?= ""

RELEASE_EXCLUDED_LAYERNAMES ?= "workspacelayer"
RELEASE_EXCLUDED_LAYERNAMES[doc] = "List of layer names to exclude from archival"

# Layers which get their own extra manifests, rather than being included in
# the main one. How they're combined or shipped from there is up to our
# release scripts.
INDIVIDUAL_MANIFEST_LAYERS ?= ""
FORKED_REPOS ?= ""
PUBLIC_REPOS ?= "${FORKED_REPOS}"

GET_REMOTES_HOOK ?= "flex_get_remotes"

def flex_get_remotes(subdir, d):
    """Any non-public github repo or url including a mentor domain
    are considered private, so no remote is included.
    """
    try:
        url = bb.process.run(['git', 'config', 'remote.origin.url'], cwd=subdir)[0].rstrip()
    except bb.process.ExecutionError:
        return None
    else:
        if not url:
            return None

    remotes = {}
    test_url = url.replace('.git', '')
    public_repos = d.getVar('PUBLIC_REPOS').split()
    if 'MentorEmbedded' in test_url:
        if not any(test_url.endswith('/' + i) for i in public_repos):
            # Private github repo
            return None
        else:
            # For the public layers, we want the user to be able to fetch
            # anonymously, not just with ssh
            url = url.replace('ssh://git@', 'https://')

            forked_repos = d.getVar('FORKED_REPOS').split()
            for f in forked_repos:
                if test_url.endswith('/' + f):
                    upstream = d.getVar('UPSTREAM_URL_%s' % f)
                    if upstream:
                        remotes['upstream'] = upstream
                        break
    elif 'mentor.com' in test_url or 'mentorg.com' in test_url:
        # Internal repo
        return None

    remotes['origin'] = url
    return remotes

# Files for the script artifact
FILESEXTRAPATHS:append = ":${@':'.join('%s/../scripts/release:%s/../scripts' % (l, l) for l in '${BBPATH}'.split(':'))}"
FLEX_SCRIPTS_FILES = "flex-checkout setup-flex setup-workspace setup-ubuntu setup-rh setup-debian"
SRC_URI += "${@' '.join('file://%s' % s for s in d.getVar('FLEX_SCRIPTS_FILES').split())}"
# }}}1

# `images` artifact configuration {{{1
# Filesystem paths in the destination for the image artifacts
BSPFILES_INSTALL_PATH = "${MACHINE}/${ARCHIVE_RELEASE_VERSION}"
BINARY_INSTALL_PATH ?= "${BSPFILES_INSTALL_PATH}/binary"
CONF_INSTALL_PATH ?= "${BSPFILES_INSTALL_PATH}/conf"

# Used to include conf-notes.txt, local.conf.sample, and bblayers.conf.sample for this BSP
TEMPLATECONF_STR ?= "${@(oe.utils.read_file('${TOPDIR}/conf/templateconf.cfg') or '${FILE_DIRNAME}/../../../conf').rstrip()}"
TEMPLATECONF = "${@os.path.join('${COREBASE}', '${TEMPLATECONF_STR}')}"

# In our `images` artifact, nclude bmaptool and, for qemu, a runqemu wrapper
SRC_URI += "https://github.com/01org/bmap-tools/releases/download/v3.4/bmaptool;name=bmaptool"
SRC_URI[bmaptool.md5sum] = "7bc226c2b15aff58af31e421fa381d34"
SRC_URI[bmaptool.sha256sum] = "8cedbb7a525dd4026b6cafe11f496de11dbda0f0e76a5b4938d2687df67bab7f"
SRC_URI:append:qemuall = " file://runqemu.in"

# Image files to be archived
IMAGE_BASENAME = "${RELEASE_IMAGE}"
EXTRA_IMAGES_ARCHIVE_RELEASE ?= ""
DEPLOY_IMAGES ?= "\
    ${@' '.join('${IMAGE_LINK_NAME}.%s' % ext for ext in d.getVar('IMAGE_EXTENSIONS').split())} \
    ${IMAGE_LINK_NAME}.license_manifest \
    ${IMAGE_LINK_NAME}.license_manifest.csv \
    ${EXTRA_IMAGES_ARCHIVE_RELEASE} \
"
DEPLOY_IMAGES:append:qemuall = "${@' ' + d.getVar('KERNEL_IMAGETYPE') if 'wic' not in d.getVar('IMAGE_EXTENSIONS') else ''}"
DEPLOY_IMAGES[doc] = "List of files from DEPLOY_DIR_IMAGE which will be archived"

# If a wic image is enabled, that's all we want
IMAGE_EXTENSIONS_FULL = "${@' '.join(d.getVar('IMAGE_EXTENSION_%s' % t) or t for t in d.getVar('IMAGE_FSTYPES').split())}"
IMAGE_EXTENSIONS_WIC = "${@' '.join(e for e in d.getVar('IMAGE_EXTENSIONS_FULL').split() if 'wic' in e)}"
IMAGE_EXTENSIONS ?= "${@d.getVar('IMAGE_EXTENSIONS_WIC') if 'wic' in d.getVar('IMAGE_FSTYPES') else d.getVar('IMAGE_EXTENSIONS_FULL')}"

# Exclude certain image types from the packaged build.
# This allows us to build in the automated environment for regression,
# general testing or simply for availability of extra image types for
# internal use without necessarily packaging them in the installers.
IMAGE_EXTENSIONS_EXCLUDED = "tar.gz tar.bz2 tar.xz"
IMAGE_EXTENSIONS:remove = "${IMAGE_EXTENSIONS_EXCLUDED}"
# }}}1

# `downloads` artifact configuration {{{1
ARCHIVE_DOWNLOAD_SIZE_CHECK ?= "\
    WARN,gitshallow*,220M \
    WARN,*,300M \
"

# Ensure we include all the uninative tarballs in our `downloads` artifact
SRC_URI += "${@' '.join(uninative_urls(d)) if 'downloads' in '${RELEASE_ARTIFACTS}'.split() else ''}"

def uninative_urls(d):
    l = d.createCopy()
    for arch, chksum in d.getVarFlags("UNINATIVE_CHECKSUM").items():
        if chksum:
            l.setVar('BUILD_ARCH', arch)
            srcuri = l.expand("${UNINATIVE_URL}${UNINATIVE_TARBALL};sha256sum=%s;unpack=no;subdir=uninative/%s;downloadfilename=uninative/%s/${UNINATIVE_TARBALL}" % (chksum, chksum, chksum))
            yield srcuri

# Default values if archive-release-downloads is not inherited
ARCHIVE_RELEASE_DL_DIR ?= "${DL_DIR}"
ARCHIVE_RELEASE_DL_BY_LAYER_PATH = '${TMPDIR}/downloads-by-layer.txt'
# }}}1


FLEXDIR ?= "${COREBASE}/.."

python () {
    # Make sure FLEXDIR is absolute, as we use it in transforms
    d.setVar('FLEXDIR', os.path.abspath(d.getVar('FLEXDIR')))

    for component in d.getVar('RELEASE_ARTIFACTS').split():
        ctask = 'do_archive_%s' % component
        if ctask not in d:
            bb.fatal('do_archive_release: no such task "%s" for component "%s" listed in RELEASE_ARTIFACTS' % (ctask, component))

        bb.build.addtask(ctask, 'do_prepare_release', 'do_patch do_prepare_recipe_sysroot', d)
        d.setVar('SSTATE_SKIP_CREATION:task-archive-%s' % component.replace('_', '-'), '1')
        d.setVarFlag(ctask, 'umask', '022')
        d.setVarFlag(ctask, 'dirs', '${S}/%s' % ctask)
        d.setVarFlag(ctask, 'cleandirs', '${S}/%s' % ctask)
        d.setVarFlag(ctask, 'sstate-inputdirs', '${S}/%s' % ctask)
        d.setVarFlag(ctask, 'sstate-outputdirs', '${DEPLOY_DIR_RELEASE}')
        d.setVarFlag(ctask, 'stamp-extra-info', '${MACHINE}')
        d.appendVarFlag(ctask, 'postfuncs', ' compress_binary_artifacts')
}

def layer_git_root(subdir):
    try:
        git_root = bb.process.run(['git', 'rev-parse', '--show-toplevel'], cwd=subdir)[0].rstrip()
    except bb.process.CmdError:
        return None
    return git_root

def get_release_info(layerdir, layername, topdir, oedir, indiv_only=None, indiv_only_toplevel=None, indiv_manifests=None):
    import collections
    import fnmatch

    if indiv_only is None:
        indiv_only = set()
    if indiv_only_toplevel is None:
        indiv_only_toplevel = set()
    if indiv_manifests is None:
        indiv_manifests = []

    relpath = None
    if layerdir not in indiv_only:
        git_root = layer_git_root(layerdir)
        if git_root:
            return git_root, os.path.basename(git_root), False

    if layername and any(fnmatch.fnmatchcase(layername, pat) for pat in indiv_manifests):
        indiv_layer = True
    else:
        indiv_layer = False

    if (layerdir not in indiv_only_toplevel and
            not layerdir.startswith(topdir + os.sep) and
            layerdir.startswith(oedir + os.sep)):
        return layerdir, os.path.relpath(layerdir, oedir), indiv_layer
    else:
        return layerdir, os.path.basename(layerdir), indiv_layer

python do_archive_layers () {
    """Archive the layers used to build, as git pack files, with a manifest."""
    import collections
    import configparser
    import oe.reproducible

    if 'layerdirs' not in d.getVar('INHERIT').split():
        save_layerdirs(d)

    directories = d.getVar('BBLAYERS').split()
    bitbake_path = bb.utils.which(d.getVar('PATH'), 'bitbake')
    bitbake_bindir = os.path.dirname(bitbake_path)
    directories.append(os.path.dirname(bitbake_bindir))

    corebase = os.path.realpath(d.getVar('COREBASE'))
    oedir = os.path.dirname(corebase)
    topdir = os.path.realpath(d.getVar('TOPDIR'))
    indiv_only_toplevel = d.getVar('SUBLAYERS_INDIVIDUAL_ONLY_TOPLEVEL').split()
    indiv_only = d.getVar('SUBLAYERS_INDIVIDUAL_ONLY').split() + indiv_only_toplevel
    indiv_manifests = d.getVar('INDIVIDUAL_MANIFEST_LAYERS').split()
    excluded_layers = d.getVar('RELEASE_EXCLUDED_LAYERNAMES').split()
    get_remotes_hook = d.getVar('GET_REMOTES_HOOK')
    if get_remotes_hook:
        get_remotes = bb.utils.get_context().get(get_remotes_hook)
        if not get_remotes:
            bb.fatal('Hook function specified in GET_REMOTES_HOOK (`%s`) does not exist' % get_remotes_hook)
    else:
        get_remotes = None

    layernames = {}
    for layername in d.getVar('BBFILE_COLLECTIONS').split():
        layerdir = d.getVar('LAYERDIR_%s' % layername)
        if layerdir:
            layernames[layerdir] = layername

    git_indivs = collections.defaultdict(set)
    to_archive, indiv_manifest_dirs = set(), set()
    path = d.getVar('PATH') + ':' + ':'.join(os.path.join(l, '..', 'scripts') for l in directories)
    for subdir in directories:
        subdir = os.path.realpath(subdir)
        layername = layernames.get(subdir)
        if layername in excluded_layers:
            bb.note('Skipping excluded layer %s' % layername)
            continue

        parent = os.path.dirname(subdir)
        git_root = layer_git_root(subdir)
        if subdir in indiv_only and git_root:
            git_indivs[git_root].add(os.path.relpath(subdir, git_root))
            if layername and any(fnmatch.fnmatchcase(layername, pat) for pat in indiv_manifests):
                indiv_manifest_dirs.add(subdir)
        else:
            archive_path, dest_path, is_indiv = get_release_info(subdir, layername, topdir, oedir, indiv_only=indiv_only, indiv_only_toplevel=indiv_only_toplevel, indiv_manifests=indiv_manifests)
            to_archive.add((archive_path, dest_path, None))
            if is_indiv:
                indiv_manifest_dirs.add(subdir)

    for parent, keep_files in git_indivs.items():
        to_archive.add((parent, os.path.basename(parent), tuple(keep_files)))

    outdir = d.expand('${S}/do_archive_layers')
    mandir = os.path.join(outdir, 'manifests')
    bb.utils.mkdirhier(mandir)
    bb.utils.mkdirhier(os.path.join(mandir, 'extra'))
    objdir = os.path.join(outdir, 'git-bundles')
    bb.utils.mkdirhier(objdir)
    manifestfn = d.expand('%s/${MANIFEST_NAME}.manifest' % mandir)
    manifests = [manifestfn]
    message = '%s %s' % (d.getVar('DISTRO_NAME') or d.getVar('DISTRO'), d.getVar('DISTRO_VERSION'))

    l = d.createCopy()
    l.setVar('SRC_URI', 'git://')
    l.setVar('WORKDIR', '/invalid')

    manifestdata = collections.defaultdict(list)
    for subdir, path, keep_paths in sorted(to_archive):
        parent = None
        if os.path.exists(os.path.join(subdir, '.git')):
            parent = subdir
        else:
            try:
                git_topdir = bb.process.run(['git', 'rev-parse', '--show-toplevel'], cwd=subdir)[0].rstrip()
            except bb.process.CmdError:
                pass
            else:
                if git_topdir != subdir:
                    subdir_relpath = os.path.relpath(subdir, git_topdir)
                    try:
                        ls = bb.process.run(['git', 'ls-tree', '-d', 'HEAD', subdir_relpath], cwd=subdir)
                    except bb.process.CmdError as exc:
                        pass
                    else:
                        if ls:
                            parent = git_topdir

        if not parent:
            bb.fatal('Unable to archive non-git directory: %s' % subdir)

        l.setVar('S', subdir)
        source_date_epoch = oe.reproducible.get_source_date_epoch(l, parent or subdir)

        if get_remotes:
            remotes = get_remotes(subdir, d) or {}
        else:
            remotes = {}
        if not remotes:
            bb.note('Skipping remotes for %s' % path)

        head = git_archive(subdir, objdir, parent, message, keep_paths, source_date_epoch, is_public=bool(remotes))

        if subdir in indiv_manifest_dirs:
            name = path.replace('/', '_')
            bb.utils.mkdirhier(os.path.join(mandir, 'extra', name))
            fn = d.expand('%s/extra/%s/${EXTRA_MANIFEST_NAME}-%s.manifest' % (mandir, name, name))
        else:
            fn = manifestfn
        manifestdata[fn].append('\t'.join([path, head] + ['%s=%s' % (k,v) for k,v in remotes.items()]) + '\n')
        bb.process.run(['tar', '-cf', 'bundle-' + '%s.tar' % head, 'git-bundles/%s.bundle' % head], cwd=outdir)
        os.unlink(os.path.join(objdir, '%s.bundle' % head))

    os.rmdir(objdir)

    infofn = d.expand('%s/${MANIFEST_NAME}.info' % mandir)
    with open(infofn, 'w') as infofile:
        c = configparser.ConfigParser()
        c['DEFAULT'] = {
            'bspfiles_path': d.getVar('BSPFILES_INSTALL_PATH'),
            'machine': d.getVar('MACHINE'),
        }
        c.write(infofile)

    for fn, lines in manifestdata.items():
        with open(fn, 'w') as manifest:
            manifest.writelines(lines)
            files = [os.path.relpath(fn, outdir)]
            if fn == manifestfn:
                files.append(os.path.relpath(infofn, outdir))
        bb.process.run(['tar', '-cf', os.path.basename(fn) + '.tar'] + files, cwd=outdir)

    scripts = d.getVar('FLEX_SCRIPTS_FILES').split()
    bb.process.run(['tar', '--transform=s,^,scripts/,', '--transform=s,^scripts/setup-flex,setup-flex,', '-cvf', d.expand('%s/${SCRIPTS_ARTIFACT_NAME}.tar' % outdir)] + scripts, cwd=d.getVar('WORKDIR'))
}
do_archive_layers[dirs] = "${S}/do_archive_layers ${S}"
do_archive_layers[vardeps] += "${GET_REMOTES_HOOK}"
# We make use of the distro version, so want to avoid changing checksum issues
# for snapshot builds.
do_archive_layers[vardepsexclude] += "DATE TIME"

def git_archive(subdir, outdir, parent, message=None, keep_paths=None, source_date_epoch=None, is_public=False):
    """Create an archive for the specified subdir, holding a single git object

    1. Clone or create the repo to a temporary location
    2. Filter out paths not in keep_paths, if set
    3. Make the repo shallow
    4. Repack the repo into a single git pack
    5. Copy the pack files to outdir
    """
    import glob
    import tempfile

    parent_git = os.path.join(parent, bb.process.run(['git', 'rev-parse', '--git-dir'], cwd=subdir)[0].rstrip())
    # Handle git worktrees
    _commondir = os.path.join(parent_git, 'commondir')
    if os.path.exists(_commondir):
        with open(_commondir, 'r') as f:
            parent_git = os.path.join(parent_git, f.read().rstrip())

    parent_head = bb.process.run(['git', 'rev-parse', 'HEAD'], cwd=subdir)[0].rstrip()

    with tempfile.TemporaryDirectory() as tmpdir:
        gitcmd = ['git', '--git-dir', tmpdir, '--work-tree', subdir]
        bb.process.run(gitcmd + ['init'])

        with open(os.path.join(tmpdir, 'objects', 'info', 'alternates'), 'w') as f:
            f.write(os.path.join(parent_git, 'objects') + '\n')

        if is_public and not keep_paths:
            head = parent_head
        else:
            bb.process.run(gitcmd + ['read-tree', parent_head])

            if message is None:
                message = 'Release of %s' % os.path.basename(subdir)
            commitcmd = ['commit-tree', '-m', message]
            commitcmd.extend(['-p', parent_head])

            bb.process.run(gitcmd + ['add', '-A', '.'], cwd=subdir)
            if keep_paths:
                files = bb.process.run(gitcmd + ['ls-tree', '-r', '--name-only', parent_head])[0].splitlines()
                kill_files = [f for f in files if f not in keep_paths and not any(f.startswith(p + '/') for p in keep_paths)]
                keep_files = set(files) - set(kill_files)
                if not keep_files:
                    bb.fatal('No files kept for %s' % parent)

                bb.process.run(gitcmd + ['update-index', '--force-remove', '--'] + kill_files, cwd=subdir)

            tree = bb.process.run(gitcmd + ['write-tree'])[0].rstrip()
            commitcmd.append(tree)

            env = {
                'GIT_AUTHOR_NAME': 'Build User',
                'GIT_AUTHOR_EMAIL': 'build_user@build_host',
                'GIT_COMMITTER_NAME': 'Build User',
                'GIT_COMMITTER_EMAIL': 'build_user@build_host',
            }
            if source_date_epoch:
                env['GIT_AUTHOR_DATE'] = str(source_date_epoch)
                env['GIT_COMMITTER_DATE'] = str(source_date_epoch)

            head = bb.process.run(gitcmd + commitcmd, env=env)[0].rstrip()

        if not is_public:
            with open(os.path.join(tmpdir, 'shallow'), 'w') as f:
                f.write(head + '\n')

        # We need a ref to ensure repack includes the new commit, as it
        # does not include dangling objects in the pack.
        bb.process.run(['git', 'update-ref', 'refs/packing', head], cwd=tmpdir)
        bb.process.run(['git', 'prune', '--expire=now'], cwd=tmpdir)
        bb.process.run(['git', 'repack', '-a', '-d'], cwd=tmpdir)
        bb.process.run(['git', 'prune-packed'], cwd=tmpdir)

        bb.process.run(['git', 'bundle', 'create', os.path.join(outdir, '%s.bundle' % head), 'refs/packing'], cwd=tmpdir)
        return head

def checksummed_downloads(dl_by_layer_fn, dl_by_layer_dl_dir, dl_dir):
    with open(dl_by_layer_fn, 'r') as f:
        lines = f.readlines()

    for layer_name, dl_path in (l.rstrip('\n').split('\t', 1) for l in lines):
        rel_path = os.path.relpath(dl_path, dl_by_layer_dl_dir)
        if rel_path.startswith('..'):
            bb.fatal('Download %s (in %s) is not relative to DL_DIR' % (dl_path, dl_by_layer_fn))

        dl_path = os.path.join(dl_dir, rel_path)
        if not os.path.exists(dl_path):
            # download is missing, probably excluded for license reasons
            bb.warn('Download %s does not exist, excluding' % os.path.basename(dl_path))
            continue

        checksum = chksum_dl(dl_path)
        yield layer_name, dl_path, rel_path, checksum

def uninative_downloads(workdir, dldir):
    for path in oe.path.find(os.path.join(workdir, 'uninative')):
        relpath = os.path.relpath(path, workdir)
        dlpath = os.path.join(dldir, relpath)
        checksum = chksum_dl(path, dlpath)
        yield None, path, relpath, checksum

def chksum_dl(path, dlpath=None):
    import pickle

    if dlpath is None:
        dlpath = path

    donefile = dlpath + '.done'
    checksum = None
    if os.path.exists(donefile):
        with open(donefile, 'rb') as cachefile:
            try:
                checksums = pickle.load(cachefile)
            except EOFError:
                pass
            else:
                checksum = checksums['sha256']

    if not checksum:
        checksum = bb.utils.sha256_file(path)

    return checksum

python do_archive_downloads () {
    import collections
    import pickle
    import oe.path

    dl_dir = d.getVar('DL_DIR')
    archive_dl_dir = d.getVar('ARCHIVE_RELEASE_DL_DIR') or dl_dir
    dl_by_layer_fn = d.getVar('ARCHIVE_RELEASE_DL_BY_LAYER_PATH')
    if not os.path.exists(dl_by_layer_fn):
        bb.fatal('%s does not exist, but downloads requires it. Please run `bitbake-layers dump-downloads` with appropriate arguments.' % dl_by_layer_fn)

    downloads = list(checksummed_downloads(dl_by_layer_fn, dl_dir, archive_dl_dir))
    downloads.extend(sorted(uninative_downloads(d.getVar('WORKDIR'), d.getVar('DL_DIR'))))
    outdir = d.expand('${S}/do_archive_downloads')
    mandir = os.path.join(outdir, 'manifests')
    dldir = os.path.join(outdir, 'downloads')
    bb.utils.mkdirhier(mandir)
    bb.utils.mkdirhier(os.path.join(mandir, 'extra'))
    bb.utils.mkdirhier(dldir)

    layer_manifests = {}
    corebase = os.path.realpath(d.getVar('COREBASE'))
    oedir = os.path.dirname(corebase)
    topdir = os.path.realpath(d.getVar('TOPDIR'))
    indiv_only_toplevel = d.getVar('SUBLAYERS_INDIVIDUAL_ONLY_TOPLEVEL').split()
    indiv_only = d.getVar('SUBLAYERS_INDIVIDUAL_ONLY').split() + indiv_only_toplevel
    indiv_manifests = d.getVar('INDIVIDUAL_MANIFEST_LAYERS').split()

    layers = set(i[0] for i in downloads)
    for layername in layers:
        if not layername:
            continue

        layerdir = d.getVar('LAYERDIR_%s' % layername)
        archive_path, dest_path, is_indiv = get_release_info(layerdir, layername, topdir, oedir, indiv_only=indiv_only, indiv_only_toplevel=indiv_only_toplevel, indiv_manifests=indiv_manifests)
        if is_indiv:
            extra_name = dest_path.replace('/', '_')
            bb.utils.mkdirhier(os.path.join(mandir, 'extra', extra_name))
            manifestfn = d.expand('%s/extra/%s/${EXTRA_MANIFEST_NAME}-%s.downloads' % (mandir, extra_name, extra_name))
            layer_manifests[layername] = manifestfn

    main_manifest = d.expand('%s/${MANIFEST_NAME}.downloads' % mandir)
    manifests = collections.defaultdict(list)
    for layername, path, dest_path, checksum in downloads:
        manifestfn = layer_manifests.get(layername) or main_manifest
        manifests[manifestfn].append((layername, path, dest_path, checksum))

    for manifest, manifest_downloads in manifests.items():
        with open(manifest, 'w') as f:
            for _, _, download_path, checksum in manifest_downloads:
                f.write('%s\t%s\n' % (download_path, checksum))

        bb.process.run(['tar', '-cf', os.path.basename(manifest) + '.tar', os.path.relpath(manifest, outdir)], cwd=outdir)

        for name, path, dest_path, checksum in manifest_downloads:
            dest = os.path.join(dldir, checksum)
            oe.path.symlink(path, dest, force=True)
            bb.process.run(['tar', '-chf', '%s/download-%s.tar' % (dldir, checksum), os.path.relpath(dest, outdir)], cwd=outdir)
            os.unlink(dest)
}
FETCHALL_TASK = "${@'do_archive_release_downloads_all' if oe.utils.inherits(d, 'archive-release-downloads') else 'do_fetchall'}"
do_archive_downloads[depends] += "${@'${RELEASE_IMAGE}:${FETCHALL_TASK}' if '${RELEASE_IMAGE}' else ''}"

python check_download_sanity () {
    from fnmatch import fnmatch
    from bb.monitordisk import convertGMK

    check_items = d.getVar('ARCHIVE_DOWNLOAD_SIZE_CHECK').split()
    checks = []
    for item in check_items:
        try:
            mode, pattern, thresholdgmk = item.split(',')
            threshold = convertGMK(thresholdgmk)
        except ValueError:
            bb.fatal('check_download_sanity: invalid entry `{}` in ARCHIVE_DOWNLOAD_SIZE_CHECK'.format(item))
        else:
            if mode not in ('WARN', 'ERROR', 'FATAL'):
                bb.fatal('check_download_sanity: invalid mode `{}` in ARCHIVE_DOWNLOAD_SIZE_CHECK'.format(mode))

            checks.append((mode, pattern, threshold, thresholdgmk))

    dl_by_layer_fn = d.getVar('ARCHIVE_RELEASE_DL_BY_LAYER_PATH')
    with open(dl_by_layer_fn, 'r') as f:
        lines = f.readlines()

    for layer_name, dl_path in (l.rstrip('\n').split('\t', 1) for l in lines):
        try:
            st = os.stat(dl_path)
        except OSError:
            continue
        basepath = os.path.basename(dl_path)

        for mode, pattern, threshold, thresholdgmk in checks:
            if fnmatch(basepath, pattern) and st.st_size >= threshold:
                msg = 'Download {} size ({}) exceeds configured threshold of {} for {}'.format(basepath, sizeof_fmt(st.st_size), sizeof_fmt(threshold), pattern)
                if mode == 'WARN':
                    bb.warn(msg)
                elif mode == 'ERROR':
                    bb.error(msg)
                elif mode == 'FATAL':
                    bb.fatal(msg)
                break
}
do_archive_downloads[prefuncs] += "check_download_sanity"

def sizeof_fmt(num, suffix='B'):
    for unit in ['','Ki','Mi','Gi','Ti','Pi','Ei','Zi']:
        if abs(num) < 1024.0:
            return "%3.1f%s%s" % (num, unit, suffix)
        num /= 1024.0
    return "%.1f%s%s" % (num, 'Yi', suffix)

archive_uninative_downloads () {
    # Ensure that uninative downloads are in ARCHIVE_RELEASE_DL_DIR, since
    # they're listed in the manifest
    find uninative -type f | while read -r fn; do
        mkdir -p "${ARCHIVE_RELEASE_DL_DIR}/$(dirname "$fn")"
        ln -sf "${DL_DIR}/$fn" "${ARCHIVE_RELEASE_DL_DIR}/$fn"
    done
}
archive_uninative_downloads[dirs] = "${WORKDIR}"
do_archive_downloads[prefuncs] += "archive_uninative_downloads"

release_tar () {
    tar --absolute-names --exclude=.svn \
        --exclude=.git --exclude=\*.pyc --exclude=\*.pyo --exclude=.gitignore "$@"  \
        -v --show-stored-names
}

repo_root () {
    git_root=$(cd $1 && git rev-parse --show-toplevel 2>/dev/null)
    # There's a chance this repo could be the overall environment
    # repository, not the layer repository, so just grab the layer
    # if the repo has submodules
    if [ -n "$git_root" ] && [ ! -e $git_root/.gitmodules ]; then
        echo $(cd $git_root && pwd)
        return
    fi

    rel=${1#${FLEXDIR}/}
    case "$rel" in
        /*)
            echo "$1"
            ;;
        *)
            echo "${FLEXDIR}/${rel%%/*}"
            ;;
    esac
}
repo_root[vardepsexclude] += "1#${FLEXDIR}/ rel%%/*"

bb_layers () {
    for layer in ${BBLAYERS}; do
        layer="${layer%/}"

        topdir="$(repo_root "$layer")"
        repo_name="${topdir##*/}"

        layer_relpath="${layer#${topdir}/}"
        if [ "$layer_relpath" = "$topdir" ]; then
            layer_relpath=$repo_name
        else
            layer_relpath=$repo_name/$layer_relpath
        fi

        if echo "${SUBLAYERS_INDIVIDUAL_ONLY}" | grep -qw "$layer"; then
            printf "%s %s %s\n" "$layer" "$layer_relpath" "$(echo "$layer_relpath" | tr / _)"
        elif echo "${SUBLAYERS_INDIVIDUAL_ONLY_TOPLEVEL}" | grep -qw "$layer"; then
            printf "%s %s\n" "$layer" "${layer##*/}"
        else
            printf "%s %s\n" "$topdir" "$layer_relpath"
        fi
    done
}
# Workaround shell function dependency issue
bb_layers[vardeps] += "repo_root"
bb_layers[vardepsexclude] += "layer%/ topdir##*/ layer#${topdir}/"

do_archive_images () {
    set -- "$@" "--transform=s,-${MACHINE},,i"
    set -- "$@" "--transform=s,${DEPLOY_DIR_IMAGE},${BINARY_INSTALL_PATH},"

    for filename in ${DEPLOY_IMAGES}; do
        echo "${DEPLOY_DIR_IMAGE}/$filename" >>include
    done

    # Lock down any autorevs
    if [ -e "${BUILDHISTORY_DIR}" ]; then
        buildhistory-collect-srcrevs -p "${BUILDHISTORY_DIR}" >"${WORKDIR}/autorevs.conf"
        if [ -s "${WORKDIR}/autorevs.conf" ]; then
            set -- "$@" "--transform=s,${WORKDIR}/autorevs.conf,${CONF_INSTALL_PATH}/autorevs.conf,"
            echo "${WORKDIR}/autorevs.conf" >>include
        fi
    fi

    if echo "${OVERRIDES}" | tr ':' '\n' | grep -qx 'qemuall'; then
        ext="$(echo ${IMAGE_EXTENSIONS} | tr ' ' '\n' | grep -v '^tar' | head -n 1 | xargs)"
        if [ ! -e "${DEPLOY_DIR_IMAGE}/${RELEASE_IMAGE}-${MACHINE}.$ext" ]; then
            bbfatal "Unable to find image for extension $ext, aborting"
        fi
        if [ -e "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin" ] || [ -e "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}.bin" ]; then
            kernel="${KERNEL_IMAGETYPE}.bin"
        else
            kernel="${KERNEL_IMAGETYPE}"
        fi
        sed -e "s/##ROOTFS##/${RELEASE_IMAGE}.$ext/; s/##KERNEL##/$kernel/" ${WORKDIR}/runqemu.in >runqemu
        chmod +x runqemu
        set -- "$@" "--transform=s,runqemu,${BINARY_INSTALL_PATH}/runqemu,"
        echo runqemu >>include
    fi

    cp ${TEMPLATECONF}/conf-notes.txt .
    sed 's,^MACHINE ??=.*,MACHINE ??= "${MACHINE}",' ${TEMPLATECONF}/local.conf.sample >local.conf.sample
    if [ -n "${DISTRO}" ]; then
        sed -i 's,^DISTRO =.*,DISTRO = "${DISTRO}",' local.conf.sample
    fi

    sourcery_version="$(echo ${SOURCERY_VERSION} | sed 's/-.*$//')"
    if [ -n "$sourcery_version" ]; then
        echo >>local.conf.sample
        echo "SOURCERY_VERSION_REQUIRED = \"$sourcery_version\"" >>local.conf.sample
    fi

    pdk_version="${PDK_DISTRO_VERSION}"
    if [ -n "$pdk_version" ]; then
        echo >>local.conf.sample
        echo "PDK_DISTRO_VERSION = \"$pdk_version\"" >>local.conf.sample
    fi

    sed -n '/^BBLAYERS/{n; :start; /\\$/{n; b start}; /^ *"$/d; :done}; p' ${TEMPLATECONF}/bblayers.conf.sample >bblayers.conf.sample
    echo 'BBLAYERS = "\' >>bblayers.conf.sample
    bb_layers | while read path relpath name; do
        printf '    $%s%s \\\n' '{FLEXDIR}/' "$relpath" >>bblayers.conf.sample
    done
    echo '"' >>bblayers.conf.sample

    set -- "$@" "--transform=s,$PWD/,${CONF_INSTALL_PATH}/,"
    echo "$PWD/local.conf.sample" >>include
    echo "$PWD/bblayers.conf.sample" >>include
    echo "$PWD/conf-notes.txt" >>include

    if [ -e "${DEPLOY_DIR_IMAGE}/${RELEASE_IMAGE}-${MACHINE}.qemuboot.conf" ]; then
        cp "${DEPLOY_DIR_IMAGE}/${RELEASE_IMAGE}-${MACHINE}.qemuboot.conf" ${WORKDIR}/qemuboot.conf
        sed -i -e 's,-${MACHINE},,g' ${WORKDIR}/qemuboot.conf
        set -- "$@" "--transform=s,${WORKDIR}/qemuboot.conf,${BINARY_INSTALL_PATH}/${RELEASE_IMAGE}.qemuboot.conf,"
        echo "${WORKDIR}/qemuboot.conf" >>include
    fi

    if [ -n "${XLAYERS}" ]; then
        for layer in ${XLAYERS}; do
            echo "$layer"
        done \
            | sort -u >"${WORKDIR}/xlayers.conf"
    fi
    if [ -e "${WORKDIR}/xlayers.conf" ]; then
        set -- "$@" "--transform=s,${WORKDIR}/xlayers.conf,${BSPFILES_INSTALL_PATH}/xlayers.conf,"
        echo "${WORKDIR}/xlayers.conf" >>include
    fi

    chmod +x "${WORKDIR}/bmaptool"
    set -- "$@" "--transform=s,${WORKDIR}/bmaptool,${BINARY_INSTALL_PATH}/bmaptool,"
    echo "${WORKDIR}/bmaptool" >>include
    release_tar "$@" --files-from=include -chf ${MACHINE}-${ARCHIVE_RELEASE_VERSION}.tar
}

do_prepare_release () {
    echo ${DISTRO_VERSION} >distro-version
}

compress_binary_artifacts () {
    for fn in ${MACHINE}*.tar; do
        if [ -e "$fn" ]; then
            if [ ${BINARY_ARTIFACTS_COMPRESSION} = ".bz2" ]; then
                bzip2 "$fn"
            elif [ ${BINARY_ARTIFACTS_COMPRESSION} = ".gz" ]; then
                gzip "$fn"
            fi
        fi
    done
}

SSTATETASKS += "do_prepare_release ${@' '.join('do_archive_%s' % i for i in "${RELEASE_ARTIFACTS}".split())}"

do_prepare_release[dirs] = "${S}/deploy"
do_prepare_release[umask] = "022"
SSTATE_SKIP_CREATION:task-prepare-release = "1"
do_prepare_release[sstate-inputdirs] = "${S}/deploy"
do_prepare_release[sstate-outputdirs] = "${DEPLOY_DIR_RELEASE}"
do_prepare_release[stamp-extra-info] = "${MACHINE}"
addtask do_prepare_release before do_build after do_patch

# Ensure that all our dependencies are entirely built
do_archive_images[depends] += "${@'${RELEASE_IMAGE}:do_image_complete' if '${RELEASE_IMAGE}' else ''}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"
deltask do_populate_sysroot

# This recipe emits no packages, and archives existing buildsystem content and
# output whose licenses are outside our control
deltask populate_lic
