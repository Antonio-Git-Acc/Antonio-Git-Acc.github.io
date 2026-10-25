#include "GameWorld.h"
#include <cstdlib>
#include <ctime>
#include <cmath>

// Constructor
GameWorld::GameWorld() {
    std::srand(static_cast<unsigned>(std::time(nullptr)));
}

// Destructor
GameWorld::~GameWorld() {
    bricks.clear();
    circles.clear();
}

// Initialize the game world
void GameWorld::initialize() {
    createBrickGrid(GameConstants::BRICK_ROWS, GameConstants::BRICK_COLS,
        GameConstants::BRICK_START_X, GameConstants::BRICK_START_Y,
        GameConstants::BRICK_SPACING);
}

// Create a grid of bricks
void GameWorld::createBrickGrid(int rows, int cols, float startX, float startY, float spacing) {
    for (int i = 0; i < rows; ++i) {
        for (int j = 0; j < cols; ++j) {
            float x = startX + j * (GameConstants::BRICK_WIDTH + spacing);
            float y = startY - i * (GameConstants::BRICK_WIDTH + spacing);

            // Random color
            float r = (std::rand() % 100) / 100.0f;
            float g = (std::rand() % 100) / 100.0f;
            float b = (std::rand() % 100) / 100.0f;

            // 80% chance DESTRUCTIBLE, 20% REFLECTIVE
            BrickType type = (std::rand() % GameConstants::REFLECTIVE_CHANCE == 0)
                ? BrickType::REFLECTIVE : BrickType::DESTRUCTIBLE;

            // Random hit points
            int hp = GameConstants::BRICK_MIN_HP +
                std::rand() % (GameConstants::BRICK_MAX_HP - GameConstants::BRICK_MIN_HP + 1);

            bricks.emplace_back(type, x, y, GameConstants::BRICK_WIDTH, r, g, b, hp);
        }
    }
}

// Update the game world
void GameWorld::update() {
    // Check all collisions
    checkAllCollisions();

    // Move all circles
    for (auto& circle : circles) {
        circle.update();
    }

    // Handle circle merging
    handleCircleMerging();

    // Remove inactive circles
    removeInactiveCircles();
}

// Render all game objects
void GameWorld::render() {
    // Render circles
    for (auto& circle : circles) {
        circle.render();
    }

    // Render bricks
    for (auto& brick : bricks) {
        brick.render();
    }
}

// Spawn a new circle at the center
void GameWorld::spawnCircle() {
    // Random color
    float r = std::rand() / static_cast<float>(RAND_MAX);
    float g = std::rand() / static_cast<float>(RAND_MAX);
    float b = std::rand() / static_cast<float>(RAND_MAX);

    // Random initial direction
    float angle = (std::rand() % 360) * GameConstants::DEG2RAD;
    float vx = GameConstants::CIRCLE_SPEED * std::cos(angle);
    float vy = GameConstants::CIRCLE_SPEED * std::sin(angle);

    circles.emplace_back(0.0, 0.0, vx, vy, GameConstants::CIRCLE_RADIUS, r, g, b);
}

// Handle input
void GameWorld::handleInput(GLFWwindow* window) {
    // Escape to close
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
        glfwSetWindowShouldClose(window, true);
    }

    // Debounce the space bar
    static bool spaceWasDown = false;
    bool spaceIsDown = (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS);

    if (spaceIsDown && !spaceWasDown) {
        spawnCircle();
    }

    spaceWasDown = spaceIsDown;
}

// Check all collisions between circles and bricks
void GameWorld::checkAllCollisions() {
    for (auto& circle : circles) {
        for (auto& brick : bricks) {
            circle.checkCollision(&brick);
        }
    }
}

// Handle circle-circle merging
void GameWorld::handleCircleMerging() {
    for (size_t i = 0; i < circles.size(); ++i) {
        if (!circles[i].isOn()) continue;

        for (size_t j = i + 1; j < circles.size(); ++j) {
            if (!circles[j].isOn()) continue;

            if (circles[i].checkCollisionWithCircle(&circles[j])) {
                circles[i].mergeWith(&circles[j]);
            }
        }
    }
}

// Remove inactive circles from the vector
void GameWorld::removeInactiveCircles() {
    circles.erase(
        std::remove_if(circles.begin(), circles.end(),
            [](const Circle& c) { return !c.isOn(); }),
        circles.end()
    );
}

// Get count of active bricks
int GameWorld::getActiveBrickCount() const {
    int count = 0;
    for (const auto& brick : bricks) {
        if (brick.isOn()) count++;
    }
    return count;
}