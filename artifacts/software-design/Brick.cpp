#include "Brick.h"

// Constructor
Brick::Brick(BrickType type, float startX, float startY, float brickWidth,
    float r, float g, float b, int hp)
    : GameObject(startX, startY, r, g, b),
    width(brickWidth),
    brickType(type),
    hitPoints(hp),
    initialRed(r),
    initialGreen(g),
    initialBlue(b) {
}

// Update method (bricks don't move, so this is empty)
void Brick::update() {
    // Bricks are static, no update needed
}

// Render the brick
void Brick::render() {
    if (!isActive) return;

    double halfSide = width / 2.0;
    glColor3d(red, green, blue);
    glBegin(GL_POLYGON);
    glVertex2d(x + halfSide, y + halfSide);
    glVertex2d(x + halfSide, y - halfSide);
    glVertex2d(x - halfSide, y - halfSide);
    glVertex2d(x - halfSide, y + halfSide);
    glEnd();
}

// Reduce hit points and darken color
void Brick::takeDamage() {
    hitPoints--;
    if (hitPoints <= 0) {
        isActive = false;
    }
    else {
        // Darken color each hit
        red *= GameConstants::DAMAGE_COLOR_MULTIPLIER;
        green *= GameConstants::DAMAGE_COLOR_MULTIPLIER;
        blue *= GameConstants::DAMAGE_COLOR_MULTIPLIER;
    }
}

// Reset to initial color
void Brick::resetColor() {
    red = initialRed;
    green = initialGreen;
    blue = initialBlue;
}

// Check if brick is destroyed
bool Brick::isDestroyed() const {
    return !isActive;
}