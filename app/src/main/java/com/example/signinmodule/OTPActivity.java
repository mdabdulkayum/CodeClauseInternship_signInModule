package com.example.signinmodule;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.signinmodule.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private ActivityOtpactivityBinding otpBinding;
    String phone, password, otp;

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        otpBinding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        View view = otpBinding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        Intent getIntent = getIntent();
        phone = getIntent.getStringExtra(SignUpActivity.valuePhone);
        password = getIntent.getStringExtra(SignUpActivity.valuePassword);
        otp = getIntent.getStringExtra(SignUpActivity.otpVar);

        otpBinding.otpNumber.setText(phone);

        startResendTimer();
        inputNext();

    }

    void inputNext() {

        otpBinding.edtTxtInput1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    otpBinding.edtTxtInput2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpBinding.edtTxtInput2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    otpBinding.edtTxtInput3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpBinding.edtTxtInput3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    otpBinding.edtTxtInput4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpBinding.edtTxtInput4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    otpBinding.edtTxtInput5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        otpBinding.edtTxtInput5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().isEmpty()) {
                    otpBinding.edtTxtInput6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void verify(View view) {
        otpBinding.pgBar.setVisibility(View.VISIBLE);
        if(!otpBinding.edtTxtInput1.getText().toString().isEmpty() && !otpBinding.edtTxtInput2.getText().toString().isEmpty()
                && !otpBinding.edtTxtInput3.getText().toString().isEmpty()
                && !otpBinding.edtTxtInput4.getText().toString().isEmpty()
                && !otpBinding.edtTxtInput5.getText().toString().isEmpty()
                && !otpBinding.edtTxtInput6.getText().toString().isEmpty()){
            String enteredOtp = otpBinding.edtTxtInput1.getText().toString()+otpBinding.edtTxtInput2.getText().toString()
                    +otpBinding.edtTxtInput3.getText().toString()+otpBinding.edtTxtInput4.getText().toString()
                    +otpBinding.edtTxtInput5.getText().toString()+otpBinding.edtTxtInput6.getText().toString();
            if(otp!=null){
                PhoneAuthCredential phoneAuth = PhoneAuthProvider.getCredential(otp, enteredOtp);
                mAuth.signInWithCredential(phoneAuth).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(OTPActivity.this, "Authenticate successful", Toast.LENGTH_SHORT).show();

                            firebaseDatabase = FirebaseDatabase.getInstance("https://signinmodule-c0d85-default-rtdb.asia-southeast1.firebasedatabase.app");
                            reference = firebaseDatabase.getReference("user_data");

                            storingUserData dataStore = new storingUserData(phone, password);
                            reference.child(phone).setValue(dataStore);

                            Toast.makeText(OTPActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                            Intent dashBoardIntent = new Intent(OTPActivity.this, MainActivity.class);
                            startActivity(dashBoardIntent);
                            finish();
                        }
                        else{
                            otpBinding.pgBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(OTPActivity.this, "Enter correct OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }
        else{
            otpBinding.pgBar.setVisibility(View.INVISIBLE);
            Toast.makeText(OTPActivity.this, "Please enter all the number", Toast.LENGTH_SHORT).show();
        }
    }


    public void reSendOtp(View view) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCodeSent(@NonNull String newOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(newOtp, forceResendingToken);
                        otp = newOtp;
                        Toast.makeText(OTPActivity.this, "Resend OTP is send", Toast.LENGTH_SHORT).show();
                    }
                });
        startResendTimer();

    }


    private void startResendTimer() {
        otpBinding.tvResendOTP.setEnabled(false);

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                otpBinding.tvResendOTP.setText("Resend OTP enabled in " + millisUntilFinished / 1000 + " seconds");
            }

            public void onFinish() {
                otpBinding.tvResendOTP.setEnabled(true);
                otpBinding.tvResendOTP.setText("Resend OTP");
            }
        }.start();
    }
}