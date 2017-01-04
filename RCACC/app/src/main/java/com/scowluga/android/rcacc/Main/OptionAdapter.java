package com.scowluga.android.rcacc.Main;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.scowluga.android.rcacc.R;

import java.util.List;

/**
 * Created by robertlu on 2016-10-31.
 */


public class OptionAdapter extends BaseExpandableListAdapter {


    private Context context;
    private List<GroupOption> options;

    public OptionAdapter(Context context, List<GroupOption> options) {
        this.context = context;
        this.options = options;
    }

    @Override
    public int getGroupCount() {
        return options.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return options.get(groupPosition).getChildren().size();
    }

    @Override
    public GroupOption getGroup(int groupPosition) {
        return options.get(groupPosition);
    }

    @Override
    public ChildOption getChild(int groupPosition, int childPosition) {
        return options.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupOption opt = getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.aaa_group_layout, parent, false);
        }
        String groupName = (String) opt.getName();
        TextView parent_text = (TextView) convertView.findViewById(R.id.parent_text);
        parent_text.setTypeface(null, Typeface.BOLD);
        parent_text.setText(groupName);

        Integer imageId = (Integer) opt.getImageId();
        ImageView parent_image = (ImageView)convertView.findViewById(R.id.parent_image);
        parent_image.setImageResource(imageId);

        ImageView parent_drop = (ImageView)convertView.findViewById(R.id.parent_drop);
        if (opt.isClickable()) {
            parent_drop.setImageResource(R.drawable.transparent);
        } else {
            if (opt.getState() == GroupOption.UP) {
                parent_drop.setImageResource(R.drawable.drop_down);
            } else {
                parent_drop.setImageResource(R.drawable.drop_up);
            }
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.aaa_child_layout, parent, false);
        }
        String childName = (String) getChild(groupPosition, childPosition).getName();
        TextView child_textview = (TextView) convertView.findViewById(R.id.child_text);
        child_textview.setText(childName);

        Integer imageId = (Integer) getChild(groupPosition, childPosition).getImageId();
        ImageView child_image = (ImageView)convertView.findViewById(R.id.child_image);
        child_image.setImageResource(imageId);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

