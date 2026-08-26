import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static int WIDTH = 400;
    public static int HEIGHT = 400;

    private Thread thread;
    private boolean running;
    private boolean paused = false;
    private BufferedImage image;
    private Graphics2D g;

    private int FPS = 30;
    private double averageFPS;

    public static Player player;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<PowerUp> powerups;

    private long waveStartTimer;
    private long waveStartTimerDiff;
    private int waveNumber;
    private boolean waveStart;
    private int waveDelay = 2000;

    public GamePanel() {
        super();
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify(){
        super.addNotify();
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
        addKeyListener(this);
    }

    public void run(){
        running = true;
        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        player = new Player();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Enemy>();
        powerups = new ArrayList<PowerUp>();

        waveStartTimer = 0;
        waveStartTimerDiff = 0;
        waveStart = true;
        waveNumber = 0;

        long startTime;
        long URDTimeMillis;
        long waitTime;
        long totalTime = 0;
        int frameCount = 0;
        int maxFrameCount = 30;
        long targetTime = 1000 / FPS;

        while (running) {
            startTime = System.nanoTime();

            if (!paused) {
                gameUpdate();
            }
            gameRender();
            gameDraw();

            URDTimeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - URDTimeMillis;
            try {
                Thread.sleep(Math.max(waitTime, 5));
            } catch (Exception e) { }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == maxFrameCount) {
                averageFPS = 1000.0 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

    private void gameUpdate(){
        if (player.isDead()) {
            running = false; 
            showGameOverScreen();
            return;
        }

        if (waveStartTimer == 0 && enemies.size() == 0) {
            waveNumber++;
            waveStart = false;
            waveStartTimer = System.nanoTime();
        } else {
            waveStartTimerDiff = (System.nanoTime() - waveStartTimer) / 1000000;
            if (waveStartTimerDiff > waveDelay) {
                waveStart = true;
                waveStartTimer = 0;
                waveStartTimerDiff = 0;
            }
        }

        if (waveStart && enemies.size() == 0) {
            createNewEnemies();
        }

        player.update();

        for (int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update();
            if (remove) {
                bullets.remove(i);
                i--;
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).update();
        }

        for (int i = 0; i < powerups.size(); i++) {
            boolean remove = powerups.get(i).update();
            if (remove) {
                powerups.remove(i);
                i--;
            }
        }

        // Mermi - Zombi collusion
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            double bx = b.getx();
            double by = b.gety();
            double br = b.getr();

            for (int j = 0; j < enemies.size(); j++) {
                Enemy e = enemies.get(j);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();

                double dx = bx - ex;
                double dy = by - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < br + er) {
                    e.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            if (enemies.get(i).isDead()) {
                Enemy e = enemies.get(i);
                double rand = Math.random();

                if (rand < 0.001) {
                    powerups.add(new PowerUp(1, e.getx(), e.gety()));
                } else if (rand < 0.002) {
                    powerups.add(new PowerUp(3, e.getx(), e.gety()));
                } else if (rand < 0.120) {
                    powerups.add(new PowerUp(2, e.getx(), e.gety()));
                }
                player.addScore(e.getType() + e.getRank());
                enemies.remove(i);
                i--;
                e.explode();
            }
        }

        // Oyuncu ile zombi arasındaki collusion
        if (!player.isRecovering()) {
            int px = player.getx();
            int py = player.gety();
            int pr = player.getr();
            for (int i = 0; i < enemies.size(); i++) {
                Enemy e = enemies.get(i);
                double ex = e.getx();
                double ey = e.gety();
                double er = e.getr();
                double dx = px - ex;
                double dy = py - ey;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < pr + er) {
                    player.loseLife();
                }
            }
        }

        // Oyuncu ile powerup collusion
        int px = player.getx();
        int py = player.gety();
        int pr = player.getr();
        for (int i = 0; i < powerups.size(); i++) {
            PowerUp p = powerups.get(i);
            double x = p.getx();
            double y = p.gety();
            double r = p.getr();
            double dx = px - x;
            double dy = py - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < pr + r) {
                int type = p.getType();
                if (type == 1) {
                    player.gainLife();
                }
                if (type == 2) {
                    player.increasePower(1);
                }
                if (type == 3) {
                    player.increasePower(2);
                }
                powerups.remove(i);
                i--;
            }
        }

        if (bullets.size() > 100) {
            bullets.remove(0);
        }
        if (enemies.size() > 50) {
            enemies.remove(0);
        }
    }

    private void gameRender(){
        
        g.setColor(new Color(50, 50, 50));
        g.fillRect(0, 0, WIDTH, HEIGHT);

        player.draw(g);

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).draw(g);
        }

        for (int i = 0; i < powerups.size(); i++) {
            powerups.get(i).draw(g);
        }

        if (waveStartTimer != 0) {
            g.setFont(new Font("Century Gothic", Font.PLAIN, 18));
            String s = "- W A V E   " + waveNumber + "   -";
            int length = (int) g.getFontMetrics().getStringBounds(s, g).getWidth();
            int alpha = (int) (255 * Math.sin(3.14 * waveStartTimerDiff / waveDelay));
            if (alpha > 255) { alpha = 255; }
            g.setColor(new Color(255, 255, 255, alpha));
            g.drawString(s, WIDTH / 2 - length / 2, HEIGHT / 2);
        }

     
        for (int i = 0; i < player.getLives(); i++) {
            g.setColor(Color.WHITE);
            g.fillOval(WIDTH - 100 + (20 * i), 30, player.getr() * 2, player.getr() * 2);
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.WHITE.darker());
            g.drawOval(WIDTH - 100 + (20 * i), 30, player.getr() * 2, player.getr() * 2);
            g.setStroke(new BasicStroke(1));
        }

       
        g.setColor(Color.YELLOW);
        g.fillRect(20, 30, player.getPower() * 8, 8);
        g.setColor(Color.YELLOW.darker());
        g.setStroke(new BasicStroke(2));
        for (int i = 0; i < player.getRequiredPower(); i++) {
            g.drawRect(20 + 8 * i, 30, 8, 8);
        }
        g.setStroke(new BasicStroke(1));

       
        g.setColor(Color.WHITE);
        g.setFont(new Font("Century Gothic", Font.PLAIN, 14));
        g.drawString("Score: " + player.getScore(), WIDTH - 100, 20);

        if(waveNumber > 10) {
            player.setCurrentWeapon(new Roketatar());
        }
        else if(waveNumber > 5) {
            player.setCurrentWeapon(new KeskinNisanciTufegi());
        }
        else if (waveNumber > 3) {
            player.setCurrentWeapon(new PompaliTufek());
        }
        else if (waveNumber > 1) {
            player.setCurrentWeapon(new PiyadeTufegi());
        }

       
        String weaponInfo = "Silah: " + player.getCurrentWeapon().toString() +
                            " | Mermi: " + player.getCurrentWeapon().getAmmo() +
                            "/" + player.getCurrentWeapon().getMagazineCapacity();
        g.drawString(weaponInfo, 20, 20);

        if (paused) {
            g.setColor(Color.RED.darker());
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("PAUSED", getWidth() / 2 - 80, getHeight() / 2);
            return; 
        }

        if (!running) { 
            showGameOverScreen();
            return;
        }
    }

    private void gameDraw(){
        Graphics g2 = this.getGraphics();
        if (g2 != null && image != null) {
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
        }
    }

    // Dalga numarasına göre yeni zombi oluşturma
    private void createNewEnemies(){
        enemies.clear();
        // Normal Zombi (mavi)
        for (int i = 0; i < 3 * waveNumber; i++) {
            enemies.add(new Enemy(1, 1));
        }
        // Sürünge Zombi (kırmızı)
        if(waveNumber > 1){
            for (int i = 0; i < 2 * (waveNumber - 1); i++) {
                enemies.add(new Enemy(2, 1));
            }
        }
        // Tank Zombi (yeşil)
        if(waveNumber > 2){
            for (int i = 0; i < 1 * (waveNumber - 2); i++) {
                enemies.add(new Enemy(3, 1));
            }
        }
        // Asit Tüküren Zombi (turuncu)
        if(waveNumber > 3){ 
            for (int i = 0; i < 1 * (waveNumber - 3); i++) {
            enemies.add(new Enemy(4, 1));
            }
        }
    }

    public void keyPressed(KeyEvent key) {
        int keyCode = key.getKeyCode();

        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(true);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(true);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(true);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(true);
        }
        if (keyCode == KeyEvent.VK_Z) {
            player.setFiring(true);
        }
        // R tuşu reload için
        if (keyCode == KeyEvent.VK_R) {
            player.reloadWeapon();
        }
        // P tuşu pause için
        if (keyCode == KeyEvent.VK_P) {
            togglePause();
        }

        if (!running && keyCode == KeyEvent.VK_S) {
            restartGame();
        }
    }

    public void keyReleased(KeyEvent key) {
        int keyCode = key.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT) {
            player.setLeft(false);
        }
        if (keyCode == KeyEvent.VK_RIGHT) {
            player.setRight(false);
        }
        if (keyCode == KeyEvent.VK_UP) {
            player.setUp(false);
        }
        if (keyCode == KeyEvent.VK_DOWN) {
            player.setDown(false);
        }
        if (keyCode == KeyEvent.VK_Z) {
            player.setFiring(false);
        }
    }

    public void togglePause(){
        paused = !paused;
    }

    public void saveGame(){
        System.out.println("Oyun kaydedildi (stub).");
    }

    public void loadGame(){
        System.out.println("Oyun yüklendi (stub).");
    }

    private void showGameOverScreen() {
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(new Color(50, 0, 0, 180)); 
        g.fillRect(0, 0, WIDTH, HEIGHT);
        
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String text = "GAME OVER";
        int length = g.getFontMetrics().stringWidth(text);
        g.drawString(text, WIDTH/2 - length/2, HEIGHT/2);
    
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        text = "Skor: " + player.getScore();
        length = g.getFontMetrics().stringWidth(text);
        g.drawString(text, WIDTH/2 - length/2, HEIGHT/2 + 40);
    }

    public void restartGame() {
        player = new Player();
        enemies.clear();
        bullets.clear();
        powerups.clear();
        waveNumber = 0;
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
    }
}