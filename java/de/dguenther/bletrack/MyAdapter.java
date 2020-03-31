package de.dguenther.bletrack;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.RecyclerItemViewHolder> {
    private ArrayList<RecyclerData> myList;
    int mLastPosition = 0;

    public MyAdapter(ArrayList<RecyclerData> myList) {
        this.myList = myList;
    }
    public RecyclerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        RecyclerItemViewHolder holder = new RecyclerItemViewHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(RecyclerItemViewHolder holder, final int position) {
        Log.d("onBindViewHoler ", myList.size() + "");
        holder.etTitleTextView.setText(myList.get(position).getTitle());
        holder.etDescriptionTextView.setText(myList.get(position).getDescription());
        mLastPosition =position;
    }
    @Override
    public int getItemCount() {
        return(null != myList?myList.size():0);
    }

    public void notifyData(ArrayList<RecyclerData> myList) {
        Log.d("notifyData ", myList.size() + "");
        this.myList = myList;
        notifyDataSetChanged();
    }

    public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView etTitleTextView;
        private final TextView etDescriptionTextView;
        private LinearLayout mainLayout;
        public RecyclerItemViewHolder(final View parent) {
            super(parent);
            etTitleTextView = (TextView) parent.findViewById(R.id.txtTitle);
            etDescriptionTextView = (TextView) parent.findViewById(R.id.txtDescription);
            mainLayout = (LinearLayout) parent.findViewById(R.id.mainLayout);
            mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(itemView.getContext(), "Position:" + Integer.toString(getPosition()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}