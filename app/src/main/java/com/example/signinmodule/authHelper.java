package com.example.signinmodule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class authHelper {
    Activity activity;
    private DialogGithubEmailBinding dialogGithubBinding;
    Dialog dialogGithub;
    FirebaseAuth mAuth;
    GoogleSignInOptions gso1;
    GoogleSignInClient gsc1;
    CallbackManager callbackManager;
    String emailGit;
    public static final int GOOGLE_SIGN_IN_REQUEST_CODE = 1000;

    public authHelper(Activity activity) {
        this.activity = activity;
        this.mAuth = FirebaseAuth.getInstance();
    }



            // Google Sign In
    public void signInWithGoogle(){
        gso1 = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc1 = GoogleSignIn.getClient(activity, gso1);

        Intent signInIntent = gsc1.getSignInIntent();
        activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_REQUEST_CODE);

    }
    public void handleGoogleSignInResult(Intent data) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            navigateToMainActivity(); // successful login
        } catch (ApiException e) {
            Toast.makeText(activity, "Something went wrong!!", Toast.LENGTH_SHORT).show();
        }
    }


    public CallbackManager getFacebookCallbackManager() {
        return callbackManager;
    }

    public void signInWithFacebook(){
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App Code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

    }

    private void handleFacebookAccessToken(AccessToken token) {


        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            navigateToMainActivity();
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(activity, ""+task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




            // GitHub Sign In
    public void signInWIthGithub(){
        dialogGithub = new Dialog(activity);
        dialogGithubBinding = DialogGithubEmailBinding.inflate(activity.getLayoutInflater());
        View viewGit = dialogGithubBinding.getRoot();
        dialogGithub.setContentView(viewGit);
        dialogGithub.show();

        dialogGithubBinding.btnSubmitEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailGit = dialogGithubBinding.edtTxtEmailGit.getEditText().getText().toString();

                if(emailGit.isEmpty()){
                    dialogGithubBinding.edtTxtEmailGit.setError("Enter Valid Email");
                    return;
                }
                else{
                    dialogGithubBinding.edtTxtEmailGit.setError(null);
                    dialogGithubBinding.edtTxtEmailGit.setErrorEnabled(false);

                    // From Firebase Documentation

                    OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");
                    // Target specific email with login hint.
                    provider.addCustomParameter("login", emailGit);

                    // Request read access to a user's email addresses.
                    // This must be preconfigured in the app's API permissions.
                    List<String> scopes =
                            new ArrayList<String>() {
                                {
                                    add("user:email");
                                }
                            };
                    provider.setScopes(scopes);

                    Task<AuthResult> pendingResultTask = mAuth.getPendingAuthResult();
                    if (pendingResultTask != null) {
                        // There's something already here! Finish the sign-in for your user.
                        pendingResultTask
                                .addOnSuccessListener(
                                        new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                // User is signed in.
                                                // IdP data available in
                                                // authResult.getAdditionalUserInfo().getProfile().
                                                // The OAuth access token can also be retrieved:
                                                // ((OAuthCredential)authResult.getCredential()).getAccessToken().
                                                // The OAuth secret can be retrieved by calling:
                                                // ((OAuthCredential)authResult.getCredential()).getSecret().
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                    } else {
                        mAuth
                                .startActivityForSignInWithProvider(activity, provider.build())
                                .addOnSuccessListener(
                                        new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                navigateToMainActivity();
                                            }
                                        })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(activity, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                    }

                }

            }
        });




    }




    private void navigateToMainActivity() {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }



}
