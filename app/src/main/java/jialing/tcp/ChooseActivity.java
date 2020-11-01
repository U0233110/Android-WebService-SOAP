package jialing.tcp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import jialing.tcp.Function.SharePreferenceMgr;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;
import static jialing.tcp.WebService.UploadFile.serverIp;

public class ChooseActivity extends Activity {

    private Button btn_location1, btn_location2, btn_new_pic, btn_setting;
    //取得軟體權限
    private final String PERMISSION_WRITE_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        btn_location1 = (Button) findViewById(R.id.btn_location1);
        btn_location2 = (Button) findViewById(R.id.btn_location2);
        btn_new_pic = (Button) findViewById(R.id.btn_new_pic);
        btn_setting = (Button) findViewById(R.id.btn_setting);

        //廢棄物接收站
        btn_location1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ChooseActivity.this, GarbageActivity.class);
                SharePreferenceMgr.putString(ChooseActivity.this, "Location", "2");
                startActivity(intent);
            }
        });

        //產源單位
        btn_location2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ChooseActivity.this, SickRoomActivity.class);
                SharePreferenceMgr.putString(ChooseActivity.this, "Location", "1");
                startActivity(intent);
            }
        });

        //圖片上傳
        btn_new_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                serverIp = SharePreferenceMgr.getString(ChooseActivity.this, "serverIp", "http://10.200.0.145/Service/Service.asmx");
                if (serverIp.equals("")) {
                    Intent intent = new Intent();
                    intent.setClass(ChooseActivity.this, SettingActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(ChooseActivity.this, NewPicActivity.class);
                    startActivity(intent);
                }

            }
        });

        //站台設定
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(ChooseActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        //取得軟體權限
        if (!hasPermission()) {
            if (needCheckPermission()) {
                //如果須要檢查權限，由於這個步驟要等待使用者確認，
                //所以不能立即執行儲存的動作，
                //必須在 onRequestPermissionsResult 回應中才執行
                return;
            }
        }

        SharePreferenceMgr.putString(ChooseActivity.this, "mac", getMacAddr().toLowerCase());

    }

    /**
     * 是否已經請求過該權限
     * API < 23 一律回傳 true
     */
    private boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (ActivityCompat.checkSelfPermission(this, PERMISSION_WRITE_STORAGE) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }

    /**
     * 確認是否要請求權限(API > 23)
     * API < 23 一律不用詢問權限
     */
    private boolean needCheckPermission() {
        //MarshMallow(API-23)之後要在 Runtime 詢問權限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {PERMISSION_WRITE_STORAGE, CAMERA, INTERNET};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
            return true;
        }
        return false;
    }

    /**
     * Get MacAddress，解決6.0以上版本問題
     *
     * @return
     */
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }
}