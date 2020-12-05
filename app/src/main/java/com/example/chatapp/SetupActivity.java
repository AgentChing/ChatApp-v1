package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private static final int REQUEST_CODE =101;
    CircleImageView profileImageView;
     EditText inputUsername,inputCity,inputCountry,inputProfession;
     Button btnSave;
     Uri imageUri;

     FirebaseAuth mAuth;
     FirebaseUser mUser;
     DatabaseReference mRef;
     StorageReference storageRef;

     ProgressDialog mLoadingBar;
     Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
    toolbar = findViewById(R.id.app_bar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setTitle("Who are you?");
        profileImageView = findViewById(R.id.profile_image);
        inputCity = findViewById(R.id.inputCity);
        inputUsername = findViewById(R.id.inputUsername);
        inputCountry = findViewById(R.id.inputCountry);
        inputProfession = findViewById(R.id.inputProfession);
        btnSave = findViewById(R.id.btnSave);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        storageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");

        mLoadingBar = new ProgressDialog(this);

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

    }

    private void saveData() {
        final String username = inputUsername.getText().toString();
        final String city = inputCity.getText().toString();
        final String country = inputCountry.getText().toString();
        final String profession = inputProfession.getText().toString();

        if(username.isEmpty() || username.length()<3)
        {
            showError(inputUsername,"what the fuck do you wanna be called...DP");
        }
        else if(city.isEmpty() || city.length()<3){
            showError(inputCity,"City name is to short, dammit");
        }
        else if(country.isEmpty() || country.length()<3){
            showError(inputCountry,"what the hell is wrong with your country?");
        }
        else if(profession.isEmpty() || profession.length()<3){
            showError(inputProfession,"what the heck is this??");
        }
        else if(imageUri==null){
            Toast.makeText(this, "Please show me your face!!!", Toast.LENGTH_SHORT).show();
        }
        else{
            mLoadingBar.setTitle("Updating Profile");
            mLoadingBar.setCanceledOnTouchOutside(false);
            mLoadingBar.show();
            storageRef.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        storageRef.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap hashmap = new HashMap();
                                hashmap.put("username",username);
                                hashmap.put("city",city);
                                hashmap.put("country",country);
                                hashmap.put("profession",profession);
                                hashmap.put("profileImage",uri.toString());
                                hashmap.put("status","offline");

                                mRef.child(mUser.getUid()).updateChildren(hashmap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(SetupActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        mLoadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mLoadingBar.dismiss();
                                        Toast.makeText(SetupActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }

    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null)
        {
          imageUri = data.getData();
          profileImageView.setImageURI(imageUri);
        }
    }

}