package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.adapters.ChatAdapter;
import com.etrungpro.appshoppet.models.Chat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    ImageView senderImage;
    TextView tvSenderName;
    TextView tvStatus;
    Button btnSendMessage;
    EditText edtMessage;
    RecyclerView rcvChat;
    ChatAdapter chatAdapter;
    private ListenerRegistration conversationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initUI();

        tvSenderName.setText(getIntent().getStringExtra("name"));// Lấy dữ liệu từ intent
        FirebaseStorage storage = FirebaseStorage.getInstance();//tạo 1 cái để lưu trên firebase
        StorageReference storageRef = storage.getReference();// trả về giá trị gốc trên firebase
        chatAdapter = new ChatAdapter(ChatActivity.this);// tạo 1 cái adapter mới  truyên vào tham số tham chiếu đến activity hiện tại
        //tạo 1 đối tương linerlayout để quản lý hiển thị các mục trong recyclerView với chiều dọc và các mục mới thêm vào đầu danh sách cũ ở cuối dnah sách
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, true);
        rcvChat.setLayoutManager(linearLayoutManager);// xác inh cách hiển thị
        rcvChat.setAdapter(chatAdapter);//kết nối dữ liệu chat với recyclerviewd để hiển thị
        getChatList();//lấy danh sách chat từ nguồn dữ kiệu và cập nhật vào châtdapter
        //Xử lý sự kiện khi nhân button gửi
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtMessage.length() == 0) {// người dùng ko nhập kí tự nào
                    return ;
                }

                FirebaseFirestore db = FirebaseFirestore.getInstance();// tạo cái firebare để luw trữ
                Timestamp timestamp = Timestamp.now();// đối tượng thời gian hiện tại
                db.collection("messages")
                        .add(new Chat(getIntent().getStringExtra("conversationId"),// thêm dữ liệu mới vào  bộ sưu tâ messages
                                timestamp,// thời gian
                                true,getIntent().getStringExtra("userId") ,//id người dùng nhắn
                                edtMessage.getText().toString()))// nội dung tin nhắn
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {// nếu thành công
                            @Override
                            public void onSuccess(DocumentReference documentReference) {//lưu vào documment

                                db.collection("conversations")
                                    .document(getIntent().getStringExtra("conversationId"))
                                    .update("lastMessage", documentReference.getId());// cập nhật last messages
                                getChatList();//cập nhật lại phương thức chat
                            }
                        });

                edtMessage.setText("");//xóa nội dung edtMessage sau khi được gửi
            }
        });
    }

    void initUI() {
        senderImage = findViewById(R.id.sender_chat_img);
        tvSenderName = findViewById(R.id.sender_chat_name);
        tvStatus = findViewById(R.id.tv_sender_chat_status);
        btnSendMessage = findViewById(R.id.btn_send_message);
        edtMessage = findViewById(R.id.edt_message);
        rcvChat = findViewById(R.id.rcv_chat);
    }

    void getChatList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        conversationListener =  db.collection("messages") //truy cập vào bộ suw tập mesages
                .orderBy("createAt", Query.Direction.DESCENDING)// sắp xếp tài liệu theo trường createAt theo thứ tụ giảm dần để tin nhắn mới xuât hiên đầu tiên trong danh sách
                .whereEqualTo("conversationId", getIntent().getStringExtra("conversationId"))// lọc các tài liệu chỉ chứa trường conversation có giá trị trùng khớp
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        ArrayList<Chat> chats = new ArrayList<>();// tạo 1 list để lưu trữ mesages
                        for (DocumentSnapshot documentSnapshot : querySnapshot) {
                            Chat chat = documentSnapshot.toObject(Chat.class);// chuyển đối tượng chat về dang document
                            chats.add(chat);
                        }

                        chatAdapter.SetList(chats);//Cập nhật  danh sạch lên firebase
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();// thực hiện thao tác hủy lớp cha
        conversationListener.remove();//ngăn sự thay đội sau khi hoạt động đã kết thúc
    }
}