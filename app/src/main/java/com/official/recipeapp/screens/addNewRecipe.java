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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.official.recipeapp.R;
import com.official.recipeapp.model.Recipe;

import java.util.HashMap;

public class addNewRecipe extends AppCompatActivity {

    ImageView recipeImage;
    EditText recipeName,recipeIngredient,recipeSteps;
    FloatingActionButton checkButton;
    Spinner recipeType;
    String recipeImageUrl = "default";
    String recipeID;

    private static final int IMAGE_REQUEST = 1;
    StorageReference storageReference;
    private Uri imageUri;
    private StorageTask uploadTask;

    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("RecipeApp").push();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_recipe);

        recipeImage = findViewById(R.id.recipeImageUpload);
        recipeName = findViewById(R.id.newRecipeName);
        recipeIngredient = findViewById(R.id.newRecipeIngredient);
        recipeSteps = findViewById(R.id.newRecipeSteps);
        checkButton = findViewById(R.id.addNewRecipeButton);
        recipeType = findViewById(R.id.newRecipeType);

        ArrayAdapter<CharSequence> spinnerType = ArrayAdapter.createFromResource(this,R.array.recipe_type,R.layout.support_simple_spinner_dropdown_item);
        spinnerType.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        recipeType.setAdapter(spinnerType);

        storageReference = FirebaseStorage.getInstance().getReference("RecipeImage");

        recipeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog dialog = new ProgressDialog(addNewRecipe.this);

                recipeID = reference.getKey();
                String recipeNameData = recipeName.getText().toString();
                String recipeIngredientData = recipeIngredient.getText().toString();
                String recipeStepsData = recipeSteps.getText().toString();
                String recipeTypeData = recipeType.getSelectedItem().toString();

                Recipe recipe = new Recipe(recipeID,recipeNameData,recipeImageUrl,recipeTypeData,recipeIngredientData,recipeStepsData);

                if (TextUtils.isEmpty(recipeNameData)){
                    Toast.makeText(addNewRecipe.this,"Enter Recipe Name!",Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(recipeIngredientData)){
                    Toast.makeText(addNewRecipe.this,"Enter Ingredient!",Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(recipeStepsData)){
                    Toast.makeText(addNewRecipe.this,"Enter Steps!",Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    dialog.setMessage("Adding new recipe....");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    reference.setValue(recipe).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                dialog.isShowing();
                                dialog.dismiss();
                                Toast.makeText(addNewRecipe.this,"New Recipe Added",Toast.LENGTH_SHORT).show();
                                Intent redirect = new Intent(addNewRecipe.this,MainActivity.class);
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
        ContentResolver contentResolver = addNewRecipe.this.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private String uploadImage(){
        final ProgressDialog pd = new ProgressDialog(addNewRecipe.this);
        pd.setMessage("Uploading..");
        pd.setCanceledOnTouchOutside(false);
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
                            recipeImageUrl = downloadUrl.toString();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("recipeImageURL",downloadUrl.toString());
                            reference.child(recipeID).updateChildren(map);
                            pd.setMessage("Upload Successful!");
                            pd.dismiss();
                        }
                    });
                }
            });
        }else {
            Toast.makeText(addNewRecipe.this,"No Image selected!",Toast.LENGTH_LONG).show();
        }
        return recipeImageUrl;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() !=null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(addNewRecipe.this,"Upload in progress...",Toast.LENGTH_LONG).show();
            }else {
                uploadImage();
            }
        }
    }
}
