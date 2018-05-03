package edu.iot.gallery1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {

    private final static int PERMISSION_REQ_CODE = 100; //권한 요청 코드

    String[] permissions; //권한 상수를 저장할 배열

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT < 23) { //롤리팝 이전이면 검사할 필요없음
            init(savedInstanceState);
        } else {
            if (checkPermissions()) { //다 체크됨
                init(savedInstanceState);
            }
        }
    }

    public void init(Bundle savedInstanceState) {
        //앱 초기화
    }

    boolean checkPermissions() {
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (permissionList.isEmpty()) return true;

        String[] permissionArr = permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(this,
                                        permissionArr,
                                        PERMISSION_REQ_CODE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean isAllGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                }
            }
            if (isAllGranted) {
                // 모든 권한이 부여된 경우
                init(null);
            } else {
                // 모든 권한이 부여되지 않은 경우
                Toast.makeText(this, "필요한 권한이 허용되지 않았습니다.", Toast.LENGTH_SHORT).show();;
                finish(); //종료
            }
        }
    }
}
