import view.ChessGameFrame;

import javax.swing.*;


public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
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
