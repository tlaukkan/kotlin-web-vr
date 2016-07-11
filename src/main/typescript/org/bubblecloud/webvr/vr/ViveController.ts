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
export class ViveController extends Object3D {
    /**
     * The controller ID.
     */
    controllerId:number;
    /**
     * The standaing matrix.
     * @type {THREE.Matrix4}
     */
    standingMatrix = new THREE.Matrix4();

    /**
     * Constructor which sets controller ID
     * @param controllerId the controller ID
     */
    constructor(controllerId) {
        super();
        this.controllerId = controllerId;
        this.matrixAutoUpdate = false;
        this.update();
    }

    /**
     * Updates object state according controller physical state.
     */
    update = () => {

        requestAnimationFrame(this.update);

        var gamepad = navigator.getGamepads()[this.controllerId];

        if (gamepad !== undefined && gamepad.pose !== null) {

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
