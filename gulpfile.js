var gulp = require("gulp");
var browserify = require("browserify");
var source = require('vinyl-source-stream');
var watchify = require("watchify");
var tsify = require("tsify");
var gutil = require("gulp-util");

var paths = {
    pages: ['src/main/resources/webapp/*.html']
};

var watchedBrowserify = watchify(browserify({
    basedir: '.',
    debug: true,
    entries: ['src/main/typescript/org/bubblecloud/webvr/main.ts'],
    cache: {},
    packageCache: {}
}).plugin(tsify));

function bundle() {
    return watchedBrowserify
        .bundle()
        .pipe(source('bundle.js'))
        .pipe(gulp.dest("build/resources/main/webapp"));
}

gulp.task("default", bundle);
watchedBrowserify.on("update", bundle);
watchedBrowserify.on("log", gutil.log);
