package controller;

import model.*;
import view.*;

import java.util.ArrayList;

import static controller.task3function.createCopy;
import static controller.ClickController.*;
/**
 * 该类实现历史记录，实现方式如下：<br>
 *
 * 将单次行棋的信息存入Arraylist中，以便读取。由此也决定了读取顺序，即存入顺序为0->c 那么读取就由c->0。<br>
 * 我们录入的单次操作的信息包含：目的地与起点，被移动的棋子的属性（ChessComponent），死亡棋子属性（null或ChessComponent实例），该次移动的行棋方
 * <br>
 *
 * 由于需要记录行棋方，我们选择在每次行棋操作后，录入该次行棋信息。即在ClickController中进行历史记录录入。
 *
 */
public class History {

    private static History thisMove;//该次行棋的信息
    private ChessColor chessColor;//该次操作的行棋方
    private ChessComponent chess0;
    private ChessComponent destChess;//目的地位置的棋子信息

    public ChessComponent getBypassChess() {
        return bypassChess;
    }

    private ChessComponent bypassChess;//过路兵信息

    private int destX;
    private int destY;
    private int srcX;
    private int srcY;


    public History(ChessColor chessColor, ChessComponent chess0, ChessComponent destChess,ChessComponent bypassChess, int destX, int destY, int srcX, int srcY){
        this.chessColor=chessColor;
        this.chess0=chess0;
        this.destChess = destChess;
        this.destX=destX;
        this.destY=destY;
        this.srcX=srcX;
        this.srcY=srcY;
        this.bypassChess=bypassChess;
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
        //chess0为传入的棋子，该棋子处于未移动的状态。
        if(!(target instanceof EmptySlotComponent)){
            deadChess1 = createCopy(chessboard,target);
        }
        if(target instanceof EmptySlotComponent){
            deadChess1 = createCopy(chessboard,target);//死亡了一个空棋子，位置为该次移动的终点
            //检查若为吃过路兵的情况


        }

        ChessComponent thischess = createCopy(chessboard,first);
        thisMove = new History(chessboard.getCurrentColor(),thischess,deadChess1,bypassEaten,
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

    public ChessComponent getDestChess() {
        return destChess;
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
