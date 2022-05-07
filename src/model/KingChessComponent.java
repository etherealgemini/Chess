package model;


import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 这个类表示国际象棋里面的王
 */

public class KingChessComponent extends ChessComponent {

    private static Image KING_WHITE;
    private static Image KING_BLACK;

    private Image kingImage;

    private boolean kingFirstMove = true;

    @Override
    public void loadResource() throws IOException {
        if (KING_WHITE == null) {
            KING_WHITE = ImageIO.read(new File("./images/king-white.png"));
        }

        if (KING_BLACK == null) {
            KING_BLACK = ImageIO.read(new File("./images/king-black.png"));
        }
    }

    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定kingImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateKingImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                kingImage = KING_WHITE;
            } else if (color == ChessColor.BLACK) {
                kingImage = KING_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KingChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateKingImage(color);
    }

    public KingChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size,boolean kingFirstMove) {
        super(chessboardPoint, location, color, listener, size);
        initiateKingImage(color);
        this.kingFirstMove=kingFirstMove;
    }

    /**
     * 王车易位的实现：
     * 我们在王的canMove方法中判定是否能走王车易位（能向左/右走两格），由此提供合法落子点
     *       在for loop中判定是否进行了易位操作
     * 在ClickController中判定是否进行易位操作，若进行则自动完成车向王后方移动的操作。
     *
     * 以上判定在kingChessComponent的canMove方法中进行
     * TODO:判定是否正在被将军(在对方棋子合法落子点位上)/路径是否会被棋子攻击(存在对方棋子合法落子点)
     *
     */

    /**
     * 王棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 王棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        /**
         * 列举出所有的合法落子
         */
        ArrayList<ChessboardPoint> legalpoints = new ArrayList<>();
        ChessboardPoint legalpoint = null;
        int row = source.getX();
        int col = source.getY();
        //以下为一般落子判定
        if(row+1<=7){
            legalpoint=new ChessboardPoint(row+1,col);
            legalpoints.add(legalpoint);
            if(col+1<=7){
                legalpoint=new ChessboardPoint(row+1,col+1);
                legalpoints.add(legalpoint);
            }
            if(col-1>=0){
                legalpoint=new ChessboardPoint(row+1,col-1);
                legalpoints.add(legalpoint);
            }
        }
        if(row-1>=0){
            legalpoint=new ChessboardPoint(row-1,col);
            legalpoints.add(legalpoint);
            if(col+1<=7){
                legalpoint=new ChessboardPoint(row-1,col+1);
                legalpoints.add(legalpoint);
            }
            if(col-1>=0){
                legalpoint=new ChessboardPoint(row-1,col-1);
                legalpoints.add(legalpoint);
            }
        }
        if(col+1<=7){
            legalpoint=new ChessboardPoint(row,col+1);
            legalpoints.add(legalpoint);
        }
        if(col-1>=0){
            legalpoint=new ChessboardPoint(row,col-1);
            legalpoints.add(legalpoint);
        }
        for (int i = 0; i < legalpoints.size(); i++) {
            if(chessComponents[legalpoints.get(i).getX()][legalpoints.get(i).getY()].getChessColor()==chessColor){
                legalpoints.remove(i);
                i--;
                //一个十分刁钻的remove问题，remove本身会改变数组索引，最好对指针作相应变换。
            }
        }

        //以下为王车易位判定
        //check1:对是否为首次移动判定
        boolean blackShortCastlingCheck1 = isKingFirstMove()&& (chessComponents[0][7] instanceof RookChessComponent && ((RookChessComponent) chessComponents[0][7]).isRookFirstMove());
        boolean blackLongCastlingCheck1 = isKingFirstMove()&&(chessComponents[0][0] instanceof RookChessComponent && ((RookChessComponent) chessComponents[0][0]).isRookFirstMove());
        //check2:越子判定
        boolean blackShortCastlingCheck2 = true;
        boolean blackLongCastlingCheck2 = true;
        for (int i = 1; i < 4; i++) {
            if(!(chessComponents[0][i] instanceof EmptySlotComponent)){
                blackLongCastlingCheck2 = false;
            }
        }
        for (int i = 1; i < 3; i++) {
            if(!(chessComponents[0][7-i] instanceof EmptySlotComponent)){
                blackShortCastlingCheck2 = false;
            }
        }
        //check3:

        //白方：
        boolean whiteShortCastlingCheck1 = isKingFirstMove()&&(chessComponents[7][7] instanceof RookChessComponent && ((RookChessComponent) chessComponents[7][7]).isRookFirstMove());
        boolean whiteLongCastlingCheck1 = isKingFirstMove()&&(chessComponents[7][0] instanceof RookChessComponent && ((RookChessComponent) chessComponents[7][0]).isRookFirstMove());


        for (int i = 0; i < legalpoints.size(); i++) {
            if(legalpoints.get(i).getX()==destination.getX()&&legalpoints.get(i).getY()==destination.getY()){
                kingFirstMove = false;
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
//        g.drawImage(kingImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(kingImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }

    public boolean isKingFirstMove() {
        return kingFirstMove;
    }

}
