#ifndef GAME_OBJECT_H
#define GAME_OBJECT_H

#include <GLFW/glfw3.h>
#include "GameConstants.h"

// ==================================================
// GameObject - Abstract base class for all game objects
// ==================================================

class GameObject {
public:
    // Position
    float x, y;

    // Color
    float red, green, blue;

    // Active state
    bool isActive;

    // Constructor
    GameObject(float startX, float startY, float r, float g, float b)
        : x(startX), y(startY), red(r), green(g), blue(b), isActive(true) {
    }

    // Virtual destructor for proper cleanup of derived classes
    virtual ~GameObject() = default;

    // Pure virtual methods to be implemented by derived classes
    virtual void update() = 0;
    virtual void render() = 0;

    // Common method to check if object is active
    bool isOn() const { return isActive; }
    void setActive(bool active) { isActive = active; }
};

#endif // GAME_OBJECT_H