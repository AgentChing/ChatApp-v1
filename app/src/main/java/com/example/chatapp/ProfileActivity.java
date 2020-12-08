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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE =101;
    CircleImageView profileImageView;
    EditText inputUsername,inputCity,inputCountry,inputProfession;
    Button btnUpdate;

    Uri imageUri;
    StorageReference storageRef;
    ProgressDialog mLoadingBar;
    Toolbar toolbar;

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.circleImageView);
        inputUsername = findViewById(R.id.inputUsername);
        inputCity = findViewById(R.id.inputCity);
        inputCountry = findViewById(R.id.inputCountry);
        inputProfession = findViewById(R.id.inputProfession);
        btnUpdate = findViewById(R.id.buttonUpdate);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");

        storageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        mLoadingBar = new ProgressDialog(this);


        mUserRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String profileImageUrl = snapshot.child("profileImage").getValue().toString();
                    String city = snapshot.child("city").getValue().toString();
                    String country = snapshot.child("country").getValue().toString();
                    String profession = snapshot.child("profession").getValue().toString();
                    String username = snapshot.child("username").getValue().toString();

                    Picasso.get().load(profileImageUrl).into(profileImageView);
                    inputCity.setText(city);
                    inputUsername.setText(username);
                    inputCountry.setText(country);
                    inputProfession.setText(profession);

                }
                else{
                    Toast.makeText(ProfileActivity.this, "Data does not exist....", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ProfileActivity.this, ""+error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
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

                                mUserRef.child(mUser.getUid()).updateChildren(hashmap).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        mLoadingBar.dismiss();
                                        Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mLoadingBar.dismiss();
                                        Toast.makeText(ProfileActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
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