public class Tabanca extends Weapon {
    public Tabanca() {
        super(12, 120); 
    }

    @Override
    public void fire(int direction, int x, int y) {
        if (canFire()) {
            GamePanel.bullets.add(new Bullet(direction, x, y, "DEFAULT"));
            ammoInMagazine--;
            setLastFiredTime(System.nanoTime());
        }
    }

    @Override
    public String toString() {
        return "Tabanca";
    }
}