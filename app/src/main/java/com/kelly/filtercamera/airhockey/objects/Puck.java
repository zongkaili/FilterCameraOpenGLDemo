/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.kelly.filtercamera.airhockey.objects;

import java.util.List;

import  com.kelly.filtercamera.airhockey.data.VertexArray;
import  com.kelly.filtercamera.airhockey.objects.ObjectBuilder.DrawCommand;
import  com.kelly.filtercamera.airhockey.objects.ObjectBuilder.GeneratedData;
import  com.kelly.filtercamera.airhockey.programs.ColorShaderProgram;
import  com.kelly.filtercamera.airhockey.util.Geometry.Cylinder;
import  com.kelly.filtercamera.airhockey.util.Geometry.Point;

public class Puck {
    private static final int POSITION_COMPONENT_COUNT = 3;

    public final float radius, height;

    private final VertexArray vertexArray;
    private final List<DrawCommand> drawList;

    public Puck(float radius, float height, int numPointsAroundPuck) {
        GeneratedData generatedData = ObjectBuilder.createPuck(new Cylinder(
            new Point(0f, 0f, 0f), radius, height), numPointsAroundPuck);

        this.radius = radius;
        this.height = height;

        vertexArray = new VertexArray(generatedData.vertexData);
        drawList = generatedData.drawList;
    }

    public void bindData(ColorShaderProgram colorProgram) {
        vertexArray.setVertexAttribPointer(0,
            colorProgram.getPositionAttributeLocation(),
            POSITION_COMPONENT_COUNT, 0);
    }

    public void draw() {
        for (DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }
}