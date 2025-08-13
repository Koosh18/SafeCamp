package com.example.campsafe.guardDashboard;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.campsafe.R;
import com.example.campsafe.dbModels.FacultyDB;
import com.example.campsafe.dbModels.NewVisitorDB;
import com.example.campsafe.dbModels.StudentDB;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NewVisitorForm {
    private final Context context;
    private String selectedDate;
    private String selectedTime;

    public NewVisitorForm(Context context) {
        this.context = context;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        selectedDate = dateFormat.format(calendar.getTime());
        selectedTime = timeFormat.format(calendar.getTime());
    }

    public void showNewVisitorFormDialog() {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.fragment_new_visitor);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9),
                    (int) (context.getResources().getDisplayMetrics().heightPixels * 0.9)
            );
        }
        dialog.show();

        EditText name = dialog.findViewById(R.id.visitorName);
        EditText numVisitors = dialog.findViewById(R.id.numberOfVisitors);
        EditText reason = dialog.findViewById(R.id.reasonForVisit);
        RadioGroup rad = dialog.findViewById(R.id.visitorTypeGroup);
        AutoCompleteTextView auto = dialog.findViewById(R.id.idNumber);
        Button submit = dialog.findViewById(R.id.submitVisitorButton);
        Button cancel = dialog.findViewById(R.id.cancelVisitorButton);

        RadioButton stud = dialog.findViewById(R.id.radioStudent);
        RadioButton fac = dialog.findViewById(R.id.radioFaculty);
        StudentDB dbst = new StudentDB(context);
        FacultyDB dbfa = new FacultyDB(context);

        ArrayAdapter<String> faculty_data = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<String> student_data = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<String> joint_data = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line);

        dbst.getAllStudents(new StudentDB.StudentListCallback() {
            @Override
            public void onStudentListReceived(List<StudentDB.Student> studentList) {
                for (StudentDB.Student stud : studentList) {
                    String name = stud.getName();
                    Integer id = stud.getId();
                    String result = name + " - " + id;
                    student_data.add(result);
                    joint_data.add(result);
                }
            }

            @Override
            public void onStudentListError(Exception e) {
                Toast.makeText(context, "Error fetching students", Toast.LENGTH_SHORT).show();
            }
        });

        dbfa.getAllFaculties(new FacultyDB.FacultyListCallback() {
            @Override
            public void onFacultyListReceived(List<FacultyDB.Faculty> facultyList) {
                for (FacultyDB.Faculty fac : facultyList) {
                    String name = fac.getName();
                    Integer id = fac.getId();
                    String result = name + " - " + id;
                    faculty_data.add(result);
                    joint_data.add(result);
                }
            }

            @Override
            public void onFacultyListError(Exception e) {
                Toast.makeText(context, "Error fetching faculty", Toast.LENGTH_SHORT).show();
            }
        });

        auto.setAdapter(joint_data);
        rad.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioStudent) {
                auto.setAdapter(student_data);
            } else if (checkedId == R.id.radioFaculty) {
                auto.setAdapter(faculty_data);
            }
        });

        submit.setOnClickListener(v -> {
            String visitorName = name.getText().toString().trim();
            String numVisitorsStr = numVisitors.getText().toString().trim();
            String visitReason = reason.getText().toString().trim();
            int selectedVisitorTypeId = rad.getCheckedRadioButtonId();
            String selectedPerson = auto.getText().toString().trim();

            if (visitorName.isEmpty()) {
                Toast.makeText(context, "Please enter visitor's name", Toast.LENGTH_SHORT).show();
                return;
            }
            if (numVisitorsStr.isEmpty()) {
                Toast.makeText(context, "Please enter number of visitors", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                Integer.parseInt(numVisitorsStr); // Validate number
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid number of visitors", Toast.LENGTH_SHORT).show();
                return;
            }
            if (visitReason.isEmpty()) {
                Toast.makeText(context, "Please enter reason for visit", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedPerson.isEmpty()) {
                Toast.makeText(context, "Please select a faculty or student", Toast.LENGTH_SHORT).show();
                return;
            }

            String selectedName = "";
            String selectedId;
            if (selectedPerson.contains(" - ")) {
                String[] parts = selectedPerson.split(" - ");
                selectedName = parts[0];
                selectedId = parts.length > 1 ? parts[1] : "";
            } else {
                selectedId = "";
                Toast.makeText(context, "Invalid person selection", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int personId = Integer.parseInt(selectedId);
                NewVisitorDB newVisitorDb = new NewVisitorDB();
                newVisitorDb.insertData(visitorName, numVisitorsStr, selectedDate, selectedTime, selectedName, personId, visitReason,
                        task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Visitor entry recorded!", Toast.LENGTH_SHORT).show();
                                name.setText("");
                                numVisitors.setText("");
                                reason.setText("");
                                rad.clearCheck();
                                auto.setText("");
                                dialog.dismiss();
                            } else {
                                Toast.makeText(context, "Error saving visitor: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (NumberFormatException e) {
                Toast.makeText(context, "Invalid person ID", Toast.LENGTH_SHORT).show();
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());
    }
}