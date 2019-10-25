package com.nayana.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nayana.example.blogapp.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private EditText CA_email;
    private EditText CA_password;
    private Button CA_createAccount;
    private ImageButton profilePic;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private ProgressDialog mProgressDialog;
    private static final int PROFILE_PIC_CODE = 1;

    private StorageReference storageReference;
    private Uri resultUri = null;
    private static final String TAG = "CreateAccountActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firstName = (EditText) findViewById(R.id.CAfirstnameID);
        lastName = (EditText) findViewById(R.id.CAlastnameID);
        CA_email = (EditText) findViewById(R.id.CAemailID);
        CA_password = (EditText) findViewById(R.id.CApasswordID);
        CA_createAccount = (Button) findViewById(R.id.CAcreateAccountID);
        profilePic = (ImageButton) findViewById(R.id.CAprofileImageButton);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("Blog_Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mProgressDialog = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference().child("Blog_users_profilePics");

        CA_createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                createNewAccount();
            }
        });

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profilePicIntent = new Intent();
                profilePicIntent.setType("image/*");
                profilePicIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(profilePicIntent, PROFILE_PIC_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == PROFILE_PIC_CODE && resultCode == RESULT_OK ){

            Uri mImageUri = data.getData();

            //crop profile pic
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                profilePic.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void createNewAccount() {

        final String firstname = firstName.getText().toString().trim();
        final String lastname = lastName.getText().toString().trim();
        String email = CA_email.getText().toString().trim();
        String password = CA_password.getText().toString().trim();

        if (!TextUtils.isEmpty(firstname) && !TextUtils.isEmpty(lastname) &&!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mProgressDialog.setMessage("Creating Account");
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword( email , password ).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {

                    if ( authResult != null ){

                        StorageReference imagePath = storageReference.child("Blog_users_profilePics").child(resultUri.getLastPathSegment());

                        imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String userID = mAuth.getCurrentUser().getUid();
                                Log.d(TAG , mAuth.getCurrentUser().getEmail()+" created new account");
                                //Log.d(TAG , userID+" created new account");

                                DatabaseReference currentUserDB = databaseReference.child(userID);
                                currentUserDB.child("firstName : ").setValue(firstname);
                                currentUserDB.child("lastName : ").setValue(lastname);
                                //currentUserDB.child("image").setValue("default");
                                currentUserDB.child("profilePic : ").setValue(resultUri.toString());

                                Log.i(TAG , "currentUserDB.toString()");
                                Log.i(TAG , currentUserDB.toString());

                                mProgressDialog.dismiss();
                                Toast.makeText( CreateAccountActivity.this , "Account Created Successfully", Toast.LENGTH_LONG).show();
                                Toast.makeText( CreateAccountActivity.this , "Welcome new Blogger", Toast.LENGTH_LONG).show();

                                //send users to PostListActivity
                                Intent intent = new Intent( CreateAccountActivity.this , PostListActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //If set, and the activity being launched is already running in the current task, then instead of launching a new instance of that activity, all of the other activities on top of it will be closed and this Intent will be delivered to the (now on top) old activity as a new Intent.
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText( CreateAccountActivity.this , "User not authorised to create account", Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText( CreateAccountActivity.this , "Problem creating Account", Toast.LENGTH_LONG).show();
                }
            });
        }else
            Toast.makeText( CreateAccountActivity.this , "Enter all fields", Toast.LENGTH_LONG).show();
    }
}
