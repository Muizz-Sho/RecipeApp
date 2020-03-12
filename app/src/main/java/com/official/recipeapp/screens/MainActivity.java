package com.official.recipeapp.screens;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.official.recipeapp.R;
import com.official.recipeapp.adapter.RecipeAdapter;
import com.official.recipeapp.model.Recipe;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    SearchView searchView;
    RecyclerView recipeList;
    ArrayList<Recipe> recipeArrayList;
    RecipeAdapter adapter;
    TextView notFound;
    ImageButton logOut;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RecipeApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recipeList = findViewById(R.id.searchList);
        notFound = findViewById(R.id.emptyList);
        logOut = findViewById(R.id.logOutButton);
        recipeList.setLayoutManager(new LinearLayoutManager(this));
        recipeList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeArrayList = new ArrayList<>();
                recipeArrayList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    recipeArrayList.add(recipe);
                }
                adapter = new RecipeAdapter(MainActivity.this,recipeArrayList);
                recipeList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                if (adapter.getItemCount() == 0){
                    recipeList.setVisibility(View.GONE);
                    notFound.setVisibility(View.VISIBLE);
                }else {
                    recipeList.setVisibility(View.VISIBLE);
                    notFound.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                Intent signOut = new Intent(MainActivity.this,LoginActivity.class);
                signOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                signOut.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signOut);
            }
        });

        searchView = findViewById(R.id.search_view);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null){
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });


        FloatingActionButton addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addNew = new Intent(MainActivity.this, addNewRecipe.class);
                startActivity(addNew);
            }
        });

    }
}


