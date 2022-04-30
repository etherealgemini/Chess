package model;

import view.Chessboard;
import view.ChessboardPoint;
import controller.ClickController;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * 这个类表示国际象棋里面的象
 */

public class BishopChessComponent extends ChessComponent {
    private static Image BISHOP_WHITE;
    private static Image BISHOP_BLACK;
    private Image bishopImage;

    @Override
    public void loadResource() throws IOException {
        if (BISHOP_WHITE == null) {
            BISHOP_WHITE = ImageIO.read(new File("./images/bishop-white.png"));
        }

        if (BISHOP_BLACK == null) {
            BISHOP_BLACK = ImageIO.read(new File("./images/bishop-black.png"));
        }
    }

    private void initiateBishopImage(ChessColor color) {
        try {
            loadResource();
            if (color == ChessColor.WHITE) {
                bishopImage = BISHOP_WHITE;
            } else if (color == ChessColor.BLACK) {
                bishopImage = BISHOP_BLACK;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BishopChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size) {
        super(chessboardPoint, location, color, listener, size);
        initiateBishopImage(color);
    }

    /**
     * 象棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 象棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        //获取棋子所在棋盘点
        //注意destination为该方法调用时即传入的数据
        ChessboardPoint source = getChessboardPoint();
        /**
         * 伪代码：
         * 如果（落点在斜角位置）：
         *      则判断 如果（落点与棋子原位置斜角位置间是否存在棋子）：
         *                  若存在，则：非法
         *                  否则（不存在）：合法落点
         * 否则：非法
         */

        //设置flag标记
        boolean firstflag = false;

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

        //若程序能运行到此处，表明非斜角走棋，非法
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        g.drawImage(bishopImage, 0, 0, getWidth() - 13, getHeight() - 20, this);
        g.drawImage(bishopImage, 0, 0, getWidth() , getHeight(), this);
        g.setColor(Color.BLACK);
        if (isSelected()) { // Highlights the model if selected.
            g.setColor(Color.RED);
            g.drawOval(0, 0, getWidth() , getHeight());
        }
    }
}
