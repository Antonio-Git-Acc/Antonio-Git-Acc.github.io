#ifndef CIRCLE_H
#define CIRCLE_H

#include "GameObject.h"
#include "Brick.h"

// ==================================================
// Circle - Represents a moving circle in the game
// ==================================================

class Circle : public GameObject {
public:
    float radius;
    float vx, vy;  // velocity components

    // Constructor
    Circle(double startX, double startY, double velocityX, double velocityY,
        float rad, float r, float g, float b);

    // Override virtual methods
    void update() override;
    void render() override;

    // Collision methods
    void checkCollision(Brick* brick);
    bool checkCollisionWithCircle(Circle* other);

    // Merge with another circle
    void mergeWith(Circle* other);

    // Movement
    void moveOneStep();

private:
    // Helper method for wall collision
    void handleWallCollision();
};

#endif // CIRCLE_H