## The below are from 2d0f725 in meta-virtualization, pulled back into kirkstone.

# Export for possible use in Makefiles, default value comes from go.bbclass
export GO_LINKSHARED

# Override 0001-libnetwork-use-GO-instead-of-go.patch
FILESEXTRAPATHS:prepend:feature-sokol-flex-staging := "${THISDIR}/files:"
