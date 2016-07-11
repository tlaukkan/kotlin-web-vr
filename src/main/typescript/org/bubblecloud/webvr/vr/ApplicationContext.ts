/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {CameraManager} from "./CameraManager";
import {DisplayManager} from "./DisplayManager";
import {ControllerManager} from "./ControllerManager";

export class ApplicationContext {

    scene: THREE.Scene;
    renderer: THREE.WebGLRenderer;
    cameraManager: CameraManager;
    displayManager: DisplayManager;
    controllerManager: ControllerManager;

    constructor() {

    }

}