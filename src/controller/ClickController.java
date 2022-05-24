package controller;

import model.*;
import view.ChessGameFrame;
import view.Chessboard;
import view.ChessboardPoint;


import javax.sound.sampled.SourceDataLine;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static controller.History.inputHistory;
import static controller.task3function.*;


public class ClickController {

    public static void setHistoryCnt(int historyCnt) {
        ClickController.historyCnt = historyCnt;
    }

    /**
     * int 历史记录指针
     */
    static int historyCnt;


    public void setChessGameFrame(ChessGameFrame chessGameFrame) {
        this.chessGameFrame = chessGameFrame;
    }

    /**
     * Arraylist 历史记录列表
     */

   private ChessGameFrame chessGameFrame;
    private ArrayList<History> history = new ArrayList<>();

    /**
     * 是否调用了AI
     */
    private boolean isRandomAI = false;

    /**
     * AI行棋方
     */
    private ChessColor AIcolor = ChessColor.WHITE;



    /**
     * 棋局结束判定
     */
    private boolean gameOver = false;

    private final Chessboard chessboard;

    private ChessComponent first;

    public ChessGameFrame getChessGameFrame() {
        return chessGameFrame;
    }

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    /**
     * 该方法实现响应针对棋盘上发生的鼠标点击事件
     * @param chessComponent
     */
    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                System.out.println("evoke handle first");
                chessComponent.setSelected(true);
                first = chessComponent;
                //TODO:若要实现点击显示合法落子，请在这里实现。
                first.repaint();
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)&&!gameOver) {
                System.out.println(chessboard.getCurrentColor());
//加入setText方法
                if (chessboard.getCurrentColor()==ChessColor.BLACK){
                chessGameFrame.getStatusLabel().setText("White");}
                else {
                    chessGameFrame.getStatusLabel().setText("Black");
                }



                //这里的chessComponent表示目标位置的棋子（包括空白）及其所有属性，first是被移动(被红圈选中的)棋子。

                //一旦通过handleSecond检测，即表示该次行棋合法，且将被执行
                //这里为demo唯一进行吃棋行为的地方，请尽可能不要在其他地方实现吃子行为。
                //demo实现逻辑是：if(该次走棋合法): then(swapChessComponents)



                //吃过路兵检测
                ChessComponent bypassEaten = null;
                if(first instanceof PawnChessComponent){
//                    System.out.println(((PawnChessComponent) first).isBypass());
                    if(((PawnChessComponent) first).isBypass()){
                        bypassEaten = createCopy(chessboard,byPassOperation(this,chessboard,first,chessComponent));
                    }
                }

                //录入历史记录
                ChessComponent firstCopy = createCopy(chessboard,first);//first为未移动前的棋子！
                ChessComponent chessComponentCopy = createCopy(chessboard,chessComponent);
                inputHistory(chessboard,history,firstCopy,chessComponentCopy,bypassEaten);bypassEaten = null;

                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);

                // 在这里执行将军相关的操作合法性检测：若移动后被将军，则该次移动非法，执行悔棋操作回退。
                boolean isCheckAfterMove = isCheck(chessboard,chessboard.getCurrentColor());
                if(isCheckAfterMove){
                    System.out.println("You will lose if move here!");
                    undo(chessboard,history);
                }

                ChessColor enemy1Color = chessboard.getCurrentColor()==ChessColor.BLACK?ChessColor.WHITE:ChessColor.BLACK;
                boolean EnemyisCheckAfterMove = isCheck(chessboard,enemy1Color);
                if(EnemyisCheckAfterMove){
                    JOptionPane.showMessageDialog(chessboard,enemy1Color+" Check!");
                }

                //change side 更换行棋方
                chessboard.swapColor();

                // 在这里执行将死判定。注意此时已经更换行棋方了
                ChessColor enemyColor = chessboard.getCurrentColor();

                if(isCheckMate(chessboard,enemyColor)){
                    System.out.println("evoke here!");
                    gameOver=true;
                    JOptionPane.showMessageDialog(chessboard,enemyColor+"负");
                }

                //reset the first (selected chess)
                first.setSelected(false);
                first = null;

                //判定ai行棋时机
                if(isRandomAI&&AIcolor==chessboard.getCurrentColor()){
                    randomAI(chessboard,AIcolor);
                }



            }
        }
    }

    /**
     * @param chessComponent 目标选取的棋子
     * @return 目标选取的棋子是否与棋盘记录的当前行棋方颜色相同
     */

    private boolean handleFirst(ChessComponent chessComponent) {
        return chessComponent.getChessColor() == chessboard.getCurrentColor();
    }

    /**
     * @param chessComponent first棋子目标移动到的棋子second
     * @return first棋子是否能够移动到second棋子位置
     */

    private boolean handleSecond(ChessComponent chessComponent) {
        //判定(吃子对象是否为对方棋子&&是否为合法移动)，返回值为Boolean值
        return chessComponent.getChessColor() != chessboard.getCurrentColor() &&
                first.canMoveTo(chessboard.getChessComponents(), chessComponent.getChessboardPoint());
    }

    /**
     * 随机行棋AI，你需要在这个方法内完成历史记录的录入。
     */
    public void randomAI(Chessboard chessboard,ChessColor AIcolor){
        isRandomAI=true;
        if(chessboard.getCurrentColor()!=AIcolor){
            return;
        }else{
            ChessComponent[][] temp1 = chessboard.getChessComponents();
            for (int i = 0; i < chessboard.getCHESSBOARD_SIZE(); i++) {
                for (int j = 0; j < chessboard.getCHESSBOARD_SIZE(); j++) {
                    if(temp1[i][j].getChessColor()==AIcolor){
                        ChessComponent chess1 = temp1[i][j];
                        for (int k = 0; k < chessboard.getCHESSBOARD_SIZE(); k++) {
                            for (int l = 0; l < chessboard.getCHESSBOARD_SIZE(); l++) {
                                ChessboardPoint destination = new ChessboardPoint(k,l);
                                if(chess1.canMoveTo(temp1,destination)){
                                    ChessComponent chess2 =temp1[k][l];
                                    inputHistory(chessboard,history,chess1,chess2,null);
                                    chessboard.swapChessComponents(chess1,chess2);
                                    chessboard.swapColor();
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public void setRandomAI(boolean randomAI) {
        isRandomAI = randomAI;
    }

    public boolean isRandomAI() {
        return isRandomAI;
    }

    public void setAIcolor(ChessColor AIcolor) {
        this.AIcolor = AIcolor;
    }

    public ChessColor getAIcolor() {
        return AIcolor;
    }

    public static int getHistoryCnt() {
        return historyCnt;
    }

    public ArrayList<History> getHistory() {
        return history;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}
