package com.example.signinmodule;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.signinmodule.databinding.ActivitySignUpBinding;
import com.example.signinmodule.databinding.DialogGithubEmailBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding signUpBinding;
    private DialogGithubEmailBinding dialogGithubBinding;

    String ccp, phone, password, confirmPassword, fullNumber, emailGit;
    Dialog dialogGithub;
    public static final String valuePhone = "Default value 0", valuePassword = "none2", otpVar = "none7";

    private FirebaseAuth mAuth;
    authHelper authHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        View view = signUpBinding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        authHelper = new authHelper(this);

                // Google Authentication
        signUpBinding.imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authHelper.signInWithGoogle();
            }
        }); // imgGoogle onClick ends here


        signUpBinding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpBinding.pgBar.setVisibility(View.VISIBLE);


                ccp = signUpBinding.ccp.getSelectedCountryCodeWithPlus();
                phone = signUpBinding.edtTxtPhoneNum.getEditText().getText().toString();
                password = signUpBinding.edtTxtPassword.getEditText().getText().toString();
                confirmPassword = signUpBinding.edtTxtConfirmPassword.getEditText().getText().toString();
                fullNumber = ccp+phone;

                if(phone.isEmpty()){
                    signUpBinding.edtTxtPhoneNum.setError("needed");
                    signUpBinding.pgBar.setVisibility(View.INVISIBLE);
                    return;
                }
                else{
                    signUpBinding.edtTxtPhoneNum.setError(null);
                    signUpBinding.edtTxtPhoneNum.setErrorEnabled(false);
                }
                if(password.isEmpty()){
                    signUpBinding.edtTxtPassword.setError("needed");
                    signUpBinding.pgBar.setVisibility(View.INVISIBLE);
                    return;
                }
                else{
                    signUpBinding.edtTxtPassword.setError(null);
                    signUpBinding.edtTxtPassword.setErrorEnabled(false);
                }
                if(confirmPassword.isEmpty() || !confirmPassword.equals(password)){
                    signUpBinding.edtTxtConfirmPassword.setError("Password not matched");
                    signUpBinding.pgBar.setVisibility(View.INVISIBLE);
                    return;
                }
                else{
                    signUpBinding.edtTxtConfirmPassword.setError(null);
                    signUpBinding.edtTxtConfirmPassword.setErrorEnabled(false);
                }

                // Check user already exist or not

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://signinmodule-c0d85-default-rtdb.asia-southeast1.firebasedatabase.app");
                DatabaseReference reference = firebaseDatabase.getReference("user_data");

                Query checkPhoneNumber = reference.orderByChild("phoneNumber").equalTo(fullNumber);

                checkPhoneNumber.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            signUpBinding.edtTxtPhoneNum.setError("User Already Exist");
                            signUpBinding.pgBar.setVisibility(View.INVISIBLE);
                            return;
                        }
                        else{
                            PhoneAuthProvider.getInstance().verifyPhoneNumber(fullNumber, 60, TimeUnit.SECONDS, SignUpActivity.this,
                                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                        @Override
                                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                            signUpBinding.pgBar.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onVerificationFailed(@NonNull FirebaseException e) {
                                            signUpBinding.pgBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                        }

                                        @Override
                                        public void onCodeSent(@NonNull String Otp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                            super.onCodeSent(Otp, forceResendingToken);

                                            signUpBinding.pgBar.setVisibility(View.INVISIBLE);

                                            Intent otpVerificationPage = new Intent(SignUpActivity.this, OTPActivity.class);
                                            otpVerificationPage.putExtra(valuePhone, fullNumber);
                                            otpVerificationPage.putExtra(valuePassword, password);
                                            otpVerificationPage.putExtra(otpVar, Otp);

                                            startActivity(otpVerificationPage);
                                            finish();
                                        }
                                    });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });





            }
        });  // btnSignUp Ends here


        signUpBinding.tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUpActivity.this, LogInActivity.class);
                startActivity(intent);
            }
        });  // btnSignUp Ends Here



        signUpBinding.imgGithub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                authHelper.signInWIthGithub();

            }
        });     // imgGithub Ends Here


        signUpBinding.imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authHelper.signInWithFacebook();

               /* Intent fIntent = new Intent(SignUpActivity.this, FacebookAuthActivity.class);
                fIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(fIntent);*/
            }
        });


    }// onCreate Ends here



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