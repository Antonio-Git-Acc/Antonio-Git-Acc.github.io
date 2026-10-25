#ifndef INPUT_HANDLER_H
#define INPUT_HANDLER_H

#include <GLFW/glfw3.h>
#include "GameWorld.h"

// ==================================================
// InputHandler - Handles all user input
// ==================================================

class InputHandler {
public:
    InputHandler();
    ~InputHandler();

    // Process input for the current frame
    void processInput(GLFWwindow* window, GameWorld& world);

private:
    bool spaceWasDown;
    bool escapeWasDown;
};

#endif // INPUT_HANDLER_H