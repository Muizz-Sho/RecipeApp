package com.official.recipeapp.screens;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Logger;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.official.recipeapp.R;
import com.official.recipeapp.model.Recipe;

import java.util.HashMap;

public class EditRecipe extends AppCompatActivity {

    //declaration
    ImageView updateImage;
    EditText updateName,updateIngredient,updateSteps;
    Spinner updateType;
    FloatingActionButton updateButton;
    String updateImageUrl;
    String recipeID;

    private static final int IMAGE_REQUEST = 1;
    StorageReference storageReference;
    private Uri imageUri;
    private StorageTask uploadTask;

    final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RecipeApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        //initialization
        updateImage = findViewById(R.id.recipeImageUpdate);
        updateName = findViewById(R.id.updateRecipeName);
        updateIngredient = findViewById(R.id.updateRecipeIngredient);
        updateSteps = findViewById(R.id.updateRecipeSteps);
        updateType = findViewById(R.id.updateRecipeType);
        updateButton = findViewById(R.id.updateRecipeButton);

        //recipe type spinner data
        ArrayAdapter<CharSequence> spinnerType = ArrayAdapter.createFromResource(this,R.array.recipe_type,R.layout.support_simple_spinner_dropdown_item);
        spinnerType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        updateType.setAdapter(spinnerType);

        storageReference = FirebaseStorage.getInstance().getReference("RecipeImage");

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        //get data from previous page
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        recipeID = data.getString("recipeID");
        final String dataReceived = (String) data.get("recipeID");

        if (data != null){

            //current data placeholder
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        Recipe recipe = snapshot.getValue(Recipe.class);
                        assert dataReceived != null;

                        if (dataReceived.equals(recipe.getRecipeID())){

                            updateName.setText(recipe.getRecipeName());
                            updateIngredient.setText(recipe.getRecipeIngredient());
                            updateSteps.setText(recipe.getRecipeSteps());
                            if (recipe.getRecipeImageURL().equals("default")){
                                updateImage.setImageResource(R.drawable.ic_launcher_background);
                            } else {
                                Glide.with(getApplicationContext()).load(recipe.getRecipeImageURL()).into(updateImage);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } else {
            Toast.makeText(EditRecipe.this,"Something went wrong",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditRecipe.this, MainActivity.class));
        }

        //button update action
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(EditRecipe.this);

                String recipeNameData = updateName.getText().toString();
                String recipeIngredientData = updateIngredient.getText().toString();
                String recipeStepsData = updateSteps.getText().toString();
                String recipeTypeData = updateType.getSelectedItem().toString();

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("recipeName",recipeNameData);
                hashMap.put("recipeIngredient",recipeIngredientData);
                hashMap.put("recipeSteps",recipeStepsData);
                hashMap.put("recipeType",recipeTypeData);

                if (TextUtils.isEmpty(recipeNameData)){
                    Toast.makeText(EditRecipe.this,"Please provide Recipe name",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(recipeIngredientData)){
                    Toast.makeText(EditRecipe.this,"Please provide Ingredient",Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(recipeStepsData)){
                    Toast.makeText(EditRecipe.this,"Please provide Steps",Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.setMessage("Updating Recipes...");
                    progressDialog.show();
                    progressDialog.setCanceledOnTouchOutside(false);

                    reference.child(dataReceived).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Intent redirect = new Intent(EditRecipe.this,MainActivity.class);
                                startActivity(redirect);
                            }
                        }
                    });
                }
            }
        });
    }

    //image upload functions
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = EditRecipe.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private String uploadImage(){
        final ProgressDialog pd = new ProgressDialog(EditRecipe.this);
        pd.setMessage("Uploading..");
        pd.show();
        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUrl) {
                            updateImageUrl = downloadUrl.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("recipeImageURL",updateImageUrl);
                            DatabaseReference refPhoto = FirebaseDatabase.getInstance().getReference("RecipeApp").child(recipeID);
                            refPhoto.updateChildren(map);
                            pd.setMessage("Upload Successful!");
                            pd.dismiss();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(EditRecipe.this,"No Image selected!",Toast.LENGTH_LONG).show();
        }
        return updateImageUrl;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() !=null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(EditRecipe.this,"Upload in progress...",Toast.LENGTH_LONG).show();
            }else {
                uploadImage();
            }
        }
    }


}
