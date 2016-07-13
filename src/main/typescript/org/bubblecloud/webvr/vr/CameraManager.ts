/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {ApplicationContext} from "./ApplicationContext";
declare var navigator:Navigator;

/**
 * Acquires positional information from connected VR devices and applies
 * the transformations to a three.js camera object.
 *
 * @author Tommi S.E. Laukkanen / https://github.com/tlaukkan
 * @author dmarcos / https://github.com/dmarcos
 * @author mrdoob / http://mrdoob.com
 */
export class CameraManager {
    /**
     * The VR display.
     */
	display: VRDisplay;
    /**
     * The perspective camera.
     */
	camera: THREE.Camera;
    /**
     * On error callback.
     */
	onError: (message: string) => void;
    /**
     * The standing matrix.
     */
	standingMatrix = new THREE.Matrix4();
    /**
     *  the Rift SDK returns the position in meters
     *  this scale factor allows the user to define how meters
     *  are converted to scene units.
     */
	scale = 1;

    /**
     * If true will use "standing space" coordinate system where y=0 is the
     * floor and x=0, z=0 is the center of the room.
     */
	standing = false;

    /**
	 * Distance from the users eyes to the floor in meters. Used when
	 * standing=true but the VRDisplay doesn't provide stageParameters.
     */
	userHeight = 1.6;

    /**
     * Default constructor for defining camera and error callback.
     * @param context the application context
     * @param onError the error callback
     */
	constructor(context: ApplicationContext, onError?: (message: string) => void) {
		context.camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 0.1, 10);
		
		this.camera = context.camera;
		this.onError = onError;
        this.standing = true;
		if (navigator.getVRDisplays) {

			navigator.getVRDisplays().then(this.gotVRDevices);

		}

	}

    /**
     * Gets standing matrix.
     * @returns {THREE.Matrix4}
     */
	getStandingMatrix = () => {
		return this.standingMatrix;
	}

    /**
     * Got VR devices callback.
     * @param devices the VR devices.
     */
	gotVRDevices = (devices) => {

		for (var i = 0; i < devices.length; i++) {

			if (( 'VRDisplay' in window && devices[i] instanceof VRDisplay )) {
				this.display = devices[i];
				break;  // We keep the first we encounter
			}

		}

		if (!this.display) {

			if (this.onError) this.onError('VR input not available.');

		}

	}

    /**
     * Updates camera position.
     */
	update = () => {

		if (this.display) {

			var pose = this.display.getPose();

			if (pose.orientation !== null) {

				this.camera.quaternion.fromArray(Array.prototype.slice.call(pose.orientation));

			}

			if (pose.position !== null) {

				this.camera.position.fromArray(Array.prototype.slice.call(pose.position));

			} else {

				this.camera.position.set(0, 0, 0);

			}

			if (this.standing) {

				if (this.display.stageParameters) {

					this.camera.updateMatrix();

					this.standingMatrix.fromArray(Array.prototype.slice.call(
						this.display.stageParameters.sittingToStandingTransform));
					this.camera.applyMatrix(this.standingMatrix);

				} else {

					this.camera.position.setY(this.camera.position.y + this.userHeight);

				}

			}

			this.camera.position.multiplyScalar(this.scale);

		}

	};

    /**
     * Resets pose.
     */
	resetPose = () => {

		if (this.display) {

			if (this.display.resetPose !== undefined) {

				this.display.resetPose();

			}

		}

	};

}
