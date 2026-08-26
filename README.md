# Top Down Shooter

A 2D top-down survival shooter built in Java (Swing/AWT), with a wave-based enemy system, multiple enemy types, a weapon-progression system, and power-ups. Built as a course project.

## Gameplay

- Survive incoming waves of enemies, which grow progressively larger and more varied as waves progress.
- Move with the arrow keys, aim/fire with `Z`, reload with `R`, pause with `P`.
- The player's weapon automatically upgrades as waves progress (Pistol → Assault Rifle → Shotgun → Sniper Rifle → Rocket Launcher).
- Collect power-ups dropped by defeated enemies: extra lives and weapon power upgrades.
- The player has 3 lives and a brief invulnerability window after taking damage.

## Enemy types

| Type | Behavior |
|---|---|
| Normal | Baseline enemy; splits into 3 smaller copies of itself when a higher-rank version is destroyed |
| Crawler | Moves faster and lunges toward the player when in range |
| Tank | Slow but high health |
| Acid Spitter | Deals area-of-effect damage to nearby enemies when destroyed |

## Weapon system

All weapons share a common `Weapon` base class (magazine capacity, fire rate, reload logic) and override `fire()` with their own firing pattern:

| Class (source name) | Weapon | Behavior |
|---|---|---|
| `Tabanca` | Pistol | Starting weapon, single shot |
| `PiyadeTufegi` | Assault Rifle | Automatic fire with slight spread |
| `PompaliTufek` | Shotgun | Fires 9 pellets in a spread pattern per shot |
| `KeskinNisanciTufegi` | Sniper Rifle | High-damage piercing shot that can hit multiple enemies |
| `Roketatar` | Rocket Launcher | Explosive projectile with area-of-effect damage |

## Tech stack

Java (Swing/AWT for rendering and input) — no external dependencies.

## Repository contents

```
src/
├── Game.java               # Entry point, window/menu setup
├── GamePanel.java          # Game loop, rendering, collision detection, wave spawning
├── Player.java             # Player state, movement, weapon handling, lives/score
├── Enemy.java              # Enemy types, movement, splitting/explosion behavior
├── Bullet.java             # Projectile logic per weapon type
├── PowerUp.java            # Power-up types and pickup logic
├── Weapon.java             # Abstract base class for all weapons
├── Tabanca.java            # Pistol
├── PiyadeTufegi.java       # Assault Rifle
├── PompaliTufek.java       # Shotgun
├── KeskinNisanciTufegi.java # Sniper Rifle
└── Roketatar.java          # Rocket Launcher
```

## Running the game

```
javac src/*.java -d out
java -cp out Game
```

## Notes

This was a course project focused on object-oriented design (inheritance and polymorphism across the weapon and enemy classes) and a real-time game loop, rather than production software architecture.
