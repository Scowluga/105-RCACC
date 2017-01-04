package com.scowluga.android.rcacc.CadetResources.Absence;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.scowluga.android.rcacc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robertlu on 2016-11-03.
 */

public class FieldsCreator {

    public static List<Field> getFields(View v) {
        List<Field> fields = new ArrayList<>();

        EditText first = (EditText) v.findViewById(R.id.absence_first);
        EditText last = (EditText) v.findViewById(R.id.absence_last);
        EditText email = (EditText) v.findViewById(R.id.absence_email);
        EditText reason = (EditText) v.findViewById(R.id.absence_reason);

        fields.add(new Field(true, "First Name", first.getText().toString(), !TextUtils.isEmpty(first.getText().toString()), first));
        fields.add(new Field(true, "Last Name", last.getText().toString(), !TextUtils.isEmpty(last.getText().toString()), last));
        fields.add(new Field(false, "Email Address", email.getText().toString(), true)); // not required
        fields.add(new Field(true, "Reason for Absence", reason.getText().toString(), !TextUtils.isEmpty(reason.getText().toString()), reason));

        RadioGroup group = (RadioGroup) v.findViewById(R.id.radio_group);
        int id = group.getCheckedRadioButtonId();
        RadioButton current = (RadioButton) v.findViewById(id);
        TextView textView = (TextView) v.findViewById(R.id.absence_event_text);
        fields.add(new Field(true, "Event type for Absence", (group.getCheckedRadioButtonId() == -1) ? null : current.getText().toString()
                , !(group.getCheckedRadioButtonId() == -1), textView));

        return fields;
    }

    public static void logError(Field field) {
        if (field.getEditText() != null) {
            EditText et = field.getEditText();
            et.setError(field.getName() + " is required");
        } else if (field.getTextView() != null) {
            TextView tv = field.getTextView();
            tv.requestFocus();
            tv.setError(field.getName() + " is required");
        }
    }
}
