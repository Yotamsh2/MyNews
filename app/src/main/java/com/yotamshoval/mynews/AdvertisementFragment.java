package com.yotamshoval.mynews;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdvertisementFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dataRef = database.getReference("news");
    private RecyclerView recyclerView;
    private AdvertisementAdapter adapter;
    private List<AdvertisementItem> items= new ArrayList<>();
    private TextView addItem;
    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the xml file
        View rootView = inflater.inflate(R.layout.fragment_advertisement, container,false);
        //initiate fragment
        setIdsAndListeners(rootView);
        readDatabase();
        setRecyclerView();
        return rootView;
    }

    private void setIdsAndListeners (View rootView){
        //set id's
        recyclerView = rootView.findViewById(R.id.advertisement_recycler);
        addItem = rootView.findViewById(R.id.addItem);

        //set listeners
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.new_ad_item_dialog, null);

                final EditText itemName = dialogView.findViewById(R.id.itemEt);
                final EditText itemDescription = dialogView.findViewById(R.id.descriptionET);
                final EditText itemLocation = dialogView.findViewById(R.id.locationET);

                builder.setView(dialogView).setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = itemName.getText().toString();
                        String description = itemDescription.getText().toString();
                        String location = itemLocation.getText().toString();

                        if (!name.equals("")) {
                            items.add(new AdvertisementItem(name, description, location));
                            adapter.notifyItemInserted(items.size() - 1);

                            //Add item to firebase
                            dataRef.child("ads").setValue(items);
                        }
                    }
                }).show();

            }
        });

    }

    private void readDatabase() {
        //Read the database from firebase

        dataRef.child("ads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                items.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AdvertisementItem item = snapshot.getValue(AdvertisementItem.class);
                        items.add(item);
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setRecyclerView() {

        adapter = new AdvertisementAdapter(getContext(),items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        adapter.setListener(new AdvertisementAdapter.AdvertisementListener() {

            @Override
            public void onItemClicked(final int position, View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View dialogView = getLayoutInflater().inflate(R.layout.item_dialog, null);

                final TextView title = dialogView.findViewById(R.id.title);
                final TextView description = dialogView.findViewById(R.id.description);
                final TextView time = dialogView.findViewById(R.id.timeTV);
                final ImageView categoryImage = dialogView.findViewById(R.id.imgCategory);

                title.setText(items.get(position).getItemName());
                description.setText(items.get(position).getItemDescription());
                categoryImage.setImageResource(R.drawable.ic_star_blue_24dp);
                time.setText(items.get(position).getItemLocation());


                builder.setView(dialogView).setPositiveButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();

            }

            @Override
            public void onItemLongClicked(int position, View view) {

            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
