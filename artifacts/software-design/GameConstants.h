#ifndef GAME_CONSTANTS_H
#define GAME_CONSTANTS_H

// ==================================================
// Game Constants
// Centralized configuration for all game parameters
// ==================================================

namespace GameConstants {

    // Window settings
    const int WINDOW_WIDTH = 480;
    const int WINDOW_HEIGHT = 480;
    constexpr const char* WINDOW_TITLE = "8-2 Assignment - Enhanced";

    // Brick grid settings
    const int BRICK_ROWS = 6;
    const int BRICK_COLS = 9;
    const float BRICK_START_X = -0.9f;
    const float BRICK_START_Y = 0.9f;
    const float BRICK_SPACING = 0.04f;
    const float BRICK_WIDTH = 0.08f;

    // Brick hit points
    const int BRICK_MIN_HP = 1;
    const int BRICK_MAX_HP = 3;
    const int BRICK_DEFAULT_HP = 3;

    // Brick type probability (1 in N chance of being reflective)
    const int REFLECTIVE_CHANCE = 5;

    // Circle settings
    const float CIRCLE_RADIUS = 0.05f;
    const float CIRCLE_SPEED = 0.03f;

    // Physics settings
    const float WALL_DAMPING = 0.0005f;
    const float BRICK_DAMPING = 0.98f;
    const float MERGE_EPSILON = 0.0001f;

    // OpenGL settings
    const int CIRCLE_SEGMENTS = 360;
    const float DEG2RAD = 3.14159f / 180.0f;

    // Color settings
    const float COLOR_MIN = 0.0f;
    const float COLOR_MAX = 1.0f;
    const float DAMAGE_COLOR_MULTIPLIER = 0.5f;

} // namespace GameConstants

#endif // GAME_CONSTANTS_H