import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Game {
    public static void main(String[] args) {
        JFrame window = new JFrame("Top Down Shooter Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem pauseItem = new JMenuItem("Duraklat");
        JMenuItem saveItem = new JMenuItem("Kaydet");
        JMenuItem loadItem = new JMenuItem("Yükle");

        menu.add(pauseItem);
        menu.add(saveItem);
        menu.add(loadItem);
        menuBar.add(menu);
        window.setJMenuBar(menuBar);

        GamePanel gamePanel = new GamePanel();
        window.setContentPane(gamePanel);

        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);

        pauseItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                gamePanel.togglePause();
            }
        });
        saveItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                gamePanel.saveGame();
            }
        });
        loadItem.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                gamePanel.loadGame();
            }
        });
    }
}