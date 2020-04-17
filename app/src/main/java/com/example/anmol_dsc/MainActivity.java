package com.example.anmol_dsc;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {
     SignInButton sign_in_button;
     FirebaseAuth mAuth;
     private final static int RC_SIGN_IN = 2;
     GoogleApiClient mGoogleApiClient;
     FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         sign_in_button = (SignInButton) findViewById(R.id.googleBtn);
         mAuth = FirebaseAuth.getInstance();

         sign_in_button.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 signIn();
             }
         });
         mAuthListener = new FirebaseAuth.AuthStateListener() {
             @Override
             public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
              if(firebaseAuth.getCurrentUser() !=  null){
                  startActivity(new Intent(MainActivity.this, UserNotice.class));
              }
             }
         };
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

         mGoogleApiClient = new GoogleApiClient.Builder(this)
                 .enableAutoManage(this/*FragmentActivity*/, new GoogleApiClient.OnConnectionFailedListener() {
                     @Override
                     public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                         Toast.makeText(MainActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                     }
                 })
                 .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                 .build();

        //mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
              GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
              if(result.isSuccess()){
                  //google sign in was succesful authenticate with firebase
                  GoogleSignInAccount account = result.getSignInAccount();
                  firebaseAuthWithGoogle(account);
              } else{
                  //google sign in failed
                  Toast.makeText(MainActivity.this,"Auth went wrong",Toast.LENGTH_SHORT).show();
              }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed." ,Toast.LENGTH_SHORT).show();
                            // updateUI(null);
                        }

                        // ...
                    }
                });
    }
     //@Override
    //public void onStart(){
      //  super.onStart();
        /* // added new code from here trying different level data access
    @Override
    public void onStart(){
        super.onStart();
        //check if user is signed in (non null) and update Ui accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        currentUser.getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult result) {
                boolean isModerator = (boolean) result.getClaims().get("moderator");
                if(isModerator){
                    // show moderator UI
                   // showModeratorUI();
                    startActivity(new Intent(MainActivity.this, AdminNotice.class));
                }
                else{
                    //show regular user UI
                    //showRegularUI();
                    startActivity(new Intent(MainActivity.this, UserNotice.class));
                }
            }
        });
    }

    }

*/

    }




