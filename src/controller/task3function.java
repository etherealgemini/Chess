package controller;

import model.*;
import view.Chessboard;
import view.ChessboardPoint;

import java.awt.*;
import java.util.ArrayList;

import static controller.ClickController.historyCnt;

/**
 * 该类为工具类，实现task3相关的所有功能，提供静态方法。<br>
 * 请不要对该类在该类外的地方进行任何的实例化操作！
 */

public class task3function {
    /**
     * 防止可能出现的实例化操作。
     */
    private task3function(){}

    /**
     * 该方法实现吃过路兵，若执行成功将返还一个被吃掉的棋子，否则返回null
     * @param listener
     * @param chessboard
     * @param move 移动的兵
     * @param eaten 被吃的过路兵
     * @return 被吃的过路兵(若没有发生吃过路兵则返回null)
     */
    public static ChessComponent byPassOperation(ClickController listener, Chessboard chessboard, ChessComponent move, ChessComponent eaten){
        if(move instanceof PawnChessComponent){
            ChessComponent target = ((PawnChessComponent) move).getBypassPawn();
            //isBypass用于判定该棋是否遭遇过路兵
            if(target instanceof PawnChessComponent&&target.getChessColor()!=move.getChessColor()){
                if(((PawnChessComponent) move).isBypass()){


                    //在这里记录死亡过路兵,进行移除被吃子操作
                    eaten = createCopy(chessboard,target);

                    ChessComponent empty;
                    chessboard.remove(target);

                    ChessboardPoint emptyBoardPoint = new ChessboardPoint(target.getChessboardPoint().getX(),target.getChessboardPoint().getY());
                    Point emptylocation = new Point(target.getX(),target.getY());
                    chessboard.add(empty=new EmptySlotComponent(emptyBoardPoint,emptylocation,listener,chessboard.getCHESS_SIZE()));
                    chessboard.getChessComponents()[empty.getChessboardPoint().getX()][empty.getChessboardPoint().getY()]=empty;

                    ((PawnChessComponent) move).setBypass(false);
                    target.repaint();
                    return eaten;
                }
            }

        }
        return null;
    }

    /*
    不需要看该段注释
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
     *  该方法用于判定指定方的王在调用该方法时是否被将军<br>
     *  也可用于判断该次走棋是否合规，若走棋后仍被将军，该次走棋违规，回退至走棋前。
     *  由于棋盘的唯一性，我们只能通过先真实地执行棋子移动，判定将军，若违规，则回退，否则合规并继续。<br>
     *  程序运行速度已经足够快到看不出是否执行了该次棋子移动，因此即便先移动再回退也不会影响观感。
     * @param chessboard
     * @param chessColor
     * @return 是否处于将军状态
     */
    public static boolean isCheck(Chessboard chessboard, ChessColor chessColor){
        for (int i = 0; i < chessboard.getCHESSBOARD_SIZE(); i++) {
            for (int j = 0; j < chessboard.getCHESSBOARD_SIZE(); j++) {
                //检索指定方的王
                if(chessboard.getChessComponents()[i][j] instanceof KingChessComponent && chessboard.getChessComponents()[i][j].getChessColor()==chessColor){
                    ChessComponent king = chessboard.getChessComponents()[i][j];
                    ChessComponent[][] chessComponents = chessboard.getChessComponents();
                    //将军检测
                    for (int k = 0; k < chessboard.getCHESSBOARD_SIZE(); k++) {
                        for (int l = 0; l < chessboard.getCHESSBOARD_SIZE(); l++) {
                            if(! (chessComponents[k][l] instanceof EmptySlotComponent)){

                                ChessComponent attack = chessComponents[k][l];

                                //敌方棋子检测
                                if(attack.getChessColor()!=chessColor){
                                    //攻击检测，若可攻击则指定方被将军
                                    if(attack.canMoveTo(chessComponents,king.getChessboardPoint())){
                                        return true;
                                    }
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
     * 该方法实现判定对指定方的将死检测1：是否可以通过移走王来脱离将军<br>
     * 请注意，必须保证该方法调用时不会影响到棋盘<br>
     * 例如：指定白方，那么判定白方能否通过移动王来脱离将军。
     */
    public static boolean canMoveKing(Chessboard chessboard, ChessColor chessColor){
        for (int i = 0; i < chessboard.getCHESSBOARD_SIZE(); i++) {
            for (int j = 0; j < chessboard.getCHESSBOARD_SIZE(); j++) {
                ChessComponent chess1 = chessboard.getChessComponents()[i][j];
                //找到指定方的王
                if(chess1 instanceof KingChessComponent && chess1.getChessColor()==chessColor){
                    for (int k = 0; k < chessboard.getCHESSBOARD_SIZE(); k++) {
                        for (int l = 0; l < chessboard.getCHESSBOARD_SIZE(); l++) {
                            ChessComponent chess2 = chessboard.getChessComponents()[k][l];

                            if(chess1.canMoveTo(chessboard.getChessComponents(),chess2.getChessboardPoint())&&chess2.getChessColor()!=chessColor){
                                ChessComponent mightDead = null;

                                    mightDead = createCopy(chessboard,chess2);

                                chessboard.swapChessComponents(chess1,chess2);

                                if(isCheck(chessboard,chessColor)){
                                    //若尝试移动后仍被将军，先恢复棋盘，再继续操作
                                    chessboard.swapChessComponents(chess1,chess2);
                                    if(mightDead!=null){
                                        chessboard.putChessOnBoard(mightDead);
                                    }
                                    continue;
                                }else{
                                    //方法成功，恢复棋盘
                                    System.out.println("evoke else");
                                    chessboard.swapChessComponents(chess1,chess2);

                                    chessboard.putChessOnBoard(mightDead);

                                    return true;
                                }

                            }
                        }
                    }
                    //尝试所有移动手段均失败，该方法失败。
                    return false;
                }
            }
        }
        //若能运行到此处，表示指定方在棋盘上没有王。
        System.out.println("No King on the board！");
        return false;
    }

    /**
     * 该方法将找出所有对指定方将军的棋子，装载到arraylist中。
     * 请务必调用createcopy深拷贝。
     * @param chessboard 棋盘
     * @param chessColor 指定方
     * @return 含所有将军棋子的列表
     */
    public static ArrayList<ChessComponent> findCheckChess(Chessboard chessboard, ChessColor chessColor){
        ArrayList<ChessComponent> CheckChesses = new ArrayList<>();
        for (int i = 0; i < chessboard.getCHESSBOARD_SIZE(); i++) {
            for (int j = 0; j < chessboard.getCHESSBOARD_SIZE(); j++) {
                //检索指定方的王
                if(chessboard.getChessComponents()[i][j] instanceof KingChessComponent && chessboard.getChessComponents()[i][j].getChessColor()==chessColor){
                    ChessComponent king = chessboard.getChessComponents()[i][j];
                    ChessComponent[][] chessComponents = chessboard.getChessComponents();
                    //将军检测
                    for (int k = 0; k < chessboard.getCHESSBOARD_SIZE(); k++) {
                        for (int l = 0; l < chessboard.getCHESSBOARD_SIZE(); l++) {
                            if(! (chessComponents[k][l] instanceof EmptySlotComponent)){
                                ChessComponent attack = chessComponents[k][l];
                                //敌方棋子检测
                                if(attack.getChessColor()!=chessColor){
                                    //攻击检测，若可攻击则指定方被将军
                                    if(attack.canMoveTo(chessComponents,king.getChessboardPoint())){
                                        ChessComponent temp = createCopy(chessboard,attack);
                                        CheckChesses.add(temp);
                                    }
                                }
                            }
                        }
                    }
                    //若检测到调用方的王且通过了将军检测，则调用方已经不可能被将军了。
                    return CheckChesses;
                }
                //exception:没有王？
            }
        }
        System.out.println("No King on the board!");
        return null;
    }

    /**
     * 该方法实现判定对指定方的将死检测2：能否通过吃将指定方的军的棋子来脱离将军
     */
    public static boolean canEat(Chessboard chessboard, ChessColor chessColor,ArrayList<ChessComponent> checkChesses){
        if(checkChesses==null){
            return false;
        }
        if(checkChesses.size()>=2){
            //一旦有两个棋子将军，该方法必然失效
            return false;
        }else if(checkChesses.size()==0){
            //嘿！没有棋子在将军！为什么会调用到个方法呢！
            System.out.println("Exception: There is no Check case!");
            return false;
        }else if(checkChesses.size()==1){
            //不需要担心arraylist中的棋子与棋盘上棋子发生链接，在存入的时候已经调用createcopy实现深拷贝了。

            ChessComponent target = createCopy(chessboard,checkChesses.get(0));
            for (int i = 0; i < chessboard.getCHESSBOARD_SIZE(); i++) {
                for (int j = 0; j < chessboard.getCHESSBOARD_SIZE(); j++) {
                    ChessComponent chess1 = chessboard.getChessComponents()[i][j];
                    if(chess1.getChessColor() == chessColor && chess1.canMoveTo(chessboard.getChessComponents(),target.getChessboardPoint())){
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 该方法实现判定对指定方的将死检测3：能否挡将。
     */
    public static boolean canBlock(Chessboard chessboard, ChessColor chessColor,ArrayList<ChessComponent> checkChesses){
        if(checkChesses==null){
            return false;
        }
        if(checkChesses.size()>=2){
            //一旦有两个棋子将军，该方法必然失效
            return false;
        }else if(checkChesses.size()==0){
            //嘿！没有棋子在将军！为什么会调用到个方法呢！
            System.out.println("Exception: There is no Check case!");
            return false;
        }
        else if(checkChesses.size()==1){
            KingChessComponent king = null;
            int kingX = 0;
            int kingY = 0;
            for (int i = 0; i < chessboard.getCHESSBOARD_SIZE(); i++) {
                for (int j = 0; j < chessboard.getCHESSBOARD_SIZE(); j++) {
                    if(chessboard.getChessComponents()[i][j] instanceof KingChessComponent && chessboard.getChessComponents()[i][j].getChessColor()==chessColor){
                        king = (KingChessComponent) chessboard.getChessComponents()[i][j];
                        kingX = king.getChessboardPoint().getX();
                        kingY = king.getChessboardPoint().getY();
                    }
                }
            }
            ChessComponent target = createCopy(chessboard,checkChesses.get(0));
            int targetX = target.getChessboardPoint().getX();
            int targetY = target.getChessboardPoint().getY();
            if(target instanceof KnightChessComponent || target instanceof PawnChessComponent){
                return false;
            }else{
                //若两棋之间相对距离仅为1，无需判定，王可以直接吃棋。
                if(Math.abs(kingX-targetX)<=1&&Math.abs(kingY-targetY)<=1){
                    return true;
                }

                //此时有车 象 后三种棋子可能将军。若为象，需要挡两者间斜线，若为车，需要挡两者间直线，若为后，根据位置挡两者间斜线或直线
                //我们存入所有可挡落点。
                ArrayList<ChessboardPoint> blockPoints = new ArrayList<>();
                ChessboardPoint blockPoint = new ChessboardPoint (0,0);

                if(target instanceof BishopChessComponent){
                    //斜角决定了xy坐标都一定不同，可挡落点在两棋斜线之间。
                    for (int x = 0; x < chessboard.getCHESSBOARD_SIZE(); x++) {
                        for (int y = 0; y < chessboard.getCHESSBOARD_SIZE(); y++) {
                            if( (x<Math.max(kingX,targetX)&&x>Math.min(kingX,targetX)) && (y<Math.max(kingY,targetY)&&y>Math.min(kingY,targetY))){
                                blockPoint.resetChessBoardPoint(x,y);
                                blockPoints.add(blockPoint);
                            }
                        }
                    }

                }
                else if(target instanceof RookChessComponent){

                    if(targetX==kingX){
                        for (int y = 0; y < chessboard.getCHESSBOARD_SIZE(); y++) {
                            if(y<Math.max(kingY,targetY)&&y>Math.min(kingY,targetY)){
                                blockPoint.resetChessBoardPoint(kingX,y);
                                blockPoints.add(blockPoint);
                            }
                        }
                    }
                    if(targetY==kingY){
                        for (int x = 0; x < chessboard.getCHESSBOARD_SIZE(); x++) {
                            if(x<Math.max(kingX,targetX)&&x>Math.min(kingX,targetX)){
                                blockPoint.resetChessBoardPoint(x,kingY);
                                blockPoints.add(blockPoint);
                            }
                        }
                    }

                }
                else if(target instanceof QueenChessComponent){

                    if(targetX==kingX){
                        for (int y = 0; y < chessboard.getCHESSBOARD_SIZE(); y++) {
                            if(y<Math.max(kingY,targetY)&&y>Math.min(kingY,targetY)){
                                blockPoint.resetChessBoardPoint(kingX,y);
                                blockPoints.add(blockPoint);
                            }
                        }
                    }
                    else if(targetY==kingY){
                        for (int x = 0; x < chessboard.getCHESSBOARD_SIZE(); x++) {
                            if(x<Math.max(kingX,targetX)&&x>Math.min(kingX,targetX)){
                                blockPoint.resetChessBoardPoint(x,kingY);
                                blockPoints.add(blockPoint);
                            }
                        }
                    }else{//此时为斜线将军
                        for (int x = 0; x < chessboard.getCHESSBOARD_SIZE(); x++) {
                            for (int y = 0; y < chessboard.getCHESSBOARD_SIZE(); y++) {
                                if( (x<Math.max(kingX,targetX)&&x>Math.min(kingX,targetX)) && (y<Math.max(kingY,targetY)&&y>Math.min(kingY,targetY))){
                                    blockPoint.resetChessBoardPoint(x,y);
                                    blockPoints.add(blockPoint);
                                }
                            }
                        }
                    }
                }

                //存储可挡落点之后：
                for (int i = 0; i < chessboard.getCHESSBOARD_SIZE(); i++) {
                    for (int j = 0; j < chessboard.getCHESSBOARD_SIZE(); j++) {
                        ChessComponent temp = chessboard.getChessComponents()[i][j];
                        //若棋子为本方棋
                        if(temp.getChessColor() == chessColor){
                            //若可行至可挡落点
                            for (int k = 0; k < blockPoints.size(); k++) {
                                if(temp.canMoveTo(chessboard.getChessComponents(),blockPoints.get(k))){
                                    return true;
                                }
                            }
                        }
                        //该棋子失效，继续尝试
                    }
                }
                //尝试结束，方法失败。
                return false;
            }
        }
        //不可能运行到此处
        System.out.println("Unknown Exception");
        return false;
    }

    /**
     * 该方法实现对指定行棋方的全部将死判定，已经集成了将军判定。
     * @param chessboard 棋盘
     * @param chessColor 指定行棋方
     * @return 指定行棋方是否被将军
     */
    public static boolean isCheckMate(Chessboard chessboard,ChessColor chessColor){
        ChessColor enemyColor = chessColor;
        boolean enemyIsCheckAfterMove = isCheck(chessboard,enemyColor);
        if(enemyIsCheckAfterMove){
            //将死判定
            ArrayList<ChessComponent> checkChess = findCheckChess(chessboard,enemyColor);
            if(canMoveKing(chessboard,enemyColor)||canEat(chessboard,enemyColor,checkChess)||canBlock(chessboard,enemyColor,checkChess)){
                //若三种方法任意一种通过，则不被将死，否则立即判负。
                return false;
            }else{
                return true;
            }
        }
        //如果该方没有被将军，那么自然不会被将死。
        return false;
    }

    /**
     * 该方法用于安全创建深拷贝棋子<br>
     * 使用时机：<br>
     * 当希望拷贝一枚棋子对象 input 并且不希望 将 拷贝 与 input 的属性链接起来时，请调用createCopy方法拷贝，该方法将返回一个对input棋子的安全拷贝对象。<br>
     * 请注意，若对某一棋子新增了某些属性时，请务必在对应棋子的构造方法中添加这个属性，并修改createCopy方法中对应部分，否则无法正确拷贝对象，详情请查阅方法内容。
     * @param chessboard 棋盘
     * @param input 希望拷贝的棋子
     * @return copy input的拷贝
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
     * 该方法实现悔棋功能。<br>
     * 注意传入的historyCnt请正确使用
     * FIXME：将传入变量move去除，完全依赖history。
     */
    public static void undo(Chessboard chessboard, ArrayList<History> history){
        if(historyCnt<=0){
            return;
        }
        else{
            //move作用:提供一个被移动后的棋子。
            //代替：将move的拷贝传入history
            int srcX = history.get(historyCnt-1).getSrcX();
            int srcY = history.get(historyCnt-1).getSrcY();

            ChessComponent dead = history.get(historyCnt-1).getDeadChess();
            ChessComponent src = history.get(historyCnt-1).getChess0();
//            EmptySlotComponent empty = new EmptySlotComponent(move.getChessboardPoint(),move.getLocation(),move.getClickController(),chessboard.getCHESS_SIZE());
            ChessComponent empty = history.get(historyCnt-1).getDeadChess();
            //逻辑如下：将被移动棋子按原位摆放回棋盘，移除移动后的棋子，设置移除位置为空棋子，若死棋不是空棋子则将死棋放回原位。
            chessboard.putChessOnBoard(src);
            chessboard.getChessComponents()[srcX][srcY]=src;

//            chessboard.remove(his);
            chessboard.putChessOnBoard(empty);
            chessboard.getChessComponents()[empty.getChessboardPoint().getX()][empty.getChessboardPoint().getY()]=empty;

            System.out.println();
            System.out.println("src: "+srcX+" "+srcY );
//            empty.repaint();
//            src.repaint();

            history.remove(historyCnt-1);
            historyCnt--;
//            System.out.println(historyCnt);

            chessboard.swapColor();
        }
    }

}
