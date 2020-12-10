/**
 * @Author NINE. LIU
 * @Date 2020/12/2 12:24
 * @Version 1.0
 */
public class Token {

    private int typeNum;
    private String key;
    private String value;


    public Token(int typeNum, String key, String value) {
        this.typeNum = typeNum;
        this.key = key;
        this.value = value;
    }

    public Token() { }

    public int getTypeNum() {
        return typeNum;
    }

    public void setTypeNum(int typeNum) {
        this.typeNum = typeNum;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
