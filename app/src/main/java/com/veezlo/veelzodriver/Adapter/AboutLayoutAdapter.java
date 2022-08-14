package com.veezlo.veelzodriver.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.veezlo.veelzodriver.DataContainer.HelpContainer;
import com.veezlo.veelzodriver.R;

import java.util.List;

public class AboutLayoutAdapter extends RecyclerView.Adapter<AboutLayoutAdapter.viewHolder>{

    List<HelpContainer> list;
    Context context;
    boolean check=false;

    public AboutLayoutAdapter(List<HelpContainer> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.aboutlayoutadapter,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder holder, int position) {

        HelpContainer container=list.get(position);

        holder.title.setText(container.getTitle());
        holder.description.setText(container.getDes());
        holder.statusImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Basit","CLIK");
                if (holder.description.getVisibility() == View.VISIBLE) {
                    holder.description.setVisibility(View.GONE);
                    holder.statusImage.setImageResource(R.drawable.showmenu);
                } else {
                    holder.description.setVisibility(View.VISIBLE);
                    holder.statusImage.setImageResource(R.drawable.upmenu);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class viewHolder extends RecyclerView.ViewHolder {
        TextView title,description;
        ImageView statusImage;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title=itemView.findViewById(R.id.title);
            description=itemView.findViewById(R.id.description);
            statusImage=itemView.findViewById(R.id.statusImage);
        }
    }
}
