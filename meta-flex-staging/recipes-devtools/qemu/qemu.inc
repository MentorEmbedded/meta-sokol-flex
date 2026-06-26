SUMMARY = "Fast open source processor emulator"
DESCRIPTION = "QEMU is a hosted virtual machine monitor: it emulates the \
machine's processor through dynamic binary translation and provides a set \
of different hardware and device models for the machine, enabling it to run \
a variety of guest operating systems"
HOMEPAGE = "http://qemu.org"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"

DEPENDS += "bison-native meson-native ninja-native"

RDEPENDS:${PN}-ptest = "bash"

require qemu-targets.inc
# https://gitlab.com/qemu-project/qemu/-/commit/81e2b198a8cb4ee5fdf108bd438f44b193ee3a36 means
# we need a full python3-native setup
inherit pkgconfig ptest update-rc.d systemd python3native

LIC_FILES_CHKSUM = "file://COPYING;md5=a3b50d8b88dcc0eb3d7d39b760b9e821 \
                    file://COPYING.LIB;endline=24;md5=8a8178c06478747a771588adec965232"

S = "${WORKDIR}/git"

SRCREV_main = "f8ed81651e61d9c2166df6121ce2af0f44f06b3e"
SRCREV_softfloat = "b64af41c3276f97f0e181920400ee056b9c88037"
SRCREV_testfloat = "e7af9751d9f9fd3b47911f51a5cfd08af256a9ab"
SRCREV_dtc = "b6910bec11614980a21e46fbccc35934b671bd81"
SRCREV_keycodemapdb = "f5772a62ec52591ff6870b7e8ef32482371f22c6"
SRCREV_libblkio = "f84cc963a444e4cb34813b2dcfc5bf8526947dc0"
SRCREV_libvfio = "0b28d205572c80b568a1003db2c8f37ca333e4d7"
SRCREV_libslirp = "26be815b86e8d49add8c9a8b320239b9594ff03d"

SRCREV_FORMAT = "main_softfloat_testfloat_dtc_keycodemapdb_libblkio_libvfio_libslirp"

SRC_URI[anyhow-1.0.98.sha256sum] = "e16d2d3311acee920a9eb8d33b8cbc1787ce4a264e85f964c2404b969bdcd487"
SRC_URI[arbitrary-int-1.2.7.sha256sum] = "c84fc003e338a6f69fbd4f7fe9f92b535ff13e9af8997f3b14b6ddff8b1df46d"
SRC_URI[attrs-0.2.9.sha256sum] = "2a207d40f43de65285f3de0509bb6cb16bc46098864fce957122bbacce327e5f"
SRC_URI[bilge-0.2.0.sha256sum] = "dc707ed8ebf81de5cd6c7f48f54b4c8621760926cdf35a57000747c512e67b57"
SRC_URI[bilge-impl-0.2.0.sha256sum] = "feb11e002038ad243af39c2068c8a72bcf147acf05025dcdb916fcc000adb2d8"
SRC_URI[either-1.12.0.sha256sum] = "3dca9240753cf90908d7e4aac30f630662b02aebaa1b58a3cadabdb23385b58b"
SRC_URI[foreign-0.3.1.sha256sum] = "17ca1b5be8c9d320daf386f1809c7acc0cb09accbae795c2001953fa50585846"
SRC_URI[glib-sys-0.21.2.sha256sum] = "d09d3d0fddf7239521674e57b0465dfbd844632fec54f059f7f56112e3f927e1"
SRC_URI[itertools-0.11.0.sha256sum] = "b1c173a5686ce8bfa551b3563d0c2170bf24ca44da99c7ca4bfdab5418c3fe57"
SRC_URI[libc-0.2.162.sha256sum] = "18d287de67fe55fd7e1581fe933d965a5a9477b38e949cfa9f8574ef01506398"
SRC_URI[proc-macro2-1.0.95.sha256sum] = "02b3e5e68a3a1a02aad3ec490a98007cbc13c37cbe84a3cd7b8e406d76e7f778"
SRC_URI[proc-macro-error-1.0.4.sha256sum] = "da25490ff9892aab3fcf7c36f08cfb902dd3e71ca0f9f9517bea02a73a5ce38c"
SRC_URI[proc-macro-error-attr-1.0.4.sha256sum] = "a1be40180e52ecc98ad80b184934baf3d0d29f979574e439af5a55274b35f869"
SRC_URI[quote-1.0.36.sha256sum] = "0fa76aaf39101c457836aec0ce2316dbdc3ab723cdda1c6bd4e6ad4208acaca7"
SRC_URI[syn-2.0.104.sha256sum] = "17b6f705963418cdb9927482fa304bc562ece2fdd4f616084c50b7023b435a40"
SRC_URI[unicode-ident-1.0.12.sha256sum] = "3354b9ac3fae1ff6755cb6db53683adb661634f67557942dea4facebec0fee4b"

SRC_URI = "git://github.com/qemu/qemu.git;protocol=https;branch=stable-10.2;submodules=1;name=main \
           git://gitlab.com/qemu-project/berkeley-softfloat-3.git;protocol=https;branch=master;destsuffix=git/subprojects/berkeley-softfloat-3;name=softfloat \
           git://gitlab.com/qemu-project/berkeley-testfloat-3.git;protocol=https;branch=master;destsuffix=git/subprojects/berkeley-testfloat-3;name=testfloat \
           git://gitlab.com/qemu-project/dtc.git;protocol=https;branch=main;destsuffix=git/subprojects/dtc;name=dtc \
           git://gitlab.com/qemu-project/keycodemapdb.git;protocol=https;branch=master;destsuffix=git/subprojects/keycodemapdb;name=keycodemapdb \
           git://gitlab.com/libblkio/libblkio;protocol=https;branch=main;destsuffix=git/subprojects/libblkio;name=libblkio \
           git://gitlab.com/qemu-project/libvfio-user.git;protocol=https;branch=master;destsuffix=git/subprojects/libvfio-user;name=libvfio \
           git://gitlab.freedesktop.org/slirp/libslirp.git;protocol=https;branch=master;destsuffix=git/subprojects/libslirp;name=libslirp \
           crate://crates.io/anyhow/1.0.98 \
           crate://crates.io/arbitrary-int/1.2.7 \
           crate://crates.io/attrs/0.2.9 \
           crate://crates.io/bilge/0.2.0 \
           crate://crates.io/bilge-impl/0.2.0 \
           crate://crates.io/either/1.12.0 \
           crate://crates.io/foreign/0.3.1 \
           crate://crates.io/glib-sys/0.21.2 \
           crate://crates.io/itertools/0.11.0 \
           crate://crates.io/libc/0.2.162 \
           crate://crates.io/proc-macro2/1.0.95 \
           crate://crates.io/proc-macro-error/1.0.4 \
           crate://crates.io/proc-macro-error-attr/1.0.4 \
           crate://crates.io/quote/1.0.36 \
           crate://crates.io/syn/2.0.104 \
           crate://crates.io/unicode-ident/1.0.12 \
           file://run-ptest \
           file://xen-pvh/0001-xen-mapcache-Assert-mapcache-existance.patch \
           file://xen-pvh/0002-xen-mapcache-Add-function-to-check-if-the-mapcache-i.patch \
           file://xen-pvh/0003-physmem-xen-Conditionalize-use-of-the-mapcache.patch \
           file://xen-pvh/0004-hw-xen-hvm-Add-a-mapcache-arg-to-xen_register_ioreq.patch \
           file://xen-pvh/0005-hw-xen-xenpvh-Add-prop-to-enable-disable-the-mapcach.patch \
           file://xen-pvh/0006-system-xen-Constify-various-MemoryRegion-arguments.patch \
           file://fix-strerrorname_np.patch \
           file://0001-qemu-Add-addition-environment-space-to-boot-loader-q.patch \
           file://0002-apic-fixup-fallthrough-to-PIC.patch \
           file://0004-qemu-Do-not-include-file-if-not-exists.patch \
           file://0006-qemu-Determinism-fixes.patch \
           file://0007-tests-meson.build-use-relative-path-to-refer-to-file.patch \
           file://0008-Define-MAP_SYNC-and-MAP_SHARED_VALIDATE-on-needed-li.patch \
           file://0010-configure-lookup-meson-exutable-from-PATH.patch \
           file://0011-qemu-Ensure-pip-and-the-python-venv-aren-t-used-for-.patch \
           file://AT1-7236-flickering-rutabaga.patch \
           file://0001-Update-for-audio-support.patch \
           file://qemu-guest-agent.init \
           file://qemu-guest-agent.udev \
"

SRC_URI:append:powerpc = " file://powerpc_rom.bin"

# file index at download.qemu.org isn't reliable: https://gitlab.com/qemu-project/qemu-web/-/issues/9
#UPSTREAM_CHECK_URI = "https://www.qemu.org"
#UPSTREAM_CHECK_REGEX = "qemu-(?P<pver>\d+(\.\d+)+)\.tar"

#SRC_URI[sha256sum] = "faeea5d45a4d9bdde60716814eafadac4286f8a03ce757be632671e0eaa4ce0b"

CVE_STATUS[CVE-2007-0998] = "not-applicable-config: The VNC server can expose host files uder some circumstances. We don't enable it by default."

# https://bugzilla.redhat.com/show_bug.cgi?id=1609015#c11
CVE_STATUS[CVE-2018-18438] = "disputed: The issues identified by this CVE were determined to not constitute a vulnerability."

# As per https://bugzilla.redhat.com/show_bug.cgi?id=2203387
CVE_STATUS[CVE-2023-2680] = "not-applicable-platform: RHEL specific issue."

# NVD DB has this CVE as version-less (with "-")
CVE_STATUS[CVE-2024-6505] = "fixed-version: this CVE is fixed since 9.1.0"
CVE_STATUS[CVE-2024-8354] = "fixed-version: this CVE is fixed since 10.0.5"

CVE_STATUS[CVE-2023-1386] = "disputed: not an issue as per https://bugzilla.redhat.com/show_bug.cgi?id=2223985"

COMPATIBLE_HOST:mipsarchn32 = "null"
COMPATIBLE_HOST:mipsarchn64 = "null"
COMPATIBLE_HOST:riscv32 = "null"

# Per https://lists.nongnu.org/archive/html/qemu-devel/2020-09/msg03873.html
# upstream states qemu doesn't work without optimization
DEBUG_BUILD = "0"

do_unpack:append() {
    import shutil
    import os

    workdir = d.getVar('WORKDIR')
    s_dir = d.getVar('S')
    subprojects_dir = os.path.join(s_dir, 'subprojects')
    packagefiles_base = os.path.join(subprojects_dir, 'packagefiles')
    os.makedirs(subprojects_dir, exist_ok=True)

    src_uri_raw = d.getVar('SRC_URI') or ""
    for url_token in src_uri_raw.split():
        if url_token.startswith('crate://'):
            clean_url = url_token.split(';')[0].rstrip('/')
            url_parts = clean_url.split('/')
            if len(url_parts) >= 5:
                crate_name = url_parts[-2]
                crate_version = url_parts[-1]
                folder_identity = f"{crate_name}-{crate_version}"

                src_path = os.path.join(workdir, folder_identity)
                dest_path = os.path.join(subprojects_dir, folder_identity)

                if os.path.exists(src_path):
                    if os.path.exists(dest_path):
                        shutil.rmtree(dest_path)
                    shutil.move(src_path, dest_path)

    special_overlays = ['berkeley-softfloat-3', 'berkeley-testfloat-3']
    for project in special_overlays:
        target_subproject_dir = os.path.join(subprojects_dir, project)
        overlay_source_dir = os.path.join(packagefiles_base, project)

        if os.path.exists(target_subproject_dir) and os.path.exists(overlay_source_dir):
            bb.note(f"Manually injecting Meson overlay rules for: {project}")
            for item in os.listdir(overlay_source_dir):
                item_src = os.path.join(overlay_source_dir, item)
                item_dest = os.path.join(target_subproject_dir, item)

                if os.path.isdir(item_src):
                    if os.path.exists(item_dest):
                        shutil.rmtree(item_dest)
                    shutil.copytree(item_src, item_dest)
                else:
                    shutil.copy2(item_src, item_dest)
}

do_install:append() {
    # Prevent QA warnings about installed ${localstatedir}/run
    if [ -d ${D}${localstatedir}/run ]; then rmdir ${D}${localstatedir}/run; fi
}

do_install_ptest() {
	cp -rL ${B}/tests ${D}${PTEST_PATH}
	find ${D}${PTEST_PATH}/tests -type f -name "*.[Sshcodp]" | xargs -i rm -rf {}

	# Don't check the file genreated by configure
	sed -i -e "1s,#!/usr/bin/bash,#!${base_bindir}/bash," ${D}${PTEST_PATH}/tests/data/acpi/disassemle-aml.sh

	# Strip the paths from the QEMU variable, we can use PATH
	makfiles=$(find ${D}${PTEST_PATH} -name "*.mak")
	sed -i -e "s#^QEMU=.*/qemu-#QEMU=qemu-#g" $makfiles

	# Strip compiler flags as they break reproducibility
	sed -i -e "s,^CC=.*,CC=gcc," \
	       -e "s,^CCAS=.*,CCAS=gcc," \
	       -e "s,^LD=.*,LD=ld," $makfiles

	# Update SRC_PATH variable to the right place on target
	sed -i -e "s#^SRC_PATH=.*#SRC_PATH=${PTEST_PATH}#g" $makfiles

	# https://gitlab.com/qemu-project/qemu/-/issues/1403
	rm ${D}${PTEST_PATH}/tests/unit/test-io-channel-command
}

# QEMU_TARGETS is overridable variable
QEMU_TARGETS ?= "arm aarch64 i386 loongarch64 mips mipsel mips64 mips64el ppc ppc64 ppc64le riscv32 riscv64 sh4 x86_64"

EXTRA_OECONF = " \
    --prefix=${prefix} \
    --bindir=${bindir} \
    --includedir=${includedir} \
    --libdir=${libdir} \
    --mandir=${mandir} \
    --datadir=${datadir} \
    --docdir=${docdir}/${BPN} \
    --sysconfdir=${sysconfdir} \
    --libexecdir=${libexecdir} \
    --localstatedir=${localstatedir} \
    --with-suffix=${BPN} \
    --disable-strip \
    --disable-werror \
    --extra-cflags='${CFLAGS}' \
    --extra-ldflags='${LDFLAGS}' \
    --disable-docs \
    --host-cc='${BUILD_CC}' \
    --disable-af-xdp \
    --disable-download \
    ${PACKAGECONFIG_CONFARGS} \
    "

EXTRA_OECONF:append:class-target = " --cross-prefix=${HOST_PREFIX}"
EXTRA_OECONF:append:class-nativesdk = " --cross-prefix=${HOST_PREFIX}"

B = "${WORKDIR}/build"

#EXTRA_OECONF:append = " --python=${HOSTTOOLS_DIR}/python3"

do_configure:prepend:class-native() {
	# Append build host pkg-config paths for native target since the host may provide sdl
	BHOST_PKGCONFIG_PATH=$(PATH=/usr/bin:/bin pkg-config --variable pc_path pkg-config || echo "")
	if [ ! -z "$BHOST_PKGCONFIG_PATH" ]; then
		export PKG_CONFIG_PATH=$PKG_CONFIG_PATH:$BHOST_PKGCONFIG_PATH
	fi
}

do_configure() {
    export PKG_CONFIG=pkg-config
    ${S}/configure ${EXTRA_OECONF}
}
do_configure[cleandirs] += "${B}"

do_install () {
	export STRIP=""
	oe_runmake 'DESTDIR=${D}' install

	# If we built the guest agent, also install startup/udev rules
	if [ -e "${D}${bindir}/qemu-ga" ]; then
		install -d ${D}${sysconfdir}/init.d/
		install -m 0755 ${WORKDIR}/qemu-guest-agent.init ${D}${sysconfdir}/init.d/qemu-guest-agent
		sed -i 's:@bindir@:${bindir}:' ${D}${sysconfdir}/init.d/qemu-guest-agent

		install -d ${D}${sysconfdir}/udev/rules.d/
		install -m 0644 ${WORKDIR}/qemu-guest-agent.udev ${D}${sysconfdir}/udev/rules.d/60-qemu-guest-agent.rules

		install -d ${D}${systemd_unitdir}/system/
		install -m 0644 ${S}/contrib/systemd/qemu-guest-agent.service ${D}${systemd_unitdir}/system
		sed -i -e 's,-/usr/bin/,-${bindir}/,g' ${D}${systemd_unitdir}/system/qemu-guest-agent.service
	fi
	# ELF binary /usr/share/qemu/s390-netboot.img has relocations in .text
	rm ${D}${datadir}/qemu/s390-netboot.img -f
	# ELF binary /usr/share/qemu/s390-ccw.img has relocations in .text [textrel]
	rm ${D}${datadir}/qemu/s390-ccw.img -f
	# We don't support PARISC and these cause strip and SDK relocation errors
	rm ${D}${datadir}/qemu/hppa* -f
}

# Disable kvm/virgl/mesa on targets that do not support it
PACKAGECONFIG:remove:darwin = "kvm virglrenderer epoxy gtk+"
PACKAGECONFIG:remove:mingw32 = "kvm virglrenderer epoxy gtk+ pie"

PACKAGECONFIG[sdl] = "--enable-sdl,--disable-sdl,virtual/libsdl2"
PACKAGECONFIG[sdl-audio] = "--audio-drv-list=sdl,,virtual/libsdl2"
PACKAGECONFIG[sdl-image] = "--enable-sdl-image,--disable-sdl-image,libsdl2-image"
PACKAGECONFIG[png] = "--enable-png,--disable-png,libpng"
PACKAGECONFIG[virtfs] = "--enable-virtfs --enable-attr --enable-cap-ng,--disable-virtfs,libcap-ng attr,"
PACKAGECONFIG[aio] = "--enable-linux-aio,--disable-linux-aio,libaio,"
PACKAGECONFIG[uring] = "--enable-linux-io-uring,--disable-linux-io-uring,liburing"
PACKAGECONFIG[xen] = "--enable-xen,--disable-xen,xen-tools,xen-tools-libxenstore xen-tools-libxenctrl xen-tools-libxenguest"
PACKAGECONFIG[vnc-sasl] = "--enable-vnc --enable-vnc-sasl,--disable-vnc-sasl,cyrus-sasl,"
PACKAGECONFIG[vnc-jpeg] = "--enable-vnc --enable-vnc-jpeg,--disable-vnc-jpeg,jpeg,"
PACKAGECONFIG[libcurl] = "--enable-curl,--disable-curl,curl,"
PACKAGECONFIG[nss] = "--enable-smartcard,--disable-smartcard,nss,"
PACKAGECONFIG[curses] = "--enable-curses,--disable-curses,ncurses,"
PACKAGECONFIG[pixman] = "--enable-pixman,--disable-pixman,pixman"
PACKAGECONFIG[gtk+] = "--enable-gtk,--disable-gtk,gtk+3 gettext-native"
PACKAGECONFIG[vte] = "--enable-vte,--disable-vte,vte gettext-native"
PACKAGECONFIG[libcap-ng] = "--enable-cap-ng,--disable-cap-ng,libcap-ng,"
PACKAGECONFIG[ssh] = "--enable-libssh,--disable-libssh,libssh,"
PACKAGECONFIG[gcrypt] = "--enable-gcrypt,--disable-gcrypt,libgcrypt,"
PACKAGECONFIG[nettle] = "--enable-nettle,--disable-nettle,nettle"
PACKAGECONFIG[libusb] = "--enable-libusb,--disable-libusb,libusb1"
PACKAGECONFIG[fdt] = "--enable-fdt,--disable-fdt,dtc"
PACKAGECONFIG[alsa] = "--audio-drv-list=default,,alsa-lib"
PACKAGECONFIG[oss]      = "--audio-drv-list=oss,,"
PACKAGECONFIG[epoxy] = "--enable-opengl,--disable-opengl,libepoxy"
PACKAGECONFIG[lzo] = "--enable-lzo,--disable-lzo,lzo"
PACKAGECONFIG[dax] = "--enable-libdaxctl,--disable-libdaxctl,ndctl"
PACKAGECONFIG[numa] = "--enable-numa,--disable-numa,numactl"
PACKAGECONFIG[gnutls] = "--enable-gnutls,--disable-gnutls,gnutls"
PACKAGECONFIG[bzip2] = "--enable-bzip2,--disable-bzip2,bzip2"
PACKAGECONFIG[libiscsi] = "--enable-libiscsi,--disable-libiscsi"
PACKAGECONFIG[kvm] = "--enable-kvm,--disable-kvm"
PACKAGECONFIG[virglrenderer] = "--enable-virglrenderer,--disable-virglrenderer,virglrenderer"
PACKAGECONFIG[rutabaga-gfx] = "--enable-rutabaga-gfx,--disable-rutabaga-gfx,rutabaga-gfx-ffi"
# spice will be in meta-networking layer
PACKAGECONFIG[spice] = "--enable-spice,--disable-spice,spice"
# usbredir will be in meta-networking layer
PACKAGECONFIG[dbus-display] = "--enable-dbus-display,--disable-dbus-display,glib-2.0-native,dbus"
PACKAGECONFIG[usb-redir] = "--enable-usb-redir,--disable-usb-redir,usbredir"
PACKAGECONFIG[snappy] = "--enable-snappy,--disable-snappy,snappy"
PACKAGECONFIG[glusterfs] = "--enable-glusterfs,--disable-glusterfs,glusterfs"
PACKAGECONFIG[xkbcommon] = "--enable-xkbcommon,--disable-xkbcommon,libxkbcommon"
PACKAGECONFIG[libudev] = "--enable-libudev,--disable-libudev,udev"
PACKAGECONFIG[attr] = "--enable-attr,--disable-attr,attr,"
PACKAGECONFIG[rbd] = "--enable-rbd,--disable-rbd,ceph,ceph"
PACKAGECONFIG[vhost] = "--enable-vhost-net,--disable-vhost-net,,"
PACKAGECONFIG[ust] = "--enable-trace-backends=ust,,lttng-ust,"
PACKAGECONFIG[pie] = "--enable-pie,--disable-pie,,"
PACKAGECONFIG[seccomp] = "--enable-seccomp,--disable-seccomp,libseccomp"
# libnfs is currently provided by meta-kodi
PACKAGECONFIG[libnfs] = "--enable-libnfs,--disable-libnfs,libnfs"
PACKAGECONFIG[pmem] = "--enable-libpmem,--disable-libpmem,pmdk"
PACKAGECONFIG[pulseaudio] = "--enable-pa --audio-drv-list=pa,--disable-pa,pulseaudio"
PACKAGECONFIG[selinux] = "--enable-selinux,--disable-selinux"
PACKAGECONFIG[bpf] = "--enable-bpf,--disable-bpf,libbpf"
PACKAGECONFIG[capstone] = "--enable-capstone,--disable-capstone"
PACKAGECONFIG[rdma] = "--enable-rdma,--disable-rdma"
PACKAGECONFIG[vde] = "--enable-vde,--disable-vde"
PACKAGECONFIG[fuse] = "--enable-fuse --enable-fuse-lseek,--disable-fuse --disable-fuse-lseek,fuse3"
PACKAGECONFIG[slirp] = "--enable-slirp,--disable-slirp,libslirp"
PACKAGECONFIG[brlapi] = "--enable-brlapi,--disable-brlapi"
PACKAGECONFIG[jack] = "--enable-jack --audio-drv-list=jack,--disable-jack,jack,"
PACKAGECONFIG[debuginfo] = "--enable-libdw,--disable-libdw,elfutils"
PACKAGECONFIG[pipewire] = "--enable-pipewire --audio-drv-list=pipewire,--disable-pipewire,pipewire"
PACKAGECONFIG[sndio] = "--enable-sndio --audio-drv-list=sndio,--disable-sndio,sndio"
PACKAGECONFIG[libkeyutils] = "--enable-libkeyutils,--disable-libkeyutils,keyutils"

INSANE_SKIP:${PN}-common = "arch"

FILES:${PN} += "${datadir}/icons"

# For user who want to install all arch packages
PACKAGES =+ "${PN}-common"
RDEPENDS:${PN} += "${PN}-common"

ALLOW_EMPTY:${PN} = "1"
FILES:${PN} = ""

FILES:${PN}-common = "${bindir}/* ${includedir}/* ${libexecdir}/* ${datadir}/* ${localstatedir}"

PACKAGES_DYNAMIC += "^${PN}-user-.*  ^${PN}-system-.*"

PACKAGESPLITFUNCS =+ "split_qemu_packages"

python split_qemu_packages () {
    archdir = d.expand('${bindir}/')
    subpackages = do_split_packages(d, archdir, r'^qemu-system-(.*)$', '${PN}-system-%s', 'QEMU full system emulation binaries(%s)' , prepend=True, extra_depends='${PN}-common')

    subpackages += do_split_packages(d, archdir, r'^qemu-((?!system|edid|ga|img|io|nbd|pr-helper|storage-daemon).*)$', '${PN}-user-%s', 'QEMU full user emulation binaries(%s)' , prepend=True, extra_depends='${PN}-common')
    if subpackages:
        d.appendVar('RDEPENDS:' + d.getVar('PN'), ' ' + ' '.join(subpackages))
}

# Put the guest agent in a separate package
PACKAGES =+ "${PN}-guest-agent"
SUMMARY:${PN}-guest-agent = "QEMU guest agent"
FILES:${PN}-guest-agent += " \
    ${bindir}/qemu-ga \
    ${sysconfdir}/udev/rules.d/60-qemu-guest-agent.rules \
    ${sysconfdir}/init.d/qemu-guest-agent \
    ${systemd_unitdir}/system/qemu-guest-agent.service \
"

INITSCRIPT_PACKAGES = "${PN}-guest-agent"
INITSCRIPT_NAME:${PN}-guest-agent = "qemu-guest-agent"
INITSCRIPT_PARAMS:${PN}-guest-agent = "defaults"

SYSTEMD_PACKAGES = "${PN}-guest-agent"
SYSTEMD_SERVICE:${PN}-guest-agent = "qemu-guest-agent.service"
