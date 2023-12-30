/*
 The main activity displaying the To-Do list Includes RecyclerView for displaying tasks,
 FloatingActionButton for adding new tasks, and utilizes a DatabaseHandler to handle database operations.
 */

package com.example.todolistapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import com.example.todolistapplication.Adapter.ToDoAdapter;
import com.example.todolistapplication.Model.ToDoModel;
import com.example.todolistapplication.Utils.DatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {


    private RecyclerView taskRecyclerView;
    private ToDoAdapter tasksAdapter;
    private List<ToDoModel> taskList;
    private DatabaseHandler db;
    private FloatingActionButton fab;

    // Called when the activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        // Initialize the database handler
        db = new DatabaseHandler(this);
        db.openDatabase();

        // Initialize the task list
        taskList = new ArrayList<>();

        // Initialize the RecyclerView
        taskRecyclerView = findViewById(R.id.tasksRecycleView);
        taskRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new ToDoAdapter(db, this);
        taskRecyclerView.setAdapter(tasksAdapter);

        // Initialize the FloatingActionButton
        fab = findViewById(R.id.fab);

        // Initialize ItemTouchHelper for swipe-to-delete functionality
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(taskRecyclerView);

        // Retrieve tasks from the database, reverse the order, and set them in the adapter
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTask(taskList);

        // Set click listener for the FloatingActionButton to open the AddNewTask dialog
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    // Called when the dialog is closed, refreshes the task list in the adapter
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTask(taskList);
        tasksAdapter.notifyDataSetChanged();
    }
}
