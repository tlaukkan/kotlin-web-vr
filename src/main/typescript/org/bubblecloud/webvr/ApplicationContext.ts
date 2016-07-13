/// <reference path="../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../typings/globals/three/index.d.ts" />

import {CameraManager} from "./vr/CameraManager";
import {DisplayManager} from "./vr/DisplayManager";
import {ControllerManager} from "./vr/ControllerManager";
import {MediaManager} from "./media/MediaManager";
import {SceneManager} from "./scene/SceneManager";

export class ApplicationContext {

    scene: THREE.Scene;
    camera: THREE.PerspectiveCamera;
    renderer: THREE.WebGLRenderer;
    loadingManager: THREE.LoadingManager;
    mediaManager: MediaManager;
    cameraManager: CameraManager;
    sceneManager: SceneManager;
    displayManager: DisplayManager;
    controllerManager: ControllerManager;

    constructor() {

    }

}