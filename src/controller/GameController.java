package controller;

import view.Chessboard;

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
