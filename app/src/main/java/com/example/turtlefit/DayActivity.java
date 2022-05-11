package com.example.turtlefit;

import androidx.annotation.RequiresApi;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendVerticalAlignment;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class DayActivity extends SimpleActivity {

    public static final int[] MANY_COLORS = {
            rgb("#984aff"), rgb("#ffa229"), rgb("#fa6eff"), rgb("#66c0d9"),
            rgb("#2bc445"), rgb("#fa5d57"), rgb("#2b68c4"), rgb("#fcbbfa"),
            rgb("#9e9e9e"), rgb("#f2e33f"), rgb("#75544a"), rgb("#5da7c2"),
            rgb("#de9e7c"), rgb("#f54545"), rgb("#a9f26d"), rgb("#db5af2")
    };

    TextView dateTxt, notes;
    PieChart pieChart;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        prevActivity = ShellActivity.class;

        dateTxt = findViewById(R.id.dateTxt);
        notes = findViewById(R.id.notes);
        pieChart = findViewById(R.id.pieChart);
        pieChart.setHoleColor(getColor(R.color.Transparent));
        dateTxt.setText(getDate());

        DBhelper db = new DBhelper(DayActivity.this);
        List<Sport> l = db.getAll(getDate());
        List<PieEntry> pieEntries = new ArrayList<>();
        for(Sport s: l){
            pieEntries.add(new PieEntry((s.getDifficulty()) + 1, s.getActivity()));
        }
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setColors(MANY_COLORS);
        PieData data = new PieData(dataSet);
        data.setValueTextSize(14);
        data.setValueTextColor(getColor(R.color.white_almost));
        pieChart.setData(data);
        pieChart.invalidate();
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);

        pieChart.getLegend().setTextSize(16);
        pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
        pieChart.getLegend().setVerticalAlignment(LegendVerticalAlignment.TOP);

        pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String txt = l.get((int) h.getX()).getNotes();
                notes.setText("");
                notes.setVisibility(View.INVISIBLE);

                if(txt.compareTo("") != 0){
                    notes.setText(txt);
                    notes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected() {
                notes.setVisibility(View.INVISIBLE);
            }
        });
    }

    private String getDate(){
        Bundle extras = getIntent().getExtras();
        String date = "-";
        if(extras != null){
            date = (String)extras.get(null);
        }
        return date;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private boolean isPointInsideView(float x, float y, View view) {
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        return ((x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight())));
    }


}