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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.Query;

import com.dynamicoders.myapplication.model.User;

public class LoginActivity extends AppCompatActivity {


    FirebaseDatabase database;
    DatabaseReference users;

    EditText edtUsername, edtPassword;
    Button buttonToLogIn, buttonToRegister;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        

        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        edtUsername = (EditText) findViewById(R.id.editText5);
        edtPassword = (EditText) findViewById(R.id.editText3);

        buttonToRegister = (Button) findViewById(R.id.buttonToRegister);
        buttonToLogIn = (Button) findViewById(R.id.buttonToLogIn);

        buttonToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent s = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(s);

            }
        });

        buttonToLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(edtUsername.getText().toString(),
                        edtPassword.getText().toString());
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();

            }
        };


    }



    private void signIn(final String username, final String password) {
        firebaseAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("lol", "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent s = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(s);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("lol", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        /*users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(username).exists()) {
                    if (!username.isEmpty()) {
                        User login = dataSnapshot.child(username).getValue(User.class);
                        if (login.getPassword().equals(password)) {
                            Toast.makeText(LoginActivity.this, "Successful Login", Toast.LENGTH_SHORT).show();
                            Intent s = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(s);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Password is Incorrect!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    else {
                        Toast.makeText(LoginActivity.this, "User is not registered", Toast.LENGTH_SHORT).show();
                    }
                }

                else {
                    Toast.makeText(LoginActivity.this, "User is not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }); */
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListner);
    }

}
