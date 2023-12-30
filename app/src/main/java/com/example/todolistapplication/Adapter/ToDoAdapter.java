/*
  This adapter connects the ToDoModel data with the UI elements in the RecyclerView,
  Handles displaying, updating, deleting, and editing
 */

package com.example.todolistapplication.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.todolistapplication.AddNewTask;
import com.example.todolistapplication.MainActivity;
import com.example.todolistapplication.Model.ToDoModel;
import com.example.todolistapplication.R;
import com.example.todolistapplication.Utils.DatabaseHandler;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    // List of ToDoModel items
    private List<ToDoModel> todoList;

    // Reference to the main activity and database handler
    private MainActivity activity;
    private DatabaseHandler db;

    // Constructor to initialize the adapter
    public ToDoAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    // Inflates the layout for individual items in the RecyclerView
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    // Binds the data from ToDoModel to the UI elements in the ViewHolder
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());

        // Check if the item has a valid date and time
        if (item.getDate() != null && item.getTime() != null) {
            holder.dateAndTime.setVisibility(View.VISIBLE);

            // Format the date and time
            String formattedDateTime = formatDateAndTime(item.getDate(), item.getTime());
            holder.dateAndTime.setText(formattedDateTime);
        } else {
            holder.dateAndTime.setVisibility(View.GONE);
        }

        // Set the checked state of the CheckBox and handle the change listener
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    // Returns the total number of items in the RecyclerView
    public int getItemCount() {
        return todoList.size();
    }

    // Converts integer to boolean
    private boolean toBoolean(int n) {
        return n != 0;
    }

    // Sets the list of ToDoModel items and notifies the adapter about the data change
    public void setTask(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    // Returns the context of the main activity
    public Context getContext() {
        return activity;
    }

    // Deletes the item at the specified position
    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    // Edits the item at the specified position by displaying a dialog fragment
    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        bundle.putString("date", item.getDate());
        bundle.putString("time", item.getTime());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    // ViewHolder class to hold the views for each item in the RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView dateAndTime;

        // Constructor to initialize the views
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            dateAndTime = view.findViewById(R.id.dateAndTimeTextView);
        }
    }

    // Formats date and time from "yyyy-MM-dd HH:mm" to "EEE, MMM d, yyyy h:mm a"
    private String formatDateAndTime(String date, String time) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date dateTime = inputFormat.parse(date + " " + time);

            SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a", Locale.getDefault());
            return outputFormat.format(dateTime);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
