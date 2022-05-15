package view;

import controller.ClickController;
import controller.GameController;
import controller.task3function;
import model.ChessColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * 这个类表示游戏过程中的整个游戏界面，是一切的载体
 * 该类将配合GameController与ClickController完成游戏的完整运行
 */
public class ChessGameFrame extends JFrame {   //JFrame用于生成一个窗体（Y）
    //    public final Dimension FRAME_SIZE ;
    private final int WIDTH;
    private final int HEIGTH;
    public final int CHESSBOARD_SIZE;
    private GameController gameController;

    public ChessGameFrame(int width, int height) {
        setTitle("2022 CS102A Project Demo"); //设置标题
        this.WIDTH = width;
        this.HEIGTH = height;
        this.CHESSBOARD_SIZE = HEIGTH * 4 / 5;

        setSize(WIDTH, HEIGTH); //设置窗体的大小（Y）
        setLocationRelativeTo(null); // Center the window.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //设置程序关闭按键，如果点击右上方的叉就游戏全部关闭了
        setLayout(null);


        addChessboard();
        addHelloButton();
        addLoadButton();
        addRestartGameButton();
        addUndoButton();
        addFileButton();
        addLabel();
        addMonkeyAIButton();


    }


    /**
     * 在游戏面板中添加棋盘
     */
    private void addChessboard() {
        Chessboard chessboard = new Chessboard(CHESSBOARD_SIZE, CHESSBOARD_SIZE);
        gameController = new GameController(chessboard);
        chessboard.setLocation(HEIGTH / 10, HEIGTH / 10);
        add(chessboard);
    }

    /**
     * 在游戏面板中添加标签
     */







    private void addLabel() { //（Y）窗口创建文本框
       JLabel statusLabel = new JLabel("Black");
        statusLabel.setLocation(HEIGTH, HEIGTH / 10);
        statusLabel.setSize(200, 60);
        statusLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(statusLabel);


    }

    /**
     * 在游戏面板中增加一个按钮，如果按下的话就会显示Hello, world!
     */

    private void addHelloButton() {
        JButton button = new JButton("Show Hello Here");
        button.addActionListener((e) -> {
            JOptionPane.showMessageDialog(this, "准备好开始一场新的对决了吗");

        // Y 在里面添加我鼠标点击该按钮之后要执行的命令语句


        });
        button.setLocation(HEIGTH, HEIGTH / 10 + 60);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);
    }

    private void addLoadButton() {
        JButton button = new JButton("Load");
        button.setLocation(HEIGTH, HEIGTH / 10 + 150);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click load");
            String path = JOptionPane.showInputDialog(this,"Input Path here");
            gameController.getChessboard().initiateEmptyChessboard();
            String feedback = gameController.loadGameFromFile(path);//在该方法中完成文件的读取
            JOptionPane.showMessageDialog(null,feedback);
            gameController.getChessboard().repaint();
        });
    }

    //Y 新加入的重新开始游戏的按钮
    private void addRestartGameButton() {
        JButton button = new JButton("Restart");
        button.setLocation(HEIGTH, HEIGTH / 10 + 250);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click restart");
            JOptionPane.showMessageDialog(this, "点击确认重开一局");
           // Y 写方法初始化游戏
            gameController.getChessboard().initiateEmptyChessboard();
            gameController.getChessboard().initiateAllChessComponents();
            gameController.getChessboard().repaint();



        });
    }

    private void addUndoButton(){
        // Z 悔棋按钮
        JButton button = new JButton("Undo");
        button.setLocation(HEIGTH, HEIGTH / 10 + 350);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click undo");
            task3function.undo(gameController.getChessboard(),gameController.getChessboard().getClickController().getHistory());

        });
    }





    private void addMonkeyAIButton(){
        // Z 选择是否交由AI（简单模式）行棋
        JButton button = new JButton("MonkeyAI");
        button.setLocation(20,15);
        button.setSize(150, 30);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Choose MonkeyAI");
            gameController.getChessboard().getClickController().setRandomAI(true);
//            Object[] options={"黑","白"};
//            int m = JOptionPane.showOptionDialog(this,"选择AI的行棋方","MonkeyAI",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.INFORMATION_MESSAGE,null,options,options[0]);
//            System.out.println(m);
//            switch (m){
//                case 0: gameController.getChessboard().getClickController().setAIcolor(ChessColor.BLACK);break;
//                case 1: gameController.getChessboard().getClickController().setAIcolor(ChessColor.WHITE);break;
//                default:break;
//            }
            // Z 避免出现AI行棋方与当前行棋方相同的异常。
            ChessColor aicolor = gameController.getChessboard().getClickController().getAIcolor();
            if(aicolor==gameController.getChessboard().getCurrentColor()){
                gameController.getChessboard().getClickController().setAIcolor(aicolor==ChessColor.BLACK?ChessColor.WHITE:ChessColor.BLACK);
            }
        });
    }












































































































































    private void addFileButton(){
        // Y 游戏存档按钮
        JButton button = new JButton("File");
        button.setLocation(HEIGTH, HEIGTH / 10 + 450);
        button.setSize(200, 60);
        button.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(button);

        button.addActionListener(e -> {
            System.out.println("Click file");
            String path = JOptionPane.showInputDialog(this,"Input Path here");
            gameController.storeGameToFile(path);
        });

    }




}
