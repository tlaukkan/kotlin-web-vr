/// <reference path="../../../../typings/globals/webvr-api/index.d.ts" />

declare var navigator: Navigator;

export var ViveController = function ( id ) {

	THREE.Object3D.call( this );

	this.matrixAutoUpdate = false;
	this.standingMatrix = new THREE.Matrix4();

	var scope = this;

	function update() {

		requestAnimationFrame( update );

		var gamepad = navigator.getGamepads()[ id ];

		if ( gamepad !== undefined && gamepad.pose !== null ) {

			var pose = gamepad.pose;

			scope.position.fromArray( pose.position );
			scope.quaternion.fromArray( pose.orientation );
			scope.matrix.compose( scope.position, scope.quaternion, scope.scale );
			scope.matrix.multiplyMatrices( scope.standingMatrix, scope.matrix );
			scope.matrixWorldNeedsUpdate = true;

			scope.visible = true;

		} else {

			scope.visible = false;

		}

	}

	update();

};

ViveController.prototype = Object.create( THREE.Object3D.prototype );
ViveController.prototype.constructor = ViveController;
