#ifndef RENDERER_H
#define RENDERER_H

#include <GLFW/glfw3.h>
#include "GameWorld.h"

// ==================================================
// Renderer - Handles all rendering operations
// ==================================================

class Renderer {
public:
    Renderer();
    ~Renderer();

    // Initialize the renderer
    bool initialize(GLFWwindow* window);

    // Begin a new frame
    void beginFrame();

    // Render the game world
    void renderWorld(GameWorld& world);

    // End the frame and swap buffers
    void endFrame(GLFWwindow* window);

private:
    int windowWidth;
    int windowHeight;
    float aspectRatio;
};

#endif // RENDERER_H