package com.dynamicoders.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dynamicoders.myapplication.model.User;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth mAuth;

    FirebaseDatabase database;
    DatabaseReference users;

    EditText edtUsername, edtPassword, edtEmail;
    Button buttonToSignUp, buttonToSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Firebase
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        edtEmail = (EditText) findViewById(R.id.edtEmail);

        buttonToSignUp = (Button) findViewById(R.id.buttonToSignUp);
        buttonToSignIn = (Button) findViewById(R.id.buttonToSignIn);

        buttonToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(s);
            }
        });

        buttonToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();

                // [START create_user_with_email]
                mAuth.createUserWithEmailAndPassword(edtEmail.getText().toString() , edtPassword.getText().toString())
                        .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d("lol", "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent s = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(s);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("lol", "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(SignInActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
// [END create_user_with_email]
           /*     final User user = new User(edtUsername.getText().toString(),
                        edtPassword.getText().toString(),
                        edtEmail.getText().toString());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(user.getUsername()).exists()) {
                            Toast.makeText(SignInActivity.this, "The Username already exists!", Toast.LENGTH_SHORT).show();
                        } else {
                            users.child(user.getUsername()).setValue(user);
                            Toast.makeText(SignInActivity.this, "Successful Registration!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }); */
            }
        });


    }
}
