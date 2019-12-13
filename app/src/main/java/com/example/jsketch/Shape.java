package com.example.jsketch;

import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

public class Shape {
	public Tool shapeType;
	public Paint paintType;
	public float startX, stopX, startY, stopY, radius, centreX, centreY;
	public Rect rect;

    public Shape(Paint p, Tool shapeType, float startX, float startY, float stopX, float stopY, float radius, float centreX, float centreY) {
    	this.paintType = p;
    	this.shapeType = shapeType;

        if (this.shapeType == Tool.CIRCLE) {
            this.radius = radius;
            this.centreX = centreX;
            this.centreY = centreY;
        }

        else if (this.shapeType == Tool.LINE) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
        }

        else if (this.shapeType == Tool.RECTANGLE) {
            // starting coordinates always have to be smaller
            this.rect = new Rect(
                    (int) Math.min(startX, stopX), // left
                    (int) Math.min(startY, stopY), // top
                    (int) Math.max(stopX, startX), // right
                    (int) Math.max(stopY, startY) // bottom
            );
        }
	}

	public Boolean isSelected(float x, float y) {
        Boolean result = false;
        if (this.shapeType == Tool.LINE) {
            result = this.detectOnLine(x, y);
        }
        else if (this.shapeType == Tool.CIRCLE) {
            result = distance(this.centreX, this.centreY, x, y) <= this.radius;
        }

        else if (this.shapeType == Tool.RECTANGLE) {
            result = this.rect.contains((int) x, (int) y);
        }
        return result;
    }

    public Boolean detectOnLine(float x, float y) {
        // by triangle inequality
        float triangleDistance = distance(this.startX, this.startY, x, y) + distance(this.stopX, this.stopY, x, y);
        float lineDistance = distance(this.startX, this.startY, this.stopX, this.stopY);

        // have 1f to compensate rounding error
        return triangleDistance <= lineDistance + 1f;
    }

    public float distance(float ax, float ay, float bx, float by) {
        return (float) Math.sqrt((ax - bx) * (ax - bx) + (ay - by) * (ay - by));
    }

    public Shape transform(float draggedX, float draggedY) {
        if (this.shapeType == Tool.CIRCLE) {
            this.centreX += draggedX;
            this.centreY += draggedY;
        }
        else if (this.shapeType == Tool.LINE) {
            this.startX += draggedX;
            this.stopX += draggedX;
            this.startY += draggedY;
            this.stopY += draggedY;
        }
        else if (this.shapeType == Tool.RECTANGLE) {
            this.rect.set(
                    (int) (this.rect.left + draggedX), // left
                    (int) (this.rect.top + draggedY), // top
                    (int) (this.rect.right + draggedX), // right
                    (int) (this.rect.bottom + draggedY) // bottom
            );
        }
        return this;
    }
}
