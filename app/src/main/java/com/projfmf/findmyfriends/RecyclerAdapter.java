package com.projfmf.findmyfriends;

import java.util.ArrayList;
import java.util.List;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<User> names;

    public RecyclerAdapter(ArrayList<User> students) {
        this.names = students;
    }
    // Create new views
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.cardview_row, parent, false);
        // create ViewHolder
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        final int pos = position;

        viewHolder.contactName.setText(names.get(position).getUsername());
        viewHolder.email.setText("("+names.get(position).getEmail()+")");
        viewHolder.checkBox.setChecked(names.get(position).getIsSelected());
        viewHolder.checkBox.setTag(names.get(position));

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                User contact = (User) cb.getTag();

                contact.setSelected(cb.isChecked());
                names.get(pos).setSelected(cb.isChecked());

                Toast.makeText(
                        v.getContext(),
                        "Group Size: " + cb.getText() + " is "
                                + cb.isChecked(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Return the size arraylist
    @Override
    public int getItemCount() {
        return names.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView contactName;
        public CheckBox checkBox;
        public TextView email;
        public User user;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            contactName = (TextView) itemLayoutView.findViewById(R.id.textView);
            email = (TextView) itemLayoutView.findViewById(R.id.emailView);
            checkBox = (CheckBox) itemLayoutView
                    .findViewById(R.id.checkBox);
        }
    }
    // method to access in activity after updating selection
    public List<User> getPersonList() {
        return names;
    }
}
