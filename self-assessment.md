---
layout: default
title: Professional Self-Assessment
---

# Professional Self-Assessment

**Antonio Turrietta**
**Bachelor of Science in Computer Science**
**Southern New Hampshire University**

I am a computer science graduate from Southern New Hampshire University, and this ePortfolio represents the culmination of my undergraduate journey. It is more than a collection of code — it is evidence of how I think, how I approach problems, and how I have grown from someone learning to write programs into an engineer capable of designing, securing, and optimizing systems that solve real problems.

Over the course of the program, I have developed the ability to work across the full stack of modern software development: from low-level graphics programming in C++ to full-stack web applications backed by MongoDB, from Android mobile development to database security with SQLCipher and the Android Keystore. This portfolio presents three enhanced artifacts that demonstrate my growth in software design and engineering, algorithms and data structures, and databases. Behind those three artifacts is a broader education — in Agile teamwork, secure coding practices, testing discipline, and stakeholder communication — that this assessment will summarize.

Collaborating in a Team Environment
Computer science is rarely a solo discipline. My CS 250 Software Development Lifecycle course was my first sustained experience working on a software team using Agile methodologies. I rotated through Product Owner, Developer, and Tester roles, and the rotation taught me something that individual projects never could: that the quality of a team's output depends less on any one person's code than on how well the team understands each other's work. As Product Owner, I translated user stories into concrete acceptance criteria. As Tester, I validated developer work against those criteria and reported defects in a form that was actionable. As Developer, I wrote features that had to survive the next tester's scrutiny. The process was iterative — sprint planning, daily standups, sprint reviews, and retrospectives — and by the end I had internalized that frequent, structured communication produces better software faster than infrequent, unstructured heroics.

That experience shaped how I designed the software design enhancement in this portfolio. When I refactored the monolithic 3D Scene project into a modular architecture, I did so with a team mindset: I separated rendering, input handling, and game state into distinct classes so that different developers could work on each without conflicting. Clean code is a form of respect for the person who will read it next, and I try to write with that respect.
Communicating with Stakeholders

The ability to explain technical decisions to non-technical audiences is a skill I developed across multiple courses. In System Analysis and Design, I translated a client interview transcript into a full set of functional and non-functional requirements for the DriverPass system, then produced UML diagrams that communicated the architecture to audiences at varying levels of technical fluency. In CS 405 Secure Coding, I wrote a vulnerability assessment report for Artemis Financial that explained cryptographic choices, dependency scan findings, and defense-in-depth recommendations in language that a decision-maker with no cryptography background could follow.

I have continued to develop this skill through the professional documentation of my three enhancements. Each narrative in this portfolio is written for a hiring manager or peer engineer who may not be familiar with the original course, and I have tried to make the technical concepts — scene graph architecture, O(log n) query optimization, SQLCipher encryption — accessible without diluting them.

Data Structures and Algorithms
Throughout the program, I came to understand that the choice of data structure is often the choice of what problems the application will or will not be able to solve efficiently. In the Algorithms and Data Structures enhancement, I took the Grazioso Salvare Dashboard from a prototype that loaded the entire MongoDB collection into Python and filtered in-memory, and transformed it into a system that uses database indexes to reduce query complexity from O(n) to O(log n), paginates reads to bound memory usage at O(k), delegates aggregation to the database server, and caches repeated queries in O(1). I wrote a benchmark script that measured the impact empirically — the warm-cache read path runs at approximately one-tenth the latency of the cold-cache path — and that empirical evidence is what turns an algorithmic claim into an algorithmic fact.

I also explored reinforcement learning in a Current and Emerging Trends course, where I implemented a deep Q-learning algorithm for a pathfinding problem using experience replay and an epsilon-greedy exploration strategy. That project taught me to reason about state and action spaces, convergence, and the trade-offs involved in hyperparameter tuning. These experiences together have given me a practical intuition for when to reach for a hash map, when to reach for a tree, and when the right answer is simply to let the database do the work.

Software Engineering and Databases

The program has trained me to build at multiple levels of abstraction. My software design enhancement demonstrates my ability to refactor existing code into a maintainable architecture: turning a procedural single-file program into an object-oriented design with a base GameObject class, a GameWorld manager, and separated rendering and input subsystems. My databases enhancement demonstrates the other end of the stack: designing a normalized five-table schema with foreign key constraints, writing complex JOIN and aggregate queries, and implementing schema migrations that preserve data across versions.

Alongside those enhancements, my full-stack coursework gave me the experience of building complete systems — a MEAN-stack application with a MongoDB backend, an Express/Node.js server, an Angular administrative interface, and a Handlebars-rendered public site. I understand how authentication (JWT and Passport) integrates with the API, how a single-page application consumes REST endpoints, and how a document-oriented database handles schema flexibility. That end-to-end view is what allows me to reason about trade-offs not just within one layer, but across layers: for example, deciding that server-side aggregation is the right place to compute breed counts because it minimizes network traffic, even though doing the counting in Python would be simpler to write.

Security
The security mindset I developed in CS 405 has become a defining part of how I approach engineering. Secure coding is not a feature that is added at the end of a project; it is a set of assumptions built into the architecture from the first line. In the Artemis Financial work, I identified vulnerabilities through OWASP dependency scanning, implemented HTTPS with a self-signed certificate, and added a SHA-256 checksum endpoint for file integrity verification. I learned to read vulnerability reports critically, distinguishing between exploitable findings and deprecated-but-unused dependencies.

In the databases enhancement, this mindset surfaced two real security weaknesses in my original EventTracker app: passwords were hashed with a static salt, and the local database was stored in plaintext. Both were latent — the app "worked" — but both were exploitable in ways that would matter in a production deployment. I addressed them by generating a unique salt per user via SecureRandom, and by encrypting the entire database with SQLCipher using a passphrase protected by an AES-256-GCM key in the Android Keystore. I verified the encryption empirically by exporting the database file and confirming that it could not be opened by a standard SQLite viewer. Security work is meaningful only when it is verified, and the discipline of verification is the part I value most from these enhancements.
How the Artifacts Fit Together

The three artifacts in this portfolio represent three complementary views of what it means to build good software. The software design and engineering enhancement shows how I structure a codebase for maintainability and team collaboration. The algorithms and data structures enhancement shows how I reason about performance and complexity in a data-intensive application. The databases enhancement shows how I design for data integrity, security, and schema evolution over time. Together, they demonstrate that I can move fluidly between abstraction layers: from the architecture of a graphics application, to the query optimization of a dashboard, to the encryption of a mobile database.

What these artifacts also show — and what I hope a prospective employer will notice — is a consistent pattern of self-critique. In each enhancement, I identified weaknesses in my own prior work, planned the fix, implemented it, and then verified the fix empirically. The static-salt password vulnerability, the missing SCHEDULE_EXACT_ALARM permission check, the unhashable query cache in the dashboard — each of these was a bug I found by testing rather than assuming. That is the mindset I want to bring to my next role: not just writing software, but stewarding it.

The artifacts that follow this assessment are the working evidence. Each is accompanied by a narrative that explains the enhancement decisions, the skills demonstrated, and the course outcomes addressed. I invite you to explore them.

