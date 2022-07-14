package com.aviumauctores.pioneers.util;

// original code taken from https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer

/**
 * Mouse drag context used for scene.
 */
class DragContext {
    double mouseAnchorX;
    double mouseAnchorY;

    double translateAnchorX;
    double translateAnchorY;
}
