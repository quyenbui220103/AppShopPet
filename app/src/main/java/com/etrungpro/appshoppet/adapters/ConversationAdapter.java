package com.etrungpro.appshoppet.adapters;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.activities.ChatActivity;
import com.etrungpro.appshoppet.models.Conversation;
import com.etrungpro.appshoppet.models.Chat;
import com.etrungpro.appshoppet.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

// Import các thư viện và package cần thiết

// Định nghĩa lớp Adapter và các biến instance
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    // ArrayList để lưu trữ dữ liệu
    ArrayList<Conversation> mList;
    Context mContext;

    // Constructor để khởi tạo Adapter
    public ConversationAdapter(Context mContext) {
        this.mContext = mContext;
    }

    // Phương thức để cập nhật dữ liệu của Adapter
    public void setList(ArrayList<Conversation> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    // Phương thức ghi đè để tạo ViewHolder mới khi cần
    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout của mỗi item trong RecyclerView
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ConversationViewHolder(v);
    }

    // Phương thức ghi đè để gắn dữ liệu vào ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        // Lấy ra conversation tại vị trí hiện tại trong danh sách
        Conversation conversation = mList.get(position);

        // Khởi tạo các đối tượng Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        // Mảng để lưu conversationId từ Firebase
        final String[] coversationId = {""};

        // Lấy userId hiện tại của người dùng
        String currentUser = FirebaseAuth.getInstance().getUid();

        // Truy vấn vào Firestore để lấy dữ liệu của message mới nhất trong conversation
        db.collection("messages")
                .document(conversation.getLastMessage())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            // Lưu trữ conversationId của message
                            coversationId[0] = documentSnapshot.getString("conversationId");

                            // Chuyển đổi DocumentSnapshot thành đối tượng Chat
                            Chat chat = documentSnapshot.toObject(Chat.class);

                            // Hiển thị nội dung của tin nhắn cuối cùng
                            if(chat.getSenderId().equals(currentUser)) {
                                holder.tvLastMessage.setText("Bạn: " + chat.getText());
                            } else {
                                holder.tvLastMessage.setText(chat.getText());
                            }

                            // Đặt màu cho tin nhắn dựa trên trạng thái đã xem
                            if(chat.isSeen()) {
                                holder.tvLastMessage.setTextColor(Color.rgb(176, 179, 184));
                            }

                            // Đặt thời gian tạo tin nhắn
                            Timestamp timestamp = chat.getCreateAt();
                            Date date = timestamp.toDate();
                            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                            String formattedTime = sdf.format(date);
                            holder.tvCreateAt.setText(formattedTime);
                        }
                    }
                });

        // Lấy danh sách người dùng trong conversation và tìm ID của người dùng khác
        ArrayList<String> usersOfCurrentConversation = conversation.getUsers();
        String anotherUserID = "";
        for(String userId : usersOfCurrentConversation) {
            if(!userId.equals(currentUser)) {
                anotherUserID = userId;
            }
        }

        // Truy vấn vào Firestore để lấy dữ liệu của người dùng khác
        User anotherUser = new User();
        db.collection("users")
                .document(anotherUserID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            // Chuyển đổi DocumentSnapshot thành đối tượng User
                            User user = documentSnapshot.toObject(User.class);

                            // Cập nhật thông tin của người gửi tin nhắn
                            anotherUser.setFirstName(user.getFirstName());
                            anotherUser.setLastName(user.getLastName());
                            anotherUser.setImg(user.getImg());

                            // Hiển thị tên và ảnh của người gửi tin nhắn
                            holder.tvSenderName.setText(user.getLastName() + " " + user.getFirstName());

                            // Tải ảnh đại diện của người gửi tin nhắn
                            storageRef.child(user.getImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(mContext)
                                            .load(uri)
                                            .into(holder.circleSenderImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Xử lý khi tải ảnh thất bại
                                    Log.e("status", "false");
                                }
                            });
                        }
                    }
                });

        // Xử lý sự kiện khi người dùng nhấp vào một cuộc trò chuyện
        holder.layoutConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang màn hình ChatActivity và truyền dữ liệu cần thiết
                Intent intent = new Intent(mContext, ChatActivity.class);
                intent.putExtra("conversationId",coversationId[0]);
                intent.putExtra("name", anotherUser.getLastName() + " " + anotherUser.getFirstName());
                intent.putExtra("profileImage", anotherUser.getImg());
                intent.putExtra("userId", currentUser);
                intent.putExtra("conversationId", coversationId[0]);
                mContext.startActivity(intent);
            }
        });

    }

    // Phương thức để trả về số lượng item trong danh sách
    @Override
    public int getItemCount() {
        if(mList != null) {
            return mList.size();
        }
        return 0;
    }

    // Lớp ViewHolder để giữ các thành phần giao diện của mỗi item
    public class ConversationViewHolder extends RecyclerView.ViewHolder {

        // Các thành phần giao diện của mỗi item
        CircleImageView circleSenderImage;
        TextView tvSenderName;
        TextView tvLastMessage;
        TextView tvCreateAt;
        TextView tvSeen;
        ConstraintLayout layoutConversation;

        // Constructor để gán các thành phần giao diện
        public ConversationViewHolder(@NonNull View itemView) {
            super(itemView);
            circleSenderImage = itemView.findViewById(R.id.sender_image);
            tvSenderName = itemView.findViewById(R.id.sender_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvCreateAt = itemView.findViewById(R.id.tv_create_at);
            layoutConversation = itemView.findViewById(R.id.layout_conversation);
            tvSeen = itemView.findViewById(R.id.tv_seen);
        }
    }
}
