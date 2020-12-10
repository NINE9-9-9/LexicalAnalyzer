import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;


/**
 * @Author NINE. LIU
 * @Date 2020/12/8 14:31
 * @Version 1.0
 */

/*
 *the user interface of LA
 */
public class UI extends JFrame {


    private JPanel panel1 = new JPanel();
    private JTextArea textArea= new JTextArea(20,35);
    private JTextArea textArea2= new JTextArea(20,35);
    private JScrollPane scrollPane1 = new JScrollPane(textArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JScrollPane scrollPane2 = new JScrollPane(textArea2,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JMenuBar menuBar = new JMenuBar();
    private JMenu file = new JMenu("File");
    private JMenu run = new JMenu("Run");
    private JMenuItem open= new JMenuItem ("Open");
    private JMenuItem execute= new JMenuItem ("Execute");
    private JFileChooser jFileChooser = new JFileChooser();
    private Font font = new Font("consoles", Font.PLAIN,20);
    private LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();



    /*
    init the frame
     */

    public UI()
    {
        execute.addActionListener(new ExecuteListener());
        open.addActionListener(new OpenListener());
        file.add(open);
        run.add(execute);
        menuBar.add(file);
        menuBar.add(run);

        //textArea.setLineWrap(true);
        textArea2.setLineWrap(true);
        textArea.setFont(font);
        textArea2.setFont(font);
        jFileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        textArea.setEditable(false);
        textArea2.setEditable(false);
        textArea.setBorder(BorderFactory.createTitledBorder("Current read file"));
        textArea2.setBorder(BorderFactory.createTitledBorder("Lexical analysis results"));
        panel1.add(scrollPane1,BorderLayout.WEST);
        panel1.add(scrollPane2,BorderLayout.EAST);

        panel1.setBackground(Color.WHITE);
        this.setTitle("Lexical Analyzer");//set the title of frame
        this.setSize(1200,615);//set size of frame
        this.setLocationRelativeTo(null);// frame center
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//close mode of frame
        this.setContentPane(panel1);
        this.setJMenuBar(menuBar);//set menu of frame
        this.setVisible(true);// set visible
    }

    public static void main(String[] arg)
    {
        UI ui = new UI();
    }

    /**
     * Open Listener
     */
    private class OpenListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {

            int status=jFileChooser.showOpenDialog(panel1);
            if(status!=JFileChooser.APPROVE_OPTION)//confirm open the file
            {
                JOptionPane.showConfirmDialog(null,"Unchecked file","Tips", JOptionPane.DEFAULT_OPTION);
            }
            else
            {
                File file=jFileChooser.getSelectedFile();
                try {
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String  s="";
                    if(!textArea.getText().equals("")) // clear the text area
                    {
                        textArea.setText("");
                        textArea2.setText("");
                        lexicalAnalyzer.getTokens().clear();
                        lexicalAnalyzer.getLines().clear();
                    }
                    while( (s = bufferedReader.readLine())!=null)
                    {
                       textArea.append(s+"\n");
                       lexicalAnalyzer.analyze(s.toCharArray());
                    }


                    bufferedReader.close();
                    fileReader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    /**
     * Execute Listener
     */
    private class ExecuteListener implements ActionListener
    {

        @Override
        public void actionPerformed(ActionEvent e) {
            if(lexicalAnalyzer.getTokens().size()!=0) //detect the tokens is not empty
            {

                for(Token w: lexicalAnalyzer.getTokens())
                {
                    textArea2.append(w.getValue()+"\t"+w.getTypeNum()+"\t"+w.getKey()+"\n"); //show the results
                }
                textArea2.append(lexicalAnalyzer.analyzeVar());

            }
            else
            {
                JOptionPane.showConfirmDialog(null,"File not open","Tips", JOptionPane.DEFAULT_OPTION);
            }
        }
    }

}
