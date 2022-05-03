package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;


import java.awt.*;
import java.util.ArrayList;

import static controller.History.inputHistory;
import static controller.task3function.*;


public class ClickController {

    /**
     * int 历史记录指针
     */
    static int historyCnt;
    /**
     * Arraylist 历史记录列表
     */
    private ArrayList<History> history = new ArrayList<>();


    private final Chessboard chessboard;

    private ChessComponent first;

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
//                    System.out.println(((PawnChessComponent) first).isBypass());
                    if(((PawnChessComponent) first).isBypass()){
                        bypassEaten = createCopy(chessboard,byPassOperation(this,chessboard,first,chessComponent));
                    }
                }

                //录入历史记录
                inputHistory(chessboard,history,first,chessComponent,bypassEaten);
                bypassEaten = null;

                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);

                // 在这里执行将军相关的操作合法性检测：若移动后被将军，则该次移动非法，执行悔棋操作回退。
                boolean isCheckAfterMove = isCheck(chessboard,chessboard.getCurrentColor());
                if(isCheckAfterMove){
                    undo(chessboard,history,first);
                }

                //change side 更换行棋方
                chessboard.swapColor();

                // 在这里执行将死判定。注意此时已经更换行棋方了
                ChessColor enemyColor = chessboard.getCurrentColor();
                if(isCheckMate(chessboard,enemyColor)){
                    //TODO:在这里发生将死后的事件
                    System.out.println(enemyColor+" is defeated!");
                }

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
