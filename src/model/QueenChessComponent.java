package model;

import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

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
    /**
     * @Fixme 请按照其他棋子的方式将合法落子点存入arraylist中
     */
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        ChessboardPoint source = getChessboardPoint();
        /**
         * 伪代码：
         * rook+bishop
         */
        //rook检测
        if (source.getX() == destination.getX()) {
            //若在同一行:
            int row = source.getX();
            //检索该行中*由棋子位置*到*落点位置*之间是否存在棋子，若存在则该次移动非法，即*检测是否越过棋子移动*
            for (int col = Math.min(source.getY(), destination.getY()) + 1;
                 col < Math.max(source.getY(), destination.getY()); col++) {

                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        }
        else if (source.getY() == destination.getY()) {
            int col = source.getY();
            for (int row = Math.min(source.getX(), destination.getX()) + 1;
                 row < Math.max(source.getX(), destination.getX()); row++) {
                if (!(chessComponents[row][col] instanceof EmptySlotComponent)) {
                    return false;
                }
            }
        }
        else { // Not on the same row or the same column. bishop test
            for (int i = -8; i <8 ; i++) {
                //棋盘坐标系：将标准坐标系沿x轴翻折
                //斜角判定，为了降低难度，即便计算值超出棋盘也没有关系，demo已经确保click不会记录超出棋盘的位置。
                //1 3象限斜角判定
                //我们将判定两点间是否存在棋子集成到此处
                //NOTICE: 不要在此处判定落点是否含棋子！
                if(destination.getX()-i==source.getX()&&destination.getY()-i==source.getY()){

                    int row = source.getX();
                    int col = source.getY();
                    //若range为正，则说明source在destination的右上方，反之同理。指针永远从source朝destination移动。
                    int range = source.getX()- destination.getX();
                    if(range>0){

                        //特别地，若range==1，for代码块将自动跳过，表示落点恰好挨着棋子原点，无需判定。
                        for (int j = 1; j < range; j++) {
                            if(!(chessComponents[row-j][col-j] instanceof  EmptySlotComponent)){
                                return false;
                            }
                        }
                    }
                    else{

                        //不需要考虑落点与棋子重合，clickController已经做好判定了。
                        //同样地，range==-1将跳过。
                        for (int j = range; j < -1; j++) {
                            if(!(chessComponents[row-j-1][col-j-1] instanceof  EmptySlotComponent)){
                                return false;
                            }
                        }
                    }
                    //若运行到此处，则表示：满足斜角，没有越过棋子
                    return true;
                }
                //1 3象限斜角判定
                if((destination.getX()+i==source.getX()&&destination.getY()-i==source.getY())|(destination.getX()-i==source.getX()&&destination.getY()+i==source.getY())){

                    int row = source.getX();
                    int col = source.getY();
                    //若range为正，则说明source在destination的右下方，反之同理。指针永远从source朝destination移动。
                    // 注意1 3象限斜角判定存在方向问题，向↖移动为 x-a，y+a 反之 x+a，y-a
                    int range = source.getX()- destination.getX();
                    if(range>0){
                        //特别地，若range==1，for代码块将自动跳过，表示落点恰好挨着棋子原点，无需判定。
                        //向左上方移动
                        for (int j = 1; j < range; j++) {

                            if(!(chessComponents[row-j][col+j] instanceof  EmptySlotComponent)){
                                return false;
                            }
                        }
                    }
                    else{
                        //不需要考虑落点与棋子重合，clickController已经做好判定了。
                        //同样地，range==-1将跳过。
                        //向右下方移动
                        for (int j = range; j < -1; j++) {
                            System.out.println("j="+j+" row="+row+" col="+col);
                            if(!(chessComponents[row-j][col+j] instanceof  EmptySlotComponent)){
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }
        return true;



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
