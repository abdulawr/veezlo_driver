package com.veezlo.veelzodriver.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.veezlo.veelzodriver.DataContainer.Transaction_Container;
import com.veezlo.veelzodriver.R;

import java.util.List;

public class Transaction_History_Adapter extends RecyclerView.Adapter<Transaction_History_Adapter.viewHolder> {

    Context context;
    List<Transaction_Container> list;

    public Transaction_History_Adapter(Context context, List<Transaction_Container> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_history_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
      Transaction_Container data=list.get(position);
      holder.mobile.setText(data.getMobile());
      holder.TranID.setText("T_ID: "+data.getTranID());
      holder.amount.setText("PKR: "+data.getAmount());
      holder.date.setText(data.getDate());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {

        TextView mobile,date,amount,TranID;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            mobile=itemView.findViewById(R.id.mobile);
            date=itemView.findViewById(R.id.date);
            amount=itemView.findViewById(R.id.amount);
            TranID=itemView.findViewById(R.id.TranID);
        }
    }
}
