/// <reference path="../../../../../../../typings/globals/webvr-api/index.d.ts" />
/// <reference path="../../../../../../../typings/globals/three/index.d.ts" />

import {ApplicationContext} from "./ApplicationContext";

declare var canvasLoader: any;

export class MediaManager {

    models: {[key: string]: THREE.Object3D} = {}
    applicationContext: ApplicationContext;
    canvasLoaderHidden = false;

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

        canvasLoader.show();
    }
    
    
}