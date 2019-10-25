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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

import java.util.HashMap;
import java.util.Map;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton addImage;
    private EditText addTitle;
    private EditText addDesc;
    private Button submitpost;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mPostReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private ProgressDialog mProgress;
    private static final int GALLERY_CODE = 1;
    private Uri mImageUri; //unique resource locator

    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        addImage = (ImageButton) findViewById(R.id.addImageID);
        addDesc = (EditText) findViewById(R.id.addDescID);
        addTitle = (EditText) findViewById(R.id.addTitleID);
        submitpost = (Button) findViewById(R.id.submitPost);

        mDatabase = FirebaseDatabase.getInstance();
        mPostReference = mDatabase.getReference().child("Blog");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mProgress = new ProgressDialog(this);

        storageReference = FirebaseStorage.getInstance().getReference();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT); //Allow the user to select a particular kind of data and return it.
                galleryIntent.setType("image/*"); // select all type of images
                startActivityForResult( galleryIntent , GALLERY_CODE );
            }
        });

        submitpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //posting to database
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ( requestCode == GALLERY_CODE && resultCode == RESULT_OK) //public static final int RESULT_OK = -1 //Standard activity result: operation succeeded.
        {
            mImageUri = data.getData(); //contains image data from gallery
            //addImage.setImageURI( mImageUri );

            // crop post image
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    //.setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();

                addImage.setImageURI(resultUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void startPosting() {

        final String titleVal = addTitle.getText().toString().trim();
        final String descVal = addDesc.getText().toString().trim();

        /*if ( !TextUtils.isEmpty( titleVal ) && !TextUtils.isEmpty( descVal )){
            //start uploading to database
            Blog blog = new Blog( "image" , "title" , "descripton" , "timestamp", "userID");

            mDatabaseReference.setValue(blog).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(AddPostActivity.this, "Item Added", Toast.LENGTH_LONG).show();
                    mProgress.dismiss();
                }
            });
        }*/

        if ( !TextUtils.isEmpty( titleVal ) && !TextUtils.isEmpty( descVal ) && mImageUri != null) {
            //start uploading to database

            mProgress.setMessage("Posting to Blog....");
            mProgress.show();

            final StorageReference filepath = storageReference.child("Blog_images") //creates "Blog_images" folder in storage to store images
                    .child(mImageUri.getLastPathSegment()); //mImageUri.getLastPathSegment() == /image/apple.jpeg URL or path of the image that is added

                filepath.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //Uri downloadUrl = taskSnapshot.getDownloadUrl(); //getDownloadUrl() replaced with taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                                Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                                while ( !downloadUrl.isSuccessful());

                                Uri downloadUri = downloadUrl.getResult();

                                Log.d( "AddPostActivity : url " , downloadUrl.toString());

                                DatabaseReference newPost = mPostReference.push(); //creates new instance id for every post
                                /*old way to upload to database
                                    newPost.setValue(blog);
                                    newPost.child("postImage").setValue(downloadUrl.toString());
                                    newPost.child("postTitle").setValue(titleVal);
                                    newPost.child("postDescription").setValue(descVal);
                                    newPost.child("userID").setValue(mUser.getUid());
                                    newPost.child("timestamp").setValue(String.valueOf(java.lang.System.currentTimeMillis()));*/

                                HashMap<String,String> dataToSave = new HashMap<>();

                                dataToSave.put("postImage" , downloadUri.toString());
                                dataToSave.put("postTitle" , titleVal);
                                dataToSave.put("postDescription" , descVal);
                                dataToSave.put("userID" , mUser.getUid());
                                dataToSave.put("userEmail" , mUser.getEmail());
                                dataToSave.put("userName" , mUser.getUid());
                                dataToSave.put("timestamp" , String.valueOf(java.lang.System.currentTimeMillis()));

                                newPost.setValue(dataToSave);
                                mProgress.dismiss();

                                Toast.makeText( AddPostActivity.this , "post added" , Toast.LENGTH_LONG).show();
                                startActivity(new Intent( AddPostActivity.this , PostListActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText( AddPostActivity.this , "Problem adding post", Toast.LENGTH_LONG).show();
                            }
                        });
            }
            else {
                Toast.makeText( AddPostActivity.this , "Enter all fields", Toast.LENGTH_LONG).show();
            }
    }
}
