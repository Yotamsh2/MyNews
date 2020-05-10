package com.yotamshoval.mynews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder> {
    private List<UpdateItem> items;
    private Context context;

    public UpdateAdapter(Context context, List<UpdateItem> items) {
        this.context = context;
        this.items = items;
    }

    interface UpdateListener {
        void onItemClicked(int position, View view);
        void onItemLongClicked(int position, View view);
    }

    private UpdateListener listener;
    public void setListener(UpdateListener listener){
        this.listener = listener;
    }

    public class UpdateViewHolder extends RecyclerView.ViewHolder{
        TextView itemName, itemDescription, timeOfPublish;
        ImageView categoryImage;

        public UpdateViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemName);
            itemDescription = itemView.findViewById(R.id.itemDescription);
            timeOfPublish = itemView.findViewById(R.id.timeOfPublish);
            categoryImage = itemView.findViewById(R.id.img);

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
    public UpdateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_update,parent,false);

        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UpdateViewHolder holder, int position) {
        UpdateItem item = items.get(position);
        holder.itemName.setText(item.getItemName());
        holder.itemDescription.setText(item.getItemDescription());
        holder.timeOfPublish.setText(item.getTimeOfPublish());

        final String catImg = item.getItemCategory();
        if(!catImg.equals(""))
            switch (catImg){
                case "Cars":
                    holder.categoryImage.setImageResource(R.drawable.ic_directions_car_black_24dp);
                    break;
                case "Sports":
                    holder.categoryImage.setImageResource(R.drawable.ic_directions_run_black_24dp);
                    break;
                case "Music":
                    holder.categoryImage.setImageResource(R.drawable.ic_music_note_black_24dp);
                    break;
                case "Health":
                    holder.categoryImage.setImageResource(R.drawable.ic_healing_black_24dp);
                    break;
                default:
                    holder.categoryImage.setImageResource(R.drawable.ic_news_black_24dp);
                    break;
            }
        else
            holder.categoryImage.setImageResource(R.drawable.ic_news_black_24dp);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }




}
