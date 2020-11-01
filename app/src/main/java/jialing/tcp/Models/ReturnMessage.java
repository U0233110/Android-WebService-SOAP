package jialing.tcp.Models;

/**
 * Created by name on 2019/3/18.
 */

public class ReturnMessage {

    private String Status;
    private String Msg;
    private String Weight;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }
}
