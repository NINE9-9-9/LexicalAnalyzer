import java.io.*;
import java.util.ArrayList;

/**
 * @Author NINE. LIU
 * @Date 2020/12/1 16:59
 * @Version 1.0
 */

public class LexicalAnalyzer {

    private ArrayList<Token> tokens = new ArrayList<>();
    private ArrayList<Line> lines = new ArrayList<>();
    private String[] keyword = {"abstract","boolean","break","byte","catch","char","class","continue","default","do"
            ,"double","else","extends","false","final","float","for","if","implements","import","instanceof","int"
            ,"interface","long","native","new","null","package","private","protected","public","short","static","super"
            ,"switch","synchronized","this","throw","throws","transient","try","true","void","volatile","while"}; // the keyword of JAVA
    public String[] separator ={"(",")","[","]","{","}",";","'","\"","#",",","."}; //the separator
    public String[] operator = {"+","-","*","/","%","=","==","&","&&","|","||","!","!=","<",">","<=",">="}; // the operator



    private boolean isAnnotation = false; //detect if annotation mode

    public LexicalAnalyzer(){}
    
    public int isSeparator(String string) //return the type number of separator
    {
        for (int i=0;i< separator.length;i++)
        {
            if (equals(separator[i],string))
                return keyword.length+i+3;
        }
        return -1;
    }
    
    public int isOperator(String string) // return the type num of operator
    {
        for (int i=0;i< operator.length;i++)
        {
            if (equals(operator[i],string))
                return keyword.length+separator.length+i+3;
        }
        return -1;
    }

    public int isKeyword(String string) //return the keyword num
    {
        for (int i=0;i< keyword.length;i++)
        {
            if (equals(keyword[i],string))
                return i+1;
        }
        return -1;
    }

    public boolean isLetter(char c) //judge if letter
    {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public boolean isDigit(char c) // judge if digit
    {
        return (c>='0'&& c<='9');
    }

    public boolean hasNext(int index,char[] chars)//judge  has next char in the line
    {
        return index+1<chars.length;
    }

    public boolean equals(String s1, String s2) // judge the strings if equal
    {
        if(s1==s2)
            return true;

        if(s1.length()==s2.length())
        {
            char[] chars1 = s1.toCharArray();
            char[] chars2 = s2.toCharArray();
            int i = 0;
            while (i<s1.length())
            {
                if(chars1[i]!=chars2[i])
                {
                    return false;
                }
                i++;
            }
            return true;
        }
        return false;


    }


    public void analyze(char[] chars) //analyze the text
    {
        Line line = new Line();
        if(!isAnnotation) //judge if annotation mode
        {
            StringBuilder string = new StringBuilder();
            for(int i=0;i<chars.length;i++) //circulation the line token
            {
                char c= chars[i];
                Token token = new Token();

                if(c == '\t'||c == '\n'||c == '\r'||c==' ')
                {
                    continue;
                }
                else if(isLetter(c)) //judge if letter
                {
                    while((isLetter(c)||isDigit(c)))
                    {
                        c = chars[i];
                        string.append(c);
                        if(hasNext(i,chars)&&(isLetter(chars[i+1])||isDigit(chars[i+1])))//judge if has next and next is letter or digit
                        {
                            i++;
                        }
                        else
                        {
                            break;
                        }

                    }

                    if(isKeyword(string.toString())!=-1) //judge if keyword
                    {
                        token.setValue(string.toString());
                        token.setTypeNum(isKeyword(string.toString()));
                        token.setKey(string.toString().toUpperCase()+"_TOKEN");
                    }
                    else//not keyword is id
                    {
                        token.setValue(string.toString());
                        token.setTypeNum(keyword.length+1);
                        token.setKey("IDENTIFIER");
                    }
                    tokens.add(token);
                    line.getLine().add(token);
                    string.delete(0,string.length()); //clear the string
                }
                else  if(isDigit(c))//judge if digit
                {
                    boolean error = false;
                    int num = 0; // number  of .
                    while (isDigit(c)||c=='.'||isLetter(c))
                    {
                        string.append(c);
                        if(hasNext(i,chars)&&(isDigit(chars[i+1])||(chars[i+1]=='.'))&&!error)//judge next is letter or .
                        {
                            i++;
                            c=chars[i];
                            if(c=='.')
                            {
                                num++;
                            }
                            if(num>1)
                                error=true;
                        }
                        else if(hasNext(i,chars)&&(isLetter(chars[i+1])||isDigit(chars[i+1])||(chars[i+1]=='.'))) //if has letter, error id
                        {                                                                                // if has more than 1 ., error num
                            i++;
                            c=chars[i];
                            error = true;
                        }
                        else
                            break;

                    }
                    token.setValue(string.toString());
                    if(!error)
                    {
                        token.setTypeNum(keyword.length+2);
                        token.setKey("NUMBER");
                    }
                    else
                    {
                        token.setTypeNum(-1);
                        token.setKey("ERROR");
                    }

                    tokens.add(token);
                    line.getLine().add(token);
                    string.delete(0,string.length());
                }
                else // other situation of operator or separator
                {
                    switch (c)
                    {
                        case '+':
                            token.setValue("+");
                            token.setTypeNum(isOperator("+"));
                            token.setKey("ADD");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '-':
                            token.setValue("-");
                            token.setTypeNum(isOperator("-"));
                            token.setKey("SUB");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '*':
                            token.setValue("*");
                            token.setTypeNum(isOperator("*"));
                            token.setKey("MUL");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '/':
                            if(hasNext(i,chars)&&chars[i+1]=='/')
                            {
                                token.setValue("//");
                                token.setTypeNum(keyword.length+operator.length+separator.length+1);
                                token.setKey("COMMENT");
                                tokens.add(token);
                                line.getLine().add(token);
                                lines.add(line);
                                return ;
                            }
                            else if(hasNext(i,chars)&&chars[i+1]=='*')
                            {
                                token.setValue("/*");
                                token.setTypeNum(keyword.length+operator.length+separator.length+1);
                                token.setKey("COMMENT");
                                tokens.add(token);
                                line.getLine().add(token);
                                lines.add(line);
                                isAnnotation=true;
                                return ;
                            }
                            else
                            {
                                token.setValue("/");
                                token.setTypeNum(isOperator("/"));
                                token.setKey("DIV");
                                tokens.add(token);
                                line.getLine().add(token);
                            }
                            break;
                        case '=':
                            if(hasNext(i,chars)&&chars[i+1]=='=')
                            {
                                i++;
                                token.setValue("==");
                                token.setTypeNum(isOperator("=="));
                                token.setKey("EQ");
                            }
                            else
                            {
                                token.setValue("=");
                                token.setTypeNum(isOperator("="));
                                token.setKey("AS");
                            }

                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '&':
                            if(hasNext(i,chars)&&chars[i+1]=='&')
                            {
                                i++;
                                token.setValue("&&");
                                token.setTypeNum(isOperator("&&"));
                            }
                            else
                            {
                                token.setValue("&");
                                token.setTypeNum(isOperator("&"));
                            }
                            token.setKey("AND");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '|':
                            if(hasNext(i,chars)&&chars[i+1]=='|')
                            {
                                i++;
                                token.setValue("||");
                                token.setTypeNum(isOperator("||"));
                            }
                            else
                            {
                                token.setValue("|");
                                token.setTypeNum(isOperator("|"));
                            }
                            token.setKey("OR");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '!':
                            if(hasNext(i,chars)&&chars[i+1]=='=')
                            {
                                i++;
                                token.setValue("!=");
                                token.setTypeNum(isOperator("!="));
                                token.setKey("NE");
                            }
                            else
                            {
                                token.setValue("!");
                                token.setTypeNum(isOperator("!"));
                                token.setKey("NOT");
                            }

                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '%':
                            token.setValue("%");
                            token.setTypeNum(isOperator("%"));
                            token.setKey("MOD");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '>':
                            if(hasNext(i,chars)&&chars[i+1]=='=')
                            {
                                i++;
                                token.setValue(">=");
                                token.setTypeNum(isOperator(">="));
                                token.setKey("GE");
                            }
                            else
                            {
                                token.setValue(">");
                                token.setTypeNum(isOperator(">"));
                                token.setKey("GT");
                            }
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '<':
                            if(hasNext(i,chars)&&chars[i+1]=='=')
                            {
                                i++;
                                token.setValue("<=");
                                token.setTypeNum(isOperator("<="));
                                token.setKey("LE");
                            }
                            else
                            {
                                token.setValue("<");
                                token.setTypeNum(isOperator("<"));
                                token.setKey("LT");
                            }
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '(':
                            token.setValue("(");
                            token.setTypeNum(isSeparator("("));
                            token.setKey("LPAR");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case ')':
                            token.setValue(")");
                            token.setTypeNum(isSeparator(")"));
                            token.setKey("RPAR");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '\"':
                            token.setValue("\"");
                            token.setTypeNum(isSeparator("\""));
                            token.setKey("DQUO");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '\'':
                            token.setValue("'");
                            token.setTypeNum(isSeparator("'"));
                            token.setKey("SQUO");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '[':
                            token.setValue("[");
                            token.setTypeNum(isSeparator("["));
                            token.setKey("LSB");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case ']':
                            token.setValue("]");
                            token.setTypeNum(isSeparator("]"));
                            token.setKey("RSB");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '{':
                            token.setValue("{");
                            token.setTypeNum(isSeparator("{"));
                            token.setKey("LCB");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '}':
                            token.setValue("}");
                            token.setTypeNum(isSeparator("}"));
                            token.setKey("RCB");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case ';':
                            token.setValue(";");
                            token.setTypeNum(isSeparator(";"));
                            token.setKey("SEM");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case ',':
                            token.setValue(",");
                            token.setTypeNum(isSeparator(","));
                            token.setKey("COM");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '#':
                            token.setValue("#");
                            token.setTypeNum(isSeparator("#"));
                            token.setKey("END");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        case '.':
                            token.setValue(".");
                            token.setTypeNum(isSeparator("."));
                            token.setKey("DOT");
                            tokens.add(token);
                            line.getLine().add(token);
                            break;
                        default:
                            token.setValue(String.valueOf(c));
                            token.setKey("ERROR");
                            token.setTypeNum(-1);
                            tokens.add(token);
                            line.getLine().add(token);
                            break;

                    }
                }
            }
        }
        else // in the annotation mode detect if quit the mode
        {
            Token token = new Token(keyword.length+separator.length+operator.length+1,"COMMENT","*");
            for(int i=0;i<chars.length;i++)
            {
                char c= chars[i];
                if(c == '*')
                {
                    if(hasNext(i,chars)) {
                        i++;
                        c = chars[i];
                        if(c=='/')
                        {
                            isAnnotation=false;
                            token.setValue("*/");
                        }
                        break;
                    }

                }
                else if(c==' ') {
                }
                else
                {
                    break;
                }

            }
            tokens.add(token);
            line.getLine().add(token);

        }
        lines.add(line);


    }

    /**
    *read the txt file to analyze
     */
    public void readFile()
    {
        File file = new File("input.txt");
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String  s = "";
            while( (s = bufferedReader.readLine())!=null)
            {
               analyze(s.toCharArray());
            }
            bufferedReader.close();
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Token w : tokens)
        {
            System.out.println(w.getValue()+"\t"+w.getTypeNum()+"\t"+w.getKey());
        }

        for(Line line :lines)
        {
            for(Token w : line.getLine())
            {
                System.out.print(w.getValue()+"\t");
            }
            System.out.print("\n");
        }
    }

    /**
     *save the result
     */
    public void saveFile()
    {
        File file = new File("output.txt");
        try {
            FileWriter fileWriter = new FileWriter(file);
            for(Token w: tokens)
            {
                fileWriter.write(w.getValue()+"\t"+w.getTypeNum()+"\t"+w.getKey()+"\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String analyzeVar()
    {
        Type type = Type.DEFAULT;
        StringBuilder s = new StringBuilder();
        for(int row=0;row<lines.size();row++)
        {
            Line line = lines.get(row);
            int num = 0;
            boolean error = false;
            boolean error2 =false;
            boolean error3 = false;
            boolean error4 = false;
            boolean error5 = false;
            for(Token token:line.getLine())
            {
                switch (token.getValue())
                {
                    case "int":
                        type=Type.INT;
                        if((line.getLine().size()<=num+1)||!line.getLine().get(num+1).getKey().equals("IDENTIFIER"))
                            error=true;
                        if((line.getLine().size()<=num+2)||(!line.getLine().get(num+2).getValue().equals("=")
                                &&!line.getLine().get(num+2).getValue().equals(";")))
                            error2=true;
                        else if(line.getLine().get(num+2).getValue().equals("="))
                        {
                            if((line.getLine().size()<=num+3)||!line.getLine().get(num+3).getKey().equals("NUMBER"))
                                error3=true;
                            else if((line.getLine().size()>num+3)&&line.getLine().get(num+3).getValue().contains("."))
                            {
                                error4=true;
                            }
                            if((line.getLine().size()<=num+4)||!line.getLine().get(num+4).getValue().equals(";"))
                                error5 = true;
                        }

                        break;


                    case "float":
                        type=Type.FLOUT;
                        if((line.getLine().size()<=num+1)||!line.getLine().get(num+1).getKey().equals("IDENTIFIER"))
                            error=true;
                        if((line.getLine().size()<=num+2)||(!line.getLine().get(num+2).getValue().equals("=")
                                &&!line.getLine().get(num+2).getValue().equals(";")))
                            error2=true;
                        else if(line.getLine().get(num+2).getValue().equals("="))
                        {
                            if((line.getLine().size()<=num+3)||!line.getLine().get(num+3).getKey().equals("NUMBER"))
                                error3=true;
                            if((line.getLine().size()<=num+4)||!line.getLine().get(num+4).getValue().equals(";"))
                                error5 = true;
                        }
                        break;
                    case "double":
                        type=Type.DOUBLE;
                        if((line.getLine().size()<=num+1)||!line.getLine().get(num+1).getKey().equals("IDENTIFIER"))
                            error=true;
                        if((line.getLine().size()<=num+2)||(!line.getLine().get(num+2).getValue().equals("=")
                                &&!line.getLine().get(num+2).getValue().equals(";")))
                            error2=true;
                        else if(line.getLine().get(num+2).getValue().equals("="))
                        {
                            if((line.getLine().size()<=num+3)||!line.getLine().get(num+3).getKey().equals("NUMBER"))
                                error3=true;
                            if((line.getLine().size()<=num+4)||!line.getLine().get(num+4).getValue().equals(";"))
                                error5 = true;
                        }
                        break;
                    case "char":
                        type=Type.CHAR;
                        if((line.getLine().size()<=num+1)||!line.getLine().get(num+1).getKey().equals("IDENTIFIER"))
                            error=true;
                        if((line.getLine().size()<=num+2)||(!line.getLine().get(num+2).getValue().equals("=")
                                &&!line.getLine().get(num+2).getValue().equals(";")))
                            error2=true;
                        else if(line.getLine().get(num+2).getValue().equals("="))
                        {
                            if((line.getLine().size()<=num+3)||!line.getLine().get(num+3).getValue().equals("'"))
                                error3=true;
                            if((line.getLine().size()<=num+4)||!line.getLine().get(num+4).getKey().equals("IDENTIFIER"))
                                error4 = true;
                            if((line.getLine().size()<=num+5)||!line.getLine().get(num+5).getValue().equals("'"))
                            {
                                error3=true;
                            }
                            if((line.getLine().size()<=num+6)||!line.getLine().get(num+6).getValue().equals(";"))
                            {
                                error5=true;
                            }
                        }
                        break;
                    default:
                        break;
                }
                num++;
            }
            if(type!=Type.DEFAULT&&error)
            {
                s.append(type).append(" ERROR in line ").append(row + 1).append(": ");
                System.out.print(type+" ERROR in line "+(row+1)+": ");
                for(Token token:line.getLine())
                {
                    System.out.print(token.getValue()+" ");
                    s.append(token.getValue()).append(" ");
                }
                System.out.print("\n");
                s.append("\n");
            }
            else if(type!=Type.DEFAULT&&error2)
            {
                System.out.print(type+" ERROR in line "+(row+1)+": ");
                s.append(type).append(" ERROR in line ").append(row + 1).append(": ");
                for(Token token:line.getLine())
                {
                    System.out.print(token.getValue()+" ");
                    s.append(token.getValue()).append(" ");
                }
                System.out.print("\n");
                s.append("\n");
            }
            else if(type!=Type.DEFAULT&&error3)
            {
                s.append(type).append(" ERROR in line ").append(row + 1).append(": ");
                System.out.print(type+" ERROR in line "+(row+1)+": ");
                for(Token token:line.getLine())
                {
                    System.out.print(token.getValue()+" ");
                    s.append(token.getValue()).append(" ");
                }
                System.out.print("\n");
                s.append("\n");
            }
            else if(type!=Type.DEFAULT&&error4)
            {
                System.out.print(type+" ERROR in line "+(row+1)+": ");
                s.append(type).append(" ERROR in line ").append(row + 1).append(": ");
                for(Token token:line.getLine())
                {
                    System.out.print(token.getValue()+" ");
                    s.append(token.getValue()).append(" ");
                }
                System.out.print("\n");
                s.append("\n");
            }
            else if(type!=Type.DEFAULT&&error5)
            {
                s.append(type).append(" ERROR in line ").append(row + 1).append(": ");
                System.out.print(type+" ERROR in line "+(row+1)+": ");
                for(Token token:line.getLine())
                {
                    System.out.print(token.getValue()+" ");
                    s.append(token.getValue()).append(" ");
                }
                System.out.print("\n");
                s.append("\n");
            }
            type=Type.DEFAULT;
        }
        return s.toString();
    }
    public ArrayList<Line> getLines()
    {
        return lines;
    }

    /**
    * get the tokens for interface
     */
    public ArrayList<Token> getTokens()
    {
        return tokens;
    }

    public static void main(String[] arg)
    {
        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();
        lexicalAnalyzer.readFile();
        lexicalAnalyzer.saveFile();
        lexicalAnalyzer.analyzeVar();
    }

}
