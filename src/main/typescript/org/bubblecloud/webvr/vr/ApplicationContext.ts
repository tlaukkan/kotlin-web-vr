/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {CameraManager} from "./CameraManager";
import {DisplayManager} from "./DisplayManager";
import {ControllerManager} from "./ControllerManager";
import {MediaManager} from "./MediaManager";
import {SceneManager} from "./SceneManager";

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