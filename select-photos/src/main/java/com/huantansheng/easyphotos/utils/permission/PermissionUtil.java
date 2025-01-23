package com.huantansheng.easyphotos.utils.permission;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.huantansheng.easyphotos.constant.Code;

import java.util.ArrayList;
import java.util.List;

/**
 * 运行时权限工具类
 * Created by huan on 2017/7/27.
 */

public class PermissionUtil {


    public interface PermissionCallBack {
        void onSuccess();

        void onShouldShow();

        void onFailed();
    }

    public static boolean checkAndRequestPermissionsInActivity(Activity cxt, String... checkPermissions) {
        boolean isHas = true;
        List<String> permissions = new ArrayList<>();
        for (String checkPermission : checkPermissions) {
            if (PermissionChecker.checkSelfPermission(cxt, checkPermission) != PermissionChecker.PERMISSION_GRANTED) {
                isHas = false;
                permissions.add(checkPermission);
            }
        }
        if (!isHas) {
            String[] p = permissions.toArray(new String[permissions.size()]);
            requestPermissionsInActivity(cxt, Code.REQUEST_PERMISSION, p);
        }
        return isHas;
    }

    public static boolean checkPhotoPermissionsInActivity(Activity cxt, boolean needCamera, boolean visualSelectedRequest, boolean needRequest) {
        List<String> permissions = new ArrayList<>();
        boolean isGranted = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            int readMediaImagesPermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.READ_MEDIA_IMAGES);
            int readMediaVisualUserSelectedPermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            if (visualSelectedRequest) {
                if (readMediaImagesPermission != PermissionChecker.PERMISSION_GRANTED) {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                    if (readMediaVisualUserSelectedPermission != PermissionChecker.PERMISSION_GRANTED) {
                        permissions.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
                    }
                } else {
                    isGranted = true;
                }
            } else {
                if (readMediaImagesPermission == PermissionChecker.PERMISSION_GRANTED ||
                        readMediaVisualUserSelectedPermission == PermissionChecker.PERMISSION_GRANTED) {
                    isGranted = true;
                } else {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
        } else {
            int writeExternalStoragePermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    int readExternalStoragePermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (readExternalStoragePermission == PermissionChecker.PERMISSION_GRANTED) {
                        isGranted = true;
                    } else {
                        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                    if (writeExternalStoragePermission == PermissionChecker.PERMISSION_GRANTED) {
                        if (isGranted) {
                            isGranted = true;
                        }
                    } else {
                        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                } else {
                    if (writeExternalStoragePermission == PermissionChecker.PERMISSION_GRANTED) {
                        isGranted = true;
                    } else {
                        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    }
                }
            } else {
                int readMediaImagesPermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.READ_MEDIA_IMAGES);
                if (readMediaImagesPermission == PermissionChecker.PERMISSION_GRANTED) {
                    isGranted = true;
                } else {
                    permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
        }
        if (isGranted && needCamera) {
            if (PermissionChecker.checkSelfPermission(cxt, Manifest.permission.CAMERA) !=
                    PermissionChecker.PERMISSION_GRANTED) {
                isGranted = false;
                permissions.add(Manifest.permission.CAMERA);
            }
        }
        if (needRequest) {
            if (!isGranted) {
                String[] p = permissions.toArray(new String[permissions.size()]);
                requestPermissionsInActivity(cxt, Code.REQUEST_PERMISSION, p);
            }
        }
        return isGranted;
    }

    public static boolean checkPhotoVisualSelectedPermissionsViewInActivity(Activity cxt) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            int readMediaImagesPermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.READ_MEDIA_IMAGES);
            int readMediaVisualUserSelectedPermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            if (readMediaImagesPermission != PermissionChecker.PERMISSION_GRANTED) {
                if (readMediaVisualUserSelectedPermission == PermissionChecker.PERMISSION_GRANTED) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void requestPermissionsInActivity(Activity cxt, int requestCode, String... permissions) {
        ActivityCompat.requestPermissions(cxt, permissions, requestCode);
    }

    public static void onPermissionResult(Activity cxt, @NonNull String[] permissions, @NonNull int[] grantResults, PermissionCallBack listener) {
        int length = grantResults.length;
        List<Integer> positions = new ArrayList<>();
        if (length > 0) {
            //READ_MEDIA_IMAGES,READ_MEDIA_VISUAL_USER_SELECTED有一个权限即可
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                int mediaImageIndex = -1;
                int visualUserSelectedPermission = PermissionChecker.checkSelfPermission(cxt, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equals(Manifest.permission.READ_MEDIA_IMAGES)) {
                        mediaImageIndex = i;
                    }
                }
                //
                if (mediaImageIndex != -1) {
                    if (grantResults[mediaImageIndex] != PackageManager.PERMISSION_GRANTED) {
                        positions.add(mediaImageIndex);
                    }
                }
                if (positions.size() > 0) {
                    if (visualUserSelectedPermission == PackageManager.PERMISSION_GRANTED) {
                        positions.clear();
                    }
                }
            } else {
                for (int i = 0; i < length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        positions.add(i);
                    }
                }
            }
        }
        if (positions.size() == 0) {
            listener.onSuccess();
            return;
        }
        progressNoPermission(cxt, listener, permissions, positions, 0);

    }

    private static void progressNoPermission(Activity cxt, PermissionCallBack listener, String[] permissions, List<Integer> positions, int i) {
        int index = positions.get(i);
        if (ActivityCompat.shouldShowRequestPermissionRationale(cxt, permissions[index])) {
            listener.onShouldShow();
            return;
        }
        if (i < positions.size() - 1) {
            i++;
            progressNoPermission(cxt, listener, permissions, positions, i);
            return;
        }
        listener.onFailed();
    }

}
