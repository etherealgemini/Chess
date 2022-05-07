package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 这个类表示国际象棋里面的车
 */
public class RookChessComponent extends ChessComponent {
    /**
     * 黑车和白车的图片，static使得其可以被所有车对象共享
     * <br>
     * FIXME: 需要特别注意此处加载的图片是没有背景底色的！！！
     */
    private static Image ROOK_WHITE;
    private static Image ROOK_BLACK;

    /**
     * 车棋子对象自身的图片，是上面两种中的一种
     */
    private Image rookImage;

    private boolean rookFirstMove = true;

    /**
     * 读取加载车棋子的图片
     *
     * @throws IOException
     */
    @Override
    public void loadResource() throws IOException {
        if (ROOK_WHITE == null) {
            ROOK_WHITE = ImageIO.read(new File("./images/rook-white.png"));
        }

        if (ROOK_BLACK == null) {
            ROOK_BLACK = ImageIO.read(new File("./images/rook-black.png"));
        }
    }


    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateRookImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                rookImage = ROOK_WHITE;
            } else if (color == ChessColor.BLACK) {
                rookImage = ROOK_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RookChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateRookImage(color);
    }

    /**
     * 车棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 车棋子移动的合法性
     */

    @Override
    /**
     * @Fixme 请按照其他棋子的方式将合法落子点存入arraylist中
     */
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        ArrayList<ChessboardPoint> legalpoints = new ArrayList<>();
        ChessboardPoint legalpoint = new ChessboardPoint(0,0);


        //若在同一行:
        //NOTICE: 此处row为竖行，而非横行！
        int row = source.getX();
        int col = source.getY();
        boolean isCrossRowUp = false;
        boolean isCrossRowDown = false;
        boolean isCrossColUp = false;
        boolean isCrossColDown = false;
        for (int i = 1; i < 8; i++) {
            if(!isCrossRowUp&&row-i>=0){
                if(!(chessComponents[row-i][col] instanceof  EmptySlotComponent)) {
                    if ((chessComponents[row - i][col].getChessColor() != chessColor)) {
                        legalpoint = new ChessboardPoint(row - i, col);
                        legalpoints.add(legalpoint);
                    }
                    isCrossRowUp=true;
                }
                legalpoint = new ChessboardPoint(row - i, col);
                legalpoints.add(legalpoint);
            }
            if(!isCrossRowDown&&row+i<=7){
                if(!(chessComponents[row+i][col] instanceof  EmptySlotComponent)) {
                    if ((chessComponents[row + i][col].getChessColor() != chessColor)) {
                        legalpoint = new ChessboardPoint(row + i, col);
                        legalpoints.add(legalpoint);
                    }
                    isCrossRowDown=true;
                }
                legalpoint = new ChessboardPoint(row + i, col);
                legalpoints.add(legalpoint);
            }
            if(!isCrossColUp&&col-i>=0){
                if(!(chessComponents[row][col-i] instanceof  EmptySlotComponent)) {
                    if ((chessComponents[row][col-i].getChessColor() != chessColor)) {
                        legalpoint = new ChessboardPoint(row, col-i);
                        legalpoints.add(legalpoint);
                    }
                    isCrossColUp=true;
                }
                legalpoint = new ChessboardPoint(row, col-i);
                legalpoints.add(legalpoint);
            }
            if(!isCrossColDown&&col+i<=7){
                if(!(chessComponents[row][col+i] instanceof  EmptySlotComponent)) {
                    if ((chessComponents[row][col+i].getChessColor() != chessColor)) {
                        legalpoint = new ChessboardPoint(row, col+i);
                        legalpoints.add(legalpoint);
                    }
                    isCrossColDown=true;
                }
                legalpoint = new ChessboardPoint(row, col+i);
                legalpoints.add(legalpoint);
            }
        }

        for (int i = 0; i < legalpoints.size(); i++) {
            if(destination.getX()==legalpoints.get(i).getX()&&destination.getY()==legalpoints.get(i).getY()){
                return true;
            }
        }

        rookFirstMove = false;
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
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(rookImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }

    public boolean isRookFirstMove() {
        return rookFirstMove;
    }
}
