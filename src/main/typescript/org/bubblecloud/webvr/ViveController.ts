/// <reference path="../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../typings/globals/three/index.d.ts" />

import Object3D = THREE.Object3D;

declare var navigator:Navigator;

/**
 * @author mrdoob / http://mrdoob.com/
 */
export class ViveController extends Object3D {

    controllerId:number;
    standingMatrix = new THREE.Matrix4();

    constructor(controllerId) {
        super();
        this.controllerId = controllerId;
        this.matrixAutoUpdate = false;
        this.update();
    }

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
