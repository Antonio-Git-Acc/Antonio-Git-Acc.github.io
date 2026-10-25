#include <GLFW/glfw3.h>
#include <cstdlib>
#include <iostream>
#include "GameConstants.h"
#include "GameWorld.h"
#include "Renderer.h"
#include "InputHandler.h"

// ==================================================
// Main Entry Point
// Enhanced 3D Scene Project - CS 499 Milestone Two
// ==================================================

int main(void) {
    // Initialize GLFW
    if (!glfwInit()) {
        std::cerr << "Failed to initialize GLFW" << std::endl;
        return EXIT_FAILURE;
    }

    // Configure GLFW
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);

    // Create window
    GLFWwindow* window = glfwCreateWindow(
        GameConstants::WINDOW_WIDTH,
        GameConstants::WINDOW_HEIGHT,
        GameConstants::WINDOW_TITLE,
        nullptr, nullptr
    );

    if (!window) {
        std::cerr << "Failed to create GLFW window" << std::endl;
        glfwTerminate();
        return EXIT_FAILURE;
    }

    // Initialize renderer
    Renderer renderer;
    if (!renderer.initialize(window)) {
        std::cerr << "Failed to initialize renderer" << std::endl;
        glfwDestroyWindow(window);
        glfwTerminate();
        return EXIT_FAILURE;
    }

    // Initialize game world
    GameWorld world;
    world.initialize();

    // Initialize input handler
    InputHandler inputHandler;

    // Main game loop
    while (!glfwWindowShouldClose(window)) {
        // Process input
        inputHandler.processInput(window, world);

        // Update game state
        world.update();

        // Render
        renderer.beginFrame();
        renderer.renderWorld(world);
        renderer.endFrame(window);
    }

    // Cleanup
    glfwDestroyWindow(window);
    glfwTerminate();

    return EXIT_SUCCESS;
}