package com.scowluga.android.rcacc.CadetResources.Absence;

import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by robertlu on 2016-11-03.
 */

public class Field {
    private boolean required;

    private String name;
    private String value;

    private EditText editText;
    private TextView textView;

    private boolean reportable;

    public Field(boolean required, String name, String value, boolean reportable, EditText editText) {
        this.required = required;
        this.name = name;
        this.value = value;
        this.reportable = reportable;
        this.editText = editText;
    }

    public Field(boolean required, String name, String value, boolean reportable, TextView textView) {
        this.required = required;
        this.name = name;
        this.value = value;
        this.reportable = reportable;
        this.textView = textView;
    }

    public Field(boolean required, String name, String value, boolean reportable) {
        this.required = required;
        this.name = name;
        this.value = value;
        this.reportable = reportable;
    }
    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public String getValue() {
        return value;
    }

    public boolean isReportable() {
        return reportable;
    }

    public EditText getEditText() {
        return editText;
    }

    public TextView getTextView() {
        return textView;
    }
}
