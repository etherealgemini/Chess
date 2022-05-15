package controller;

import model.*;
import view.Chessboard;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
            File file = new File(path);
            if (!file.exists()){
                if (file.mkdirs()){
                    System.out.println("创建"+path + "成功");
                }
                else {
                    System.out.println("创建"+path+"失败");
                }
            }
            String finalPath = path + "\\GameFile.txt";

            file = new File(finalPath);
            if (!file.exists()){
               if(file.createNewFile()) {
                   System.out.println("创建"+path + "成功");
               }
               else {
                   System.out.println("创建"+path+"失败");
               }
            }

            FileWriter fileWriter = new FileWriter(finalPath);
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

    public String loadGameFromFile(String path) {
        try {
            path = path + "\\GameFile.txt";
            List<String> chessData = Files.readAllLines(Path.of(path));
            //判断棋盘是不是8*8
           boolean isEightColumn = true;
            for (int i = 0; i < 8; i++) {
                if (chessData.get(i).length() != 8) {
                    isEightColumn = false;
                    break;
                }
            }
            if (!isEightColumn || chessData.size() !=9){

                return "错误类型：101";
            }
            else {
            //判断棋子是否六种棋子之一
                boolean isChessPieces = true;
                int up = 0;
                int low = 0;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    char c = chessData.get(i).charAt(j);
                  if (c!='B' && c!='K'&&c!='N'&& c!='P'&& c!='Q'&& c!='R'&& c!='E'&& c!='b'&& c!='k'&& c!='n'&& c!='p'&& c!='q'&& c!='r'){
                      isChessPieces = false;
                      break;
                  }
                  if (Character.isUpperCase(c)){
                      up++;
                  }
                  if (Character.isLowerCase(c)){
                      low++;
                  }
                }
            }
            if (!isChessPieces || up == 0 || low == 0){

                return "错误类型：102";
            }
            else {
               if (chessData.size() < 9 || chessData.get(8).length()!= 1 || (chessData.get(8).charAt(0)!='0' &&chessData.get(8).charAt(0)!= '1')){
                   return "错误类型：103";
                }
            }
            }

            chessboard.loadGame(chessData);//在该方法中实现游戏数据的载入
            return "Successful!";
        } catch (IOException e) {
            e.printStackTrace();

        }
        return "错误类型：104";
    }

}
