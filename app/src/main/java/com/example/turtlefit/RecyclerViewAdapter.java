package com.example.turtlefit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context mContext;
    private List<Sport> sportList = new ArrayList<>();
    public static String others = "Other";

    public RecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setSportList(List<Sport> recordList) {
        this.sportList = recordList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String activityName = sportList.get(position).getActivity();
        holder.activity.setText(activityName);

        View.OnClickListener addListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TodayActivity.square.getVisibility() == View.INVISIBLE) {
                    TodayActivity.pos = position;
                    holder.add.setVisibility(View.INVISIBLE);
                    TodayActivity.square.setVisibility(View.VISIBLE);
                    TodayActivity.save.setEnabled(false);
                    TodayActivity.selTxt.setText(holder.activity.getText());
                    if (holder.activity.getText().toString().compareTo(others) != 0) {
                        TodayActivity.actEdit.setVisibility(View.INVISIBLE);
                    } else {
                        TodayActivity.actEdit.setVisibility(View.VISIBLE);
                    }
                }
            }
        };


        View.OnClickListener savedListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TodayActivity.square.getVisibility() == View.INVISIBLE) {
                    TodayActivity.square.setVisibility(View.VISIBLE);
                    TodayActivity.actEdit.setVisibility(View.INVISIBLE);
                    TodayActivity.setSport(sportList.get(position));
                    TodayActivity.modifying = true;
                }
            }
        };

        holder.add.setOnClickListener(addListener);

        if(sportList.get(position).isSaved()){ //can modify
            holder.parent.setOnClickListener(savedListener);
            holder.add.setVisibility(View.INVISIBLE);
            holder.star.setVisibility(View.VISIBLE);
        } else {    //can add
            holder.parent.setOnClickListener(addListener);
            holder.add.setVisibility(View.VISIBLE);
            holder.star.setVisibility(View.INVISIBLE);
        }

        holder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Sport s: sportList){
                    if(s.getActivity() == activityName){
                        TodayActivity.toAdd.remove(s);
                        s.setSaved(false);
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage("Are you sure you want to remove this activity?");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete element from db
                        DBhelper db = new DBhelper(mContext);
                        db.deleteOne(sportList.get(position));
                        db.close();

                        holder.add.setVisibility(View.VISIBLE);
                        holder.star.setVisibility(View.INVISIBLE);

                        if(TodayActivity.isOthers(sportList.get(position))){    //remove from the list only if "Others"
                            sportList.remove(sportList.get(position));
                        }
                        TodayActivity.showAll(sportList, mContext);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.create().show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return sportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView activity;
        ImageView add;
        ImageView star;
        CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            parent = itemView.findViewById(R.id.parentCard);
            activity = itemView.findViewById(R.id.activityTxt);
            add = itemView.findViewById(R.id.addImage);
            star = itemView.findViewById(R.id.starImage);
        }
    }

}
