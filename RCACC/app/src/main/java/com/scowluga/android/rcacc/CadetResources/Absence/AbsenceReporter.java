package com.scowluga.android.rcacc.CadetResources.Absence;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.scowluga.android.rcacc.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AbsenceReporter extends Fragment {

    public AbsenceReporter() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.absence_reporting_frag, container, false);

        Button report = (Button)v.findViewById(R.id.reportButton);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Field> fields = FieldsCreator.getFields(v);
                if (reportable(fields)) {
                    reportAbsence(fields);
                } else {
                    reportError(fields);
                }
            }
        });
        return v;
    }

    private void reportError(List<Field> fields) {
        Toast.makeText(getContext(), "One or more required fields are missing", Toast.LENGTH_SHORT);
        for (Field field : fields) {
            if (!field.isReportable()) {
                FieldsCreator.logError(field);
            }
        }
    }

    private void reportAbsence(List<Field> fields) {
        Toast.makeText(getContext(), "Absence Reported!", Toast.LENGTH_SHORT).show();
        // Spreadsheets!!
    }

    public static boolean reportable(List<Field> fields) {
        for (Field field: fields) {
            if (!field.isReportable()) {
                return false;
            }
        }
        return true;
    }
}
