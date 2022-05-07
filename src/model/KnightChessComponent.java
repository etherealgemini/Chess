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
 * 这个类表示国际象棋里面的马
 */


public class KnightChessComponent extends ChessComponent {
    /**
     * For details, please refer to the RookChessComponent.java
     */
    private static Image KNIGHT_WHITE;
    private static Image KNIGHT_BLACK;
    private Image knightImage;

    @Override
    public void loadResource() throws IOException {
        if (KNIGHT_WHITE == null) {
            KNIGHT_WHITE = ImageIO.read(new File("./images/knight-white.png"));
        }

        if (KNIGHT_BLACK == null) {
            KNIGHT_BLACK = ImageIO.read(new File("./images/knight-black.png"));
        }
    }

    private void initiateKnightImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                knightImage = KNIGHT_WHITE;
            } else if (color == ChessColor.BLACK) {
                knightImage = KNIGHT_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public KnightChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateKnightImage(color);
    }

    /**
     * 马棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 马棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        /**伪代码：
         * 列举所有合法落点
         * 如果（destination！=合法落点）
         * 那么：非法
         * 否则：合法
         */
        ArrayList<ChessboardPoint> legalpoints = new ArrayList<>();
        ChessboardPoint legalpoint = null;

        //确保不会超出边界，边界值如下：（0，0）->(0,7) ->(7,7) ->(7,0) ->(0,0)，由左上顺时针进行。
        //向上走2步
        if(source.getY()-2>=0){
            //向左走1步
            if(source.getX()-1>=0){
                legalpoint = new ChessboardPoint(source.getX()-1, source.getY()-2);
                legalpoints.add(legalpoint);
            }
            //向右走1步
            if(source.getX()+1<=7){
                legalpoint = new ChessboardPoint(source.getX()+1, source.getY()-2);
                legalpoints.add(legalpoint);
            }
        }
        //向下走2步
        if(source.getY()+2<=7){
            //向左走1步
            if(source.getX()-1>=0){
                legalpoint = new ChessboardPoint(source.getX()-1, source.getY()+2);
                legalpoints.add(legalpoint);
            }
            //向右走1步
            if(source.getX()+1<=7){
                legalpoint = new ChessboardPoint(source.getX()+1, source.getY()+2);
                legalpoints.add(legalpoint);
            }
        }
        //向左走2步
        if(source.getX()-2>=0){
            //向上走1步
            if(source.getY()-1>=0){
                legalpoint = new ChessboardPoint(source.getX()-2, source.getY()-1);
                legalpoints.add(legalpoint);
            }
            //向下走1步
            if(source.getY()+1<=7){
                legalpoint = new ChessboardPoint(source.getX()-2, source.getY()+1);
                legalpoints.add(legalpoint);
            }
        }
        //向右走2步
        if(source.getX()+2<=7){
            //向上走1步
            if(source.getY()-1>=0){
                legalpoint = new ChessboardPoint(source.getX()+2, source.getY()-1);
                legalpoints.add(legalpoint);
            }
            //向下走1步
            if(source.getY()+1<=7){
                legalpoint = new ChessboardPoint(source.getX()+2, source.getY()+1);
                legalpoints.add(legalpoint);
            }
        }
        for (int i = 0; i < legalpoints.size(); i++) {
            if(chessComponents[legalpoints.get(i).getX()][legalpoints.get(i).getY()].getChessColor()==chessColor){
                legalpoints.remove(i);
            }
        }
        //实现检索是否落点位置在合法点位，同样地，不需要进行落点处是否有子的判定
        //特别地，马是可以越子步进的。
        for (int i = 0; i < legalpoints.size(); i++) {
            legalpoint=legalpoints.get(i);
            if(destination.getX()==legalpoint.getX()&&destination.getY()==legalpoint.getY()){
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
//        g.drawImage(knightImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(knightImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }
}
