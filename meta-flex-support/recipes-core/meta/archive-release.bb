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
RELEASE_USE_TAGS ?= "false"
RELEASE_USE_TAGS[doc] = "Use git tags rather than just # of commits for layer archive versioning"
RELEASE_USE_TAGS[type] = "boolean"
BINARY_ARTIFACTS_COMPRESSION ?= ""
BINARY_ARTIFACTS_COMPRESSION[doc] = "Compression type for images and downloads artifacts.\
 Available: '.bz2' and '.gz'. No compression if empty"

ARCHIVE_RELEASE_VERSION ?= "${DISTRO_VERSION}"
MANIFEST_NAME ?= "${DISTRO}-${ARCHIVE_RELEASE_VERSION}-${MACHINE}"
EXTRA_MANIFEST_NAME ?= "${DISTRO}-${ARCHIVE_RELEASE_VERSION}"
SCRIPTS_VERSION ?= "0"
SCRIPTS_ARTIFACT_NAME ?= "${DISTRO}-scripts-${DISTRO_VERSION}.${SCRIPTS_VERSION}"

# `layers` artifact configuration {{{1
SUBLAYERS_INDIVIDUAL_ONLY ?= ""
SUBLAYERS_INDIVIDUAL_ONLY_TOPLEVEL ?= ""

RELEASE_EXCLUDED_LAYERNAMES ?= "workspacelayer"
RELEASE_EXCLUDED_LAYERNAMES[doc] = "List of layer names to exclude from archival"

# Files for the script artifact
FILESEXTRAPATHS:append = ":${@':'.join('%s/../scripts/release:%s/../scripts' % (l, l) for l in '${BBPATH}'.split(':'))}"
FLEX_SCRIPTS_FILES = "flex-checkout setup-flex setup-workspace setup-ubuntu setup-rocky"
SRC_URI += "${@' '.join('file://%s' % s for s in d.getVar('FLEX_SCRIPTS_FILES').split())}"
# }}}1

# `images` artifact configuration {{{1
# Filesystem paths in the destination for the image artifacts
BSPFILES_INSTALL_PATH = "${MACHINE}/${ARCHIVE_RELEASE_VERSION}"
BINARY_INSTALL_PATH ?= "${BSPFILES_INSTALL_PATH}/binary"
CONF_INSTALL_PATH ?= "${BSPFILES_INSTALL_PATH}/conf"

# In our `images` artifact, include a runqemu wrapper for qemu
SRC_URI:append:qemuall = " file://runqemu.in"

# Image files to be archived
IMAGE_BASENAME = "${RELEASE_IMAGE}"
EXTRA_IMAGES_ARCHIVE_RELEASE ?= ""
KERNEL_DEVICETREE_ARCHIVE_RELEASE ?= ""
DEPLOY_IMAGES ?= "\
    ${@' '.join('${IMAGE_LINK_NAME}.%s' % ext for ext in d.getVar('IMAGE_EXTENSIONS').split())} \
    ${EXTRA_IMAGES_ARCHIVE_RELEASE} \
    ${KERNEL_DEVICETREE_ARCHIVE_RELEASE} \
"
DEPLOY_IMAGES:append:qemuall = "${@' ' + d.getVar('KERNEL_IMAGETYPE') if 'wic' not in d.getVar('IMAGE_EXTENSIONS') else ''}"
DEPLOY_IMAGES[doc] = "List of files from DEPLOY_DIR_IMAGE which will be archived"

# License manifests to be archived
DEPLOY_LIC_MANIFESTS ?= "\
    package.manifest \
    license.manifest \
    image_license.manifest \
"
DEPLOY_LIC_MANIFESTS[doc] = "List of license manifest files from LICENSE_DIRECTORY/SSTATE_PKGARCH/IMAGE_LINK_NAME directory which will be archived"

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
ARCHIVE_RELEASE_DL_TOPDIR ?= "${ARCHIVE_RELEASE_DL_DIR}"

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

def uninative_downloads(workdir, dldir):
    for path in oe.path.find(os.path.join(workdir, 'uninative')):
        relpath = os.path.relpath(path, workdir)
        dlpath = os.path.join(dldir, relpath)
        checksum = chksum_dl(path, dlpath)
        yield None, path, relpath, checksum

archive_uninative_downloads () {
    # Ensure that uninative downloads are in ARCHIVE_RELEASE_DL_DIR, since
    # they're listed in the manifest
    find uninative -type f | while read -r fn; do
        mkdir -p "${ARCHIVE_RELEASE_DL_DIR}/$(dirname "$fn")"
        ln -sf "${DL_DIR}/$fn" "${ARCHIVE_RELEASE_DL_DIR}/$fn"
    done
}
archive_uninative_downloads[dirs] = "${UNPACKDIR}"
do_archive_downloads[prefuncs] += "archive_uninative_downloads"

release_tar () {
    tar --absolute-names --exclude=.svn \
        --exclude=.git --exclude=\*.pyc --exclude=\*.pyo --exclude=.gitignore "$@"  \
        -v --show-stored-names
}

git_tar () {
    path="$1"
    shift
    name="$1"
    shift
    rel="${path##*/}"

    if [ -e "$path/.git" ]; then
        git --git-dir=$path/.git archive --format=tar --prefix="${rel:-.}/" HEAD | bzip2 >${name}.tar.bz2
    else
        if repo_root "$path" | grep -q "^${FLEXDIR}/"; then
            release_tar $path "$@" -cjf ${name}.tar.bz2
        else
            release_tar $path "$@" -cjf $name.tar.bz2
        fi
    fi
}
# Workaround shell function dependency issue
git_tar[vardeps] += "repo_root"

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

do_archive_layers () {
    >${MACHINE}-layers.txt
    bb_layers | while read path relpath name; do
        echo "$relpath" >>${MACHINE}-layers.txt
    done

    bb_layers | sort -k1,1 -u | while read path relpath name; do
        if [ -z "$name" ]; then
            name="${path##*/}"
        fi

        if echo "${SUBLAYERS_INDIVIDUAL_ONLY_TOPLEVEL}" | grep -qw "$path"; then
            # Grab the entire toplevel dir for non-individually-archived
            # sub-layers
            git_tar "$path" "$name" "--transform=s,^$path,$name,"
        else
            git_tar "$path" "$name" "--transform=s,^$path,$relpath,"
        fi
    done
}

do_archive_downloads () {
    for layer in ${BBLAYERS}; do
        ${@bb.utils.which('${BBPATH}', '../scripts/bb-print-layer-data')} "$layer/conf/layer.conf"
    done 2>/dev/null | sed -n 's/^\([^:]*\):[^|]*|\([^|]*\)|.*/\1|\2/p' >layermap.txt

    mkdir -p downloads
    if [ -e ${WORKDIR}/uninative ]; then
        cp -a ${WORKDIR}/uninative downloads/
        # We symlink to the root of downloads so the downloads dir can be
        # used either as a mirror or directly as the DL_DIR
        (cd downloads && find uninative -type f -print0 | xargs -0 -I"{}" sh -c 'touch "{}.done"; ln -sf "{}" .; ln -sf "{}.done" .')
    fi

    if [ "${ARCHIVE_RELEASE_DL_TOPDIR}" != "${ARCHIVE_RELEASE_DL_DIR}" ]; then
        for dir in ${ARCHIVE_RELEASE_DL_TOPDIR}/*/; do
            dir="${dir%/}"
            name=$(basename $dir)
            mkdir -p downloads/$name
            find -L $dir -type f -maxdepth 2 | while read source; do
                source_name="$(basename "$source")"
                if [ -e "${DL_DIR}/$source_name" ]; then
                    ln -sf "${DL_DIR}/$source_name" "downloads/$name/$source_name"
                    touch "downloads/$name/$source_name.done"
                fi
            done
            cd downloads/$name
            for file in ${RELEASE_EXCLUDED_SOURCES}; do
                rm -f "$file"
            done
            cd - >/dev/null
            layerpath="$(sed -n "s/^$name|//p" layermap.txt)" || exit 1
            if [ -n "$layerpath" ]; then
                layerroot="$(repo_root "$layerpath")"
                layerbase="${layerroot##*/}"
                if echo "${LAYERS_OWN_DOWNLOADS}" | grep -Eq "\<$name\>"; then
                    layer_relpath="${layerpath#${layerroot}/}"
                    if [ "$layer_relpath" = "$layerroot" ]; then
                        layer_relpath=$layerbase
                    else
                        layer_relpath=$layerbase/$layer_relpath
                    fi
                    release_tar "--transform=s,^downloads/$name,$layer_relpath/downloads," -chf \
                            $name-downloads.tar downloads/$name
                else
                    release_tar "--transform=s,^downloads/$name,downloads," -rhf \
                            $layerbase-downloads.tar downloads/$name
                fi
            fi
        done
        if [ -n "${UNINATIVE_TARBALL}" ]; then
            release_tar -chf ${MACHINE}-downloads.tar downloads/uninative $(find downloads/uninative -type f | sed 's,^.*/,downloads/,')
        fi
    else
        mkdir -p downloads
        find -L ${ARCHIVE_RELEASE_DL_DIR} -type f -maxdepth 2 | while read source; do
            source_name="$(basename "$source")"
            if [ -e "${DL_DIR}/$source_name" ]; then
                ln -sf "${DL_DIR}/$source_name" "downloads/$source_name"
                touch "downloads/$source_name.done"
            fi
        done
        cd downloads
        for file in ${RELEASE_EXCLUDED_SOURCES}; do
            rm -f "$file"
        done
        cd - >/dev/null
        release_tar -chf ${MACHINE}-downloads.tar downloads/
    fi
    rm -rf downloads layermap.txt
}
# Workaround shell function dependency issue
do_archive_downloads[vardeps] += "repo_root"
addtask archive_downloads after do_fetch

do_archive_bitbake () {
    bitbake_dir="$(which bitbake)"
    bitbake_via_layers=0
    bb_layers | while read -r path _; do
        case "$bitbake_dir" in
            $path/*)
                return
                ;;
        esac
    done

    bitbake_path="$(repo_root $(dirname $(which bitbake))/..)"
    git_tar "$bitbake_path" bitbake "--transform=s,^$bitbake_path,${bitbake_path##*/},"
}

do_archive_images () {
    # transform IMAGE_LINK_NAME first before removing IMAGE_MACHINE_SUFFIX
    set -- "$@" "--transform=s,${LICENSE_DIRECTORY}/${SSTATE_PKGARCH}/${IMAGE_LINK_NAME},${BINARY_INSTALL_PATH},"
    set -- "$@" "--transform=s,${IMAGE_NAME_SUFFIX},,i"
    set -- "$@" "--transform=s,${IMAGE_MACHINE_SUFFIX},,i"
    set -- "$@" "--transform=s,${DEPLOY_DIR_IMAGE},${BINARY_INSTALL_PATH},"

    for filename in ${DEPLOY_IMAGES}; do
        echo "${DEPLOY_DIR_IMAGE}/$filename" >>include
    done

    for filename in ${DEPLOY_LIC_MANIFESTS}; do
        echo "${LICENSE_DIRECTORY}/${SSTATE_PKGARCH}/${IMAGE_LINK_NAME}/$filename" >>include
    done

    # Lock down any autorevs
    if [ -e "${BUILDHISTORY_DIR}" ]; then
        buildhistory-collect-srcrevs -p "${BUILDHISTORY_DIR}" >"${UNPACKDIR}/autorevs.conf"
        if [ -s "${UNPACKDIR}/autorevs.conf" ]; then
            set -- "$@" "--transform=s,${UNPACKDIR}/autorevs.conf,${CONF_INSTALL_PATH}/autorevs.conf,"
            echo "${UNPACKDIR}/autorevs.conf" >>include
        fi
    fi

    if echo "${OVERRIDES}" | tr ':' '\n' | grep -qx 'qemuall'; then
        ext="$(echo ${IMAGE_EXTENSIONS} | tr ' ' '\n' | grep -v '^tar' | head -n 1 | xargs)"
        if [ ! -e "${DEPLOY_DIR_IMAGE}/${RELEASE_IMAGE}-${MACHINE}${IMAGE_NAME_SUFFIX}.$ext" ]; then
            bbfatal "Unable to find image for extension $ext, aborting"
        fi
        if [ -e "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin" ] || [ -e "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}.bin" ]; then
            kernel="${KERNEL_IMAGETYPE}.bin"
        else
            kernel="${KERNEL_IMAGETYPE}"
        fi
        sed -e "s/##ROOTFS##/${RELEASE_IMAGE}.$ext/; s/##KERNEL##/$kernel/" ${UNPACKDIR}/runqemu.in >runqemu
        chmod +x runqemu
        set -- "$@" "--transform=s,runqemu,${BINARY_INSTALL_PATH}/runqemu,"
        echo runqemu >>include
    fi

    set -- "$@" "--transform=s,$PWD/,${CONF_INSTALL_PATH}/,"

    if [ -e "${DEPLOY_DIR_IMAGE}/${RELEASE_IMAGE}-${MACHINE}${IMAGE_NAME_SUFFIX}.qemuboot.conf" ]; then
        cp "${DEPLOY_DIR_IMAGE}/${RELEASE_IMAGE}-${MACHINE}${IMAGE_NAME_SUFFIX}.qemuboot.conf" ${UNPACKDIR}/qemuboot.conf
        sed -i -e 's,-${MACHINE},,g' ${UNPACKDIR}/qemuboot.conf
        set -- "$@" "--transform=s,${UNPACKDIR}/qemuboot.conf,${BINARY_INSTALL_PATH}/${RELEASE_IMAGE}.qemuboot.conf,"
        echo "${UNPACKDIR}/qemuboot.conf" >>include
    fi

    if [ -n "${XLAYERS}" ]; then
        for layer in ${XLAYERS}; do
            echo "$layer"
        done \
            | sort -u >"${UNPACKDIR}/xlayers.conf"
    fi
    if [ -e "${UNPACKDIR}/xlayers.conf" ]; then
        set -- "$@" "--transform=s,${UNPACKDIR}/xlayers.conf,${BSPFILES_INSTALL_PATH}/xlayers.conf,"
        echo "${UNPACKDIR}/xlayers.conf" >>include
    fi

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
