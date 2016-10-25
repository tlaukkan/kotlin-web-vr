/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {ApplicationContext} from "../ApplicationContext";
declare var navigator: Navigator;

/**
* @author dmarcos / https://github.com/dmarcos
* @author mrdoob / http://mrdoob.com
*
* WebVR Spec: http://mozvr.github.io/webvr-spec/webvr.html
*
* Firefox: http://mozvr.com/downloads/
* Chromium: https://drive.google.com/folderview?id=0BzudLt22BqGRbW9WTHMtOWMzNjQ&usp=sharing#list
*
*/

export class DisplayManager {

    display: VRDisplay;
    eyeTranslationL = new THREE.Vector3();
    eyeTranslationR = new THREE.Vector3();
    renderRectL;
    renderRectR;
    eyeFOVL;
    eyeFOVR;
    isPresenting = false;
    scale = 1;
    renderer;
    rendererSize;
    rendererPixelRatio;

    // fullscreen

    canvas;
    requestFullscreen;
    exitFullscreen;
    fullscreenElement;

    cameraL;
    cameraR;

    onError;

    constructor(context: ApplicationContext, onError?) {
        context.renderer = new THREE.WebGLRenderer({antialias: true});
        context.renderer.setClearColor(0x101010);
        context.renderer.setPixelRatio(window.devicePixelRatio);
        context.renderer.setSize(window.innerWidth, window.innerHeight);
        context.renderer.sortObjects = false;

        this.renderer = context.renderer;
        this.onError = onError;
        this.rendererSize = this.renderer.getSize();
        this.rendererPixelRatio = this.renderer.getPixelRatio();
        this.canvas = this.renderer.domElement;

        if (navigator.getVRDisplays) {

            navigator.getVRDisplays().then(this.gotVRDevices);

        }

        if (this.canvas.requestFullscreen) {

            this.requestFullscreen = 'requestFullscreen';
            this.fullscreenElement = 'fullscreenElement';
            this.exitFullscreen = 'exitFullscreen';
            document.addEventListener('fullscreenchange', this.onFullscreenChange, false);

        } else if (this.canvas.mozRequestFullScreen) {

            this.requestFullscreen = 'mozRequestFullScreen';
            this.fullscreenElement = 'mozFullScreenElement';
            this.exitFullscreen = 'mozCancelFullScreen';
            document.addEventListener('mozfullscreenchange', this.onFullscreenChange, false);

        } else {

            this.requestFullscreen = 'webkitRequestFullscreen';
            this.fullscreenElement = 'webkitFullscreenElement';
            this.exitFullscreen = 'webkitExitFullscreen';
            document.addEventListener('webkitfullscreenchange', this.onFullscreenChange, false);

        }

        window.addEventListener('vrdisplaypresentchange', this.onFullscreenChange, false);

        // render

        this.cameraL = new THREE.PerspectiveCamera();
        this.cameraR = new THREE.PerspectiveCamera();
        this.cameraL.layers.enable(1);
        this.cameraR.layers.enable(2);
    }

    gotVRDevices = (devices) => {

        for (var i = 0; i < devices.length; i++) {

            if ('VRDisplay' in window && devices[i] instanceof VRDisplay) {

                this.display = devices[i];
                break; // We keep the first we encounter

            }

        }

        if (this.display === undefined) {

            if (this.onError) this.onError('HMD not available');

        }

    }


    setSize = (width, height) => {

        this.rendererSize = {width: width, height: height};

        if (this.isPresenting) {

            var eyeParamsL = this.display.getEyeParameters('left');
            this.renderer.setPixelRatio(1);

            this.renderer.setSize(eyeParamsL.renderWidth * 2, eyeParamsL.renderHeight, false);


        } else {

            this.renderer.setPixelRatio(this.rendererPixelRatio);
            this.renderer.setSize(width, height);

        }

    }

    setPresent = (boolean) => {
        var scope = this;
        return new Promise(function (resolve, reject) {

            if (scope.display === undefined) {

                reject(new Error('No VR hardware found.'));
                return;

            }

            if (scope.isPresenting === boolean) {

                resolve();
                return;

            }

            if (boolean) {

                resolve(scope.display.requestPresent([{source: scope.canvas}]));

            } else {

                resolve(scope.display.exitPresent());

            }

        });
    }


    onFullscreenChange = () => {

        var wasPresenting = this.isPresenting;
        this.isPresenting = this.display !== undefined && ( this.display.isPresenting || ( document[this.fullscreenElement] instanceof HTMLElement ) );

        if (wasPresenting === this.isPresenting) {

            return;

        }

        if (this.isPresenting) {

            this.rendererPixelRatio = this.renderer.getPixelRatio();
            this.rendererSize = this.renderer.getSize();

            var eyeParamsL = this.display.getEyeParameters('left');
            var eyeWidth, eyeHeight;

            eyeWidth = eyeParamsL.renderWidth;
            eyeHeight = eyeParamsL.renderHeight;

            this.renderer.setPixelRatio(1);
            this.renderer.setSize(eyeWidth * 2, eyeHeight, false);

        } else {

            this.renderer.setPixelRatio(this.rendererPixelRatio);
            this.renderer.setSize(this.rendererSize.width, this.rendererSize.height);

        }

    }


    requestPresent = () => {

        return this.setPresent(true);

    };

    exitPresent = () => {

        return this.setPresent(false);

    };

    render = (scene, camera) => {

        if (this.display && this.isPresenting) {

            var autoUpdate = scene.autoUpdate;

            if (autoUpdate) {

                scene.updateMatrixWorld();
                scene.autoUpdate = false;

            }

            var eyeParamsL = this.display.getEyeParameters('left');
            var eyeParamsR = this.display.getEyeParameters('right');

            this.eyeTranslationL.fromArray(Array.prototype.slice.call(eyeParamsL.offset));
            this.eyeTranslationR.fromArray(Array.prototype.slice.call(eyeParamsR.offset));
            this.eyeFOVL = eyeParamsL.fieldOfView;
            this.eyeFOVR = eyeParamsR.fieldOfView;

            if (Array.isArray(scene)) {

                console.warn('THREE.DisplayManager.render() no longer supports arrays. Use camera.layers instead.');
                scene = scene[0];

            }

            // When rendering we don't care what the recommended size is, only what the actual size
            // of the backbuffer is.
            var size = this.renderer.getSize();
            this.renderRectL = {x: 0, y: 0, width: size.width / 2, height: size.height};
            this.renderRectR = {x: size.width / 2, y: 0, width: size.width / 2, height: size.height};

            this.renderer.setScissorTest(true);
            this.renderer.clear();

            if (camera.parent === null) camera.updateMatrixWorld();

            this.cameraL.projectionMatrix = this.fovToProjection(this.eyeFOVL, true, camera.near, camera.far);
            this.cameraR.projectionMatrix = this.fovToProjection(this.eyeFOVR, true, camera.near, camera.far);

            camera.matrixWorld.decompose(this.cameraL.position, this.cameraL.quaternion, this.cameraL.scale);
            camera.matrixWorld.decompose(this.cameraR.position, this.cameraR.quaternion, this.cameraR.scale);

            var scale = this.scale;
            this.cameraL.translateOnAxis(this.eyeTranslationL, scale);
            this.cameraR.translateOnAxis(this.eyeTranslationR, scale);


            // render left eye
            this.renderer.setViewport(this.renderRectL.x, this.renderRectL.y, this.renderRectL.width, this.renderRectL.height);
            this.renderer.setScissor(this.renderRectL.x, this.renderRectL.y, this.renderRectL.width, this.renderRectL.height);
            this.renderer.render(scene, this.cameraL);

            // render right eye
            this.renderer.setViewport(this.renderRectR.x, this.renderRectR.y, this.renderRectR.width, this.renderRectR.height);
            this.renderer.setScissor(this.renderRectR.x, this.renderRectR.y, this.renderRectR.width, this.renderRectR.height);
            this.renderer.render(scene, this.cameraR);

            this.renderer.setScissorTest(false);

            if (autoUpdate) {

                scene.autoUpdate = true;

            }

            this.display.submitFrame();

            return;

        }

        // Regular render mode if not HMD

        this.renderer.render(scene, camera);

    };

    //

    fovToNDCScaleOffset = (fov) => {

        var pxscale = 2.0 / ( fov.leftTan + fov.rightTan );
        var pxoffset = ( fov.leftTan - fov.rightTan ) * pxscale * 0.5;
        var pyscale = 2.0 / ( fov.upTan + fov.downTan );
        var pyoffset = ( fov.upTan - fov.downTan ) * pyscale * 0.5;
        return {scale: [pxscale, pyscale], offset: [pxoffset, pyoffset]};

    }

    fovPortToProjection = (fov, rightHanded, zNear, zFar) => {

        rightHanded = rightHanded === undefined ? true : rightHanded;
        zNear = zNear === undefined ? 0.01 : zNear;
        zFar = zFar === undefined ? 10000.0 : zFar;

        var handednessScale = rightHanded ? -1.0 : 1.0;

        // start with an identity matrix
        var mobj = new THREE.Matrix4();
        var m = mobj.elements;

        // and with scale/offset info for normalized device coords
        var scaleAndOffset = this.fovToNDCScaleOffset(fov);

        // X result, map clip edges to [-w,+w]
        m[0 * 4 + 0] = scaleAndOffset.scale[0];
        m[0 * 4 + 1] = 0.0;
        m[0 * 4 + 2] = scaleAndOffset.offset[0] * handednessScale;
        m[0 * 4 + 3] = 0.0;

        // Y result, map clip edges to [-w,+w]
        // Y offset is negated because this proj matrix transforms from world coords with Y=up,
        // but the NDC scaling has Y=down (thanks D3D?)
        m[1 * 4 + 0] = 0.0;
        m[1 * 4 + 1] = scaleAndOffset.scale[1];
        m[1 * 4 + 2] = -scaleAndOffset.offset[1] * handednessScale;
        m[1 * 4 + 3] = 0.0;

        // Z result (up to the app)
        m[2 * 4 + 0] = 0.0;
        m[2 * 4 + 1] = 0.0;
        m[2 * 4 + 2] = zFar / ( zNear - zFar ) * -handednessScale;
        m[2 * 4 + 3] = ( zFar * zNear ) / ( zNear - zFar );

        // W result (= Z in)
        m[3 * 4 + 0] = 0.0;
        m[3 * 4 + 1] = 0.0;
        m[3 * 4 + 2] = handednessScale;
        m[3 * 4 + 3] = 0.0;

        mobj.transpose();

        return mobj;

    }

    fovToProjection = (fov, rightHanded, zNear, zFar) => {

        var DEG2RAD = Math.PI / 180.0;

        var fovPort = {
            upTan: Math.tan(fov.upDegrees * DEG2RAD),
            downTan: Math.tan(fov.downDegrees * DEG2RAD),
            leftTan: Math.tan(fov.leftDegrees * DEG2RAD),
            rightTan: Math.tan(fov.rightDegrees * DEG2RAD)
        };

        return this.fovPortToProjection(fovPort, rightHanded, zNear, zFar);

    }

}

