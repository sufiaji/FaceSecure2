package com.merkaba.facesecure2.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.merkaba.facesecure2.R;

import java.util.ArrayList;

public class DebugAdapter extends ArrayAdapter<DebugLog> {

    private ArrayList<DebugLog> dataSet;
    private Context mContext;

    // View lookup cache
    private static class ViewHolder {
        AppCompatTextView txtView;
    }

    public DebugAdapter(ArrayList<DebugLog> data, Context context) {
        super(context, R.layout.debuglog_row, data);
        dataSet = data;
        mContext = context;
    }

    private int lastPosition = -1;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        // Get the data item for this position
        DebugLog dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.debuglog_row, parent, false);
            viewHolder.txtView = convertView.findViewById(R.id.idlogrow);

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        lastPosition = position;

        viewHolder.txtView.setText(dataModel.getText());

        // Return the completed view to render on screen
        return convertView;

    }
}
