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
    private boolean firstMove = true;
    private boolean doubleMove = true;

    public void setBypass(boolean bypass) {
        this.bypass = bypass;
    }

    private boolean bypass = false;
    private ChessComponent bypassPawn=null;
    private boolean firstBypass = false;


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

    public PawnChessComponent(ChessboardPoint chessboardPoint, Point location, ChessColor color, ClickController listener, int size, boolean firstBypass, boolean firstMove, boolean doubleMove, boolean bypass, ChessComponent bypassPawn){
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

    /**
     * 兵棋子的移动规则
     *
     * @param chessComponents 棋盘
     * @param destination     目标位置，如(0, 0), (0, 7)等等
     * @return 兵棋子移动的合法性
     */

    @Override
    public boolean canMoveTo(ChessComponent[][] chessComponents, ChessboardPoint destination) {
        //wait until the ass5 over. I have already done the method, and I will move back when the ass5 is over.
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
