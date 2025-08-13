package com.example.campsafe.studentDashboard;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.campsafe.R;
import com.example.campsafe.dbModels.CampusInOutDB;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CampusInOutForm {
    private final Context context;
    private String outDate;
    private String outTime;
    private String inDate;
    private String inTime;
    private final String name;
    private final int id;

    // Constructor to initialize context
    public CampusInOutForm(Context context, String studentName, int studentId) {
        this.context = context;
        this.name = studentName;
        this.id = studentId;
    }

    public void showCampusInOutDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_campusinout_form);
        if (dialog.getWindow() != null) {
            // Set dialog size to 90% width and height of the screen
            dialog.getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9),
                    (int) (context.getResources().getDisplayMetrics().heightPixels * 0.9)
            );
        }
        dialog.show();

        // Find dialog UI elements
        //TextView titleText = dialog.findViewById(R.id.titleText);
        TextView purpose = dialog.findViewById(R.id.purpose);
        Button outDatePicker = dialog.findViewById(R.id.out_date_picker);
        Button outTimePicker = dialog.findViewById(R.id.out_time_picker);
        Button inDatePicker = dialog.findViewById(R.id.in_date_picker);
        Button inTimePicker = dialog.findViewById(R.id.in_time_picker);
        Button submitButton = dialog.findViewById(R.id.submitButton);
        Button cancelButton = dialog.findViewById(R.id.btn_cancel);


        // Date picker dialog
        outDatePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(context, (view, year, month, day) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                outDate = dateFormat.format(selectedCal.getTime());
                outDatePicker.setText(outDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time picker dialog
        outTimePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(context, (view, hour, minute) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(Calendar.HOUR_OF_DAY, hour);
                selectedCal.set(Calendar.MINUTE, minute);
                selectedCal.set(Calendar.SECOND, 0);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                outTime = timeFormat.format(selectedCal.getTime());
                outTimePicker.setText(outTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // Date picker dialog
        inDatePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(context, (view, year, month, day) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                inDate = dateFormat.format(selectedCal.getTime());
                inDatePicker.setText(inDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time picker dialog
        inTimePicker.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(context, (view, hour, minute) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(Calendar.HOUR_OF_DAY, hour);
                selectedCal.set(Calendar.MINUTE, minute);
                selectedCal.set(Calendar.SECOND, 0);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                inTime = timeFormat.format(selectedCal.getTime());
                inTimePicker.setText(inTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // Submit button click
        submitButton.setOnClickListener(v -> {
            String purposeText = purpose.getText().toString().trim();

            // Validate inputs
            if (purposeText.isEmpty() || outDate == null || outTime== null || inDate == null || inTime == null) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }
            // Insert booking data into Firestore
            CampusInOutDB campusinout = new CampusInOutDB();
            campusinout.insertData(id, name,purposeText,outDate,outTime,inDate,inTime,task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "CampusInOut Record saved successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Failed to save Record: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Cancel button click dismisses dialog
        cancelButton.setOnClickListener(v -> dialog.dismiss());
    }
}
