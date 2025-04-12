package com.example.signinmodule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.signinmodule.databinding.ActivityLoginBinding;
import com.example.signinmodule.databinding.DialogGithubEmailBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LogInActivity extends AppCompatActivity {

    private ActivityLoginBinding logInBinding;


    String ccp, phone, password, fullNumber;

    private FirebaseAuth mAuth;
    authHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logInBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = logInBinding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        authHelper = new authHelper(this);

        logInBinding.imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                authHelper.signInWithGoogle();

            }
        });  // imgGoogle Ends Here

        //last signin history checking/ every time auto login

        /*GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            navigateToMainActivity();

        }*/

        logInBinding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logInBinding.pgBar.setVisibility(View.VISIBLE);

                ccp = logInBinding.ccp.getSelectedCountryCodeWithPlus();
                phone = logInBinding.edtTxtPhoneNum.getEditText().getText().toString();
                password = logInBinding.edtTxtPassword.getEditText().getText().toString();

                if(phone.isEmpty()){
                    logInBinding.edtTxtPhoneNum.setError("needed");
                    logInBinding.pgBar.setVisibility(View.INVISIBLE);
                    return;
                }
                else{
                    logInBinding.edtTxtPhoneNum.setError(null);
                    logInBinding.edtTxtPhoneNum.setErrorEnabled(false);
                }
                if(password.isEmpty()){
                    logInBinding.edtTxtPassword.setError("needed");
                    logInBinding.pgBar.setVisibility(View.INVISIBLE);
                    return;
                }
                else{
                    logInBinding.edtTxtPassword.setError(null);
                    logInBinding.edtTxtPassword.setErrorEnabled(false);
                }
                fullNumber = ccp+phone;
                logInBinding.pgBar.setVisibility(View.VISIBLE);

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://signinmodule-c0d85-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference reference = firebaseDatabase.getReference("user_data");

                Query checkPhoneNumber = reference.orderByChild("phoneNumber").equalTo(fullNumber);

                checkPhoneNumber.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if(snapshot.exists()){
                            logInBinding.edtTxtPhoneNum.setError(null);
                            logInBinding.edtTxtPhoneNum.setErrorEnabled(false);

                            String checkPass = snapshot.child(fullNumber).child("password").getValue(String.class);
                            if(checkPass.equals(password)){
                                logInBinding.pgBar.setVisibility(View.INVISIBLE);

                                Intent dashBoardIntent = new Intent(LogInActivity.this, MainActivity.class);

                                startActivity(dashBoardIntent);
                                finish();
                            }
                            else{
                                Toast.makeText(LogInActivity.this, "Error", Toast.LENGTH_LONG).show();
                                logInBinding.edtTxtPassword.setError("wrong password");
                                logInBinding.pgBar.setVisibility(View.INVISIBLE);
                            }
                        }
                        else{
                            logInBinding.edtTxtPhoneNum.setError("User dose not exits");
                            logInBinding.pgBar.setVisibility(View.INVISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });  // btnSignIn Ends Here

        logInBinding.tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });  // btnSignUp Ends Here


                             // Facebook Authentication

        logInBinding.imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                authHelper.signInWithFacebook();

                /*Intent fIntent = new Intent(LogInActivity.this, FacebookAuthActivity.class);
                fIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(fIntent);*/
            }
        });


                             // Github Authentication

        logInBinding.imgGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                authHelper.signInWIthGithub();


            }
        }); // imgGithub Ends here

    }  // onCreate Ends Here


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==authHelper.GOOGLE_SIGN_IN_REQUEST_CODE){
            authHelper.handleGoogleSignInResult(data);
        }
        if (authHelper.getFacebookCallbackManager() != null) {
            authHelper.getFacebookCallbackManager().onActivityResult(requestCode, resultCode, data);
        }
    }

    private void navigateToMainActivity() {

        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
        finish();
    }


}