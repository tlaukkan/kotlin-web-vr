# Kotlin Web VR

A Kotlin Web VR client-server framework.

Status:
 * Server can be run locally as a sandbox.
 * Cells and servers can be linked using YAML configuration file.
 * World state is saved as JSON object in data folder.
 * Interpolation of object translation, rotation and scaling.
 * Tool API for building tool components for VR controllers.
 * Simple world building using lights and boxes/sphere primitives.
 * In world movement by point and click.
 * Automatic loading of textures from server path.
 * Physical lighting and shadows.

Requirements:
 * Latest chrome build for Web VR: https://webvr.info/get-chrome/
 * HTC Vive
 * Steam Open VR
 * IntelliJ Idea

Design:
 * Client
    * Web VR
    * ThreeJS
    * KotlinJS -> JavaScript
 * Server
    * Rest API: JSON / HTTP
    * 3D scene synch: JSON / Web Socket
    * Kotlin -> JVM
    
Development:

* Open project in IntelliJ Idea
* Run server/src/main/kotlin/vr/Main.kt from IDE
* Open Chrome Web VR to URL http://127.0.0.1:8080/