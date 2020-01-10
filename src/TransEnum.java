/**
 * @author xiongqimeng
 * @version 1.0
 * @date 2020/1/10 17:09
 */
public enum TransEnum {

    RECEIVE_FILE_NAME("RECEIVE_FILE_NAME", "接收传输文件名称"),
    SEND_OK("SEND_OK", "发送OK");

    TransEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private String type;
    private String desc;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
