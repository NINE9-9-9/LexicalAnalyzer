import java.util.ArrayList;

/**
 * @Author NINE. LIU
 * @Date 2020/12/5 15:46
 * @Version 1.0
 */
public class Line {

    private ArrayList<Token> line = new ArrayList<>();

    public Line(){}

    public Line(ArrayList<Token> line)
    {
        this.line = line;
    }

    public ArrayList<Token> getLine() {
        return line;
    }

    public void setLine(ArrayList<Token> line) {
        this.line = line;
    }
}
