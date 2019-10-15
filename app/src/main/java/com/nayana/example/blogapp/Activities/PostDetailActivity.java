package com.nayana.example.blogapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.nayana.example.blogapp.R;
import com.squareup.picasso.Picasso;

public class PostDetailActivity extends AppCompatActivity {

    private ImageView postImage;
    private TextView postTitle;
    private TextView postDescription;
    private TextView postTimestamp;
    private Bundle bundle;
    private static final String TAG = "PostDetailActivity : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        bundle = getIntent().getExtras();
        postTitle = (TextView) findViewById(R.id.postTitleID);
        postDescription = (TextView) findViewById(R.id.postDescriptionID);
        postTimestamp = (TextView) findViewById(R.id.postTimeStampID);
        postImage = (ImageView) findViewById(R.id.postImageID);

        postTitle.setText(bundle.getString("Title"));
        postDescription.setText(bundle.getString("Text"));
        postTimestamp.setText(bundle.getString("Date"));

        Picasso.get()
                .load(bundle.getString("Image"))
                .into(postImage);

        Log.println( 1 , TAG , postTitle.toString());

        Log.i( TAG , "PostImage : "+ postImage.toString()
                +" PostTitle : "+ postTitle.toString()
                +" PostDescription() : "+ postDescription.toString()
                +" Timestamp() : "+ postTimestamp.toString());
    }
}
