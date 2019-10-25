package com.nayana.example.blogapp.Data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nayana.example.blogapp.Activities.PostDetailActivity;
import com.nayana.example.blogapp.Model.Blog;
import com.nayana.example.blogapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

public class BlogRecyclerViewAdapter extends RecyclerView.Adapter<BlogRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private List<Blog> blogList;
    private static final String TAG = "BlogRecyclerViewAdapter";

    public BlogRecyclerViewAdapter(Context context, List<Blog> blogList) {
        this.context = context;
        this.blogList = blogList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_row , parent , false);

        return new ViewHolder(view , context);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Blog blog = blogList.get(position);
        String imageURL = null ;

        holder.title.setText(blog.getPostTitle());
        holder.description.setText(blog.getPostDescription());
        //holder.timeStamp.setText(blog.getTimestamp()); to get in formatted date

        DateFormat dateFormat = DateFormat.getDateInstance();
        String formattedDate = dateFormat.format( new Date( Long.valueOf(blog.getTimestamp())).getTime());

        holder.timeStamp.setText(formattedDate);

        imageURL = blog.getPostImage();
        Log.i( "BlogRecyclerViewAdapter : IMAGE URL : " , imageURL);

        //TODO : use picasso library to load image
        //Picasso.with(context).load(imageURL).into(holder.image);

        //https://square.github.io/picasso/
            /*
            * RESOURCE LOADING
                Resources, assets, files, content providers are all supported as image sources.
                Picasso.get().load(R.drawable.landing_screen).into(imageView1);
                Picasso.get().load("file:///android_asset/DvpvklR.png").into(imageView2);
                Picasso.get().load(new File(...)).into(imageView3);
            * */


        Picasso.with(context)
                .load(imageURL)
                .placeholder(R.mipmap.ic_launcher_round)
                .fit()
                .centerCrop()
                .into(holder.image); //with(context) replaced with get()
    }

    @Override
    public int getItemCount() {

        //Log.i( " blogRVA : blogList.size() " , String.valueOf(blogList.size()));
        return blogList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        public TextView title;
        public TextView description;
        public TextView timeStamp;
        public ImageView image;

        public String usedID;

        public ViewHolder(@NonNull View itemView , final Context ctx) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.postTitleList);
            description = (TextView) itemView.findViewById(R.id.postDescList);
            timeStamp = (TextView) itemView.findViewById(R.id.postTimeStampList);
            image = (ImageView) itemView.findViewById(R.id.addImageID);

            usedID = null;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //we can start next activity

                    Blog blog = blogList.get(getAdapterPosition());

                    Intent intent = new Intent(context, PostDetailActivity.class);

                    intent.putExtra("Title", blog.getPostTitle());
                    intent.putExtra("Text", blog.getPostDescription());
                    intent.putExtra("Date", timeStamp.getText());
                    intent.putExtra("Image", blog.getPostImage());

                    Log.d( TAG , "PostTitle() : "+ blog.getPostTitle()
                                    +" PostDescription() : "+ blog.getPostDescription()
                                    +" Timestamp() : "+ blog.getTimestamp()
                                    +" UserID() : "+ blog.getUserID()
                                    +" PostImage() : "+ blog.getPostImage() );

                    ctx.startActivity(intent);
                }
            });
        }
    }
}
