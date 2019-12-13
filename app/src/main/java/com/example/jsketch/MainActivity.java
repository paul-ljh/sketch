package com.example.jsketch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity{
    private Model model;
    private ImageButton lineButton, circleButton, selectButton, rectButton, blueButton, greenButton, redButton, eraseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portrait);
        appSetUp();
    }

    private void appSetUp() {
        this.model = Model.getInstance();

        this.eraseButton = findViewById(R.id.erase);
        this.eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.eraseSelectedShape();
            }
        });

        this.selectButton = findViewById(R.id.select);
        this.selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setCurrentTool(Tool.SELECT);
            }
        });

        this.lineButton = findViewById(R.id.line);
        this.lineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setCurrentTool(Tool.LINE);
            }
        });

        this.circleButton = findViewById(R.id.circle);
        this.circleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setCurrentTool(Tool.CIRCLE);
            }
        });

        this.rectButton = findViewById(R.id.rect);
        this.rectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setCurrentTool(Tool.RECTANGLE);
            }
        });

        this.blueButton = findViewById(R.id.blue);
        this.blueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setCurrentPaint(2);
            }
        });

        this.greenButton = findViewById(R.id.green);
        this.greenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setCurrentPaint(1);
            }
        });

        this.redButton = findViewById(R.id.red);
        this.redButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setCurrentPaint(0);
            }
        });

        CanvasView canvasView = new CanvasView(this, this.model);
        ViewGroup canvasVg = findViewById(R.id.canvas_view);
        canvasVg.addView(canvasView);

        this.model.addObserver(canvasView);
        this.model.sendChangesToObservers();
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        Log.d("DEMO", "orientation change");
//        super.onConfigurationChanged(newConfig);
//
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(R.layout.landscape);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
//            setContentView(R.layout.portrait);
//        }
//    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putBundle("newBundle", newBundle);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        savedInstanceState.getBundle("newBundle");
//    }
}
