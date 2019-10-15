package com.nayana.example.blogapp.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nayana.example.blogapp.Data.BlogRecyclerViewAdapter;
import com.nayana.example.blogapp.Model.Blog;
import com.nayana.example.blogapp.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PostListActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    private RecyclerView recyclerView;
    private BlogRecyclerViewAdapter blogRecyclerViewAdapter;
    private List<Blog> postBlogList;
    private static final String TAG = "PostListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("Blog");
        mDatabaseReference.keepSynced(true);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager( new LinearLayoutManager(this));

        postBlogList = new ArrayList<>();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Blog blog = dataSnapshot.getValue(Blog.class);
                postBlogList.add(blog);

                Log.d( "PostListActivity" ,
                        "PostTitle() : "+ blog.getPostTitle()
                                +" PostDescription() : "+ blog.getPostDescription()
                                +" Timestamp() : "+ blog.getTimestamp()
                                +" UserID() : "+ blog.getUserID()
                                +" PostImage() : "+ blog.getPostImage() );

                Collections.reverse(postBlogList);

                blogRecyclerViewAdapter = new BlogRecyclerViewAdapter( PostListActivity.this , postBlogList);
                recyclerView.setAdapter(blogRecyclerViewAdapter);
                blogRecyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu , menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch ( item.getItemId()){

            case R.id.action_add :
                if ( mAuth != null && mUser != null ) {

                    startActivity( new Intent( PostListActivity.this , AddPostActivity.class));
                    finish();
                }
                break;

            case R.id.action_signout :
                if ( mAuth != null && mUser != null){

                    mAuth.signOut();
                    Toast.makeText( PostListActivity.this , "User Signed Out" , Toast.LENGTH_LONG).show();
                    Log.d( TAG , mUser.getEmail());
                    startActivity( new Intent( PostListActivity.this , MainActivity.class));
                    finish();
                }
        }

        return super.onOptionsItemSelected(item);
    }
}
