#include "Circle.h"
#include <cmath>
#include <algorithm>

// Constructor
Circle::Circle(double startX, double startY, double velocityX, double velocityY,
    float rad, float r, float g, float b)
    : GameObject(static_cast<float>(startX), static_cast<float>(startY), r, g, b),
    radius(rad),
    vx(static_cast<float>(velocityX)),
    vy(static_cast<float>(velocityY)) {
}

// Update method
void Circle::update() {
    if (!isActive) return;
    moveOneStep();
}

// Render the circle
void Circle::render() {
    if (!isActive) return;

    glColor3f(red, green, blue);
    glBegin(GL_POLYGON);
    for (int i = 0; i < GameConstants::CIRCLE_SEGMENTS; i++) {
        float degInRad = i * GameConstants::DEG2RAD;
        glVertex2f(std::cos(degInRad) * radius + x,
            std::sin(degInRad) * radius + y);
    }
    glEnd();
}

// Check collision with a brick
void Circle::checkCollision(Brick* brick) {
    if (brick == nullptr || !brick->isOn() || !isActive) return;

    // Calculate the half-size of the brick
    float half = brick->width / 2.0f;

    // Find the closest point on the brick to the circle center
    float closestX = std::max(brick->x - half, std::min(x, brick->x + half));
    float closestY = std::max(brick->y - half, std::min(y, brick->y + half));

    // Vector from circle center to closest point
    float distX = x - closestX;
    float distY = y - closestY;
    float distanceSq = distX * distX + distY * distY;

    // If the circle's radius reaches the closest point, we have a collision
    if (distanceSq <= radius * radius) {
        if (brick->brickType == BrickType::REFLECTIVE) {
            // Determine which axis has the smallest overlap
            float overlapX = radius - std::fabs(distX);
            float overlapY = radius - std::fabs(distY);

            if (overlapX < overlapY) {
                // Horizontal collision – reverse x velocity
                vx = -vx;
                if (distX > 0)
                    x = closestX + radius;
                else
                    x = closestX - radius;
            }
            else {
                // Vertical collision – reverse y velocity
                vy = -vy;
                if (distY > 0)
                    y = closestY + radius;
                else
                    y = closestY - radius;
            }
            // Small damping on collision
            vx *= GameConstants::BRICK_DAMPING;
            vy *= GameConstants::BRICK_DAMPING;
        }
        else if (brick->brickType == BrickType::DESTRUCTIBLE) {
            // Reduce hit points and update color
            brick->takeDamage();

            // Push circle out slightly to prevent repeated hits
            if (distX != 0.0f || distY != 0.0f) {
                float invLen = radius / std::sqrt(distanceSq);
                x += distX * invLen * 0.1f;
                y += distY * invLen * 0.1f;
            }
        }
    }
}

// Check collision with another circle
bool Circle::checkCollisionWithCircle(Circle* other) {
    if (other == nullptr || !other->isActive || !isActive) return false;

    float dx = x - other->x;
    float dy = y - other->y;
    float dist = std::sqrt(dx * dx + dy * dy);

    return dist < radius + other->radius && dist > GameConstants::MERGE_EPSILON;
}

// Merge with another circle
void Circle::mergeWith(Circle* other) {
    if (other == nullptr) return;

    // New radius preserves total area
    float newRadius = std::sqrt(radius * radius + other->radius * other->radius);
    radius = newRadius;

    // Blend colors
    red = (red + other->red) / 2.0f;
    green = (green + other->green) / 2.0f;
    blue = (blue + other->blue) / 2.0f;

    // Average velocities (simplified momentum conservation)
    vx = (vx + other->vx) * 0.5f;
    vy = (vy + other->vy) * 0.5f;

    // Deactivate the other circle
    other->setActive(false);
}

// Move the circle and handle wall collisions
void Circle::moveOneStep() {
    handleWallCollision();
    x += vx;
    y += vy;
}

// Handle wall collisions with reflection and friction
void Circle::handleWallCollision() {
    // Right wall
    if (x + radius >= 1.0f) {
        vx = -vx - GameConstants::WALL_DAMPING * (vx > 0 ? 1.0f : -1.0f);
        x = 1.0f - radius;
    }
    // Left wall
    if (x - radius <= -1.0f) {
        vx = -vx - GameConstants::WALL_DAMPING * (vx < 0 ? 1.0f : -1.0f);
        x = -1.0f + radius;
    }
    // Top wall
    if (y + radius >= 1.0f) {
        vy = -vy - GameConstants::WALL_DAMPING * (vy > 0 ? 1.0f : -1.0f);
        y = 1.0f - radius;
    }
    // Bottom wall
    if (y - radius <= -1.0f) {
        vy = -vy - GameConstants::WALL_DAMPING * (vy < 0 ? 1.0f : -1.0f);
        y = -1.0f + radius;
    }
}