package com.example.jsketch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.lang.Math;

import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;


enum Tool {
    // Excluded erase here becuz erase function is based on select
	LINE, CIRCLE, RECTANGLE, SELECT
}

public class Model extends Observable {
    private static final Model ourInstance = new Model();
    private final ArrayList<Paint> paintList = new ArrayList<>(
            Arrays.asList(new Paint(), new Paint(), new Paint())
    );
    private ArrayList<Shape> shapeList = new ArrayList<>();

    // to detect double clicks
    private Boolean hasMoved = false;

    // Selected item will be the last item in shapeList if this variable is true
    private Boolean hasSelected = false;
    private Boolean hasDragged = false;

    private Shape shapeInProgress = null;
    private Tool currentTool = Tool.LINE;
    private Paint currentPaint;

    Model() {
        this.setupPaint();
    }

    private void setupPaint() {
        for (int i = 0; i < 3; ++i) {
            this.paintList.get(i).setAntiAlias(true);
            this.paintList.get(i).setStyle(Paint.Style.FILL_AND_STROKE);
            this.paintList.get(i).setStrokeWidth(30f);
            if (i == 0) {
                this.paintList.get(i).setColor(Color.RED);
            }
            else if (i == 1) {
                this.paintList.get(i).setColor(Color.GREEN);
            }
            else if (i == 2) {
                this.paintList.get(i).setColor(Color.BLUE);
            }
        }
        this.currentPaint = this.paintList.get(1);
    }

    public static Model getInstance() {
        return ourInstance;
    }

    public ArrayList<Shape> getShapeList() {
        return this.shapeList;
    }

    public Tool getCurrentTool() {
        return this.currentTool;
    }

    public Paint getCurrentPaint() { return this.currentPaint; }

    public Shape getShapeInProgress() {
        return this.shapeInProgress;
    }

    public Boolean getHasMoved() { return this.hasMoved; }

    public Boolean getHasSelected() { return this.hasSelected; }

    public Boolean getHasDragged() { return this.hasDragged; }

    public void setHasDragged(Boolean bool) { this.hasDragged = bool; }

    public void setHasMoved(Boolean bool) { this.hasMoved = bool; }

    public void setHasSelected(Boolean bool) { this.hasSelected = bool; }

    public void setCurrentPaint(int paintListIndex) {
        if (paintListIndex == 0) {
            this.currentPaint = this.paintList.get(0);
        }
        else if (paintListIndex == 1) {
            this.currentPaint = this.paintList.get(1);
        }
        else if (paintListIndex == 2) {
            this.currentPaint = this.paintList.get(2);
        }

        // if a paint is selected while a shape is already selected, change the selected shape's color
        if (this.hasSelected) this.shapeList.get(this.shapeList.size() - 1).paintType = this.currentPaint;
        this.sendChangesToObservers();
    }

    public void setCurrentTool(Tool newTool) {
        if (newTool == Tool.SELECT) {
            this.currentTool = newTool;
        }
        // newTool != Tool.SELECT
        else {
            // disallow setting tool to Circle, Line or Triangle when there is a shape selected
            if (!this.hasSelected) {
                this.currentTool = newTool;
                this.hasDragged = false;
                this.hasSelected = false;
            }
        }
    }

    public void addToShapeList(Shape shape) {
        this.shapeList.add(shape);
    }

    public Shape generateShape(float startX, float startY, float stopX, float stopY) {
        Shape returnShape = null;
        if (this.currentTool == Tool.LINE) {
            returnShape = new Shape(this.currentPaint, Tool.LINE, startX, startY, stopX, stopY, 0, 0, 0);
        }
        else if (this.currentTool == Tool.CIRCLE) {
            // width and height could be negative, depending on the direction drawn
            float width = stopX - startX;
            float height = stopY - startY;
            float centreX = startX + width * 0.5f;
            float centreY = startY + height * 0.5f;

            // however radius cannot be negative, therefore taking the absolute
            float radius = Math.min(Math.abs(width), Math.abs(height)) * 0.5f;
            returnShape = new Shape(this.currentPaint, Tool.CIRCLE, 0, 0, 0, 0, radius, centreX, centreY);
        }
        else if (this.currentTool == Tool.RECTANGLE) {
            returnShape = new Shape(this.currentPaint, Tool.RECTANGLE, startX, startY, stopX, stopY, 0,0, 0);
        }
        else if (this.currentTool == Tool.SELECT) {
            float draggedX = stopX - startX;
            float draggedY = stopY - startY;
            returnShape = this.shapeList.get(this.shapeList.size() - 1).transform(draggedX, draggedY);
        }
        return returnShape;
    }

    public void drawInProgress(float startX, float startY, float stopX, float stopY) {
        this.shapeInProgress = this.generateShape(startX, startY, stopX, stopY);
        sendChangesToObservers();
    }

    public void finishedDrawing(float startX, float startY, float stopX, float stopY) {
        // add to shapeList when it isn't in selection mode
        if (!this.hasSelected) {
            addToShapeList(this.generateShape(startX, startY, stopX, stopY));
        }
        Log.d("DEMO", this.shapeList.size() + " shapes are in the list");
        this.shapeInProgress = null;
        sendChangesToObservers();
    }

    public void selectShape(float x, float y) {
        // Backwards search, since the last item is drawn last
        for (int i = this.shapeList.size()-1; i >= 0; --i) {
            if (this.shapeList.get(i).isSelected(x, y)) {
                // move the selected shape to the back of the list
                Shape selected = this.shapeList.remove(i);
                this.shapeList.add(selected);

                // reflect on the color palette if a shape is selected
                this.currentPaint = selected.paintType;

                this.hasSelected = true;
                Log.d("DEMO", "hasSelected is true");
                sendChangesToObservers();
                return;
            }
        }
        // un-select a shape
        this.hasSelected = false;
        Log.d("DEMO", "hasSelected is false");
    }

    public void eraseSelectedShape() {
        Log.d("DEMO", "erasing. hasSelected is " + this.hasSelected);
        if (this.hasSelected) {
            this.hasSelected = false;
            this.shapeList.remove(this.shapeList.size() - 1);
            Log.d("DEMO", "There are " + this.shapeList.size() + "shapes in the list" );
            sendChangesToObservers();
        }
    }

    public void sendChangesToObservers() {
        setChanged();
        notifyObservers();
    }

    @Override
    public synchronized void deleteObserver(Observer o)
    {
        super.deleteObserver(o);
    }

    @Override
    public synchronized void addObserver(Observer o)
    {
        super.addObserver(o);
    }

    @Override
    public synchronized void deleteObservers()
    {
        super.deleteObservers();
    }

    @Override
    public void notifyObservers()
    {
        super.notifyObservers();
    }
}
