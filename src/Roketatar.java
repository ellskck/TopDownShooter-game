public class Roketatar extends Weapon {
    public Roketatar() {
        super(1, 10);
    }
    
    @Override
    public void fire(int direction, int x, int y) {
        if (canFire()) {
            GamePanel.bullets.add(new Bullet(direction, x, y, "ROCKET"));
            ammoInMagazine--;
        }
    }
    
    @Override
    public String toString() {
        return "Roketatar";
    }
}