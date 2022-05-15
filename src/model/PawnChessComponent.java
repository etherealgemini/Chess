package model;

import view.Chessboard;
import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 这个类表示国际象棋里面的兵
 */

public class PawnChessComponent extends ChessComponent {
    public boolean isFirstMove() {
        return firstMove;
    }

    /**
     * 我们需要在这里实现对该兵的几种判断：
     * 是否首次移动（移动两格的合法性）
     * 是否可以吃过路兵
     * 是否第一次遇到过路兵。
     */
    private boolean firstMove = true;//一旦移动后立即设定为false，不再变化，除非悔棋
    private boolean doubleMove = true;//一旦进行二步移动后立即设定为true，否则即为false。

    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    private boolean bypass = false;
    private ChessComponent bypassPawn=null;
    private boolean firstBypass = false;

    public ArrayList<ChessboardPoint> getOrigin() {
        return origin;
    }

    private ArrayList<ChessboardPoint> origin = new ArrayList<>();



    private static Image PAWN_WHITE;
    private static Image PAWN_BLACK;

    private Image pawnImage;



    public boolean isDoubleMove() {
        return doubleMove;
    }

    @Override
    public void loadResource() throws IOException {
        if (PAWN_WHITE == null) {
            PAWN_WHITE = ImageIO.read(new File("./images/pawn-white.png"));
        }

        if (PAWN_BLACK == null) {
            PAWN_BLACK = ImageIO.read(new File("./images/pawn-black.png"));
        }
    }

    private void initiatePawnImage(ChessColor color) {
        //该方法将在初始化的时候确定该棋子的黑白方
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                pawnImage = PAWN_WHITE;
            } else if (color == ChessColor.BLACK) {
                pawnImage = PAWN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public PawnChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiatePawnImage(color);
    }



    public void setFirstMove(boolean firstMove) {
        this.firstMove = firstMove;
    }

    public void setDoubleMove(boolean doubleMove) {
        this.doubleMove = doubleMove;
    }

    public void setBypassPawn(ChessComponent bypassPawn) {
        this.bypassPawn = bypassPawn;
    }

    public PawnChessComponent(ChessboardPoint chessboardPoint,Point location, ChessColor color, ClickController listener, int size, boolean firstBypass, boolean firstMove, boolean doubleMove, boolean bypass, ChessComponent bypassPawn){
        super(chessboardPoint, location, color, listener, size);
        initiatePawnImage(color);
        this.firstBypass=firstBypass;
        this.bypassPawn=bypassPawn;
        this.bypass=bypass;
        this.doubleMove=doubleMove;
        this.firstMove=firstMove;
    }


    public boolean isFirstBypass() {
        return firstBypass;
    }

    public void setFirstBypass(boolean firstBypass) {
        this.firstBypass = firstBypass;
    }


    public boolean isPawnFirstMove(){
        boolean isfirstMove = false;
        if(getChessColor()==ChessColor.WHITE){
            for (int i = 0; i < 8; i++) {
                ChessboardPoint originpoint = new ChessboardPoint(6,i);
                origin.add(originpoint);
            }
        }
        if(getChessColor()==ChessColor.BLACK){
            for (int i = 0; i < 8; i++) {
                ChessboardPoint originpoint = new ChessboardPoint(1,i);
                origin.add(originpoint);
            }
        }

        int row = getChessboardPoint().getX();
        int col = getChessboardPoint().getY();



        for (int i = 0; i < origin.size(); i++) {
            if(row==origin.get(i).getX()&&col==origin.get(i).getY()){
                isfirstMove=true;
            }
        }

        return isfirstMove;
    }


    /**
     * 兵棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 兵棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {

        ChessboardPoint source = getChessboardPoint();

//        System.out.println("first evoke: "+firstMove);
        ArrayList<ChessboardPoint> legalpoints = new ArrayList<>();
        ChessboardPoint legalpoint = null;
        //NOTICE: 根据黑白方不同，行棋方向有区别
        //黑方在上，白方在下。因此黑方朝下，白方朝上
        //NOTICE: row为竖行，col为横列，下加上减，右加左减。

        //用于判定该次攻击是否为 吃过路兵

        boolean attack = false;

        int row = source.getX();
        int col = source.getY();

        PawnChessComponent Target = null;
//        boolean isInRowUpperBound = row+1<=7;
//        boolean isInRowLowerBound = row-1>=0;
        firstMove = isPawnFirstMove();
        System.out.println("Firstmove = "+firstMove);
        //检测：若为 非第一回合 且 上一回合为走兵 且 上一回合走兵走两格 则为真
        boolean instanceBypass = ClickController.getHistoryCnt()-1>=0 && getClickController().getHistory().get(ClickController.getHistoryCnt()-1).getChess0() instanceof PawnChessComponent &&
                ((PawnChessComponent) getClickController().getHistory().get(ClickController.getHistoryCnt()-1).getChess0()).isDoubleMove();

        if(pawnImage==PAWN_WHITE){
            if(row-1<=7&&(chessComponents[row-1][col] instanceof EmptySlotComponent)){
                legalpoint= new ChessboardPoint(row-1,col);
                legalpoints.add(legalpoint);
            }
            if(firstMove&&(chessComponents[row-2][col] instanceof EmptySlotComponent)){
                firstBypass=true;
                legalpoint = new ChessboardPoint(row-2,col);
                legalpoints.add(legalpoint);
//                this.getClickController().setBypasscheck(true);
            }
            //斜角攻击
            //若向左上方走：
            if(row-1>=0&&col-1>=0&&(! (chessComponents[row-1][col-1] instanceof EmptySlotComponent)||chessComponents[row][col-1] instanceof PawnChessComponent)){
                //特别地，不需要考虑冲突情况，因为不可能发生同时有过路兵和斜角有子的情况，除非棋盘错误。
                if(! (chessComponents[row-1][col-1] instanceof EmptySlotComponent)){
                    legalpoint = new ChessboardPoint(row-1,col-1);
                    legalpoints.add(legalpoint);
                }
                //若左方有兵
                if(instanceBypass && chessComponents[row][col-1] instanceof PawnChessComponent){
                    Target = (PawnChessComponent) chessComponents[row][col-1];
                    if(Target.isDoubleMove()&&this.getChessColor()!=Target.getChessColor()){
                        legalpoint = new ChessboardPoint(row-1,col-1);
                        bypassPawn = Target;
                        legalpoints.add(legalpoint);
                    }
                }

            }
            //以下为逻辑相似的代码，仅仅是方向/位置不同。
            if(row-1>=0&&col+1<=7&&(!(chessComponents[row-1][col+1] instanceof EmptySlotComponent)||chessComponents[row][col+1] instanceof PawnChessComponent)){
                if(! (chessComponents[row-1][col+1] instanceof EmptySlotComponent)){
                    legalpoint = new ChessboardPoint(row-1,col+1);
                    legalpoints.add(legalpoint);
                }
                if(instanceBypass &&  chessComponents[row][col+1] instanceof PawnChessComponent){
                    Target = (PawnChessComponent) chessComponents[row][col+1];
                    if(Target.isDoubleMove()&&this.getChessColor()!=Target.getChessColor()){
                        legalpoint = new ChessboardPoint(row-1,col+1);
                        bypassPawn = Target;
                        legalpoints.add(legalpoint);
                    }
                }
            }
//            if(chessComponents[row][col+1] instanceof PawnChessComponent){
//                PawnChessComponent Target = (PawnChessComponent) chessComponents[row][col+1];
//                if(Target.isDoubleMove()&&this.getChessColor()!=Target.getChessColor()){
//                   legalpoint = new ChessboardPoint(row+1,col+1);
//                   legalpoints.add(legalpoint);
//                }
//            }
        }

        if(pawnImage==PAWN_BLACK){
            if(row+1<=7&&(chessComponents[row+1][col] instanceof EmptySlotComponent)){
                legalpoint= new ChessboardPoint(row+1,col);
                legalpoints.add(legalpoint);
            }
            if(firstMove&&(chessComponents[row+2][col] instanceof EmptySlotComponent)){
                firstBypass=true;
                legalpoint = new ChessboardPoint(row+2, col);
                legalpoints.add(legalpoint);
            }
            //向右下方走
            if(row+1<=7&&col+1<=7&&(!(chessComponents[row+1][col+1] instanceof EmptySlotComponent)||chessComponents[row][col+1] instanceof PawnChessComponent)){
                if(! (chessComponents[row+1][col+1] instanceof EmptySlotComponent)){
                    legalpoint = new ChessboardPoint(row+1,col+1);
                    legalpoints.add(legalpoint);
                }
                //若右方有兵
                if(instanceBypass &&  chessComponents[row][col+1] instanceof PawnChessComponent){
                    Target = (PawnChessComponent) chessComponents[row][col+1];
                    if(Target.isDoubleMove()&&this.getChessColor()!=Target.getChessColor()){
                        legalpoint = new ChessboardPoint(row+1,col+1);
                        bypassPawn = Target;
                        legalpoints.add(legalpoint);
                    }
                }
            }
            //向左下方（row+ col-）
            if(row+1<=7&&col-1>=0&&(!(chessComponents[row+1][col-1] instanceof EmptySlotComponent)||chessComponents[row][col-1] instanceof PawnChessComponent)){
                if(! (chessComponents[row+1][col-1] instanceof EmptySlotComponent)){
                    legalpoint = new ChessboardPoint(row+1,col-1);
                    legalpoints.add(legalpoint);
                }
                if(instanceBypass &&  chessComponents[row][col-1] instanceof PawnChessComponent){
                    Target = (PawnChessComponent) chessComponents[row][col-1];
                    if(Target.isDoubleMove()&&Target.getChessColor()!=this.getChessColor()){
                        legalpoint = new ChessboardPoint(row+1,col-1);
                        bypassPawn = Target;
                        legalpoints.add(legalpoint);
                    }
                }
            }
        }
        for (int i = 0; i < legalpoints.size(); i++) {
            if(chessComponents[legalpoints.get(i).getX()][legalpoints.get(i).getY()].getChessColor()==chessColor){
                legalpoints.remove(i);
                i--;
            }
        }
        for (int i = 0; i < legalpoints.size(); i++) {
            if(legalpoints.get(i).getX()==destination.getX()&&legalpoints.get(i).getY()==destination.getY()){
                firstMove=false;
                if(destination.getX()==source.getX()+2||destination.getX()==source.getX()-2){
                    doubleMove=true;
                }
                if(destination.getX()==source.getX()+1||destination.getX()==source.getX()-1){
                    doubleMove=false;
                }
                //若为斜角行棋
                if(destination.getX()!=source.getX()&&destination.getY()!=source.getY()){
                    if(bypassPawn instanceof PawnChessComponent){
                        bypass = true;
                    }else{
                        bypass = false;
                    }
                }


                return true;
            }
        }
        return false;
    }



    /**
     * 注意这个方法，每当窗体受到了形状的变化，或者是通知要进行绘图的时候，就会调用这个方法进行画图。
     *
     * @param g 可以类比于画笔
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(pawnImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(pawnImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }

    public boolean isBypass() {
        return bypass;
    }

    public ChessComponent getBypassPawn() {
        return bypassPawn;
    }
}
