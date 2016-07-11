/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {Controller} from "./Controller";
import {ApplicationContext} from "./ApplicationContext";
import violet = THREE.ColorKeywords.violet;

declare var navigator: Navigator;

export class ControllerManager {

    applicationContext: ApplicationContext;

    controllers: {[key: number]: Controller} = {}

    viveControllerModel: THREE.Object3D;

    constructor(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext;
        this.update();
    }

    update = () => {
        setTimeout(() => this.update(), 1000);
        
        var gamepads: Gamepad[] = navigator.getGamepads();
        for (var i = 0; i < gamepads.length; i++) {
            var gamepad: Gamepad = gamepads[i];
            if (gamepad && gamepad.connected && gamepad.pose && !this.controllers[gamepad.index]) {
                var controller: Controller = new Controller(gamepad.index, gamepad.id);
                controller.standingMatrix = this.applicationContext.cameraManager.getStandingMatrix();

                this.controllers[gamepad.index] = controller;
                console.log("Controller added: " + gamepad.index + ":" + gamepad.id);
            }
        }

        for (var index in this.controllers) {
            var controller: Controller = this.controllers[index];
            var gamepad: Gamepad = navigator.getGamepads()[controller.index];

            // Delete controller if gamepad does not exist or is of different type.
            if (!gamepad || (gamepad.id != controller.type || !gamepad.connected || !gamepad.pose) ) {

                this.applicationContext.scene.remove(controller);
                delete this.controllers[index];
                console.log("Controller removed: " + gamepad.index + ":" + gamepad.id);
                continue;
            }

            // If model has not been set then attempt to set it.
            if (controller.children.length == 0) {
                // Detect gamepad type and apply appropriate model.
                if (gamepad.id == "OpenVR Gamepad" && this.viveControllerModel) {
                    controller.add(this.viveControllerModel.clone());

                    this.applicationContext.scene.add(controller);
                    console.log("Controller model set and added to scene: " + gamepad.index + ":" + gamepad.id);
                }
            }
        }

        console.log("Controller manager update.");

    }


}