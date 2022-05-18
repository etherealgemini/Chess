import view.ChessGameFrame;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Music audioPlayWave = new Music("images\\小星星变奏曲.wav");// 开音乐 音樂名
            audioPlayWave.start();
            @SuppressWarnings("unused")
            int musicOpenLab = 1;
            ChessGameFrame mainFrame = new ChessGameFrame(1000, 760);
            mainFrame.setVisible(false);

            ChessGameFrame startFrame = new ChessGameFrame(500);
            startFrame.setVisible(true);
           startFrame.getStartGame().addActionListener(e ->{
               mainFrame.setVisible(true);
               startFrame.setVisible(false);
           });
        });




    }
}
