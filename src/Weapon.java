public abstract class Weapon {
    protected int magazineCapacity;
    protected int ammoInMagazine;
    protected int fireRate; 
    protected long lastFiredTime;
    protected long firingDelay;

    public Weapon(int magazineCapacity, int fireRate) {
        this.magazineCapacity = magazineCapacity;
        this.fireRate = fireRate;
        this.ammoInMagazine = magazineCapacity;
        this.lastFiredTime = 0;
        this.firingDelay = 60000 / fireRate;
    }

    public boolean canFire() {
        long elapsed = (System.nanoTime() - lastFiredTime) / 1000000;
        return elapsed >= firingDelay && ammoInMagazine > 0;
    }

    public abstract void fire(int direction, int x, int y);

    public void reload() {
        ammoInMagazine = magazineCapacity;
    }

    public int getAmmo() {
        return ammoInMagazine;
    }

    public int getMagazineCapacity() {
        return magazineCapacity;
    }

    protected void setLastFiredTime(long time) {
        lastFiredTime = time;
    }
}