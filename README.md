# Android WebService SOAP
## Android呼叫WebService
 
**Ksoap2-android**

Android呼叫WebService需要依賴於第三方套件ksoap2，它是一個SOAP Web service客戶端開發包，下載地址是：http://code.google.com/p/ksoap2-android/

**WebService介紹**

使用方法，定義以下內容
* NameSpace: http://tempuri.org/
* MethodName: UploadFile
* SoapAction: http://tempuri.org/UploadFile
* URL: http://XXX.XXX.XXX.XXX:XXXX/MwIMS/Service/Service.asmx?op=UploadFile

參數格式
* BelongDate，String.class
* INID，String.class
* BagType，String.class
* fileName，String.class
* buffer，MarshalBase64.BYTE_ARRAY_CLASS

![GITHUB](https://raw.githubusercontent.com/U0233110/Android-WebService-SOAP/main/Pic/WS.PNG)


**Android呼叫WebService範例**
 
 ```
 public class UploadFile {
    public static String serverIp ;

    public static String UploadFile(String BelongDate, String INID, String BagType, String fileName, byte[] buffer) {
        String NAMESPACE = "http://tempuri.org/";
        String METHOD_NAME = "UploadFile";
        String SOAP_ACTION = "http://tempuri.org/UploadFile";
        String URL = serverIp ;

        String rtnMSG = ""; //回傳結果
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo P1 = new PropertyInfo();
        PropertyInfo P2 = new PropertyInfo();
        PropertyInfo P3 = new PropertyInfo();
        PropertyInfo P4 = new PropertyInfo();
        PropertyInfo P5 = new PropertyInfo();

        // Set Name
        P1.setName("BelongDate");
        // Set Value
        P1.setValue(BelongDate);
        // Set dataType
        P1.setType(String.class);

        P2.setName("INID");
        P2.setValue(INID);
        P2.setType(String.class);

        P3.setName("BagType");
        P3.setValue(BagType);
        P3.setType(String.class);

        P4.setName("fileName");
        P4.setValue(fileName);
        P4.setType(String.class);

        P5.setName("buffer");
        P5.setValue(Base64.encodeToString(buffer, Base64.DEFAULT));
        P5.setType(MarshalBase64.BYTE_ARRAY_CLASS);

        // Add the property to request object
        request.addProperty(P1);
        request.addProperty(P2);
        request.addProperty(P3);
        request.addProperty(P4);
        request.addProperty(P5);

        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                SoapEnvelope.VER11);

        // 設定為 .net 預設編碼
        envelope.dotNet = true;

        // Set output SOAP object
        envelope.setOutputSoapObject(request);

        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Get the response
            SoapPrimitive response = (SoapPrimitive) envelope.getResponse();

            // Assign it to rtnMSG variable
            rtnMSG = response.toString();
        } catch (Exception e) {
            //Print error
            e.printStackTrace();
            //Assign error message to rtnMSG
            rtnMSG = e.toString();
        }
        return rtnMSG;
    }
}
```

```
btn_sent.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        WebService_UploadFile webService_uploadFile = new WebService_UploadFile();
        webService_uploadFile.execute();
    }
});
 ```
 ``` 
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
  ``` 
