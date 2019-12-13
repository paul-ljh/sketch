package com.example.jsketch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Observable;
import java.util.Observer;

public class VerticalView extends LinearLayout implements Observer {
    private CanvasView canvasView;
//    private ToolbarView toolbarView;
    private Model model;


    public VerticalView(Context context, Model model) {
        super(context);

        this.model = model;

        this.canvasView = new CanvasView(context, this.model);
        ViewGroup canvasVg = findViewById(R.id.canvas_view);
        canvasVg.addView(this.canvasView);
    }

    // model will calls this to update the view
	public void update(Observable observable, Object data) {
        this.canvasView.postInvalidate();
    }
}
