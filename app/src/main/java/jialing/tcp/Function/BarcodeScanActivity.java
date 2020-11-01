package jialing.tcp.Function;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import static android.content.ContentValues.TAG;

/**
 * Created by A0016065 on 2019/3/11.
 */

public class BarcodeScanActivity extends Activity implements ZBarScannerView.ResultHandler {

    private ZBarScannerView mScannerView;
    private String barcode = "";
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view

        Bundle bundle = getIntent().getExtras();
        barcode = bundle.getString("barcode");

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.v(TAG, rawResult.getContents()); // Prints scan results
        Log.v(TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)

        if(barcode.equals("barcode1")){
            SharePreferenceMgr.putString(BarcodeScanActivity.this, "barcode1", rawResult.getContents());
        }else if(barcode.equals("barcode2")){
            SharePreferenceMgr.putString(BarcodeScanActivity.this, "barcode2", rawResult.getContents());
        }else if(barcode.equals("barcode3")){
            SharePreferenceMgr.putString(BarcodeScanActivity.this, "barcode3", rawResult.getContents());
        }

        finish();
        //clear barcode
//        barcode = "";
//        //get barcode
//        barcode = rawResult.getContents();

        return;

    }
}
