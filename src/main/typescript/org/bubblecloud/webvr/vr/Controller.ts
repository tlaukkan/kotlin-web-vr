/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import Object3D = THREE.Object3D;

declare var navigator:Navigator;

/**
 * The Vive controller object.
 *
 * @author Tommi S.E. Laukkanen / https://github.com/tlaukkan
 * @author mrdoob / http://mrdoob.com/
 */
export class Controller extends Object3D {
    /**
     * The controller ID.
     */
    index: number;
    /**
     * The controller type.
     */
    type: string;
    /**
     * The standaing matrix.
     * @type {THREE.Matrix4}
     */
    standingMatrix = new THREE.Matrix4();

    /**
     * Constructor which sets controller index
     * @param index the controller index
     */
    constructor(index: number, type: string) {
        super();
        this.index = index;
        this.type = type;
        this.matrixAutoUpdate = false;
        this.update();
    }

    /**
     * Updates object state according controller physical state.
     */
    update = () => {

        requestAnimationFrame(this.update);

        var gamepad: Gamepad = navigator.getGamepads()[this.index];

        if (gamepad !== undefined && gamepad.id == "OpenVR Gamepad" && gamepad.pose !== null) {

            var pose = gamepad.pose;

            this.position.fromArray(pose.position);
            this.quaternion.fromArray(pose.orientation);
            this.matrix.compose(this.position, this.quaternion, this.scale);
            this.matrix.multiplyMatrices(this.standingMatrix, this.matrix);
            this.matrixWorldNeedsUpdate = true;

            this.visible = true;

        } else {

            this.visible = false;

        }

    }

}

