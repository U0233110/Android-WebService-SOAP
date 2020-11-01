package jialing.tcp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.RequiresApi;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import jialing.tcp.Function.BarcodeScanActivity;
import jialing.tcp.Function.SharePreferenceMgr;
import jialing.tcp.Models.DateMessage;
import jialing.tcp.Models.ReturnMessage;

public class SickRoomActivity extends AppCompatActivity {
    private final String TAG = "GarbageActivity";
    private Button btn_barcode1, btn_barcode2, btn_barcode3, btn_sent;
    private EditText tv_barcode3;
    private TextView tv_barcode1, tv_barcode2, tv_recv;
    private String barcode = "", PhoneID = "", Location = "", barcode1 = "", barcode2 = "", barcode3 = "";
    private Toast toast;
    //tcp
    private boolean isConnected = false;
    Socket socket = null;
    BufferedWriter writer = null;
    BufferedReader reader = null;
    private String line;
    //資料
    private DateMessage condition;
    private List<DateMessage> dateMessageArrayList = new ArrayList<DateMessage>();//後送Server資訊
    private List<ReturnMessage> returnMessages = new ArrayList<ReturnMessage>();//棧板資料

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sick_room);

        btn_barcode1 = (Button) findViewById(R.id.btn_barcode1);
        btn_barcode2 = (Button) findViewById(R.id.btn_barcode2);
        btn_barcode3 = (Button) findViewById(R.id.btn_barcode3);
        btn_sent = (Button) findViewById(R.id.btn_sent);
        tv_barcode1 = (TextView) findViewById(R.id.tv_barcode1);
        tv_barcode2 = (TextView) findViewById(R.id.tv_barcode2);
        tv_barcode3 = (EditText) findViewById(R.id.tv_barcode3);
        tv_recv = (TextView) findViewById(R.id.tv_recv);
        SharePreferenceMgr.putString(SickRoomActivity.this, "barcode1", "");
        SharePreferenceMgr.putString(SickRoomActivity.this, "barcode2", "");
        SharePreferenceMgr.putString(SickRoomActivity.this, "barcode3", "");
        tv_recv.setMovementMethod(ScrollingMovementMethod.getInstance());

        PhoneID = SharePreferenceMgr.getString(SickRoomActivity.this, "mac", "");
        Location = SharePreferenceMgr.getString(SickRoomActivity.this, "Location", "");

        //將log檔寫入檔案中
        if (isExternalStorageWritable()) {
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/AppFolder");
            File logDirectory = new File(appDirectory + "/log");
            File logFile = new File(logDirectory, "logcat" + System.currentTimeMillis() + ".txt");
            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }
            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }
            // clear the previous logcat and then write the new one to the file
            try {
                Process process = Runtime.getRuntime().exec("logcat -c");
                process = Runtime.getRuntime().exec("logcat -f " + logFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //取得目錄下檔案
            File[] list = logDirectory.listFiles();
            //依異動時間排序
            Arrays.sort(list, new Comparator<File>() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                public int compare(File f1, File f2) {
                    return Long.compare(f1.lastModified(), f2.lastModified());
                }
            });

            //如果log檔大於等於5筆就刪除最早的一筆
            if (list.length >= 5) {
                deleteFile(list[0]);
            }
        }

        //掃取責任中心
        btn_barcode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcode = "barcode1";
                //初始化Intent物件
                Intent intent = new Intent();
                //從LoginActivity to ReasonsActivity
                intent.setClass(SickRoomActivity.this, BarcodeScanActivity.class);
                intent.putExtra("barcode", barcode);//此方式可以放所有基本型別
                //開啟Activity
                startActivity(intent);
            }
        });
        //掃取垃圾種類
        btn_barcode2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcode = "barcode2";
                //初始化Intent物件
                Intent intent = new Intent();
                //從LoginActivity to ReasonsActivity
                intent.setClass(SickRoomActivity.this, BarcodeScanActivity.class);
                intent.putExtra("barcode", barcode);//此方式可以放所有基本型別
                //開啟Activity
                startActivity(intent);
            }
        });
        //掃取包數
        btn_barcode3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barcode = "barcode3";
                //初始化Intent物件
                Intent intent = new Intent();
                //從LoginActivity to ReasonsActivity
                intent.setClass(SickRoomActivity.this, BarcodeScanActivity.class);
                intent.putExtra("barcode", barcode);//此方式可以放所有基本型別
                //開啟Activity
                startActivity(intent);
            }
        });
        //提交
        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清除資料
                dateMessageArrayList.clear();
                //取得時間
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String currentDateandTime = sdf.format(new Date());
                //新增資料
                condition = new DateMessage("200", barcode1, barcode2, barcode3,
                        null, 0, null, Location, PhoneID,
                        currentDateandTime);
                dateMessageArrayList.add(condition);

                //資料上傳
                //將list轉換成json
                Gson gson = new Gson();
                Type listType = new TypeToken<List<DateMessage>>() {
                }.getType();

                //新的巡檢資料
                String json = gson.toJson(dateMessageArrayList, listType);
                Log.e("json", json);
                json = json.replace("[", "");
                json = json.replace("]", "");
                //呼叫 向输出流写数据
                send(json);
            }
        });
        //连接按钮处理函数
        connect();
    }

    /**
     * 刪除指定目錄下檔案及目錄 * * @return
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            // 檔案不存在
            return;
        } else {
            if (file.isFile() && file.exists()) {
                // 刪除檔案
                file.delete();
                return;
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //取得暫存資料
        barcode1 = SharePreferenceMgr.getString(SickRoomActivity.this, "barcode1", "");
        barcode2 = SharePreferenceMgr.getString(SickRoomActivity.this, "barcode2", "");
        barcode3 = SharePreferenceMgr.getString(SickRoomActivity.this, "barcode3", "");
        tv_barcode1.setText(barcode1);
        tv_barcode2.setText(barcode2);
        tv_barcode3.setText(barcode3);
    }

    @Override
    protected void onDestroy() {
        try {
            /* 关闭socket */
            if (null != socket) {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        }
        tv_recv.setText("");
        super.onDestroy();
    }

    /* 定义Handler对象 */
    private Handler handler = new Handler() {
        @Override
        /* 当有消息发送出来的时候就执行Handler的这个方法 */
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //解析Json
            Gson gson = new Gson();
            returnMessages = gson.fromJson("[" + line + "]", new TypeToken<List<ReturnMessage>>() {
            }.getType());

            if (returnMessages.get(0).getStatus().equals("200")) {
                /* 更新UI */
                SharePreferenceMgr.putString(SickRoomActivity.this, "barcode1", "");
                SharePreferenceMgr.putString(SickRoomActivity.this, "barcode2", "");
                SharePreferenceMgr.putString(SickRoomActivity.this, "barcode3", "");
                tv_barcode1.setText("");
                tv_barcode2.setText("");
                tv_barcode3.setText("");
            }

            /* 更新UI */
            tv_recv.append(returnMessages.get(0).getMsg());
            /* 调试输出 */
            Log.i("PDA", "----->" + line);

        }
    };

    /* 连接按钮处理函数：建立Socket连接 */
    @SuppressLint({"HandlerLeak", "StaticFieldLeak"})
    public void connect() {
        if (false == isConnected) {
            new Thread() {
                public void run() {
                    String IPAdr = "221.224.144.165";
                    int PORT = 64000;
                    try {
                        /* 建立socket */
                        socket = new Socket(IPAdr, PORT);
                        /* 输出流 */
                        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                        /* 输入流 */
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        /* 调试输出 */
                        Log.i(TAG, "输入输出流获取成功");
                        Log.i(TAG, "检测数据");
                        /* 读数据并更新UI */
                        char[] buf = new char[2048];
                        int i;
                        while ((i = reader.read(buf, 0, 100)) != -1) {
                            line = new String(buf, 0, i);
                            Message msg = handler.obtainMessage();
                            msg.obj = line;
                            handler.sendMessage(msg);
                            Log.i(TAG, "send to handler");
                        }

                    } catch (UnknownHostException e) {
                        Looper.prepare();
                        showToast("無法建立連線" + e, Toast.LENGTH_SHORT);
                        Looper.loop();
                        e.printStackTrace();
                        isConnected = false;
                    } catch (IOException e) {
                        Looper.prepare();
                        showToast("無法建立連線" + e, Toast.LENGTH_SHORT);
                        Looper.loop();
                        e.printStackTrace();
                        isConnected = false;
                    } catch (RuntimeException e) {
                        Looper.prepare();
                        showToast("無法建立連線" + e, Toast.LENGTH_SHORT);
                        Looper.loop();
                        e.printStackTrace();
                        isConnected = false;
                    } catch (Exception e) {
                        Looper.prepare();
                        showToast("無法建立連線" + e, Toast.LENGTH_SHORT);
                        Looper.loop();
                        e.printStackTrace();
                        isConnected = false;
                    }
                }
            }.start();
            isConnected = true;
            /* 更新UI */
//            btn_connect.setText("断开");
//            Toast.makeText(GarbageActivity.this, "連線成功", Toast.LENGTH_SHORT).show();

        } else {
            isConnected = false;
            tv_recv.setText("");
            /* 关闭socket */
            onDestroy();
            showToast("無法建立連線", Toast.LENGTH_SHORT);

        }
    }

    /* 发送按钮处理函数：向输出流写数据 */
    public void send(String message) {
        try {
            if (writer != null) {
                writer.write(message);
                /* 更新UI */
                SharePreferenceMgr.putString(SickRoomActivity.this, "barcode1", "");
                SharePreferenceMgr.putString(SickRoomActivity.this, "barcode2", "");
                SharePreferenceMgr.putString(SickRoomActivity.this, "barcode3", "");
                tv_barcode1.setText("");
                tv_barcode2.setText("");
                tv_barcode3.setText("");
                writer.flush();/**HERE*/
            } else {
                showToast("無法建立連線", Toast.LENGTH_SHORT);

            }
            /* 向输出流写数据 */

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 显示最后的Toast
     *
     * @param msg
     * @param length
     */
    protected void showToast(String msg, int length) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, length);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }
}