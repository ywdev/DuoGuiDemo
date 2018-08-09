package com.appdev.duoguidemo;

import com.esri.core.geometry.Point;

import java.util.ArrayList;
import java.util.List;

public class EditingStates {


    List<Point> points = new ArrayList<>();
    boolean midPointSelected = false;
    boolean vertexSelected = false;
    int insertingIndex;

    public EditingStates(List<Point> points, boolean midpointselected, boolean vertexselected, int insertingindex) {
        this.points.addAll(points);
        this.midPointSelected = midpointselected;
        this.vertexSelected = vertexselected;
        this.insertingIndex = insertingindex;
    }


}
