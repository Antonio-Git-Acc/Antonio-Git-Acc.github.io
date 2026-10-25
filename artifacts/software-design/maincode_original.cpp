#include <GLFW/glfw3.h>
#include "linmath.h"
#include <stdlib.h>
#include <stdio.h>
#include <conio.h>
#include <iostream>
#include <vector>
#include <windows.h>
#include <time.h>
#include <cmath>    // for sqrt, fabs, max, min

using namespace std;

const float DEG2RAD = 3.14159f / 180.0f;

// Forward declaration
void processInput(GLFWwindow* window);

// Brick type enumeration
enum BRICKTYPE { REFLECTIVE, DESTRUCTABLE };
enum ONOFF { ON, OFF };

// ----------------------------------------------------
// Brick class (modified)
// ----------------------------------------------------
class Brick
{
public:
    float red, green, blue;
    float x, y, width;
    BRICKTYPE brick_type;
    ONOFF onoff;
    int hitPoints;                      // hits needed to destroy (if DESTRUCTABLE)
    float initRed, initGreen, initBlue; // original colour for fading

    // Constructor with default 3 hit points
    Brick(BRICKTYPE bt, float xx, float yy, float ww,
        float rr, float gg, float bb, int hp = 3)
    {
        brick_type = bt;
        x = xx;
        y = yy;
        width = ww;
        red = rr;
        green = gg;
        blue = bb;
        initRed = rr;
        initGreen = gg;
        initBlue = bb;
        hitPoints = hp;
        onoff = ON;
    }

    void drawBrick()
    {
        if (onoff == ON)
        {
            double halfside = width / 2.0;
            glColor3d(red, green, blue);
            glBegin(GL_POLYGON);
            glVertex2d(x + halfside, y + halfside);
            glVertex2d(x + halfside, y - halfside);
            glVertex2d(x - halfside, y - halfside);
            glVertex2d(x - halfside, y + halfside);
            glEnd();
        }
    }
};

// ----------------------------------------------------
// Circle class (modified)
// ----------------------------------------------------
class Circle
{
public:
    float red, green, blue;
    float radius;
    float x, y;
    float vx, vy;          // velocity components
    float speed;           // base speed factor (not directly used after construction)

    Circle(double xx, double yy, double vxx, double vyy,
        float rad, float r, float g, float b)
    {
        x = (float)xx;
        y = (float)yy;
        vx = (float)vxx;
        vy = (float)vyy;
        radius = rad;
        red = r;
        green = g;
        blue = b;
        speed = 0.03f;     // just a stored reference (not needed for movement now)
    }

    // Collision with a single brick (circle vs. square)
    void CheckCollision(Brick* brk)
    {
        if (brk->onoff == OFF) return;

        // Calculate the half-size of the brick
        float half = brk->width / 2.0f;

        // Find the closest point on the brick to the circle center
        float closestX = max(brk->x - half, min(x, brk->x + half));
        float closestY = max(brk->y - half, min(y, brk->y + half));

        // Vector from circle center to closest point
        float distX = x - closestX;
        float distY = y - closestY;
        float distanceSq = distX * distX + distY * distY;

        // If the circle's radius reaches the closest point, we have a collision
        if (distanceSq <= radius * radius)
        {
            if (brk->brick_type == REFLECTIVE)
            {
                // Determine which axis has the smallest overlap to decide reflection direction
                float overlapX = radius - fabs(distX);
                float overlapY = radius - fabs(distY);

                if (overlapX < overlapY)
                {
                    // Horizontal collision – reverse x velocity
                    vx = -vx;
                    // Push circle out to avoid sticking
                    if (distX > 0)
                        x = closestX + radius;
                    else
                        x = closestX - radius;
                }
                else
                {
                    // Vertical collision – reverse y velocity
                    vy = -vy;
                    if (distY > 0)
                        y = closestY + radius;
                    else
                        y = closestY - radius;
                }
                // Small damping on collision with reflective brick
                vx *= 0.98f;
                vy *= 0.98f;
            }
            else if (brk->brick_type == DESTRUCTABLE)
            {
                // Reduce hit points and update colour
                brk->hitPoints--;
                if (brk->hitPoints <= 0)
                {
                    brk->onoff = OFF;
                }
                else
                {
                    // Darken colour each hit (multiply by 0.5)
                    brk->red *= 0.5f;
                    brk->green *= 0.5f;
                    brk->blue *= 0.5f;
                }
                // Push circle out slightly so it doesn't hit the same brick repeatedly
                if (distX != 0.0f || distY != 0.0f)
                {
                    float invLen = radius / sqrt(distanceSq);
                    x += distX * invLen * 0.1f;
                    y += distY * invLen * 0.1f;
                }
            }
        }
    }

    // Move the circle and handle wall collisions with reflection and friction
    void MoveOneStep()
    {
        const float damping = 0.0005f;   // friction per wall bounce

        // Right wall
        if (x + radius >= 1.0f)
        {
            vx = -vx - damping * (vx > 0 ? 1.0f : -1.0f);
            x = 1.0f - radius;
        }
        // Left wall
        if (x - radius <= -1.0f)
        {
            vx = -vx - damping * (vx < 0 ? 1.0f : -1.0f);
            x = -1.0f + radius;
        }
        // Top wall (OpenGL y-axis up)
        if (y + radius >= 1.0f)
        {
            vy = -vy - damping * (vy > 0 ? 1.0f : -1.0f);
            y = 1.0f - radius;
        }
        // Bottom wall
        if (y - radius <= -1.0f)
        {
            vy = -vy - damping * (vy < 0 ? 1.0f : -1.0f);
            y = -1.0f + radius;
        }

        x += vx;
        y += vy;
    }

    void DrawCircle()
    {
        glColor3f(red, green, blue);
        glBegin(GL_POLYGON);
        for (int i = 0; i < 360; i++) {
            float degInRad = i * DEG2RAD;
            glVertex2f(cos(degInRad) * radius + x,
                sin(degInRad) * radius + y);
        }
        glEnd();
    }
};

// Global containers
vector<Circle> world;
vector<Brick> bricks;

// ----------------------------------------------------
// Function to create a grid of bricks (expanded to cover more screen)
// ----------------------------------------------------
void createBrickGrid(int rows, int cols, float startX, float startY, float spacing)
{
    float brickWidth = 0.08f;   // smaller bricks for a denser grid
    for (int i = 0; i < rows; ++i)
    {
        for (int j = 0; j < cols; ++j)
        {
            float x = startX + j * (brickWidth + spacing);
            float y = startY - i * (brickWidth + spacing);  // rows go downward

            // Random colour
            float r = (rand() % 100) / 100.0f;
            float g = (rand() % 100) / 100.0f;
            float b = (rand() % 100) / 100.0f;

            // 80% chance DESTRUCTABLE, 20% REFLECTIVE
            BRICKTYPE type = (rand() % 5 == 0) ? REFLECTIVE : DESTRUCTABLE;

            // Random hit points 1-3
            int hp = 1 + rand() % 3;

            bricks.emplace_back(type, x, y, brickWidth, r, g, b, hp);
        }
    }
}

// ----------------------------------------------------
// Main entry point
// ----------------------------------------------------
int main(void)
{
    srand((unsigned)time(NULL));

    if (!glfwInit()) {
        exit(EXIT_FAILURE);
    }
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
    GLFWwindow* window = glfwCreateWindow(480, 480, "8-2 Assignment - Enhanced", NULL, NULL);
    if (!window) {
        glfwTerminate();
        exit(EXIT_FAILURE);
    }
    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);

    // Expanded grid: 6 rows, 9 columns, covering ~80% of the width and upper half of screen
    createBrickGrid(6, 9, -0.9f, 0.9f, 0.04f);

    // Main animation loop
    while (!glfwWindowShouldClose(window))
    {
        // Setup view
        float ratio;
        int width, height;
        glfwGetFramebufferSize(window, &width, &height);
        ratio = width / (float)height;
        glViewport(0, 0, width, height);
        glClear(GL_COLOR_BUFFER_BIT);

        processInput(window);

        // ---- Circle-brick collisions ----
        for (auto& c : world)
        {
            for (auto& b : bricks)
            {
                c.CheckCollision(&b);
            }
        }

        // ---- Movement & wall bouncing ----
        for (auto& c : world)
        {
            c.MoveOneStep();
        }

        // ---- Circle-circle merging ----
        for (size_t i = 0; i < world.size(); ++i)
        {
            for (size_t j = i + 1; j < world.size(); )
            {
                float dx = world[i].x - world[j].x;
                float dy = world[i].y - world[j].y;
                float dist = sqrt(dx * dx + dy * dy);
                if (dist < world[i].radius + world[j].radius && dist > 0.0001f)
                {
                    // Combine into a single larger circle
                    // New radius preserves total area
                    float newRadius = sqrt(world[i].radius * world[i].radius +
                        world[j].radius * world[j].radius);
                    world[i].radius = newRadius;

                    // Blend colours
                    world[i].red = (world[i].red + world[j].red) / 2.0f;
                    world[i].green = (world[i].green + world[j].green) / 2.0f;
                    world[i].blue = (world[i].blue + world[j].blue) / 2.0f;

                    // Average velocities (momentum conservation simplified)
                    world[i].vx = (world[i].vx + world[j].vx) * 0.5f;
                    world[i].vy = (world[i].vy + world[j].vy) * 0.5f;

                    // Remove the second circle
                    world.erase(world.begin() + j);
                    // Do NOT increment j - vector size decreased
                }
                else
                {
                    ++j;
                }
            }
        }

        // ---- Draw everything ----
        for (auto& c : world)
        {
            c.DrawCircle();
        }
        for (auto& b : bricks)
        {
            b.drawBrick();
        }

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    glfwDestroyWindow(window);
    glfwTerminate();
    exit(EXIT_SUCCESS);
}

// ----------------------------------------------------
// Process input (space bar debounced)
// ----------------------------------------------------
void processInput(GLFWwindow* window)
{
    // Escape to close
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
        glfwSetWindowShouldClose(window, true);

    // Debounce the space bar so that a circle spawns only once per press
    static bool spaceWasDown = false;
    bool spaceIsDown = (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS);

    if (spaceIsDown && !spaceWasDown)   // just pressed this frame
    {
        // Random colour
        float r = rand() / (float)RAND_MAX;
        float g = rand() / (float)RAND_MAX;
        float b = rand() / (float)RAND_MAX;

        // Random initial direction (angle in radians)
        float angle = (rand() % 360) * DEG2RAD;
        float sp = 0.03f;          // constant speed
        float vx = sp * cos(angle);
        float vy = sp * sin(angle);

        Circle B(0.0, 0.0, vx, vy, 0.05, r, g, b);
        world.push_back(B);
    }

    spaceWasDown = spaceIsDown;   // remember for the next frame
}