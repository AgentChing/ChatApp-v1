package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MForgotPasswordActivity extends AppCompatActivity {
    EditText inputEmail;
    Button btnSend;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_forgot_password);
        inputEmail = findViewById(R.id.inputPasswordReset);
        btnSend = findViewById(R.id.btnReset);
        mAuth = FirebaseAuth.getInstance();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                if(email.isEmpty())
                {
                    Toast.makeText(MForgotPasswordActivity.this, "Please Enter Your Email", Toast.LENGTH_SHORT).show();
                }
                else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(MForgotPasswordActivity.this, "An email has been send...Check it...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(MForgotPasswordActivity.this, "Error sending Email", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}