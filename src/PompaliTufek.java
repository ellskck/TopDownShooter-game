public class PompaliTufek extends Weapon {
    public PompaliTufek() {
        super(5, 60);
    }
    
    @Override
    public void fire(int direction, int x, int y) {
        if (canFire()) {
            int[] offsets = {-20, -15, -10, -5, 0, 5, 10, 15, 20};
            for (int offset : offsets) {
                GamePanel.bullets.add(new Bullet(direction + offset, x, y, "DEFAULT"));
            }
            ammoInMagazine--;
            setLastFiredTime(System.nanoTime());
        }
    }
    
    @Override
    public String toString() {
        return "Pompalı Tüfek";
    }
}