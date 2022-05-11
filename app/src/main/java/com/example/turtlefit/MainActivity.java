package com.example.turtlefit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button today, shell;
    ImageView bg;

    float initialX, initialY;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bg = findViewById(R.id.bg);
        bg.setScaleType(ImageView.ScaleType.CENTER_CROP);

        today = findViewById(R.id.todayBtn);
        shell = findViewById(R.id.shellBtn);

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startToday();
            }
        });

        shell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startShell();
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:   //initial positions
                initialX = event.getX();
                initialY = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:   //intermediates
                break;

            case MotionEvent.ACTION_UP:     //final release
                float finalX = event.getX();
                float finalY = event.getY();
                if (initialX > finalX && initialX - finalX < 500) {    //swipe left
                    Intent intent = new Intent(MainActivity.this, TodayActivity.class);
                    MainActivity.this.startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                } else if (initialX < finalX && finalX - initialX < 500) {    //swipe right - do nothing
                } else if (initialY < finalY) {    //swipe down
                    startToday();
                } else if (initialY > finalY) {    //swipe up
                    startShell();
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_OUTSIDE:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void startToday(){
        Intent intent = new Intent(MainActivity.this, TodayActivity.class);
        MainActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void startShell(){
        Intent intent = new Intent(MainActivity.this, ShellActivity.class);
        MainActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "To exit the app, press back again", Toast.LENGTH_SHORT).show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}