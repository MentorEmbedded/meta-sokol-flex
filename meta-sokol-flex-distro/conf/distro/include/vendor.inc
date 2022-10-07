inherit layerdirs

def layerdirs(d):
    dirs = {}
    for layerpath in d.getVar('BBLAYERS', True).split():
        layerconf = os.path.join(layerpath, 'conf', 'layer.conf')

        l = bb.data.init()
        l.setVar('LAYERDIR', layerpath)
        l = bb.parse.handle(layerconf, l)
        l.expandVarref('LAYERDIR')

        for layername in (l.getVar('BBFILE_COLLECTIONS', True) or '').split():
            dirs[layername] = layerpath
    return dirs

def machine_layername(d):
    machine_conf = bb.utils.which(d.getVar('BBPATH'), 'conf/machine/%s.conf' % d.getVar('MACHINE'))
    if not machine_conf:
        bb.warn('Failed to locate conf/machine/%s.conf in BBPATH' % d.getVar('MACHINE'))
    else:
        conf_layer = os.path.dirname(os.path.dirname(os.path.dirname(machine_conf)))

        dirs = layerdirs(d)
        for layername in (d.getVar('BBFILE_COLLECTIONS', True) or '').split():
            layerdir = dirs.get(layername)
            if conf_layer == layerdir:
                return layername.replace('meta-', '')

MACHINE_LAYERNAME := "${@machine_layername(d) or 'UNKNOWN'}"
VENDOR ?= "${MACHINE_LAYERNAME}"
VENDOR_CONF ?= "conf/vendor/${VENDOR}.conf"

include ${VENDOR_CONF}
