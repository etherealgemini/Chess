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
        if(col-1<=0){
            legalpoint=new ChessboardPoint(row,col-1);
            legalpoints.add(legalpoint);
        }

        for (int i = 0; i < legalpoints.size(); i++) {
            if(legalpoints.get(i).getX()==destination.getX()&&legalpoints.get(i).getY()==destination.getY()){
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

}
