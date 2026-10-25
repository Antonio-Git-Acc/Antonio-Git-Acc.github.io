#include "InputHandler.h"

// Constructor
InputHandler::InputHandler()
    : spaceWasDown(false),
    escapeWasDown(false) {
}

// Destructor
InputHandler::~InputHandler() {}

// Process input for the current frame
void InputHandler::processInput(GLFWwindow* window, GameWorld& world) {
    // Escape to close
    bool escapeIsDown = (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS);
    if (escapeIsDown && !escapeWasDown) {
        glfwSetWindowShouldClose(window, true);
    }
    escapeWasDown = escapeIsDown;

    // Space bar to spawn circle (debounced)
    bool spaceIsDown = (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS);
    if (spaceIsDown && !spaceWasDown) {
        world.spawnCircle();
    }
    spaceWasDown = spaceIsDown;
}