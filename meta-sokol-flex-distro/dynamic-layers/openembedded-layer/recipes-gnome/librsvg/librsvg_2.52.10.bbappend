# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

SRC_URI:remove = "${GNOME_MIRROR}/${GNOMEBN}/${@gnome_verdir("${PV}")}/${GNOMEBN}-${PV}.tar.${GNOME_COMPRESS_TYPE};name=archive"
SRC_URI:prepend = "https://github.com/MentorEmbedded/flex_package_source/releases/download/${BPN}-${PV}/${BPN}-${PV}.tar.gz;name=archive "

SRC_URI[archive.sha256sum] = "66e6a26b1abb103cbbfe1c7b6f97e9c48f4a33e8df17ca0a84f7af2bc896ef1d"
