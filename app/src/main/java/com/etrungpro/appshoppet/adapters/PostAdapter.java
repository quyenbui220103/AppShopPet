package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.Post;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
    Context context;
    ArrayList<Post> postArrayList ;

    // Constructor để khởi tạo adapter với dữ liệu và context
    public PostAdapter(Context context, ArrayList<Post> postArrayList) {
        this.context = context;
        this.postArrayList = postArrayList;
    }

    // Phương thức này được sử dụng để cập nhật dữ liệu danh sách bài viết
    public void setPostArrayList(ArrayList<Post> postArrayList) {
        this.postArrayList = postArrayList;
        notifyDataSetChanged();
    }

    // Phương thức này tạo ViewHolder cho mỗi item
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout post_item.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item,parent,false);
        return new MyViewHolder(v);
    }

    // Phương thức này được gọi để hiển thị dữ liệu tại vị trí đã chỉ định từ danh sách bài viết
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Lấy ra đối tượng Post tại vị trí position trong danh sách bài viết
        Post post = postArrayList.get(position);

        // Đặt tiêu đề bài viết lên TextView
        holder.tvTitle.setText(post.getTitle());

        // Định dạng thời gian đăng bài viết và đặt lên TextView
        Timestamp timestamp = post.getPublished_at();
        Date date = timestamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedTime = sdf.format(date);
        holder.tvPublished.setText("Ngày đăng: " + formattedTime);

        // Tải hình ảnh từ URL và hiển thị bằng Glide
        Glide.with(context).load(post.getImage()).into(holder.ivImage);

        // Xử lý sự kiện khi người dùng nhấn vào nút "Xem thêm"
        holder.btnXemThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở URL trong trình duyệt mặc định
                String url = post.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            }
        });
    }

    // Phương thức này trả về số lượng item trong danh sách bài viết
    @Override
    public int getItemCount() {
        return postArrayList.size();
    }

    // Class ViewHolder để giữ các thành phần giao diện cho mỗi item trong RecyclerView
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle,tvPublished;
        public ImageView ivImage;
        public Button btnXemThem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần giao diện từ layout post_item.xml
            tvTitle = itemView.findViewById(R.id.tvPost_title);
            tvPublished = itemView.findViewById(R.id.tvPost_published_at);
            ivImage = itemView.findViewById(R.id.ivPost_image);
            btnXemThem = itemView.findViewById(R.id.btnXemthem);
        }
    }
}
