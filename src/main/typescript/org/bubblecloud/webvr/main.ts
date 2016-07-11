/// <reference path="../../../../../../typings/globals/three/index.d.ts" />

import Group = THREE.Group;
import MeshBasicMaterial = THREE.MeshBasicMaterial;
import Object3D = THREE.Object3D;

import {OBJLoader} from "./vr/OBJLoader";
import {WebVR} from "./vr/WebVR";
import {CameraManager} from "./vr/CameraManager";
import {Controller} from "./vr/Controller";
import {DisplayManager} from "./vr/DisplayManager";
import {ControllerManager} from "./vr/ControllerManager";
import {ApplicationContext} from "./vr/ApplicationContext";
import {MediaManager} from "./vr/MediaManager";

var webVR = new WebVR();
if (webVR.isAvailable() == false) {
    document.body.appendChild(webVR.getMessage());
}

var camera, scene, renderer;
var displayManager, cameraManager;
var room;

var applicationContext: ApplicationContext = new ApplicationContext();

init();
animate();


function init() {

    var container = document.createElement('div');
    document.body.appendChild(container);

    var info = document.createElement('div');
    info.style.position = 'absolute';
    info.style.top = '10px';
    info.style.width = '100%';
    info.style.textAlign = 'center';
    info.innerHTML = '<a href="http://threejs.org" target="_blank">three.js</a> webgl - htc vive';
    container.appendChild(info);

    applicationContext.mediaManager = new MediaManager(applicationContext);
    applicationContext.scene = new THREE.Scene();
    applicationContext.camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 0.1, 10);

    scene = applicationContext.scene;
    camera = applicationContext.camera;


    scene.add(camera);

    room = new THREE.Mesh(
        new THREE.BoxGeometry(6, 6, 6, 10, 10, 10),
        new THREE.MeshBasicMaterial({color: 0x202020, wireframe: true})
    );
    room.position.y = 3;
    scene.add(room);

    scene.add(new THREE.HemisphereLight(0x404020, 0x202040, 0.5));

    var light = new THREE.DirectionalLight(0xffffff);
    light.position.set(1, 1, 1).normalize();
    scene.add(light);

    var geometry = new THREE.BoxGeometry(0.2, 0.2, 0.2);

    for (var i = 0; i < 200; i++) {

        var object = new THREE.Mesh(geometry, new THREE.MeshLambertMaterial({color: Math.random() * 0xffffff}));

        object.position.x = Math.random() * 4 - 2;
        object.position.y = Math.random() * 4 - 2;
        object.position.z = Math.random() * 4 - 2;

        object.rotation.x = Math.random() * 2 * Math.PI;
        object.rotation.y = Math.random() * 2 * Math.PI;
        object.rotation.z = Math.random() * 2 * Math.PI;

        object.scale.x = Math.random() + 0.5;
        object.scale.y = Math.random() + 0.5;
        object.scale.z = Math.random() + 0.5;

        object.userData.velocity = new THREE.Vector3();
        object.userData.velocity.x = Math.random() * 0.01 - 0.005;
        object.userData.velocity.y = Math.random() * 0.01 - 0.005;
        object.userData.velocity.z = Math.random() * 0.01 - 0.005;

        room.add(object);

    }


    applicationContext.renderer = new THREE.WebGLRenderer({antialias: true});
    applicationContext.renderer.setClearColor(0x101010);
    applicationContext.renderer.setPixelRatio(window.devicePixelRatio);
    applicationContext.renderer.setSize(window.innerWidth, window.innerHeight);
    applicationContext.renderer.sortObjects = false;
    container.appendChild(applicationContext.renderer.domElement);
    renderer = applicationContext.renderer;

    applicationContext.cameraManager = new CameraManager(camera);
    applicationContext.cameraManager.standing = true;
    cameraManager = applicationContext.cameraManager;

    // controllers

    applicationContext.controllerManager = new ControllerManager(applicationContext);
    applicationContext.controllerManager.controllerHandlers["OpenVR Gamepad"] = function (controller: Controller) {
        var gamepad = controller.gamepad;
        var buttons = gamepad.buttons;
        var padTouched: boolean = false;
        for (var i = 0; i < buttons.length; i++) {
            var button: any = <any> buttons[i];
            if (button.pressed) {
                console.log("Button " + i + " pressed with value: " + button.value);
            }
            if (button.touched) {
                console.log("Button " + i + " touched with value: " + button.value);
            }
            if (i == 0 && button.touched) {
                padTouched = true;
            }
        }

        var axes = gamepad.axes;
        for (var i = 0; i < axes.length; i++) {
            var axis = axes[i];
            if (padTouched) {
                console.log("Axis " + i + ": " + axis);
            }
        }
    }

    var vivePath = 'models/obj/vive-controller/';
    var loader = new OBJLoader();
    loader.load(vivePath + 'vr_controller_vive_1_5.obj', function (object:THREE.Object3D) {

        var loader = new THREE.TextureLoader(applicationContext.loadingManager);

        var controller:Object3D = object.children[0];
        (<MeshBasicMaterial>controller.material).map = loader.load(vivePath + 'onepointfive_texture.png');
        (<MeshBasicMaterial>controller.material).specularMap = loader.load(vivePath + 'onepointfive_spec.png');

        applicationContext.controllerManager.controllerModels["OpenVR Gamepad"] = object;
    });

    applicationContext.displayManager = new DisplayManager(renderer);
    displayManager =  applicationContext.displayManager;

    if (webVR.isAvailable() === true) {

        document.body.appendChild(webVR.getButton(applicationContext.displayManager));

    }

    window.addEventListener('resize', onWindowResize, false);
}

function onWindowResize() {

    camera.aspect = window.innerWidth / window.innerHeight;
    camera.updateProjectionMatrix();

    displayManager.setSize(window.innerWidth, window.innerHeight);

}

//

function animate() {

    requestAnimationFrame(animate);
    render();

}

function render() {

    cameraManager.update();

    for (var i = 0; i < room.children.length; i++) {

        var cube = room.children[i];

        if (cube.geometry instanceof THREE.BoxGeometry === false) continue;

        // cube.position.add( cube.userData.velocity );

        if (cube.position.x < -3 || cube.position.x > 3) {

            cube.position.x = THREE.Math.clamp(cube.position.x, -3, 3);
            cube.userData.velocity.x = -cube.userData.velocity.x;

        }

        if (cube.position.y < -3 || cube.position.y > 3) {

            cube.position.y = THREE.Math.clamp(cube.position.y, -3, 3);
            cube.userData.velocity.y = -cube.userData.velocity.y;

        }

        if (cube.position.z < -3 || cube.position.z > 3) {

            cube.position.z = THREE.Math.clamp(cube.position.z, -3, 3);
            cube.userData.velocity.z = -cube.userData.velocity.z;

        }

        cube.rotation.x += 0.01;

    }

    displayManager.render(scene, camera);

}
