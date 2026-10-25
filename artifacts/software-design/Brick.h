#ifndef BRICK_H
#define BRICK_H

#include "GameObject.h"

// ==================================================
// Brick - Represents a brick in the game
// ==================================================

enum class BrickType {
    REFLECTIVE,
    DESTRUCTIBLE
};

class Brick : public GameObject {
public:
    float width;
    BrickType brickType;
    int hitPoints;
    float initialRed, initialGreen, initialBlue;

    // Constructor
    Brick(BrickType type, float startX, float startY, float brickWidth,
        float r, float g, float b, int hp = GameConstants::BRICK_DEFAULT_HP);

    // Override virtual methods
    void update() override;
    void render() override;

    // Brick-specific methods
    void takeDamage();
    void resetColor();
    bool isDestroyed() const;
};

#endif // BRICK_H