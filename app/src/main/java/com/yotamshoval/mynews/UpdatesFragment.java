package com.yotamshoval.mynews;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class UpdatesFragment extends Fragment {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference dataRef = database.getReference("news");
    private RecyclerView recyclerView;
    private UpdateAdapter updateAdapter;
    private List<UpdateItem> items= new ArrayList<>();
    private TextView addItem;
    private ProgressBar progressBar;
    private TextView messageTV;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the xml file
        View rootView = inflater.inflate(R.layout.fragment_updates, container,false);
        //initiate fragment
        setIdsAndListeners(rootView);
        readDatabase();
        setRecyclerView();
        return rootView;
    }

    private void setIdsAndListeners (View rootView){
        //set id's
        recyclerView = rootView.findViewById(R.id.update_recycler);
        addItem = rootView.findViewById(R.id.addItem);
        messageTV = rootView.findViewById(R.id.messageTV);
        progressBar = rootView.findViewById(R.id.progressBar);

        //set listeners
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.new_item_dialog, null);

                final EditText itemName = dialogView.findViewById(R.id.itemEt);
                final EditText itemDescription = dialogView.findViewById(R.id.descriptionET);
                final EditText itemTime = dialogView.findViewById(R.id.timeET);
                final ImageButton iconSport = dialogView.findViewById(R.id.iconSports);
                final ImageButton iconCars = dialogView.findViewById(R.id.iconCars);
                final ImageButton iconHealth = dialogView.findViewById(R.id.iconHealth);
                final ImageButton iconMusic = dialogView.findViewById(R.id.iconMusic);
                final TextView categoryTV = dialogView.findViewById(R.id.categoryTV);

                itemTime.setText(getTime());

                iconCars.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.LTGRAY);
                        iconHealth.setBackgroundColor(Color.WHITE);
                        iconMusic.setBackgroundColor(Color.WHITE);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Cars");
                    }
                });
                iconSport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.WHITE);
                        iconMusic.setBackgroundColor(Color.WHITE);
                        iconSport.setBackgroundColor(Color.LTGRAY);
                        categoryTV.setText("Sports");

                    }});
                iconHealth.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.LTGRAY);
                        iconMusic.setBackgroundColor(Color.WHITE);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Health");

                    }});
                iconMusic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        iconCars.setBackgroundColor(Color.WHITE);
                        iconHealth.setBackgroundColor(Color.WHITE);
                        iconMusic.setBackgroundColor(Color.LTGRAY);
                        iconSport.setBackgroundColor(Color.WHITE);
                        categoryTV.setText("Music");

                    }});

                builder.setView(dialogView).setPositiveButton("Add Item", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = itemName.getText().toString();
                        String description = itemDescription.getText().toString();
                        String time = itemTime.getText().toString();
                        String category = categoryTV.getText().toString();

                        if (!name.equals("")) {
                            items.add(new UpdateItem(name, description, category, time));
                            updateAdapter.notifyItemInserted(items.size() - 1);

                            //Add item to firebase
                            dataRef.child("updates").setValue(items);
                        }
                    }
                }).show();

            }
        });

    }

    private void readDatabase() {
        //Read the database from firebase
        progressBar.setVisibility(View.VISIBLE);
        messageTV.setText("Loading...");
        messageTV.setVisibility(View.VISIBLE);

        final CountDownTimer timer = new CountDownTimer(60000,1000){
            public void onTick(long millisUntilFinished) {
                //someTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                progressBar.setVisibility(View.GONE);
                messageTV.setText("Connection issue.\nPlease check your connection\nand try again in a few minutes.");
            }
        }.start();

        dataRef.child("updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                items.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UpdateItem item = snapshot.getValue(UpdateItem.class);
                        items.add(item);
                    }
                    updateAdapter.notifyDataSetChanged();
                }
                timer.cancel();
                progressBar.setVisibility(View.GONE);
                messageTV.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setRecyclerView() {

        updateAdapter = new UpdateAdapter(getContext(),items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(updateAdapter);

        updateAdapter.setListener(new UpdateAdapter.UpdateListener() {

            @Override
            public void onItemClicked(final int position, View view) {

                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View dialogView = getLayoutInflater().inflate(R.layout.item_dialog, null);

                final TextView title = dialogView.findViewById(R.id.title);
                final TextView description = dialogView.findViewById(R.id.description);
                final TextView time = dialogView.findViewById(R.id.timeTV);
                final ImageView categoryImage = dialogView.findViewById(R.id.imgCategory);

                title.setText(items.get(position).getItemName());
                description.setText(items.get(position).getItemDescription());
                time.setText(items.get(position).getTimeOfPublish());

                String catImg = items.get(position).getItemCategory();
                if(!catImg.equals(""))
                    switch (catImg){
                        case "Cars":
                            categoryImage.setImageResource(R.drawable.ic_directions_car_black_24dp);
                            break;
                        case "Sports":
                            categoryImage.setImageResource(R.drawable.ic_directions_run_black_24dp);
                            break;
                        case "Music":
                            categoryImage.setImageResource(R.drawable.ic_music_note_black_24dp);
                            break;
                        case "Health":
                            categoryImage.setImageResource(R.drawable.ic_healing_black_24dp);
                            break;
                        default:
                            categoryImage.setImageResource(R.drawable.ic_news_black_24dp);
                            break;
                    }
                else
                    categoryImage.setImageResource(R.drawable.ic_news_black_24dp);


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

    private String getTime(){
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(today);
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
