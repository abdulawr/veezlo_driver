package com.veezlo.veelzodriver.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.veezlo.veelzodriver.DataContainer.NotificationContainer;
import com.veezlo.veelzodriver.Driver_Activity.UploadImagesForUserRequestThroughNotification;
import com.veezlo.veelzodriver.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.viewHolder> {

   Context context;
   List<NotificationContainer> list;

    public NotificationAdapter(Context context, List<NotificationContainer> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notificationadapterview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
      NotificationContainer data=list.get(position);
      holder.date.setText("Date: "+data.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView date;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            date=itemView.findViewById(R.id.date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           Intent intent=new Intent(context, UploadImagesForUserRequestThroughNotification.class);
           intent.putExtra("object",list.get(getLayoutPosition()));
           context.startActivity(intent);
        }
    }
}
