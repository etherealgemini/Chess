package view;


import model.*;
import controller.ClickController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 这个类表示面板上的棋盘组件对象
 */
public class Chessboard extends JComponent {
    public ClickController getClickController() {
        return clickController;
    }

    /**
     * CHESSBOARD_SIZE： 棋盘是8 * 8的
     * <br>
     * BACKGROUND_COLORS: 棋盘的两种背景颜色
     * <br>
     * chessListener：棋盘监听棋子的行动
     * <br>
     * chessboard: 表示8 * 8的棋盘
     * <br>
     * currentColor: 当前行棋方
     */
    private static final int CHESSBOARD_SIZE = 8;

    /**
     * 棋盘坐标本身即记录了该坐标对应棋子类型，使用例：
     * chessComponents[a][b] instanceof EmptySlotComponent
     * chessComponents[a][b] instanceof KingChessComponent
     *
     * 这里chessComponents[a][b]自己就是ChessComponent的一个子类，即，chessComponents[][]是chessComponent类型的二维数组，其中每个元素均为chessComponent
     */
    private final ChessComponent[][] chessComponents = new ChessComponent[CHESSBOARD_SIZE][CHESSBOARD_SIZE];

    private ChessColor currentColor = ChessColor.WHITE;
    //all chessComponents in this chessboard are shared only one model controller
    private final ClickController clickController = new ClickController(this);


    public int getCHESS_SIZE() {
        return CHESS_SIZE;
    }

    public int getCHESSBOARD_SIZE(){
        return CHESSBOARD_SIZE;
    }

    private final int CHESS_SIZE;

    public Chessboard(int width, int height) {
        setLayout(null); // Use absolute layout.
        setSize(width, height);
        CHESS_SIZE = width / 8;
        System.out.printf("chessboard size = %d, chess size = %d\n", width, CHESS_SIZE);

        initiateEmptyChessboard();

        // FIXME: Initialize chessboard for testing only.

        // Y 初始化各类棋子在棋盘上的位置
        initiateAllChessComponents();

    }

    public void initiateAllChessComponents(){
        initPawnOnBoard(1, 0, ChessColor.BLACK);
        initPawnOnBoard(1, 1, ChessColor.BLACK);
        initPawnOnBoard(1, 2, ChessColor.BLACK);
        initPawnOnBoard(1, 3, ChessColor.BLACK);
        initPawnOnBoard(1, 4, ChessColor.BLACK);
        initPawnOnBoard(1, 5, ChessColor.BLACK);
        initPawnOnBoard(1, 6, ChessColor.BLACK);
        initPawnOnBoard(1, CHESSBOARD_SIZE - 1, ChessColor.BLACK);
        initPawnOnBoard(CHESSBOARD_SIZE-2, 0, ChessColor.WHITE);
        initPawnOnBoard(CHESSBOARD_SIZE-2, 1, ChessColor.WHITE);
        initPawnOnBoard(CHESSBOARD_SIZE-2, 2, ChessColor.WHITE);
        initPawnOnBoard(CHESSBOARD_SIZE-2, 3, ChessColor.WHITE);
        initPawnOnBoard(CHESSBOARD_SIZE-2, 4, ChessColor.WHITE);
        initPawnOnBoard(CHESSBOARD_SIZE-2, 5, ChessColor.WHITE);
        initPawnOnBoard(CHESSBOARD_SIZE-2, 6, ChessColor.WHITE);
        initPawnOnBoard(CHESSBOARD_SIZE-2, CHESSBOARD_SIZE-1, ChessColor.WHITE);
        initRookOnBoard(0, 0, ChessColor.BLACK);
        initRookOnBoard(0, CHESSBOARD_SIZE - 1, ChessColor.BLACK);
        initRookOnBoard(CHESSBOARD_SIZE - 1, 0, ChessColor.WHITE);
        initRookOnBoard(CHESSBOARD_SIZE - 1, CHESSBOARD_SIZE - 1, ChessColor.WHITE);
        initKnightOnBoard(0,1,ChessColor.BLACK);
        initKnightOnBoard(0,CHESSBOARD_SIZE - 2,ChessColor.BLACK);
        initKnightOnBoard(CHESSBOARD_SIZE-1,1,ChessColor.WHITE);
        initKnightOnBoard(CHESSBOARD_SIZE-1,CHESSBOARD_SIZE - 2,ChessColor.WHITE);
        initBishopOnBoard(0,2,ChessColor.BLACK);
        initBishopOnBoard(0,CHESSBOARD_SIZE - 3,ChessColor.BLACK);
        initBishopOnBoard(CHESSBOARD_SIZE - 1,2,ChessColor.WHITE);
        initBishopOnBoard(CHESSBOARD_SIZE - 1,CHESSBOARD_SIZE - 3,ChessColor.WHITE);
        initQueenOnBoard(0,3,ChessColor.BLACK);
        initQueenOnBoard(CHESSBOARD_SIZE - 1,3,ChessColor.WHITE);
        initKingOnBoard(0,4,ChessColor.BLACK);
        initKingOnBoard(CHESSBOARD_SIZE - 1, 4,ChessColor.WHITE);


    }




    public ChessComponent[][] getChessComponents() {
        return chessComponents;
    }

    public ChessColor getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(ChessColor currentColor) {
        this.currentColor = currentColor;
    }

    /**
     * 该方法实现放置棋子，并以完全覆盖的方式放置。
     * @param chessComponent
     */
    public void putChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();

        if (chessComponents[row][col] != null) {
            remove(chessComponents[row][col]);
        }
        add(chessComponents[row][col] = chessComponent);
    }

    /**
     * 该方法实现放置棋子，不会覆盖掉原棋子，且棋盘数组中不会存储该棋子信息。目前仅用于放置合法落子点标记。
     * @param chessComponent
     */
    public void addChessOnBoard(ChessComponent chessComponent) {
        int row = chessComponent.getChessboardPoint().getX(), col = chessComponent.getChessboardPoint().getY();
        add(chessComponent);
    }

    public void swapChessComponents(ChessComponent chess1, ChessComponent chess2) {
        // Note that chess1 has higher priority, 'destroys' chess2 if exists.
        if (!(chess2 instanceof EmptySlotComponent)) {
            remove(chess2);
            add(chess2 = new EmptySlotComponent(chess2.getChessboardPoint(), chess2.getLocation(), clickController, CHESS_SIZE));
        }

        chess1.swapLocation(chess2);
        int row1 = chess1.getChessboardPoint().getX(), col1 = chess1.getChessboardPoint().getY();
        chessComponents[row1][col1] = chess1;
        int row2 = chess2.getChessboardPoint().getX(), col2 = chess2.getChessboardPoint().getY();
        chessComponents[row2][col2] = chess2;

        chess1.repaint();
        chess2.repaint();
    }


    public void initiateEmptyChessboard() {
        for (int i = 0; i < chessComponents.length; i++) {
            for (int j = 0; j < chessComponents[i].length; j++) {
                putChessOnBoard(new EmptySlotComponent(new ChessboardPoint(i, j), calculatePoint(i, j), clickController, CHESS_SIZE));
            }
        }
    }

    /**
     * 该方法实现交换执棋方。
     */
    public void swapColor() {
        //黑方先走，每次走棋后换方
        currentColor = currentColor == ChessColor.BLACK ? ChessColor.WHITE : ChessColor.BLACK;
    }

    //for test


    private void initRookOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new RookChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initBishopOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new BishopChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initKnightOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KnightChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initQueenOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new QueenChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initKingOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new KingChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    private void initPawnOnBoard(int row, int col, ChessColor color) {
        ChessComponent chessComponent = new PawnChessComponent(new ChessboardPoint(row, col), calculatePoint(row, col), color, clickController, CHESS_SIZE);
        chessComponent.setVisible(true);
        putChessOnBoard(chessComponent);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }


    private Point calculatePoint(int row, int col) {
        return new Point(col * CHESS_SIZE, row * CHESS_SIZE);
    }

    /**
     * 该方法实现棋盘数据载入
     * FIXME:这里仅print出了你输入的内容。
     * @param chessData 棋盘数据
     */
    public void loadGame(List<String> chessData) {
        chessData.forEach(System.out::println);
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // 黑方的象
                if (chessData.get(i).charAt(j) == 'B'){
                    initBishopOnBoard(i,j,ChessColor.BLACK);
                }
                //白方的象
                if (chessData.get(i).charAt(j) == 'b'){
                    initBishopOnBoard(i,j,ChessColor.WHITE);
                }
                //黑方的王
                if (chessData.get(i).charAt(j) == 'K'){
                   initKingOnBoard(i,j,ChessColor.BLACK);
                }
                //白方的王
                if (chessData.get(i).charAt(j) == 'k'){
                    initKingOnBoard(i,j,ChessColor.WHITE);
                }
                //黑方的骑士
                if (chessData.get(i).charAt(j) == 'N'){
                 initKnightOnBoard(i,j,ChessColor.BLACK);
                }
                //白方的骑士
                if (chessData.get(i).charAt(j) == 'n'){
                 initKnightOnBoard(i,j,ChessColor.WHITE);
                }
                //黑方的兵
                if (chessData.get(i).charAt(j) == 'P'){
                  initPawnOnBoard(i,j,ChessColor.BLACK);
                }
                //白方的兵
                if (chessData.get(i).charAt(j) == 'p'){
                   initPawnOnBoard(i,j,ChessColor.WHITE);
                }
                //黑方的皇后
                if (chessData.get(i).charAt(j) == 'Q'){
                  initQueenOnBoard(i,j,ChessColor.BLACK);
                }
                //白方的皇后
                if (chessData.get(i).charAt(j) == 'q'){
                   initQueenOnBoard(i,j,ChessColor.WHITE);
                }
                //黑方的车
                if (chessData.get(i).charAt(j) == 'R'){
                  initRookOnBoard(i,j,ChessColor.BLACK);
                }
                //白方的车
                if (chessData.get(i).charAt(j) == 'r'){
                   initRookOnBoard(i,j,ChessColor.WHITE);
                }
                //空的，没有棋子
//                if (chessData.get(i).charAt(j) == 'E'){
//
//                }
            }

        }
        //黑方执棋
        if (chessData.get(8).charAt(0) == '0'){
            currentColor = ChessColor.BLACK;
            clickController.getChessGameFrame().getStatusLabel().setText("Black");
        }
        //白方执棋
        if (chessData.get(8).charAt(0) == '1'){
            currentColor = ChessColor.WHITE;
            clickController.getChessGameFrame().getStatusLabel().setText("White");
        }


    }
}
