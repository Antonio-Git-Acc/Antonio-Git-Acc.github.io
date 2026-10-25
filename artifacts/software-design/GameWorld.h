#ifndef GAME_WORLD_H
#define GAME_WORLD_H

#include <vector>
#include "Brick.h"
#include "Circle.h"

// ==================================================
// GameWorld - Manages all game objects and state
// ==================================================

class GameWorld {
public:
    GameWorld();
    ~GameWorld();

    // Initialization
    void initialize();
    void createBrickGrid(int rows, int cols, float startX, float startY, float spacing);

    // Update and render
    void update();
    void render();

    // Input handling
    void spawnCircle();
    void handleInput(GLFWwindow* window);

    // Getters
    const std::vector<Brick>& getBricks() const { return bricks; }
    const std::vector<Circle>& getCircles() const { return circles; }
    int getCircleCount() const { return static_cast<int>(circles.size()); }
    int getActiveBrickCount() const;

private:
    std::vector<Brick> bricks;
    std::vector<Circle> circles;

    // Collision handling
    void checkAllCollisions();
    void handleCircleMerging();
    void removeInactiveCircles();
};

#endif // GAME_WORLD_H