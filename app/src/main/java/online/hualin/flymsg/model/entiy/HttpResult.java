package online.hualin.flymsg.model.entiy;

/**
 * Description：TODO
 * Create Time：2016/10/620:32
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */
public class HttpResult<T> {

    private int resultCode;
    private String resultText;

    private T resultData;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultText() {
        return resultText;
    }

    public void setResultText(String resultText) {
        this.resultText = resultText;
    }

    public T getResultData() {
        return resultData;
    }

    public void setResultData(T resultData) {
        this.resultData = resultData;
    }

}
