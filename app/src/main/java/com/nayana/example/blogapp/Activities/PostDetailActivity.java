package com.nayana.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nayana.example.blogapp.Model.Blog;
import com.nayana.example.blogapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postTitle;
    private TextView postDescription;
    private TextView postTimestamp;
    private Button postEditButton;
    private Button postDeleteButton;
    private Bundle bundle;
    private static final String TAG = "PostDetailActivity : ";

    private List<Blog> blogList;

    private Context context;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog; //A subclass of Dialog that can display one, two or three buttons.
    private LayoutInflater inflater; //Instantiates a layout XML file into its corresponding View  objects.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        bundle = getIntent().getExtras();
        postTitle = (TextView) findViewById(R.id.postTitleID);
        postDescription = (TextView) findViewById(R.id.postDescriptionID);
        postTimestamp = (TextView) findViewById(R.id.postTimeStampID);
        postImage = (ImageView) findViewById(R.id.postImageID);

        postEditButton = (Button) findViewById(R.id.DetEditButton);
        postDeleteButton = (Button) findViewById(R.id.DetDeleteButton);

        postEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPost();
            }
        });

        postDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePost();
            }
        });

        postTitle.setText(bundle.getString("Title"));
        postDescription.setText(bundle.getString("Text"));
        postTimestamp.setText(bundle.getString("Date"));

//        Picasso.get()
//                .load(bundle.getString("Image"))
//                .into(postImage);

        Picasso.with(context)
                .load(bundle.getString("Image"))
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerCrop()
                .into(postImage);

        Log.i( TAG , "PostImage : "+ postImage.toString()
                +" PostTitle : "+ postTitle.toString()
                +" PostDescription() : "+ postDescription.toString()
                +" Timestamp() : "+ postTimestamp.toString());
    }

    private void editPost() {

        Toast.makeText( PostDetailActivity.this , "trying to edit" , Toast.LENGTH_LONG).show();
    }

    private void deletePost() {

        Toast.makeText( PostDetailActivity.this , "trying to delete" , Toast.LENGTH_LONG).show();
        //TODO
    }
}
