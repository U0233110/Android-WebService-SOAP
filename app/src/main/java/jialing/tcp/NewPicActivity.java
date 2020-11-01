package jialing.tcp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import jialing.tcp.Function.BarcodeScanActivity;
import jialing.tcp.Function.SharePreferenceMgr;
import jialing.tcp.Models.DateMessage;
import jialing.tcp.Models.ReturnMessage;
import jialing.tcp.WebService.UploadFile;

public class NewPicActivity extends AppCompatActivity {

    private Button btn_barcode1, btn_barcode2, btn_barcode3, btn_picture, btn_sent;
    private EditText tv_barcode3;
    private TextView tv_barcode1, tv_barcode2, tv_recv;
    private ImageView imageView;
    private String BelongDate = "", barcode = "", addFileName = "", PhoneID = "", Location = "", barcode1 = "", barcode2 = "", barcode3 = "";
    private Toast toast;
    //照相
    File file;
    Uri uriMyImage;
    private Bitmap bitmap;
    byte[] bytes;
    String imageString = "";
    long imageSize;
    //-----
    private static String SOAP_ACTION2 = "http://tempuri.org/UploadFile";
    private static String NAMESPACE = "http://tempuri.org/";
    private static String METHOD_NAME2 = "UploadFile";
    private static String URL = "http://123.192.50.241:1201/MwIMS/Service/Service.asmx?op=UploadFile?WSDL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garbage);

        btn_barcode1 = (Button) findViewById(R.id.btn_barcode1);
        btn_barcode2 = (Button) findViewById(R.id.btn_barcode2);
        btn_barcode3 = (Button) findViewById(R.id.btn_barcode3);
        btn_picture = (Button) findViewById(R.id.btn_picture);
        btn_sent = (Button) findViewById(R.id.btn_sent);
        tv_barcode1 = (TextView) findViewById(R.id.tv_barcode1);
        tv_barcode2 = (TextView) findViewById(R.id.tv_barcode2);
        tv_barcode3 = (EditText) findViewById(R.id.tv_barcode3);
        imageView = (ImageView) findViewById(R.id.imageView);
        tv_recv = (TextView) findViewById(R.id.tv_recv);
        SharePreferenceMgr.putString(NewPicActivity.this, "barcode1", "");
        SharePreferenceMgr.putString(NewPicActivity.this, "barcode2", "");
        SharePreferenceMgr.putString(NewPicActivity.this, "barcode3", "");
        tv_recv.setMovementMethod(ScrollingMovementMethod.getInstance());

        PhoneID = SharePreferenceMgr.getString(NewPicActivity.this, "mac", "");
        Location = SharePreferenceMgr.getString(NewPicActivity.this, "Location", "");

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
                intent.setClass(NewPicActivity.this, BarcodeScanActivity.class);
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
                intent.setClass(NewPicActivity.this, BarcodeScanActivity.class);
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
                intent.setClass(NewPicActivity.this, BarcodeScanActivity.class);
                intent.putExtra("barcode", barcode);//此方式可以放所有基本型別
                //開啟Activity
                startActivity(intent);
            }
        });

        //拍照
        btn_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //拍照
                tackPicture();
            }
        });

        //提交new
        btn_sent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //取得時間
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                BelongDate = sdf.format(new Date());

                WebService_UploadFile webService_uploadFile = new WebService_UploadFile();
                webService_uploadFile.execute();

            }


        });
    }

    public class WebService_UploadFile extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String wsOutput = "";
                wsOutput = UploadFile.UploadFile(BelongDate, barcode1, barcode2, uriMyImage.toString().substring(uriMyImage.toString().lastIndexOf("/") + 1), bytes);

                return wsOutput;
            } catch (Exception e) {
                Log.e("Error", e.toString());

            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String result) {
            tv_recv.append(result);

        }
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
        barcode1 = SharePreferenceMgr.getString(NewPicActivity.this, "barcode1", "");
        barcode2 = SharePreferenceMgr.getString(NewPicActivity.this, "barcode2", "");
        barcode3 = SharePreferenceMgr.getString(NewPicActivity.this, "barcode3", "");
        tv_barcode1.setText(barcode1);
        tv_barcode2.setText(barcode2);
        tv_barcode3.setText(barcode3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tv_recv.setText("");
    }

    /**
     * 照相功能
     */
    public void tackPicture() {
        try {
            //新建一个File，传入文件夹目录
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Camera");
            //判断文件夹是否存在，如果不存在就创建，否则不创建
            if (!file.exists()) {
                //通过file的mkdirs()方法创建<span style="color:#FF0000;">目录中包含却不存在</span>的文件夹
                file.mkdirs();
            }
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd_HHmmss");
            Date curDate = new Date(System.currentTimeMillis()); // 獲取當前時間
            String str = formatter.format(curDate);

            //設置圖片檔案名稱
            addFileName = formatter.format(curDate) + ".png";
            String strImage = file + "/" + addFileName;
            File myImage = new File(strImage);

            File imageFile = null;
            imageFile = File.createTempFile(str, ".png", file);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uriMyImage = FileProvider.getUriForFile(this, "jialing.tcp.fileprovider", imageFile);
            } else {
                uriMyImage = Uri.fromFile(myImage);
            }

            //判斷系統是否有相機
            if (cameraIsCanUse()) {
                //有相機
                //intent 到照相機
                Intent intent;
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriMyImage);
                startActivityForResult(intent, 1);
            } else {
                //沒有相機
                showToast("沒有照相功能", Toast.LENGTH_LONG);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 照相功能回傳值
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    bitmap = decodeUri(uriMyImage, 400);
                    imageView.setImageBitmap(bitmap);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    //读取图片到ByteArrayOutputStream
                    bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
                    bytes = baos.toByteArray();
                    imageString = Base64.encodeToString(bytes, Base64.DEFAULT);
                    //圖片大小
                    imageSize = imageString.length();
                    Log.e("ANDROID_LAB", "imageSize =" + imageSize);
                    System.out.println("imageString--->" + imageString);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 判斷手機是否有相機功能
     * 返回 true 表示可以使用/ false 表示不可以使用相機
     */
    public boolean cameraIsCanUse() {
        boolean isCanUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters(); //针对魅族手机
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            isCanUse = false;
        }

        if (mCamera != null) {
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
                return isCanUse;
            }
        }
        return isCanUse;
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

    //**************************************************************************************
    // 照片解析
    //COnvert and resize our image to 400dp for faster uploading our images to DB
    protected Bitmap decodeUri(Uri selectedImage, int REQUIRED_SIZE) {

        try {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

            // The new size we want to scale to
            // final int REQUIRED_SIZE =  size;

            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE) {
                    break;
                }
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //**************************************************************************************
}