package com.saladjack.moemusic.permission;

import java.util.List;

/**
 * @author: saladjack
 * @date: 2016/10/9
 * @desciption: 拒绝权限
 */

public interface OnPermissionsDeniedListener {
    void onPermissionsDenied(PermissionBuilder builder, List<String> perms);
}
