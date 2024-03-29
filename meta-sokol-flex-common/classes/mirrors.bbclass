# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

PREMIRRORS:append:sokol-flex = " \
git://sources.redhat.com/ git://sourceware.org/ \n\
"

MIRRORS:append:sokol-flex = "\
${KERNELORG_MIRROR}/ https://kernel.googlesource.com/ \n\
${KERNELORG_MIRROR}/ http://mirror.nexcess.net/kernel.org/ \n\
${KERNELORG_MIRROR}/ http://mirror.gbxs.net/pub/ \n\
${APACHE_MIRROR}/ http://archive.apache.org/dist/ \n\
${DEBIAN_MIRROR}/ ftp://archive.debian.org/debian/pool/ \n\
git://git.kernel.org/pub/ http://mirror.nexcess.net/kernel.org/ \n\
\
(ftp|https?)://.*/.* http://autobuilder.yoctoproject.org/sources/ \n\
(ftp|https?)://.*/.* http://sources.openembedded.org/ \n\
(ftp|https?)://.*/.* http://www.angstrom-distribution.org/unstable/sources/ \n\
\
(cvs|svn|git|gitsm|hg|bzr|osc|p4|svk)://.*/.* http://downloads.yoctoproject.org/mirror/sources/ \n\
(cvs|svn|git|gitsm|hg|bzr|osc|p4|svk)://.*/.* http://autobuilder.yoctoproject.org/sources/ \n\
(cvs|svn|git|gitsm|hg|bzr|osc|p4|svk)://.*/.* http://sources.openembedded.org/ \n\
(cvs|svn|git|gitsm|hg|bzr|osc|p4|svk)://.*/.* http://www.angstrom-distribution.org/unstable/sources/ \n\
"
