import java.awt.*;

public class Enemy {
   
    protected double x, y, dx, dy;
    protected int r;
    protected double rad;
    protected double speed;
    protected int health;
    
    protected int type;  
    protected int rank;  
    
    protected Color color1;
    protected boolean ready;
    protected boolean dead;
    protected boolean hit;
    protected long hitTimer;

    public Enemy(int type, int rank) {
        this.type = type;
        this.rank = rank; 
        
        
        switch (type) {
            case 1: // Normal Zombie
                color1 = Color.BLUE;
                speed = 1.5;
                r = 10;
                health = 3;
                break;
            case 2: // Sürünge Zombie
                color1 = Color.RED;
                speed = 3.0;
                r = 7;
                health = 1;
                break;
            case 3: // Tank Zombie
                color1 = Color.GREEN;
                speed = 0.5;
                r = 30;
                health = 5;
                break;
            case 4: // Asit Tüküren Zombie
                color1 = Color.ORANGE;
                speed = 1.5;
                r = 8;
                health = 1;
                break;
            default:
                color1 = Color.BLUE;
                speed = 1.5;
                r = 10;
                health = 3;
                break;
        }
        
        x = Math.random() * GamePanel.WIDTH / 2 + GamePanel.WIDTH / 4;
        y = -r;

        double angle = Math.random() * 140 + 20;
        rad = Math.toRadians(angle);

        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;
        
        ready = false;
        dead = false;
        hit = false;
        hitTimer = 0;
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public int getr() {
        return r;
    }

    public int getType() {
        return type;
    }

    public int getRank() {
        return rank;
    }

   
    public void hit() {
        health--;
        if (health <= 0) {
            dead = true;
        }
        hit = true;
        hitTimer = System.nanoTime();
    }

    public void explode() {
        if (type == 4) { 
            double acidRadius = 50; 
            for (Enemy enemy : GamePanel.enemies) {
                if (enemy != this) {
                    double dx = enemy.x - this.x;
                    double dy = enemy.y - this.y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < acidRadius) {
                        enemy.hit();  
                    }
                }
            }
        } else if (rank > 1) {
         
            int amount = 0;
            if (type == 1) {
                amount = 3;
            }
            for (int i = 0; i < amount; i++) {
                Enemy e = new Enemy(this.type, this.rank - 1);
                e.x = this.x;
                e.y = this.y;
                double angle;
                if (!ready) {
                    angle = Math.random() * 140 + 20;
                } else {
                    angle = Math.random() * 360;
                }
                e.rad = Math.toRadians(angle);
                GamePanel.enemies.add(e);
            }
        }
    }

    public boolean isDead() {
        return dead;
    }

    
    public void update() {
        // Sürünge zombi
        if (type == 2 && GamePanel.player != null) {
            double dxPlayer = GamePanel.player.getx() - x;
            double dyPlayer = GamePanel.player.gety() - y;
            double distToPlayer = Math.sqrt(dxPlayer * dxPlayer + dyPlayer * dyPlayer);
            if (distToPlayer < 100) {  
                // Hızını geçici olarak 2 katına çıkararak zıplama etkisi 
                dx = Math.cos(rad) * speed * 2;
                dy = Math.sin(rad) * speed * 2;
            }
        }
        
        x += dx;
        y += dy;

       
        if (!ready) {
            if (x > r && x < GamePanel.WIDTH - r && y > r && y < GamePanel.HEIGHT - r) {
                ready = true;
            }
        }

        if (x < r && dx < 0) {
            dx = -dx;
        }
        if (y < r && dy < 0) {
            dy = -dy;
        }
        if (x > GamePanel.WIDTH - r && dx > 0) {
            dx = -dx;
        }
        if (y > GamePanel.HEIGHT - r && dy > 0) {
            dy = -dy;
        }

        if (hit) {
            long elapsed = (System.nanoTime() - hitTimer) / 1000000;
            if (elapsed > 50) {
                hit = false;
                hitTimer = 0;
            }
        }
    }

    public void draw(Graphics2D g) {
        if (hit) {
            g.setColor(Color.WHITE);
            g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(3));
            g.setColor(Color.WHITE.darker());
            g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));
        } else {
            g.setColor(color1);
            g.fillOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(3));
            g.setColor(color1.darker());
            g.drawOval((int) (x - r), (int) (y - r), 2 * r, 2 * r);
            g.setStroke(new BasicStroke(1));
        }
    }
}