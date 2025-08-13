package com.example.campsafe.studentDashboard;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.campsafe.R;
import com.example.campsafe.dbModels.PreBookDB;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PreBookForm {
    private final Context context;
    private String selectedDate;
    private String selectedTime;
    private final String name;
    private final int id;

    // Constructor to initialize context
    public PreBookForm(Context context, String studentName, int studentId) {
        this.context = context;
        this.name = studentName;
        this.id = studentId;
    }

    public void showPreBookFormDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.pre_book);
        if (dialog.getWindow() != null) {
            // Set dialog size to 90% width and height of the screen
            dialog.getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9),
                    (int) (context.getResources().getDisplayMetrics().heightPixels * 0.9)
            );
        }
        dialog.show();

        // Find dialog UI elements
        Button selectDateBtn = dialog.findViewById(R.id.date_picker);
        Button selectTimeBtn = dialog.findViewById(R.id.time_picker);
        Button submitBtn = dialog.findViewById(R.id.btn_submit);
        Button cancelBtn = dialog.findViewById(R.id.btn_cancel);
        TextView visitorNameInput = dialog.findViewById(R.id.visitor_name);
        TextView numVisitorsInput = dialog.findViewById(R.id.num_visitors);

        // Date picker dialog
        selectDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(context, (view, year, month, day) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(year, month, day);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selectedDate = dateFormat.format(selectedCal.getTime());
                selectDateBtn.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time picker dialog
        selectTimeBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new TimePickerDialog(context, (view, hour, minute) -> {
                Calendar selectedCal = Calendar.getInstance();
                selectedCal.set(Calendar.HOUR_OF_DAY, hour);
                selectedCal.set(Calendar.MINUTE, minute);
                selectedCal.set(Calendar.SECOND, 0);
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                selectedTime = timeFormat.format(selectedCal.getTime());
                selectTimeBtn.setText(selectedTime);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
        });

        // Submit button click
        submitBtn.setOnClickListener(v -> {
            String visitorName = visitorNameInput.getText().toString().trim();
            String numVisitorsStr = numVisitorsInput.getText().toString().trim();

            // Validate inputs
            if (visitorName.isEmpty() || numVisitorsStr.isEmpty() || selectedDate == null || selectedTime == null) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int numVisitors;
            try {
                numVisitors = Integer.parseInt(numVisitorsStr);
                if (numVisitors <= 0) {
                    Toast.makeText(context, "Number of visitors must be positive", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid number of visitors", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insert booking data into Firestore
            PreBookDB prebook = new PreBookDB();
            prebook.insertData(visitorName, String.valueOf(numVisitors), selectedDate, selectedTime, name, id, task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "Pre-booking saved successfully", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(context, "Failed to save pre-booking: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Cancel button click dismisses dialog
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
    }
}