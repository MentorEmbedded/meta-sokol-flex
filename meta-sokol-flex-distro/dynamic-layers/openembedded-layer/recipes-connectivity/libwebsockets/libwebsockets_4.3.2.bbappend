SRCREV:sokol-flex = "6665a80979e6e364547b4862dc8d8e69a862a7de"
SRC_URI:sokol-flex = "git://github.com/MentorEmbedded/libwebsockets.git;protocol=https;branch=v4.3-stable"

EXTRA_OECMAKE += "-DLWS_WITHOUT_TESTAPPS=ON"
