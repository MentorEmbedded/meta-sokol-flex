do_install:append:feature-mentor-staging () {
        chown -R root:root ${D}${datadir}/jamvm/classes.zip
}

