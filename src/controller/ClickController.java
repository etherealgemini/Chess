package controller;


import jdk.javadoc.doclet.Taglet;
import model.*;
import view.Chessboard;
import view.ChessboardPoint;

import javax.sound.sampled.SourceDataLine;
import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;

public class ClickController {



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

    public ClickController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    public static void byPassOperation(ClickController listener,Chessboard chessboard,ChessComponent move,ChessComponent eaten){
        if(move instanceof PawnChessComponent){
            ChessComponent target = ((PawnChessComponent) move).getBypassPawn();
            //isBypass用于判定该棋是否遭遇过路兵
            if(((PawnChessComponent) move).isBypass()){


                //在这里记录死亡过路兵
                eaten = target;
                ChessComponent empty;
                chessboard.remove(target);
                chessboard.add(empty=new EmptySlotComponent(target.getChessboardPoint(),target.getLocation(),listener,chessboard.getCHESS_SIZE()));
                ((PawnChessComponent) move).setBypass(false);
                target.repaint();//TODO:啥叫repaint
            }
        }
    }

    /*
  我们在这里实现将军判定、将死判定：
  采取方法（以黑方先走为例）：
  保存将军状态
  method1：如果(白方不在将军状态 且 黑方不在将军状态){
      黑方走棋之后，对棋盘扫描，检测白方王的安全(将军检测)：
      如果(白方王在黑方该棋的合法落子点上){
          白方被将军
          将死检测：
               如果(以下三种方法 1.吃掉对方将军的棋子
                   2.在对方的棋子和自己的王中间点上自己的棋子
                   3.移走自己的王 均无法进行){
                       白方被将死，立即判负，黑方胜利
                   }否则{
                       保存白方将军状态，该回合结束，换方，由白方行棋。
                   }
      }否则{
          安全，该回合结束，换方。
      }
  }
  method2：如果(白方不在将军状态 且 黑方正在被将军){
      黑方走棋后，(显示动画前)对棋盘扫描，检测黑方王的安全，如果(黑方仍被将军){
          该次行棋非法，不予行棋。
      }否则{
          黑方脱离将军状态，执行method1.
      }
  }

  特殊情况：
  同时被将军(若一方被将军，该方必然需要应将，否则即将死，若发生该情况属于异常)
  行棋时对方正在被将军(若对方无法应将，即被将死，不可能轮到该回合，若发生该情况属于异常)

  需要传入：棋盘、将被移动的棋子

 */

    /**
     *  该方法用于判定指定方的王在调用该方法时是否被将军
     *  也可用于判断该次走棋是否合规，若走棋后仍被将军，该次走棋违规，回退至走棋前。
     *  由于棋盘的唯一性，我们只能通过先真实地执行棋子移动(但不播放动画)，判定将军，若违规，则回退，否则合规并播放动画(repaint)。
     *  我们首先需要引入回退(悔棋)。
     */
    public static boolean isCheck(Chessboard chessboard, ChessColor chessColor){
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                //检索指定方的王
                if(chessboard.getChessComponents()[i][j] instanceof KingChessComponent && chessboard.getChessComponents()[i][j].getChessColor()==chessColor){
                    ChessComponent king = chessboard.getChessComponents()[i][j];
                    ChessComponent[][] chessComponents = chessboard.getChessComponents();
                    //将军检测
                    for (int k = 0; k < 8; k++) {
                        for (int l = 0; l < 8; l++) {
                            if(! (chessComponents[k][l] instanceof EmptySlotComponent)){
                                ChessComponent attack = chessComponents[k][l];
                                //敌方棋子检测
                                if(attack.getChessColor()!=chessColor){
                                    //攻击检测，若可攻击则指定方被将军
                                    attack.canMoveTo(chessComponents,king.getChessboardPoint());
                                    return true;
                                }
                            }
                        }
                    }
                    //若检测到调用方的王且通过了将军检测，则调用方已经不可能被将军了。
                    return false;
                }
                //exception:没有王？
            }
        }
        return false;
    }

    /**
     * 该方法用于安全创建深拷贝棋子
     * 使用时机：
     * 当希望拷贝一枚棋子对象 chess1 并不希望改变 chess1 的属性时，请调用createCopy方法拷贝，该方法将返回一个对input棋子的安全拷贝对象。
     * 请注意，若对某一棋子新增了某些属性时，请务必在对应棋子的构造方法中添加这个属性，并修改createCopy方法中对应部分，否则无法正确拷贝对象，详情请查阅方法内容。
     * @param chessboard
     * @param input
     * @return copy
     */
    public static ChessComponent createCopy(Chessboard chessboard,ChessComponent input){
        if(input==null){
            return null;
        }
        if(input instanceof EmptySlotComponent){
            ChessboardPoint outputBoardPoint = new ChessboardPoint(input.getChessboardPoint().getX(),input.getChessboardPoint().getY());
            Point outputPoint = new Point(input.getX(),input.getY());
            EmptySlotComponent output = new EmptySlotComponent(outputBoardPoint,outputPoint, input.getClickController(), chessboard.getCHESS_SIZE());
            return output;
        }
        else if(input instanceof RookChessComponent){
            ChessboardPoint outputBoardPoint = new ChessboardPoint(input.getChessboardPoint().getX(),input.getChessboardPoint().getY());
            Point outputPoint = new Point(input.getX(),input.getY());
            RookChessComponent output = new RookChessComponent(outputBoardPoint,outputPoint,input.getChessColor(), input.getClickController(),chessboard.getCHESS_SIZE());
            return output;
        }
        else if(input instanceof KingChessComponent){
            ChessboardPoint outputBoardPoint = new ChessboardPoint(input.getChessboardPoint().getX(),input.getChessboardPoint().getY());
            Point outputPoint = new Point(input.getX(),input.getY());
            KingChessComponent output = new KingChessComponent(outputBoardPoint,outputPoint,input.getChessColor(), input.getClickController(), chessboard.getCHESS_SIZE(),((KingChessComponent) input).isKingFirstMove());
            return output;
        }
        else if(input instanceof QueenChessComponent){
            ChessboardPoint outputBoardPoint = new ChessboardPoint(input.getChessboardPoint().getX(),input.getChessboardPoint().getY());
            Point outputPoint = new Point(input.getX(),input.getY());
            QueenChessComponent output = new QueenChessComponent(outputBoardPoint,outputPoint,input.getChessColor(),input.getClickController(),chessboard.getCHESS_SIZE());
            return output;
        }
        else if(input instanceof PawnChessComponent){
            ChessboardPoint outputBoardPoint = new ChessboardPoint(input.getChessboardPoint().getX(),input.getChessboardPoint().getY());
            Point outputPoint = new Point(input.getX(),input.getY());
            PawnChessComponent output = new PawnChessComponent(outputBoardPoint,outputPoint,input.getChessColor(),
                    input.getClickController(),chessboard.getCHESS_SIZE(),
                    ((PawnChessComponent) input).isFirstBypass(),((PawnChessComponent) input).isFirstMove(),
                    ((PawnChessComponent) input).isDoubleMove(),((PawnChessComponent) input).isBypass(),
                    ((PawnChessComponent) input).getBypassPawn());
            return output;
        }
        else if(input instanceof KnightChessComponent){
            ChessboardPoint outputBoardPoint = new ChessboardPoint(input.getChessboardPoint().getX(),input.getChessboardPoint().getY());
            Point outputPoint = new Point(input.getX(),input.getY());
            KnightChessComponent output = new KnightChessComponent(outputBoardPoint,outputPoint,input.getChessColor(), input.getClickController(),chessboard.getCHESS_SIZE());
            return output;
        }
        else if(input instanceof BishopChessComponent){
            ChessboardPoint outputBoardPoint = new ChessboardPoint(input.getChessboardPoint().getX(),input.getChessboardPoint().getY());
            Point outputPoint = new Point(input.getX(),input.getY());
            BishopChessComponent output = new BishopChessComponent(outputBoardPoint,outputPoint,input.getChessColor(), input.getClickController(), chessboard.getCHESS_SIZE());
            return output;
        }
        return null;
    }

    /**
     * 该方法实现悔棋
     * 注意传入的historyCnt按照我们的定义来使用
     */
    public static void undo(Chessboard chessboard,ArrayList<History> history,ChessComponent move){
        if(false){
            //异常
        }else{

            int srcX = history.get(historyCnt-1).getSrcX();
            int srcY = history.get(historyCnt-1).getSrcY();

            ChessComponent dead = history.get(historyCnt-1).getDeadChess();
            ChessComponent src = history.get(historyCnt-1).getChess0();
            EmptySlotComponent empty = new EmptySlotComponent(move.getChessboardPoint(),move.getLocation(),move.getClickController(),chessboard.getCHESS_SIZE());

            //逻辑如下：将被移动棋子按原位摆放回棋盘，移除移动后的棋子，设置移除位置为空棋子，若死棋不是空棋子则将死棋放回原位。
            chessboard.putChessOnBoard(src);
            chessboard.getChessComponents()[srcX][srcY]=src;

            chessboard.remove(move);
            chessboard.putChessOnBoard(empty);
            chessboard.getChessComponents()[empty.getChessboardPoint().getX()][empty.getChessboardPoint().getY()]=empty;
            if(!(dead instanceof EmptySlotComponent)){
                chessboard.putChessOnBoard(dead);
                dead.repaint();
            }
            empty.repaint();
            src.repaint();


            history.remove(historyCnt-1);
            historyCnt--;
            System.out.println(historyCnt);

            chessboard.swapColor();
        }
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
                //这里的chessComponent表示目标位置的棋子（包括空白）及其所有属性，first是被移动(被红圈选中的)棋子。

                //一旦通过handleSecond检测，即表示该次行棋合法，且将被执行
                //这里为demo唯一进行吃棋行为的地方，请尽可能不要在其他地方实现吃子行为。
                //demo实现逻辑是：if(该次走棋合法): then(swapChessComponents)

                //死亡棋子记录
                ChessComponent deadChess = null;
                if(!(chessComponent instanceof EmptySlotComponent)){
                    deadChess = createCopy(chessboard,chessComponent);
                }
                if(chessComponent instanceof EmptySlotComponent){
                    deadChess = createCopy(chessboard,chessComponent);
                }

                //吃过路兵检测
                byPassOperation(this,chessboard,first,chessComponent);


                //录入
                ChessComponent thischess = createCopy(chessboard,first);
                thisMove = new History(chessboard.getCurrentColor(),thischess,deadChess,deadChess.getChessboardPoint().getX(),deadChess.getChessboardPoint().getY(),thischess.getChessboardPoint().getX(),thischess.getChessboardPoint().getY());
                history.add(thisMove);
                //计数器加1，指针向后移动一位。
                //注意这意味着historyCnt永远指向列表中最后一项的下一项，直接调用history.set(historyCnt)等类似方法会引起空指针异常或越界。
                historyCnt++;

                //repaint in swap chess method.
                chessboard.swapChessComponents(first, chessComponent);
                //change side
                chessboard.swapColor();

                //FIXME: 悔棋方法测试：若可行，在吃棋后行棋立即自动悔棋，程序中表现为进行吃棋后红圈自动消失
                if(! (history.get(historyCnt-1).getDeadChess() instanceof EmptySlotComponent)){
                    undo(chessboard,history,first);
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
