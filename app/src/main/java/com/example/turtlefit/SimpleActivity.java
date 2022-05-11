package com.example.turtlefit;

import android.content.Intent;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class SimpleActivity extends AppCompatActivity {

    Class prevActivity;
    float initialX, initialY;

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

                if (initialX < finalX) {    //swipe left
                    Intent intent = new Intent(this, prevActivity);
                    this.startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                break;

            case MotionEvent.ACTION_OUTSIDE:
                break;
        }
        return super.onTouchEvent(event);
    }
}
