package jialing.tcp.Models;

/**
 * Created by name on 2019/3/12.
 */

public class DateMessage {

    /**
     * Status：
     * Barcode1 :
     * Barcode2 :
     * Barcode3 :
     * ImageEncode : base64
     * ImageSize：
     * ImageType : image/jpeg
     * Location :
     * PhoneID : 02:00:00:00:00:00
     * Time : 20190312124005
     */

    private String Status;
    private String Barcode1;
    private String Barcode2;
    private String Barcode3;
    private String ImageEncode;
    private long ImageSize;
    private String ImageType;
    private String Location;
    private String PhoneID;
    private String Time;

    public DateMessage(String status, String barcode1, String barcode2, String barcode3, String imageEncode, long imageSize, String imageType, String location, String phoneID, String time) {
        Status = status;
        Barcode1 = barcode1;
        Barcode2 = barcode2;
        Barcode3 = barcode3;
        ImageEncode = imageEncode;
        ImageSize = imageSize;
        ImageType = imageType;
        Location = location;
        PhoneID = phoneID;
        Time = time;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getBarcode1() {
        return Barcode1;
    }

    public void setBarcode1(String barcode1) {
        Barcode1 = barcode1;
    }

    public String getBarcode2() {
        return Barcode2;
    }

    public void setBarcode2(String barcode2) {
        Barcode2 = barcode2;
    }

    public String getBarcode3() {
        return Barcode3;
    }

    public void setBarcode3(String barcode3) {
        Barcode3 = barcode3;
    }

    public String getImageEncode() {
        return ImageEncode;
    }

    public void setImageEncode(String imageEncode) {
        ImageEncode = imageEncode;
    }

    public long getImageSize() {
        return ImageSize;
    }

    public void setImageSize(long imageSize) {
        ImageSize = imageSize;
    }

    public String getImageType() {
        return ImageType;
    }

    public void setImageType(String imageType) {
        ImageType = imageType;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getPhoneID() {
        return PhoneID;
    }

    public void setPhoneID(String phoneID) {
        PhoneID = phoneID;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}