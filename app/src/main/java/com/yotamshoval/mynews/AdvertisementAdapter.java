package com.yotamshoval.mynews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdvertisementAdapter extends RecyclerView.Adapter<AdvertisementAdapter.AdvertisementViewHolder> {
    private List<AdvertisementItem> items;
    private Context context;

    public AdvertisementAdapter(Context context, List<AdvertisementItem> items) {
        this.context = context;
        this.items = items;
    }

    interface AdvertisementListener {
        void onItemClicked(int position, View view);
        void onItemLongClicked(int position, View view);
    }

    private AdvertisementListener listener;
    public void setListener(AdvertisementListener listener){
        this.listener = listener;
    }

    public class AdvertisementViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemDescription, itemLocation;

        public AdvertisementViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            itemLocation = itemView.findViewById(R.id.itemLocation);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(getAdapterPosition(),view);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClicked(getAdapterPosition(),view);
                    return true;
                }
            });

        }
    }

    //Must implement

    @NonNull
    @Override
    public AdvertisementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_advertisement,parent,false);

        return new AdvertisementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdvertisementViewHolder holder, int position) {
        AdvertisementItem item = items.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemDescription.setText(item.getItemDescription());
        holder.itemLocation.setText(item.getItemLocation());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }




}
