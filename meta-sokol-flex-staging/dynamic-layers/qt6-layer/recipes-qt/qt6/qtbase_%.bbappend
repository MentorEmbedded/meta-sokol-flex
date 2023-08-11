do_install:append:class-nativesdk() {
    RELPATH=${@os.path.relpath(d.getVar('prefix') + '/share/cmake/Qt6Toolchain.cmake', d.getVar('QT6_INSTALL_LIBDIR') + '/cmake/Qt6')}
    sed -i ${D}${QT6_INSTALL_LIBDIR}/cmake/Qt6/qt.toolchain.cmake \
        -e "s|/.*/toolchain.cmake|\${CMAKE_CURRENT_LIST_DIR}/$RELPATH|"
}
