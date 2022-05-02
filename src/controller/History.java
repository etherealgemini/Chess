package controller;

import model.*;
import view.*;

import java.util.ArrayList;

import static controller.task3function.createCopy;
import static controller.ClickController.*;

public class History {



    /**
     * 该类实现历史记录，实现方式如下：
     *
     * 将单次操作的内容存入Arraylist中，以便读取。由此也决定了读取顺序，即存入顺序为0->c 那么读取就由c->0。
     *
     * 历史记录：记录下每次行棋的操作
     * 悔棋：
     * 将上一步的destination位置和source位置进行swap（即挪回原位）
     * 若上一步有棋子死亡，将死亡棋子按照原属性置回原位。（不一是destination位置！如 吃过路兵）。
     * 交换行棋方（例：下完该步棋后 白->黑，白方悔棋，黑->白）
     *
     * 因此历史记录单项需要记录下：destination与source，被移动的棋子的属性（ChessComponent），死亡棋子属性（null或ChessComponent实例），该次移动的行棋方
     *
     * 由于需要记录行棋方，我们选择在每次行棋操作后，录入该次行棋信息。即在ClickController中进行历史记录录入。
     *
     */

    private static History thisMove;//该次行棋的信息
    private ChessColor chessColor;//该次操作的行棋方
    private ChessComponent chess0;
    private ChessComponent deadChess;
    private int destX;
    private int destY;
    private int srcX;
    private int srcY;


    public History(ChessColor chessColor,ChessComponent chess0, ChessComponent deadChess, int destX, int destY, int srcX, int srcY){
        this.chessColor=chessColor;
        this.chess0=chess0;
        this.deadChess=deadChess;
        this.destX=destX;
        this.destY=destY;
        this.srcX=srcX;
        this.srcY=srcY;
    }

    /**
     * 该方法实现history的录入
     * @see History
     * @param chessboard 棋盘
     * @param history 历史记录列表
     * @param first 被选中的棋子
     * @param target 目标位置棋子（包括空棋） notnull
     * @param bypassEaten 吃过路兵检测
     */
    public static void inputHistory(Chessboard chessboard, ArrayList<History> history, ChessComponent first, ChessComponent target, ChessComponent bypassEaten){
        //死亡棋子记录
        ChessComponent deadChess1 = null;

        if(!(target instanceof EmptySlotComponent)){
            deadChess1 = createCopy(chessboard,target);
        }
        if(target instanceof EmptySlotComponent){
            deadChess1 = createCopy(chessboard,target);//死亡了一个空棋子，位置为该次移动的终点
            //检查若为吃过路兵的情况
            if(bypassEaten!=null){
                deadChess1 = createCopy(chessboard, bypassEaten);
            }

        }

        ChessComponent thischess = createCopy(chessboard,first);
        thisMove = new History(chessboard.getCurrentColor(),thischess,deadChess1,
                deadChess1.getChessboardPoint().getX(),deadChess1.getChessboardPoint().getY(),
                thischess.getChessboardPoint().getX(),thischess.getChessboardPoint().getY());
        history.add(thisMove);
        historyCnt++;
    }


    /**
     * 为避免重名
     */

    public ChessColor getHistoryChessColor() {
        return chessColor;
    }

    public ChessComponent getChess0() {
        return chess0;
    }

    public ChessComponent getDeadChess() {
        return deadChess;
    }

    public int getDestX() {
        return destX;
    }

    public int getDestY() {
        return destY;
    }

    public int getSrcX() {
        return srcX;
    }

    public int getSrcY() {
        return srcY;
    }
}
