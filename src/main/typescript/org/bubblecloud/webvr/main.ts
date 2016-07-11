/// <reference path="../../../../../../typings/globals/three/index.d.ts" />

import Group = THREE.Group;
import MeshBasicMaterial = THREE.MeshBasicMaterial;
import Object3D = THREE.Object3D;

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

var context: ApplicationContext = new ApplicationContext();
var room;

init();
load();
animateLoop();

function init() {

    var container = document.createElement('div');
    document.body.appendChild(container);

    context.mediaManager = new MediaManager(context);
    context.scene = new THREE.Scene();
    context.camera = new THREE.PerspectiveCamera(70, window.innerWidth / window.innerHeight, 0.1, 10);
    context.scene.add(context.camera);
    context.renderer = new THREE.WebGLRenderer({antialias: true});
    context.renderer.setClearColor(0x101010);
    context.renderer.setPixelRatio(window.devicePixelRatio);
    context.renderer.setSize(window.innerWidth, window.innerHeight);
    context.renderer.sortObjects = false;
    context.renderer = context.renderer;
    
    context.cameraManager = new CameraManager(context.camera);
    context.controllerManager = new ControllerManager(context);
    context.controllerManager.controllerHandlers["OpenVR Gamepad"] = handleViewController;
    context.displayManager = new DisplayManager(context.renderer);


    container.appendChild(context.renderer.domElement);

    if (webVR.isAvailable() === true) {
        document.body.appendChild(webVR.getButton(context.displayManager));
    }

    window.addEventListener('resize', onResize, false);
}

function load() {
    // Load media
    var vivePath = 'models/obj/vive-controller/';
    context.mediaManager.loadModel('OpenVR Gamepad', vivePath + 'vr_controller_vive_1_5.obj', function (name:string, model:THREE.Object3D) {
        var controller:Object3D = model.children[0];

        (<MeshBasicMaterial>controller.material).map = context.mediaManager.loadTexture(vivePath + 'onepointfive_texture.png');
        (<MeshBasicMaterial>controller.material).specularMap = context.mediaManager.loadTexture(vivePath + 'onepointfive_spec.png');

        context.controllerManager.controllerModels["OpenVR Gamepad"] = model;
    });

    // Load demo scene
    room = new THREE.Mesh(
        new THREE.BoxGeometry(6, 6, 6, 10, 10, 10),
        new THREE.MeshBasicMaterial({color: 0x202020, wireframe: true})
    );
    room.position.y = 3;
    context.scene.add(room);

    context.scene.add(new THREE.HemisphereLight(0x404020, 0x202040, 0.5));

    var light = new THREE.DirectionalLight(0xffffff);
    light.position.set(1, 1, 1).normalize();
    context.scene.add(light);

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
}

function animateLoop() {
    requestAnimationFrame(animateLoop);
    
    context.cameraManager.update();
    for (var i = 0; i < room.children.length; i++) {

        var cube = room.children[i];

        if (cube.geometry instanceof THREE.BoxGeometry === false) continue;

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

    context.displayManager.render(context.scene, context.camera);
}

function onResize() {
    context.camera.aspect = window.innerWidth / window.innerHeight;
    context.camera.updateProjectionMatrix();
    context.displayManager.setSize(window.innerWidth, window.innerHeight);
}


function handleViewController(controller: Controller) {
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
