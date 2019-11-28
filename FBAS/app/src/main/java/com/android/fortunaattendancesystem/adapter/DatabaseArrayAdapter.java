// The present software is not subject to the US Export Administration Regulations (no exportation license required), May 2012
package com.android.fortunaattendancesystem.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.fortunaattendancesystem.R;

import java.util.ArrayList;

public class DatabaseArrayAdapter extends ArrayAdapter <DatabaseItem> {
    private Context c;
    private int id;
    private ArrayList <DatabaseItem> items;

    public DatabaseArrayAdapter(Context context, int textViewResourceId, ArrayList <DatabaseItem> objects) {
        super(context, textViewResourceId, objects);
        c = context;
        id = textViewResourceId;
        items = objects;
    }

    public DatabaseItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }
        final DatabaseItem o = items.get(position);
        if (o != null) {
            TextView t1 = (TextView) v.findViewById(R.id.tvEmpId);
            TextView t2 = (TextView) v.findViewById(R.id.tvCardId);
            TextView t3 = (TextView) v.findViewById(R.id.tvEmpName);
            if (t1 != null) {
                t1.setText(o.getId());
                try {
                    int color = Color.TRANSPARENT;
                    View father = (View) t1.getParent();
                    if (o.isSelected()) {
                        color = Color.CYAN;
                    }
                    father.setBackgroundColor(color);
                } catch (Exception e) {
                }
            }
            if (t2 != null) {
                t2.setText(o.getFirstName());
            }
            if (t3 != null) {
                t3.setText(o.getLastName());
            }
        }
        return v;
    }
}
