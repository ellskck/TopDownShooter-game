import java.awt.*;

public class Player {
    private int x, y, r, dx, dy;
    private int speed;
    private int lives;
    private boolean left, right, up, down;
    private boolean firing;
    private int firingDirection;
    private long recoveryTimer;
    public boolean recovering;
    private Color color1, color2;
    private int score; 
    private int powerLevel;
    private int power;
    private int[] requiredPower = {1, 2, 3, 4, 5};
    private Weapon currentWeapon;
    private boolean isDead;

    public Player() {
        x = GamePanel.WIDTH / 2;
        y = GamePanel.HEIGHT / 2;
        r = 5;
        dx = 0;
        dy = 0;
        speed = 5;
        lives = 3;
        color1 = Color.WHITE;
        color2 = Color.RED;

        firing = false;
        firingDirection = 270;
        recovering = false;
        recoveryTimer = 0;
        score = 0;
        powerLevel = 0;
        power = 0;

        // Oyuna Tabanca ile başla
        currentWeapon = new Tabanca();
    }

    public int getx() { return x; }
    public int gety() { return y; }
    public int getr() { return r; }
    public int getScore() { return score; }
    public int getLives() { return lives; }
    public boolean isRecovering() { return recovering; }

    public void setLeft(boolean b) { left = b; }
    public void setRight(boolean b) { right = b; }
    public void setUp(boolean b) { up = b; }
    public void setDown(boolean b) { down = b; }
    public void setFiring(boolean b) { firing = b; }
    public void setFiringDirection(int direction) { firingDirection = direction; }

    public void addScore(int i) { score += i; }
    public void gainLife() { lives++; }
    public void loseLife() {
        lives--;
        if (lives <= 0) {
            isDead = true;
        } else {
            recovering = true;
            recoveryTimer = System.nanoTime();
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public void increasePower(int i) {
        power += i;
        if (power >= requiredPower[powerLevel]) {
            power -= requiredPower[powerLevel];
            powerLevel++;
        }
    }

    public int getPowerLevel() { return powerLevel; }
    public int getPower() { return power; }
    public int getRequiredPower() { return requiredPower[powerLevel]; }

    public Weapon getCurrentWeapon() { return currentWeapon; }

    public Weapon setCurrentWeapon(Weapon currentWeapon) {
        this.currentWeapon = currentWeapon;
        return currentWeapon;
    }

    // R tuşu ile silahın yeniden doldurulması için metod
    public void reloadWeapon() {
        currentWeapon.reload();
    }

    public void update() {
        // Hareket kontrolleri
        if (left) { dx = -speed; }
        if (right) { dx = speed; }
        if (up) { dy = -speed; }
        if (down) { dy = speed; }

        x += dx;
        y += dy;

        // Ekran sınırları kontrolü
        if (x < r) { x = r; }
        if (y < r) { y = r; }
        if (x > GamePanel.WIDTH - r) { x = GamePanel.WIDTH - r; }
        if (y > GamePanel.HEIGHT - r) { y = GamePanel.HEIGHT - r; }

        dx = 0;
        dy = 0;

        // Ateş etme: mevcut silahın fire() metodu çağrılıyor.
        if (firing) {
            currentWeapon.fire(firingDirection, x, y);
        }

        // İyileşme süresi kontrolü
        if (recovering) {
            long elapsed = (System.nanoTime() - recoveryTimer) / 1000000;
            if (elapsed > 2000) {
                recovering = false;
            }
        }
    }

    public void draw(Graphics2D g) {
        if (recovering) {
            g.setColor(color2);
        } else {
            g.setColor(color1);
        }
        g.fillOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(3));
        g.setColor(g.getColor().darker());
        g.drawOval(x - r, y - r, 2 * r, 2 * r);
        g.setStroke(new BasicStroke(1));
    }
}