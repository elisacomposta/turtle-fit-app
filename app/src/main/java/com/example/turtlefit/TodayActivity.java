package com.example.turtlefit;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayActivity extends SimpleActivity{

    TextView date;
    private static RecyclerView activitiesList;
    ImageButton xBtn;
    ImageButton vBtn;
    Button back;
    public static Button save;
    public static CardView square;
    private static TextView act;
    private static RadioGroup rg;
    private static EditText notes;
    public static TextView selTxt;
    public static EditText actEdit;
    int difficulty = -1;
    public static boolean modifying = false;
    private static RecyclerViewAdapter adapter;

    public static int pos;      //position in RecyclerView currently updating
    static List<Sport> toAdd = new ArrayList<>();
    static List<Sport> toUpdate = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);

        prevActivity = MainActivity.class;

        date = findViewById(R.id.dateTxt);
        activitiesList = findViewById(R.id.actList);
        back = findViewById(R.id.backBtn);
        save = findViewById(R.id.saveBtn);
        square = findViewById(R.id.sq);
        act = findViewById(R.id.activityTxt);
        rg = findViewById(R.id.opt);
        notes = findViewById(R.id.notesEdit);
        xBtn = findViewById(R.id.xBtn);
        vBtn = findViewById(R.id.vBtn);
        selTxt = findViewById(R.id.activityTxt);
        actEdit = findViewById(R.id.actEdit);

        setInitialDate(date);
        manageDate(this, date);
        showAll(manageList(), this);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.opt1: difficulty = 0; break;
                    case R.id.opt2: difficulty = 1; break;
                    case R.id.opt3: difficulty = 2; break;
                    default: difficulty = -1;
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        xBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        vBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftKeyboard();

                Sport s = getSport();
                boolean isOthers = false;
                List<Sport> l  = createList();
                for(Sport x: l){
                    if(s.getActivity() == x.getActivity()){
                        break;
                    }
                    if(l.indexOf(x) == l.size()-1){ //last element, "Others"
                        isOthers = true;
                    }
                }

                if(isOthers && s.getActivity().toString().isEmpty()){
                    Toast.makeText(TodayActivity.this, "Please insert activity", Toast.LENGTH_SHORT).show();
                } else if(rg.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(TodayActivity.this, "Please select difficulty", Toast.LENGTH_SHORT).show();
                } else {
                    if (!modifying) {
                        toAdd.add(s);
                    } else {
                        toUpdate.add(s);
                    }
                    square.setVisibility(View.INVISIBLE);
                    showAll(manageList(), TodayActivity.this);
                    clearCard();
                    save.setEnabled(true);
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBhelper db = new DBhelper(TodayActivity.this);
                db.addAll(toAdd);       //add new records to add
                db.updateAll(toUpdate); //update records to update
                modifying = false;
                db.close();
                Toast.makeText(TodayActivity.this, "Saved", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(TodayActivity.this, MainActivity.class);
                TodayActivity.this.startActivity(intent);
                toAdd.clear();
                toUpdate.clear();
            }
        });
    }

    public void setInitialDate(TextView dateTextView){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        dateTextView.setText(sdf.format(cal.getTime()));
    }

    public void manageDate(final Context context, final TextView dateTxt){
        final Calendar cal = Calendar.getInstance();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        final DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateTxt.setText(sdf.format(cal.getTime()));

                showAll(manageList(), TodayActivity.this);
            }
        };

        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(context, dateListener,
                        cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(square.getVisibility() == View.INVISIBLE){
            toAdd.clear();
            toUpdate.clear();
            Intent intent = new Intent(TodayActivity.this, MainActivity.class);
            TodayActivity.this.startActivity(intent);
        } else {
            hideSoftKeyboard();
            square.setVisibility(View.INVISIBLE);
            adapter.notifyItemChanged(pos);
        }
        clearCard();
        save.setEnabled(true);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    private Sport getSport(){
        String activity = act.getText().toString();
        if(act.getText().toString() == RecyclerViewAdapter.others){
            activity = actEdit.getText().toString();
        }
        Sport s = new Sport(activity, date.getText().toString(), difficulty, notes.getText().toString(), true);
        return s;
    }

    private static List<Sport> createList(){
        List<Sport> list = new ArrayList<>();
        list.clear();
        list.add(new Sport("Stretching"));
        list.add(new Sport("Training"));
        list.add(new Sport("Walking"));
        list.add(new Sport("Running"));
        list.add(new Sport("Bike riding"));
        list.add(new Sport("Rollerblading"));
        list.add(new Sport("Other"));
        return list;
    }

    private List<Sport> manageList(){
        DBhelper db = new DBhelper(TodayActivity.this);
        List<Sport> saved = db.getAll(date.getText().toString());
        List<Sport> list = createList();

        for(Sport y: saved){    //set elements already saved
            for(Sport x: list){
                if(x.getActivity().toString().compareTo(y.getActivity().toString())==0){
                    list.set(list.indexOf(x), y);
                    break;
                }
                if(list.indexOf(x) == list.size()-1){ //last one
                    list.set(list.indexOf(x), y);
                    list.add(x);
                }
            }
        }

        for(Sport k: toAdd){    //add elements to save
            for(Sport x: list){
                if(x.getActivity().toString().compareTo(k.getActivity().toString())==0){
                    list.set(list.indexOf(x), k);
                    break;
                }
                if(list.indexOf(x) == list.size()-1){ //last one, "Others"
                    list.set(list.indexOf(x), k);
                    list.add(x);
                }
            }
        }

        db.close();
        return list;
    }

    public static void showAll(List<Sport> l, Context context){
        adapter = new RecyclerViewAdapter(context);
        adapter.setSportList(l);
        activitiesList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        activitiesList.setLayoutManager(new LinearLayoutManager(context));
    }

    public static void setSport(Sport s){
        act.setText(s.getActivity());
        switch(s.getDifficulty()){
            case 0: rg.check(R.id.opt1); break;
            case 1: rg.check(R.id.opt2); break;
            case 2: rg.check(R.id.opt3); break;
        }
        notes.setText(s.getNotes());
        save.setEnabled(false);
    }

    private void clearCard(){
        rg.clearCheck();
        notes.setText("");
        actEdit.setText("");
    }

    public static boolean isOthers(Sport s){
        List<Sport> l = createList();
        for(Sport x: l){
            if(x.getActivity().toString().compareTo(s.getActivity().toString())==0){
                return false;
            }
        }
        return true;
    }

    public void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(imm.isAcceptingText()){
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
    }
}