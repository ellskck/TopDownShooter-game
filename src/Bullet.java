import java.awt.*;
import java.util.List;

public class Bullet {
    private double x, y, dx, dy;
    private int r;
    private double speed;
    private double rad;
    private Color color1;
    private boolean isSniperBullet; 
    private boolean isRocket; 
    private double explosionRad;
    private int piercingCount;

    public Bullet(double angle, int x, int y, String bulletType) {
        this.x = x;
        this.y = y;
        this.rad = Math.toRadians(angle);

        
        switch (bulletType) {
            case "SNIPER":
                this.isSniperBullet = true;
                this.r = 3;
                this.speed = 15;
                this.color1 = Color.RED;
                this.piercingCount = 3; 
                break;

            case "ROCKET":
                this.isRocket = true;
                this.r = 6; 
                this.speed = 8;
                this.color1 = Color.ORANGE;
                this.explosionRad = 50; 
                break;

            default: 
                this.r = 2;
                this.speed = 10;
                this.color1 = Color.YELLOW;
        }

        this.dx = Math.cos(rad) * speed;
        this.dy = Math.sin(rad) * speed;
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public double getr() {
        return r;
    }

    public boolean update() {
        x += dx;
        y += dy;

        if(x < -r || x > GamePanel.WIDTH + r || y < -r || y > GamePanel.HEIGHT + r){
            return true;
        }

        return false;
    }

    public void draw(Graphics2D g) {
        g.setColor(color1);
        g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
    }

    
    public boolean checkForZombieCollision(List<Enemy> enemies) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            double dist = Math.sqrt(Math.pow(e.getx() - x, 2) + Math.pow(e.gety() - y, 2));
    
            if (dist < r + e.getr() && !e.isDead()) {
                e.hit();
    
               
                if (isRocket) {
                    for (Enemy other : enemies) {
                        double explosionDist = Math.sqrt(Math.pow(other.getx() - x, 2) + Math.pow(other.gety() - y, 2));
                        if (explosionDist < explosionRad && other != e) {
                            other.hit();
                        }
                    }
                    return true; 
                }
    
            
                if (isSniperBullet && --piercingCount > 0) {
                    continue; 
                }
                return true;
            }
        }
        return false; 
    }
}