#include "Renderer.h"

// Constructor
Renderer::Renderer()
    : windowWidth(GameConstants::WINDOW_WIDTH),
    windowHeight(GameConstants::WINDOW_HEIGHT),
    aspectRatio(1.0f) {
}

// Destructor
Renderer::~Renderer() {}

// Initialize the renderer
bool Renderer::initialize(GLFWwindow* window) {
    if (window == nullptr) return false;

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);

    return true;
}

// Begin a new frame
void Renderer::beginFrame() {
    glClear(GL_COLOR_BUFFER_BIT);
}

// Render the game world
void Renderer::renderWorld(GameWorld& world) {
    world.render();
}

// End the frame and swap buffers
void Renderer::endFrame(GLFWwindow* window) {
    glfwSwapBuffers(window);
    glfwPollEvents();
}