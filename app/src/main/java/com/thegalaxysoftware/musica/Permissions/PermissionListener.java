package com.thegalaxysoftware.musica.Permissions;

public interface PermissionListener {

    void permissionsChanged(String permissionChanged);

    void permissionsGranted(String permissionGranted);

    void permissionsRemoved(String permissionRemoved);
}
