# ---------------------------------------------------------------------------------------------------------------------
# SPDX-License-Identifier: MIT
# ---------------------------------------------------------------------------------------------------------------------

# Enable use of the 'ROOT_PASSWORD' variable to set the root password for your
# image or images. In addition to setting to a specific password, it also
# handles special cases. If set to '0', empty root password is explicitly
# allowed. If set to '*', root login is explicitly disabled. This also changes
# the default behavior when ROOT_PASSWORD is empty -- if the image lacks
# debug-tweaks and empty-root-password, the image qa check will fail, as the
# user will have no way to login.

ROOT_PASSWORD ?= ""
ROOT_PASSWORD_HASH_ROUNDS ?= "10000"

IMAGE_INCOMPATIBLE_ZAPPED_MESSAGE = "ROOT_PASSWORD has not been set, and this \
image has neither debug-tweaks nor empty-root-password set. This will result \
in an image whose root login is disabled. Please set ROOT_PASSWORD to the root \
password, to '*' to explicitly disable root login, or to '0' to explicitly \
allow root login with an empty password."

EMPTY_ROOT_PASSWORD = "${@'empty-root-password' if d.getVar('ROOT_PASSWORD', True) == '0' else ''}"
IMAGE_FEATURES += "${EMPTY_ROOT_PASSWORD}"

def encrypt_root_pw(d):
    import os
    import crypt
    import base64
    import shlex

    prefix = '$6$'

    password = d.getVar('ROOT_PASSWORD')
    if any(password == pattern for pattern in [ '', '0', '*' ]):
        return ''

    salt = base64.b64encode(os.urandom(8))

    rounds = d.getVar('ROOT_PASSWORD_HASH_ROUNDS')
    if rounds is not None:
        rounds = max(1000, min(999999999, int(rounds) or 10000))
        prefix += 'rounds={0}$'.format(rounds)

    return shlex.quote(crypt.crypt(password, prefix + salt.decode('UTF-8')).replace('$','\$'))

inherit extrausers
ACTUAL_ROOT_PASSWORD = '${@encrypt_root_pw(d)}'
EXTRA_USERS_PARAMS:prepend = "${@'usermod -p %s root;' % d.getVar('ACTUAL_ROOT_PASSWORD') if d.getVar('ACTUAL_ROOT_PASSWORD') != '' else ''}"

# Change the default behavior when the root password is empty. If the image
# lacks empty-root-password and debug-tweaks, rather than defaulting to
# disabling root login, error out. The user can explicitly opt-in to the old
# behavior with this class inherited by setting ROOT_PASSWORD to '*'.
IMAGE_QA_COMMANDS += "image_check_zapped_root_password"
python image_check_zapped_root_password () {
    root_password = d.getVar('ROOT_PASSWORD')
    zapped_empty = bb.utils.contains_any('IMAGE_FEATURES', ['debug-tweaks', 'empty-root-password'], False, True, d)
    if not root_password and zapped_empty:
        message = d.getVar('IMAGE_INCOMPATIBLE_ZAPPED_MESSAGE')
        raise oe.utils.ImageQAFailed(message, 'image_check_zapped_root_password')
}

# Skip the QA check for initramfs images
IMAGE_QA_COMMANDS:remove = "${@'image_check_zapped_root_password' if d.getVar('IMAGE_FSTYPES') == d.getVar('INITRAMFS_FSTYPES') != '' else ''}"
