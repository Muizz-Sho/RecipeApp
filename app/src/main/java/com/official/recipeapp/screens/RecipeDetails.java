package com.official.recipeapp.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.official.recipeapp.R;
import com.official.recipeapp.model.Recipe;


public class RecipeDetails extends AppCompatActivity {


    //declaration
    ImageView detailImage;
    TextView detailName,detailIngredient,detailSteps;
    TextView detailType;
    Toolbar toolbarTitle;
    ImageButton optionDelete,optionEdit;

    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RecipeApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //initialization
        detailImage = findViewById(R.id.recipeImageDetail);
        detailName = findViewById(R.id.detailRecipeName);
        detailIngredient = findViewById(R.id.detailRecipeIngredient);
        detailSteps = findViewById(R.id.detailRecipeSteps);
        detailType = findViewById(R.id.detailType);
        optionDelete = findViewById(R.id.optionDelete);
        optionEdit = findViewById(R.id.optionEdit);

        //get data from previous page
        Intent intent = getIntent();
        final Bundle data = intent.getExtras();
        final String dataID = data.getString("recipeID");

        optionEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(RecipeDetails.this,EditRecipe.class);
                edit.putExtra("recipeID",dataID);
                startActivity(edit);
            }
        });

        optionDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String dataReceived = (String) data.get("recipeID");
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            Recipe recipe = snapshot.getValue(Recipe.class);
                            assert dataReceived != null;

                            if (dataReceived.equals(recipe.getRecipeID())){
                                reference.child(dataReceived).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        ProgressDialog progressDialog = new ProgressDialog(RecipeDetails.this);
                                        progressDialog.setMessage("Deleting Recipe");
                                        progressDialog.show();
                                        if (task.isSuccessful()){
                                            Intent redirect = new Intent(RecipeDetails.this,MainActivity.class);
                                            startActivity(redirect);
                                            progressDialog.dismiss();
                                        }
                                    }
                                });
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


        if (data != null){
            final String dataReceived = (String) data.get("recipeID");

            //current data placeholder
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Recipe recipe = snapshot.getValue(Recipe.class);
                        assert dataReceived != null;

                        if (dataReceived.equals(recipe.getRecipeID())){
                            detailName.setText(recipe.getRecipeName());
                            detailIngredient.setText(recipe.getRecipeIngredient());
                            detailSteps.setText(recipe.getRecipeSteps());
                            detailType.setText(recipe.getRecipeType());
                            if (recipe.getRecipeImageURL().equals("default")){
                                detailImage.setImageResource(R.drawable.ic_launcher_background);
                            } else {
                                Glide.with(getApplicationContext()).load(recipe.getRecipeImageURL()).into(detailImage);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(RecipeDetails.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RecipeDetails.this, MainActivity.class));
        }

    }
}
