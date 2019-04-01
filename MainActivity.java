package com.avioxofficial.login_with_google;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

public class MainActivity extends AppCompatActivity implements View.OnClickListener , GoogleApiClient.OnConnectionFailedListener {


    LinearLayout prof_section;
    SignInButton signIn_BT;
    Button logout_BT;
    TextView nameTV, emailTV;
    ImageView prof_pic;
    private GoogleApiClient googleApiClient;
    public static final int REQ_CODE = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        signIn_BT = findViewById(R.id.signIn_Bt);
        logout_BT = findViewById(R.id.logout_BT);
        prof_pic = findViewById(R.id.prof_pic);
        prof_section = findViewById(R.id.prof_section);
        nameTV = findViewById(R.id.nameTV);
        emailTV = findViewById(R.id.emailTV);


        logout_BT.setOnClickListener(this);
        signIn_BT.setOnClickListener(this);

        prof_section.setVisibility(View.GONE);

        GoogleSignInOptions signInOptions=new GoogleSignInOptions.
                Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleApiClient=new GoogleApiClient.Builder(this).
                enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,signInOptions).build();

    }


    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.signIn_Bt:
                signIN();
                break;

            case R.id.logout_BT:
                logOut();
                break;

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Toast.makeText(this,"Connection error",Toast.LENGTH_LONG).show();


    }

    private void signIN()
    {

        Intent intent=Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,REQ_CODE);


    }

    private void logOut()
    {

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                updateUi(false);
            }
        });

    }

    private void handleResult(GoogleSignInResult result)
    {

        if (result.isSuccess())
        {

            Toast.makeText(this,"result",Toast.LENGTH_LONG).show();


            GoogleSignInAccount account=result.getSignInAccount();
            String name=account.getDisplayName();
            String email=account.getEmail();
            nameTV.setText(name);
            emailTV.setText(email);

            try {

                String img_url=account.getPhotoUrl().toString();

                if (img_url!=null) {

                    Glide.with(this).load(img_url).into(prof_pic);
                    updateUi(true);
                }else {


                    Glide.with(this).load(R.mipmap.ic_launcher).into(prof_pic);
                    updateUi(true);
                }
            }catch (Exception e)
            {
                Toast.makeText(this,"Error image not found in your account." +
                        "Check your account and try again!",Toast.LENGTH_LONG).show();
                Auth.GoogleSignInApi.signOut(googleApiClient);

            }


        }
        else {
            Toast.makeText(this,"error",Toast.LENGTH_LONG).show();
            updateUi(false);
        }
    }
    private void updateUi(boolean isLogin)
    {

        if (isLogin)
        {
            prof_section.setVisibility(View.VISIBLE);
            signIn_BT.setVisibility(View.GONE);
        }

        else {
            prof_section.setVisibility(View.GONE);
            signIn_BT.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==REQ_CODE)
        {
            GoogleSignInResult inResult=Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleResult(inResult);

        }

    }

}
