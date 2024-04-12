package com.etrungpro.appshoppet.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.adapters.ConversationAdapter;
import com.etrungpro.appshoppet.models.Conversation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView rcvConversation;
    private ConversationAdapter conversationAdapter;
    private ArrayList<Conversation> conversations;
    private ListenerRegistration conversationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);// khởi tạo lại lớp cha và khôi phục lại trạng thái ban đầu
        setContentView(R.layout.activity_conversation);
        initUI();

        conversations = new ArrayList<>();// tạo 1 list để luw trữ
        conversationAdapter = new ConversationAdapter(this);// gọi adapter để chuyển
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ConversationActivity.this, LinearLayoutManager.VERTICAL, false);// không bảo ngược thứ tự
        conversationAdapter.setList(conversations);// Cung cấp list cuộc trò chuyện cho phía adapter
        rcvConversation.setLayoutManager(linearLayoutManager);//  gọi đến và thiết lập layout và hiển thị lên
        rcvConversation.setAdapter(conversationAdapter);

        getConversationList();// cập nhật cuộc c=trò chuyện vào recuclerView
    }

    @Override
    protected void onResume() {
        super.onResume();// tiếp tục hoạt động của lớp cha
        getConversationList();// sau khi activity hoạt đong lại sau khoảng thời gian tạm dưng thì tin nhắn vẫn đc cập nhật tin mới nhất
    }

    void initUI() {
        rcvConversation = findViewById(R.id.rcv_conversation);
    }

    private void getConversationList() {
        String userId = FirebaseAuth.getInstance().getUid();// lây id người dùng hiện tại từ firebaseAuth
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        conversationListener = db.collection("conversations")// truy vấn vào conversation trên firebase
                .whereArrayContains("users", userId)// lọc cuộc trò chuyện của người dùng có cùng id
                .addSnapshotListener(new EventListener<QuerySnapshot>() {// nếu có thay đổi thì cập nhật lại tài liệu mới
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Xử lý lỗi
                            return;
                        }

                        conversations.clear();// xóa danh sách các cuộc trò chuyện hiện tại để lấy dữ liếu mới nất trên firebase

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Conversation conversation = document.toObject(Conversation.class);// chuyển đối tượng về dạng documment
                            conversations.add(conversation); //thêm nó vào
                        }

                        conversationAdapter.setList(conversations);// cập nhật dữ liệu vào conversationAdapter
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();// hủy hành động của lớp cha
        conversationListener.remove();// ngăn s thay đổi trong conversation trong khi hoạt động kết thúc
    }
}