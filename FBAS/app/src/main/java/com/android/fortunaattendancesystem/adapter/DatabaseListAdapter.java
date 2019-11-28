package com.android.fortunaattendancesystem.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.fortunaattendancesystem.R;

import java.util.ArrayList;

/**
 * Created by fortuna on 14/11/19.
 */

public class DatabaseListAdapter extends BaseAdapter implements Filterable {


    private Activity activity;
    private ArrayList<DatabaseItem> employeeDetailsList;
    private ArrayList <DatabaseItem> employeeDetailsFilter;
    private DatabaseListAdapter.EmployeeDataFilter employeeDataFilter;

    public DatabaseListAdapter(Activity activity, ArrayList <DatabaseItem> employeeDetailsList) {
        this.activity = activity;
        this.employeeDetailsList = employeeDetailsList;
        this.employeeDetailsFilter = employeeDetailsList;
    }

    @Override
    public int getCount() {
        return employeeDetailsList.size();
    }

    @Override
    public DatabaseItem getItem(int i) {
        return employeeDetailsList.get(i);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row_view = convertView;
        if (row_view == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row_view = inflater.inflate(R.layout.database_view, null, true);
        }

        DatabaseItem employeeDetails = employeeDetailsList.get(position);

        if(employeeDetails!=null){

            TextView emp_Id = (TextView) row_view.findViewById(R.id.tvEmpId);
            TextView card_Id = (TextView) row_view.findViewById(R.id.tvCardId);
            TextView emp_Name = (TextView) row_view.findViewById(R.id.tvEmpName);

            if (emp_Id != null) {
                emp_Id.setText(employeeDetails.getId());
                try {
                    int color = Color.TRANSPARENT;
                    View father = (View) emp_Id.getParent();
                    if (employeeDetails.isSelected()) {
                        color = Color.CYAN;
                    }
                    father.setBackgroundColor(color);
                } catch (Exception e) {
                }
            }
            if (card_Id != null) {
                card_Id.setText(employeeDetails.getFirstName().replaceAll("\\G0", " ").trim());
            }
            if (emp_Name != null) {
                emp_Name.setText(employeeDetails.getLastName());
            }
        }

        return row_view;
    }

    @Override
    public Filter getFilter() {
        if (employeeDataFilter == null) {
            employeeDataFilter = new DatabaseListAdapter.EmployeeDataFilter();
        }
        return employeeDataFilter;
    }

    private class EmployeeDataFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults filterResults = new FilterResults();
            if (charSequence != null && charSequence.toString().length() > 0) {
                ArrayList <DatabaseItem> employeeDetails_result = new ArrayList <DatabaseItem>();
                for (int i = 0, l = employeeDetailsFilter.size(); i < l; i++) {
                    DatabaseItem employeeDetails_obj = employeeDetailsFilter.get(i);
                    if (employeeDetails_obj.getId().toUpperCase().contains(charSequence.toString().toUpperCase()) ||
                            employeeDetails_obj.getFirstName().toUpperCase().contains(charSequence.toString().toUpperCase()) ||
                            employeeDetails_obj.getLastName().toUpperCase().contains(charSequence.toString().toUpperCase())) {
                        employeeDetails_result.add(employeeDetails_obj);
                    }
                }
                filterResults.count = employeeDetails_result.size();
                filterResults.values = employeeDetails_result;
            } else {
                filterResults.count = employeeDetailsFilter.size();
                filterResults.values = employeeDetailsFilter;
            }
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            employeeDetailsList = (ArrayList <DatabaseItem>) results.values;
            notifyDataSetChanged();
        }
    }
}
