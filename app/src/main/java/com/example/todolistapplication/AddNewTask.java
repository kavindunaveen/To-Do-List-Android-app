/*
   Fragment for adding or updating To-Do items, Provides a user interface to input task details,
   including text, date, and time
 */

package com.example.todolistapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.example.todolistapplication.Model.ToDoModel;
import com.example.todolistapplication.Utils.DatabaseHandler;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.Calendar;

public class AddNewTask extends BottomSheetDialogFragment {

    // identifying the BottomSheetDialogFragment
    public static final String TAG = "ActionBottomDialog";


    private EditText newTaskText;
    private Button newTaskSaveButton;
    private DatabaseHandler db;
    private TextView dateTextView;
    private TextView timeTextView;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private Calendar calendar;

    // Creates a new instance of AddNewTask
    public static AddNewTask newInstance() {
        return new AddNewTask();
    }

    // Called when the fragment is created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the dialog style
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    // Called when creating the view of the fragment
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        // Adjust the keyboard when it appears
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    // Called after the view is created
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize
        newTaskText = requireView().findViewById(R.id.newTaskText);
        newTaskSaveButton = getView().findViewById(R.id.newTaskBtn);
        dateTextView = requireView().findViewById(R.id.dateTextView);
        timeTextView = requireView().findViewById(R.id.timeTextView);
        datePicker = requireView().findViewById(R.id.datePicker);
        timePicker = requireView().findViewById(R.id.timePicker);
        calendar = Calendar.getInstance();

        // Check if it is an update operation
        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if (bundle != null) {
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            assert task != null;
            if (task.length() > 0)
                newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_200));
        }

        // Initialize the database handler
        db = new DatabaseHandler(getActivity());
        db.openDatabase();

        // Add text change listener to enable/disable the save button based on input
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals("")) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.purple_200));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Set click listeners for the save button, date, and time fields
        final boolean finalIsUpdate = isUpdate;
        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                String date = dateTextView.getText().toString();
                String time = timeTextView.getText().toString();

                if (finalIsUpdate) {
                    db.updateTask(bundle.getInt("id"), text);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    task.setDate(date);
                    task.setTime(time);
                    db.insertTask(task);
                }
                dismiss();
            }
        });

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    // Display the date picker dialog
    public void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String formattedDate = android.text.format.DateFormat.format("yyyy-MM-dd", calendar).toString();
                        dateTextView.setText(formattedDate);
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Display the time picker dialog
    public void showTimePicker() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
                        timeTextView.setText(formattedTime);
                    }
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true // 24-hour format
        );
        timePickerDialog.show();
    }

    // Called when the dialog is dismissed
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        // Notify the activity that the dialog is closed
        Activity activity = getActivity();
        if (activity instanceof DialogCloseListener)
            ((DialogCloseListener) activity).handleDialogClose(dialog);
    }
}
