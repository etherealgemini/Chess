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
    /**
     * @Fixme 请按照其他棋子的方式将合法落子点存入arraylist中
     */
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        /**
         * 伪代码：
         * 如果（落点在斜角位置）：
         *      则判断 如果（落点与棋子原位置斜角位置间是否存在棋子）：
         *                  若存在，则：非法
         *                  否则（不存在）：合法落点
         * 否则：非法
         * 存入所有合法落点
         * 检索目标位置是否在合法落点列表中
         * 若是，则可以移动
         * 否则不能移动
         */
        //获取棋子所在棋盘点
        //注意destination为该方法调用时即传入的数据
        ChessboardPoint source = getChessboardPoint();
        ArrayList<ChessboardPoint> legalpoints = new ArrayList<>();
        ChessboardPoint legalpoint = new ChessboardPoint(0,0);



        for (int i = -8; i <8 ; i++) {
            //棋盘坐标系：将标准坐标系顺时针旋转90°
            //斜角判定，为了降低难度，即便计算值超出棋盘也没有关系，demo已经确保click不会记录超出棋盘的位置。
            //1 3象限斜角判定
            //我们将录入所有合法落子点
            if(destination.getX()-i==source.getX()&&destination.getY()-i==source.getY()){
                int row = source.getX();
                int col = source.getY();
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

            int row = source.getX();
            int col = source.getY();
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
