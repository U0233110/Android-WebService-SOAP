package jialing.tcp;

import androidx.appcompat.app.AppCompatActivity;
import jialing.tcp.Function.SharePreferenceMgr;
import jialing.tcp.WebService.UploadFile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static jialing.tcp.WebService.UploadFile.serverIp;

public class SettingActivity extends AppCompatActivity {

    private EditText ed_server_ip;
    private Button btn_cancel, btn_confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        ed_server_ip = (EditText) findViewById(R.id.ed_server_ip);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);
        btn_confirm = (Button) findViewById(R.id.btn_confirm);

        serverIp = SharePreferenceMgr.getString(SettingActivity.this, "serverIp", "http://123.192.50.241:1201/MwIMS/Service/Service.asmx?op=UploadFile");
        ed_server_ip.setText(serverIp);

        //確定
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharePreferenceMgr.putString(SettingActivity.this, "serverIp", ed_server_ip.getText().toString());
                serverIp = SharePreferenceMgr.getString(SettingActivity.this, "serverIp", "http://123.192.50.241:1201/MwIMS/Service/Service.asmx?op=UploadFile");
                finish();

            }
        });

        //取消
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}