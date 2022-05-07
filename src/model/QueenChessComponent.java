package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 这个类表示国际象棋里面的后
 */

public class QueenChessComponent extends ChessComponent {
    private static Image QUEEN_WHITE;
    private static Image QUEEN_BLACK;

    private Image queenImage;

    /**
     * 读取加载车棋子的图片
     *
     * @throws IOException
     */
    @Override
    public void loadResource() throws IOException {
        if (QUEEN_WHITE == null) {
            QUEEN_WHITE = ImageIO.read(new File("./images/queen-white.png"));
        }

        if (QUEEN_BLACK == null) {
            QUEEN_BLACK = ImageIO.read(new File("./images/queen-black.png"));
        }
    }

    /**
     * 在构造棋子对象的时候，调用此方法以根据颜色确定rookImage的图片是哪一种
     *
     * @param color 棋子颜色
     */

    private void initiateQueenImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                queenImage = QUEEN_WHITE;
            } else if (color == ChessColor.BLACK) {
                queenImage = QUEEN_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public QueenChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateQueenImage(color);
    }

    /**
     * 后棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 后棋子移动的合法性
     */

    @Override

    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        ArrayList<ChessboardPoint> legalpoints = new ArrayList<>();
        ChessboardPoint legalpoint = new ChessboardPoint(0,0);
        /**
         * 伪代码：
         * rook+bishop
         */
        //rook检测
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
//Bishop check
        for (int i = -8; i <8 ; i++) {
            //棋盘坐标系：将标准坐标系顺时针旋转90°
            //斜角判定，为了降低难度，即便计算值超出棋盘也没有关系，demo已经确保click不会记录超出棋盘的位置。
            //1 3象限斜角判定
            //我们将录入所有合法落子点
            if(destination.getX()-i==source.getX()&&destination.getY()-i==source.getY()){

                //朝左上角检索
                for (int j = 1; j < 8; j++) {
                    if(row-j<0||col-j<0){
                        break;//防止越界
                    }
//                        if(row-j>7||col-j>7){
//                            continue;
//                        }
                    //一旦遭遇第一个友方棋子则停止，敌方棋子则将该子位置录入后停止
                    if(!(chessComponents[row-j][col-j] instanceof  EmptySlotComponent)) {
                        if ((chessComponents[row - j][col - j].getChessColor() != chessColor)) {
                            legalpoint = new ChessboardPoint(row - j, col - j);
                            legalpoints.add(legalpoint);
                        }
                        break;
                    }
                    legalpoint = new ChessboardPoint(row-j,col-j);
                    legalpoints.add(legalpoint);
                }
                for (int j = -1; j > -8; j--) {
                    if(row-j>7||col-j>7){
                        break;//防止越界
                    }
//                    if(row-j<0||col-j<0){
//                        continue;
//                    }
                    //一旦遭遇第一个友方棋子则停止，敌方棋子则将该子位置录入后停止
                    if(!(chessComponents[row-j][col-j] instanceof  EmptySlotComponent)) {
                        if ((chessComponents[row - j][col - j].getChessColor() != chessColor)) {
                            legalpoint = new ChessboardPoint(row - j, col - j);
                            legalpoints.add(legalpoint);
                        }
                        break;
                    }
                    legalpoint = new ChessboardPoint(row-j,col-j);
                    legalpoints.add(legalpoint);
                }



//                    //不需要考虑落点与棋子重合，clickController已经做好判定了。
//                    //同样地，range==-1将跳过。
//                    for (int j = range; j < -1; j++) {
//                        if(!(chessComponents[row-j-1][col-j-1] instanceof  EmptySlotComponent)){
//                            break;
//                        }
//                        legalpoint = new ChessboardPoint(row-j-1,col-j-1);
//                        legalpoints.add(legalpoint);
//                    }
//
//                //若运行到此处，则表示：满足斜角，没有越过棋子

            }
            //2 4象限斜角判定


            //若range为正，则说明source在destination的右下方，反之同理。指针永远从source朝destination移动。
            // 注意2 4象限斜角判定存在方向问题，向↖移动为 x-a，y+a 反之 x+a，y-a


            //特别地，若range==1，for代码块将自动跳过，表示落点恰好挨着棋子原点，无需判定。
            //向左上方移动
            for (int j = 1; j < 8; j++) {
                if(row-j<0||col+j>7){
                    break;
                }
                if(!(chessComponents[row-j][col+j] instanceof  EmptySlotComponent)){
                    if ((chessComponents[row - j][col + j].getChessColor() != chessColor)) {
                        legalpoint = new ChessboardPoint(row - j, col + j);
                        legalpoints.add(legalpoint);
                    }
                    break;
                }
                legalpoint = new ChessboardPoint(row-j,col+j);
                legalpoints.add(legalpoint);
            }


            //不需要考虑落点与棋子重合，clickController已经做好判定了。
            //同样地，range==-1将跳过。
            //向右上方移动
            for (int j = -1; j > -8; j--) {
                if(row-j>7||col+j<0){
                    break;
                }
//                        System.out.println("j="+j+" row="+row+" col="+col);
                if(!(chessComponents[row-j][col+j] instanceof  EmptySlotComponent)){
                    if ((chessComponents[row - j][col - j].getChessColor() != chessColor)) {
                        legalpoint = new ChessboardPoint(row - j, col + j);
                        legalpoints.add(legalpoint);
                    }
                    break;
                }
                legalpoint = new ChessboardPoint(row-j,col+j);
                legalpoints.add(legalpoint);
            }


        }

        for (int i = 0; i < legalpoints.size(); i++) {
            if(destination.getX()==legalpoints.get(i).getX()&&destination.getY()==legalpoints.get(i).getY()){
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
//        g.drawImage(rookImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(queenImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }


}
