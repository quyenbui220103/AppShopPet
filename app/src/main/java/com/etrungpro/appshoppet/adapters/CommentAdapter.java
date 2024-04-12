package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.Comment;
import com.etrungpro.appshoppet.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    ArrayList<Comment> mList;
    Context mContext;

    public CommentAdapter(Context mContext) {
        this.mContext = mContext;
    }

    // Phương thức này để cập nhật danh sách bình luận
    public void setList(ArrayList<Comment> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    // Phương thức này tạo ViewHolder cho mỗi item
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout user_comment.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_comment, parent, false);
        return new CommentViewHolder(v);
    }

    // Phương thức này được gọi để hiển thị dữ liệu tại vị trí đã chỉ định từ danh sách bình luận
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // Lấy ra đối tượng Comment tại vị trí position trong danh sách bình luận
        Comment comment = mList.get(position);

        // Hiển thị nội dung bình luận
        holder.tvUserComment.setText(comment.getContent());

        // Lấy ID của người dùng
        String userId = comment.getUserId();

        // Khởi tạo đối tượng FirebaseFirestore để truy vấn dữ liệu từ Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Truy vấn vào collection "users" để lấy thông tin của người dùng
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            // Chuyển đổi dữ liệu người dùng thành đối tượng User
                            User user = documentSnapshot.toObject(User.class);
                            // Hiển thị hình ảnh người dùng và tên người dùng lên giao diện
                            holder.imgUser.setImageResource(R.drawable.toc_tim); // Sử dụng ảnh mặc định, có thể thay đổi sau
                            holder.tvUserName.setText(user.getLastName() + " " + user.getFirstName());
                        } else {
                            // Xử lý trường hợp không tìm thấy người dùng
                            // Có thể thêm mã xử lý ở đây nếu cần
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Xử lý khi truy vấn không thành công
                        // Có thể thêm mã xử lý ở đây nếu cần
                    }
                });
    }

    // Phương thức này trả về số lượng item trong danh sách bình luận
    @Override
    public int getItemCount() {
        if(mList != null) {
            return mList.size();
        }
        return 0;
    }

    // Lớp này định nghĩa ViewHolder cho mỗi item
    public class CommentViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView tvUserName;
        TextView tvUserComment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.user_comment_img); // ImageView hiển thị hình ảnh người dùng
            tvUserName = itemView.findViewById(R.id.tv_user_comment_name); // TextView hiển thị tên người dùng
            tvUserComment = itemView.findViewById(R.id.tv_user_comment_content); // TextView hiển thị nội dung bình luận
        }
    }
}
