package com.example.turtlefit;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static com.github.mikephil.charting.utils.ColorTemplate.rgb;

public class ShellActivity extends SimpleActivity{
    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    final int[] weekColors = {rgb("#893F04"), rgb("#C67F43"), rgb("#D49B7E"),
            rgb("#e5a88b"), rgb("#989E8B"), rgb("#7B876D"), rgb("#556254")};
    BarChart barChart;
    TextView from, to;
    ImageView left, right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell);

        prevActivity = MainActivity.class;

        barChart = findViewById(R.id.barChart);
        from = findViewById(R.id.fromDate);
        to = findViewById(R.id.toDate);
        left = findViewById(R.id.left);
        right = findViewById(R.id.right);

        Description description = new Description();        //remove description bottom right corner
        description.setEnabled(false);
        barChart.setDescription(description);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setDrawLabels(false);
        XAxis x = barChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));


        setData(Calendar.getInstance());

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(from.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(calendar.DAY_OF_YEAR, -1);
                setData(calendar);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(sdf.parse(to.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calendar.add(calendar.DAY_OF_YEAR, 7);
                setData(calendar);

            }
        });

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Calendar c = Calendar.getInstance();
                try {
                    c.setTime(sdf.parse(from.getText().toString()));
                } catch (ParseException parseException) {
                    parseException.printStackTrace();
                }
                c.add(c.DAY_OF_YEAR, (int)e.getX());
                String date = sdf.format(c.getTime());
                Intent intent = new Intent(ShellActivity.this, DayActivity.class);
                intent.putExtra(null, date);
                ShellActivity.this.startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        View.OnClickListener dateListener =  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        setData(cal);
                    }
                };

                new DatePickerDialog(ShellActivity.this, dateListener,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();


            }
        };

        from.setOnClickListener(dateListener);
        to.setOnClickListener(dateListener);
    }

    private List<Integer> retrieveData(Calendar lastDay){   //returns total for each day of the week
        List<Integer> values = new ArrayList<>();

        Calendar calendar = lastDay;
        String date = sdf.format(calendar.getTime());   //set last date
        to.setText(date);

        DBhelper db = new DBhelper(ShellActivity.this);     //retrieve totals
        int tot;
        for(int i=7; i>0; i--){
            tot = 0;
            for(Sport s: db.getAll(date)){      //get total
                tot += s.getDifficulty() + 1 ;  //difficulties are 0-based
            }
            values.add(tot);
            calendar.add(calendar.DAY_OF_YEAR, -1);
            date = sdf.format(calendar.getTime());
        }

        calendar.add(calendar.DAY_OF_YEAR, 1);  //last for-loop iteration
        date = sdf.format(calendar.getTime());      //set first date
        from.setText(date);

        db.close();
        Collections.reverse(values);
        return values;
    }


    private void setData(Calendar day){
        int toSun = 7 - day.get(Calendar.DAY_OF_WEEK) + 1;    //get days to sunday == days to the last
        if(toSun != 7){
            day.add(Calendar.DAY_OF_YEAR, toSun);
        }
        List<Integer> valueList = retrieveData(day);   //input data
        List<BarEntry> entries = new ArrayList<>();

        for(int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i));
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Total activities");
        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barDataSet.setColors(weekColors);

        barChart.invalidate();  //refresh
        barChart.animateY(1000);    //y-axis animation
    }

    public ArrayList<String> getAreaCount() {
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        xAxisLabel.add("Mon");
        xAxisLabel.add("Tue");
        xAxisLabel.add("Wed");
        xAxisLabel.add("Thu");
        xAxisLabel.add("Fri");
        xAxisLabel.add("Sat");
        xAxisLabel.add("Sun");
        return xAxisLabel ;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ShellActivity.this, MainActivity.class);
        ShellActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}