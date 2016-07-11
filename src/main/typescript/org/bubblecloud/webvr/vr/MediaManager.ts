/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {ApplicationContext} from "./ApplicationContext";
import {OBJLoader} from "./OBJLoader";

declare var canvasLoader: any;

export class MediaManager {

    models: {[key: string]: THREE.Object3D} = {}
    textures: {[key: string]: THREE.Texture} = {}

    applicationContext: ApplicationContext;
    canvasLoaderHidden = false;

    objLoader: OBJLoader;
    textureLoader: THREE.TextureLoader;

    constructor(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext;

        applicationContext.loadingManager = new THREE.LoadingManager();
        applicationContext.loadingManager.onProgress = function (item, loaded, total) {
            console.log(item, loaded, total);
            if (this.canvasLoaderHidden) {
                this.canvasLoaderHidden = false
                canvasLoader.show();
            }
        };
        applicationContext.loadingManager.onLoad = function () {
            console.log('all items loaded');
            canvasLoader.hide();
            this.canvasLoaderHidden = true
        };
        applicationContext.loadingManager.onError = function () {
            console.log('there has been an error');
        };

        this.objLoader = new OBJLoader(applicationContext.loadingManager);
        this.textureLoader = new THREE.TextureLoader(applicationContext.loadingManager);
    }

    loadModel(name: string, path: string, onLoad: (name: String, model:THREE.Object3D) => void) {
        this.objLoader.load(path, (object:THREE.Object3D) => {
            this.models[name] = object;
            onLoad(name, object);
        } );
    }

    loadTexture(path: string) {
        return this.textureLoader.load(path, (texture: THREE.Texture) => {
            this.textures[path] = texture;
        } );
    }
}