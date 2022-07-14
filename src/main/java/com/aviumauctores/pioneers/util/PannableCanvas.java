package com.aviumauctores.pioneers.util;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

// original code taken from https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer

public class PannableCanvas extends Pane {

    DoubleProperty myScale = new SimpleDoubleProperty(1.0);

    public PannableCanvas() {
        // add scale transform
        scaleXProperty().bind(myScale);
        scaleYProperty().bind(myScale);
    }

    public double getScale() {
        return myScale.get();
    }

    public void setScale(double scale) {
        myScale.set(scale);
    }

    public void setPivot(double x, double y) {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
    }
}