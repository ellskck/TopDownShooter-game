public class KeskinNisanciTufegi extends Weapon {
    public KeskinNisanciTufegi() {
        super(5, 30);
    }
    
    @Override
    public void fire(int direction, int x, int y) {
        if (canFire()) {
            GamePanel.bullets.add(new Bullet(direction, x, y, "SNIPER"));
            ammoInMagazine--;
        }
    }
    
    @Override
    public String toString() {
        return "Keskin Nişancı Tüfeği";
    }
}