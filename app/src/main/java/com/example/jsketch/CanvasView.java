package com.example.jsketch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class CanvasView extends View implements Observer {
    private Model model;
    private float startX, startY, prevX, prevY; // latter two for dragging

    public CanvasView(Context context, Model model) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);

        this.model = model;
    }

    private void drawShape(Canvas canvas, Shape shape) {
        if (shape.shapeType == Tool.LINE) {
            canvas.drawLine(shape.startX, shape.startY, shape.stopX, shape.stopY, shape.paintType);
        }
        else if (shape.shapeType == Tool.CIRCLE) {
            canvas.drawCircle(shape.centreX, shape.centreY, shape.radius, shape.paintType);
        }
        else if (shape.shapeType == Tool.RECTANGLE) {
            canvas.drawRect(shape.rect, shape.paintType);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ArrayList<Shape> shapeList = this.model.getShapeList();
//        Log.d("DEMO", "There are " + shapeList.size() + "shapes in the list" );
        for (Shape shape : shapeList) {
            this.drawShape(canvas, shape);
        }

        Shape shapeInProgress = this.model.getShapeInProgress();
        if (shapeInProgress != null) {
            this.drawShape(canvas, shapeInProgress);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            this.startX = x;
            this.startY = y;
            if (this.model.getCurrentTool() == Tool.LINE || this.model.getCurrentTool() == Tool.CIRCLE || this.model.getCurrentTool() == Tool.RECTANGLE) {
                this.model.drawInProgress(startX, startY, startX, startY);
            }
            else if (this.model.getCurrentTool() == Tool.SELECT) {
                this.prevX = x;
                this.prevY = y;
                this.model.selectShape(x, y);
            }
        }

        else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (this.model.getCurrentTool() == Tool.LINE || this.model.getCurrentTool() == Tool.CIRCLE || this.model.getCurrentTool() == Tool.RECTANGLE) {
                this.model.setHasMoved(true);
                this.model.drawInProgress(startX, startY, x, y);
            }
            else if (this.model.getCurrentTool() == Tool.SELECT && this.model.getHasSelected()) {
                this.model.drawInProgress(prevX, prevY, x, y);
                // update the last dragged point
                this.prevX = x;
                this.prevY = y;
                this.model.setHasDragged(true);
            }
        }

        else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            if (this.model.getHasMoved() && (this.model.getCurrentTool() == Tool.LINE || this.model.getCurrentTool() == Tool.CIRCLE || this.model.getCurrentTool() == Tool.RECTANGLE)) {
                this.model.setHasMoved(false);
                this.model.finishedDrawing(startX, startY, x, y);
            }
            else if (this.model.getCurrentTool() == Tool.SELECT && this.model.getHasSelected()) {
                Log.d("DEMO", "released");
                this.model.setHasDragged(false);
                this.model.finishedDrawing(prevX, prevY, x, y);
            }
        }
        return true;
    }

    public void update(Observable observable, Object data) {
        this.postInvalidate();
    }
}
