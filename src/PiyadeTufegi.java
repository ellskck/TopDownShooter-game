public class PiyadeTufegi extends Weapon {
    public PiyadeTufegi() {
        super(30, 600);
    }

    @Override
    public void fire(int direction, int x, int y) {
        if (canFire()) {
            int deviation = (int)(Math.random() * 31) - 15; 
            int newDirection = direction + deviation;
            GamePanel.bullets.add(new Bullet(newDirection, x, y, "DEFAULT"));
            ammoInMagazine--;
            setLastFiredTime(System.nanoTime());
        }
    }
    
    @Override
    public String toString() {
        return "Piyade Tüfeği";
    }
}