package controller;

import model.*;
import view.Chessboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 该类负责所有与游戏加载、运行等相关功能控制的实现
 */
public class GameController {
    public Chessboard getChessboard() {
        return chessboard;
    }

    private Chessboard chessboard;

    public GameController(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

//Y 写个方法存档
    public void storeGameToFile (String path){

        //存棋子和棋盘坐标
              char [][] coordinate = new char[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {

                //如果棋子为象(B  b)
                if (chessboard.getChessComponents()[i][j] instanceof BishopChessComponent ){
                    //黑白棋
                    if (chessboard.getChessComponents()[i][j].getChessColor() == ChessColor.BLACK ){
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'B';}
                        else {
                            coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'b';
                        }
                    }
              //如果棋子为王（K k）
                if (chessboard.getChessComponents()[i][j] instanceof KingChessComponent){
                    if (chessboard.getChessComponents()[i][j].getChessColor() == ChessColor.BLACK ){
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'K';}
                    else {
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'k';
                    }
                }
             //若棋子为骑士 （N n )
                if (chessboard.getChessComponents()[i][j] instanceof KnightChessComponent){
                    if (chessboard.getChessComponents()[i][j].getChessColor() == ChessColor.BLACK ){
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'N';}
                    else {
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'n';
                    }
                }

            //若棋子为兵（P p)
                if (chessboard.getChessComponents()[i][j] instanceof PawnChessComponent){
                    if (chessboard.getChessComponents()[i][j].getChessColor() == ChessColor.BLACK ){
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'P';}
                    else {
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'p';
                    }
                }

            //若棋子为皇后 (Q q)
                if (chessboard.getChessComponents()[i][j] instanceof QueenChessComponent){
                    if (chessboard.getChessComponents()[i][j].getChessColor() == ChessColor.BLACK ){
                            coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'Q';}
                    else {
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'q';
                    }
                }
            //若棋子为车(R r)
                if (chessboard.getChessComponents()[i][j] instanceof RookChessComponent){
                    if (chessboard.getChessComponents()[i][j].getChessColor() == ChessColor.BLACK ){
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'R';}
                    else {
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'r';
                    }
                }
            //没有棋子，空的(E)
                if (chessboard.getChessComponents()[i][j] instanceof EmptySlotComponent){
                        coordinate[chessboard.getChessComponents()[i][j].getChessboardPoint().getX()][chessboard.getChessComponents()[i][j].getChessboardPoint().getY()] = 'E';}
                }
            }

        //存储当前执棋方
        char player ;
        if (chessboard.getCurrentColor() == ChessColor.BLACK){
            player = '0';
        }
        else {
            player = '1';
        }
        //写入文件
        try {
            FileWriter fileWriter = new FileWriter(new File(path));
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    fileWriter.append(coordinate[i][j]);
                }
                fileWriter.append('\n');
                }
            fileWriter.append(player);
            fileWriter.close();

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }







    /**
     * 该方法将实现存档读取，当然，你可以在这里配合其他方法实现错误存档检测等内容
     * @param path 存档文件路径
     * @return
     */

    public List<String> loadGameFromFile(String path) {
        try {
            List<String> chessData = Files.readAllLines(Path.of(path));
            chessboard.loadGame(chessData);//在该方法中实现游戏数据的载入
            return chessData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
