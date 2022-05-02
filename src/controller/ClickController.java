package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;


import java.awt.*;
import java.util.ArrayList;

import static controller.task3function.*;

public class ClickController {

    /**
     * 历史记录：
     * int 历史记录指针
     * Arraylist 历史记录列表，同时实现了回合数记录。
     */
    static int historyCnt;
    private ArrayList<History> history = new ArrayList<>();
    private static History thisMove;

    private final Chessboard chessboard;

    private ChessComponent first;

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    /**
     * @see History
     * @param chessboard 棋盘
     * @param history 历史记录列表
     * @param first 被选中的棋子
     * @param target 目标位置棋子（包括空棋） notnull
     * @param bypassEaten 吃过路兵检测
     */
    public static void inputHistory(Chessboard chessboard,ArrayList<History> history,ChessComponent first,ChessComponent target,ChessComponent bypassEaten){
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
     * 该方法实现响应针对棋盘上发生的鼠标点击事件
     * @param chessComponent
     */
    public void onClick(ChessComponent chessComponent) {
        if (first == null) {
            if (handleFirst(chessComponent)) {
                chessComponent.setSelected(true);
                first = chessComponent;
                first.repaint();
            }
        } else {
            if (first == chessComponent) { // 再次点击取消选取
                chessComponent.setSelected(false);
                ChessComponent recordFirst = first;
                first = null;
                recordFirst.repaint();
            } else if (handleSecond(chessComponent)) {
                //这里的chessComponent表示目标位置的棋子（包括空白）及其所有属性，first是被移动(被红圈选中的)棋子。

                //一旦通过handleSecond检测，即表示该次行棋合法，且将被执行
                //这里为demo唯一进行吃棋行为的地方，请尽可能不要在其他地方实现吃子行为。
                //demo实现逻辑是：if(该次走棋合法): then(swapChessComponents)



                //吃过路兵检测
                ChessComponent bypassEaten = null;
                if(first instanceof PawnChessComponent){
                    System.out.println(((PawnChessComponent) first).isBypass());
                    if(((PawnChessComponent) first).isBypass()){
                        bypassEaten = createCopy(chessboard,byPassOperation(this,chessboard,first,chessComponent));
                    }
                }

                //录入历史记录
                inputHistory(chessboard,history,first,chessComponent,bypassEaten);
                bypassEaten = null;

                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);

                //change side
                chessboard.swapColor();

                // 在这里执行检测：若移动后被将军，则该次移动非法
                // 第二个参数为取反，因为上面执行了一次换方
                boolean isCheckAfterMove = isCheck(chessboard,chessboard.getCurrentColor()==ChessColor.BLACK?ChessColor.WHITE:ChessColor.BLACK);
                if(isCheckAfterMove){
                    undo(chessboard,history,first);
                }

                boolean enemyIsCheckAfterMove = isCheck(chessboard,chessboard.getCurrentColor());
                if(enemyIsCheckAfterMove){
                    //TODO:请在这里进行将死判定
                }


//                if(! (history.get(historyCnt-1).getDeadChess() instanceof EmptySlotComponent)){
//                    //悔棋方法测试：若可行，在吃棋后行棋立即自动悔棋，程序中表现为进行吃棋后红圈自动消失，该次行棋无效。
//                    System.out.println((history.get(historyCnt-1).getDeadChess() instanceof PawnChessComponent));
//                    undo(chessboard,history,first,bypassEaten);
//                }

//                System.out.println((history.get(historyCnt-1).getDeadChess() instanceof PawnChessComponent));

                //reset the first (selected chess)
                first.setSelected(false);
                first = null;
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

    public static int getHistoryCnt() {
        return historyCnt;
    }

    public ArrayList<History> getHistory() {
        return history;
    }

}
