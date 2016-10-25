/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {ApplicationContext} from "../ApplicationContext";
export class SceneManager {

    constructor(context: ApplicationContext) {
        context.scene = new THREE.Scene();
        context.scene.add(context.camera);
    }


}