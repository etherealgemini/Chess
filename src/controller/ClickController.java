package controller;


import model.ChessComponent;
import model.EmptySlotComponent;
import model.PawnChessComponent;
import view.Chessboard;

import java.util.ArrayList;

public class ClickController {

    public static int getHistoryCnt() {
        return historyCnt;
    }

    public ArrayList<History> getHistory() {
        return history;
    }

    /**
     * 历史记录：
     * int 历史记录指针
     * Arraylist 历史记录列表，同时实现了回合数记录。
     */
    private static int historyCnt;
    private ArrayList<History> history = new ArrayList<>();
    private History thisMove;

    private final Chessboard chessboard;
    private ChessComponent first;

    /**
     * 吃过路兵检测实现：
     * 初始bypasscheck=true
     * 任意一方兵单次走两步时：bypasscheck=true
     * 此时：对方若走兵：执行检测:(对方上一次走兵且两步 且 有过路兵可吃)
     * 若对方走其他棋子：bypasscheck=false 此后不可吃该过路兵。
     */


    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

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
                //这里的chessComponent表示目标位置的棋子（包括空白）及其所有属性，first才是被移动棋子。

                //一旦通过handleSecond检测，即表示该次行棋合法，且将被执行
                //这里为demo唯一进行吃棋行为的地方，请尽可能不要在其他地方实现吃子行为。
                //demo实现逻辑是：if(该次走棋合法): then(swapChessComponents)

                //录入行棋记录
                ChessComponent deadChess = null;
                //死亡棋子记录
                if(!(chessComponent instanceof EmptySlotComponent)){
                    deadChess = chessComponent;
                }

                //TODO:判定是否为吃过路兵,且为首次遭遇
                //有了历史记录之后，判定是否为立即吃过路兵就非常简易了，只需要判定上一次操作是否为走兵且走两格即可。
                //在这里记录死亡过路兵
                if(first instanceof PawnChessComponent){
                    ChessComponent target = ((PawnChessComponent) first).getBypassPawn();
                    //判定当前是否立即在遭遇过路兵后吃棋
                    if(history.get(historyCnt-1).getChess0() instanceof PawnChessComponent && ((PawnChessComponent) history.get(historyCnt-1).getChess0()).isDoubleMove() ){
                        //isBypass用于判定该棋是否遭遇过路兵
                        if(((PawnChessComponent) first).isBypass()){
                            deadChess = target;
                            chessboard.remove(target);
                            chessboard.add(target=new EmptySlotComponent(target.getChessboardPoint(),target.getLocation(),this,chessboard.getCHESS_SIZE()));
                            ((PawnChessComponent) first).setBypass(false);
                            target.repaint();//TODO:啥叫repaint
                        }
                    }else{

                    }
                }

                //录入
                thisMove = new History(chessboard.getCurrentColor(),first,deadChess,chessComponent.getX(),chessComponent.getY(),first.getX(),first.getY());
                history.add(thisMove);
                //计数器加1，指针向后移动一位，注意这意味着historyCnt永远指向列表中最后一项的下一项，直接调用history.set(historyCnt)等类似方法会引起空指针异常或越界。
                historyCnt++;


                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);
                chessboard.swapColor();

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



}
