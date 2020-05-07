package com.example.hamza.social_app;

import android.app.AlertDialog;
import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import  com.example.hamza.social_app.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>{
    private Context context;
    private List<BlogPost> data;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public RecyclerViewAdapter(Context context, List<BlogPost> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.single_blog_post,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        final String uid = data.get(position).getUid();
        final String blogPostId = data.get(position).BlogPostId;
        String desc_data = data.get(position).getDescription();
        holder.setDescText(desc_data);

        final String image_Url = data.get(position).getPath();
        String thumb = data.get(position).getImage();
        holder.setBlogImage(image_Url,thumb);

        holder.like_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("posts/"+blogPostId+"/likes").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()){
                            Map<String , Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firestore.collection("posts/"+blogPostId+"/likes").document(uid).set(likesMap);
                            holder.like_btn.setImageDrawable(context.getDrawable(R.drawable.like_click));
                        }
                        else {
                            firestore.collection("posts/"+blogPostId+"/likes").document(uid).delete();
                            holder.like_btn.setImageDrawable(context.getDrawable(R.drawable.like_unclick));
                        }
                    }
                });
            }
        });
        firestore.collection("profile_info").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String username = task.getResult().getString("name");
                String userImage = task.getResult().getString("image");
                holder.userImageUrl = userImage;
                holder.userName = username;
                holder.setUserData(username,userImage);
            }
        });

        firestore.collection("posts/"+blogPostId+"/likes").document(uid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()){
                    holder.like_btn.setImageDrawable(context.getDrawable(R.drawable.like_click));
                }
                else{
                    holder.like_btn.setImageDrawable(context.getDrawable(R.drawable.like_unclick));
                }
            }
        });

        firestore.collection("posts/"+blogPostId+"/likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){

                    int count = queryDocumentSnapshots.size();

                    holder.updateLikesCount(count);

                } else {

                    holder.updateLikesCount(0);

                }
            }
        });

        try {
            long millisecond = data.get(position).getTime().getTime();
            String dateString = DateFormat.format("MMM dd ", new Date(millisecond)).toString();
            holder.setTime(dateString);

        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_LONG).show();

        }
        holder.user_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(context);
                View view = layoutInflater.inflate(R.layout.image_dialog,null);
                ImageView imageView = view.findViewById(R.id.user_img_click);
                TextView textView = view.findViewById(R.id.dialog_username);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setView(view);
                textView.setText(holder.userName);
                Glide.with(context).load(holder.userImageUrl).into(imageView);
                final AlertDialog dialog = builder.create();
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public  class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TextView tv_username , tv_timestamp , tv_desc , tv_likes_count;
        CircleImageView user_img = itemView.findViewById(R.id.user_image);
        ImageView imageView , like_btn ;
        String userImageUrl,userName;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            like_btn = itemView.findViewById(R.id.like_btn);
            tv_username = mView.findViewById(R.id.user_name);
            imageView = mView.findViewById(R.id.post_image);
        }
        public void setTv_username(String tv_username){
            this.tv_username.setText(tv_username);
        }

        public void setDescText(String descText){
            this.tv_desc = mView.findViewById(R.id.single_post_desc);
            this.tv_desc.setText(descText);

        }

        public void setBlogImage(String downloadUri, String thumbUri){

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.add_post_img);

            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail(
                    Glide.with(context).load(thumbUri)
            ).into(imageView);

        }
        public void setUserData(String name, String image){

            this.tv_username = mView.findViewById(R.id.user_name);

            setTv_username(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.circle);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(user_img);

        }
        public void setTime(String date) {

            tv_timestamp = mView.findViewById(R.id.time_stamp);
            tv_timestamp.setText(date);

        }
        public void updateLikesCount(int count){
            tv_likes_count = itemView.findViewById(R.id.like_count);
            tv_likes_count.setText(count + " Likes");
        }
    }
}
