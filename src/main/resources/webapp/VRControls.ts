/// <reference path="../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../typings/globals/three/index.d.ts" />

declare var navigator: Navigator;


/**
 * @author dmarcos / https://github.com/dmarcos
 * @author mrdoob / http://mrdoob.com
 */

export class VRControls {
 
	vrInput: VRDisplay;
	object;
	onError;
	standingMatrix = new THREE.Matrix4();
	// the Rift SDK returns the position in meters
	// this scale factor allows the user to define how meters
	// are converted to scene units.

	scale = 1;

	// If true will use "standing space" coordinate system where y=0 is the
	// floor and x=0, z=0 is the center of the room.
	standing = false;

	// Distance from the users eyes to the floor in meters. Used when
	// standing=true but the VRDisplay doesn't provide stageParameters.
	userHeight = 1.6;

	constructor ( object, onError? ) {
		this.object = object
		this.onError = onError

		if ( navigator.getVRDisplays ) {

			navigator.getVRDisplays().then( this.gotVRDevices );

		} else if ( navigator.getVRDevices ) {

			// Deprecated API.
			navigator.getVRDevices().then( this.gotVRDevices );

		}

	}

	getStandingMatrix = () => {
		return this.standingMatrix;
	}

	gotVRDevices = ( devices ) => {

		for ( var i = 0; i < devices.length; i ++ ) {

			if ( ( 'VRDisplay' in window && devices[ i ] instanceof VRDisplay ) ) {
				this.vrInput = devices[ i ];
				break;  // We keep the first we encounter
			}

		}

		if ( !this.vrInput ) {

			if ( this.onError ) this.onError( 'VR input not available.' );

		}

	}

	update = () =>  {

		if ( this.vrInput ) {

			var pose = this.vrInput.getPose();

			if ( pose.orientation !== null ) {

				this.object.quaternion.fromArray( pose.orientation );

			}

			if ( pose.position !== null ) {

				this.object.position.fromArray( pose.position );

			} else {

				this.object.position.set( 0, 0, 0 );

			}

			if ( this.standing ) {

				if ( this.vrInput.stageParameters ) {

					this.object.updateMatrix();

					this.standingMatrix.fromArray(Array.prototype.slice.call(
						this.vrInput.stageParameters.sittingToStandingTransform));
					this.object.applyMatrix( this.standingMatrix );

				} else {

					this.object.position.setY( this.object.position.y + this.userHeight );

				}

			}

			this.object.position.multiplyScalar( this.scale );

		}

	};

	resetPose = () => {

		if ( this.vrInput ) {

			if ( this.vrInput.resetPose !== undefined ) {

				this.vrInput.resetPose();

			}

		}

	};

	resetSensor = () => {
		console.warn( 'THREE.VRControls: .resetSensor() is now .resetPose().' );
		this.resetPose();
	}

	zeroSensor = () =>  {
		console.warn( 'THREE.VRControls: .zeroSensor() is now .resetPose().' );
		this.resetPose();
	}

	dispose = () =>  {
		this.vrInput = null;
	}

};
