package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.etrungpro.appshoppet.adapters.CommentAdapter;
import com.etrungpro.appshoppet.models.ProductDetail;
import com.etrungpro.appshoppet.adapters.ProductDetailAdapter;
import com.etrungpro.appshoppet.models.Comment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.AggregateQuerySnapshot;
import com.google.firebase.firestore.AggregateSource;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    TextView tvDetailName;
    TextView tvDetailPrice;
    TextView tvDetailDesc;
    RecyclerView rcvProductImage;
    ProductDetailAdapter productDetailAdapter;
    CommentAdapter commentAdapter;
    ImageView btnAddQuatity;
    ImageView btnSubQuatity;
    ImageView productImageBig;
    EditText edtQuatity;
    Button btnAddToCart;
    ArrayList<String> productImages;
    RecyclerView rcvComment;
    ImageView btnSendComment;
    EditText edtComment;
    ArrayList<Comment> commentList;
    TextView tvDanhGia;
    String productId;
    String productImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);// kết nối với giao diện
        initUI();
        Intent intent = getIntent();// trả về intent hoạt động trc
        Bundle bundle = intent.getExtras();//trả về dữ liệu bundle
        String productName = bundle.getString("productName");// lấy ra dữ liệu trong bundle
        String productDesc = bundle.getString("productDesc");
        productId = bundle.getString("productId");// cái này là khóa để lấy các trường khác
        int productPrice = bundle.getInt("productPrice");// laays giả trị số nguyên trong bundle (giá)
        productImages = new ArrayList<>();//khỏi tao list mới để lưu trũ giá danh sách tương ứng
        productImages = bundle.getStringArrayList("productImages");// lấy danh sách giá trị tương ứng

        tvDetailName.setText(productName);// thiết lập nội dung của text view bằng giá trị của productname
        tvDetailPrice.setText(String.valueOf(productPrice));// chuyển đổi giá trị nguyên thành chuỗi
        tvDetailDesc.setText(productDesc);

        productDetailAdapter = new ProductDetailAdapter(this, new ProductDetailAdapter.IProductDetailEvent() {
            // cấp cho adapter quyển truy cập vào activity( sử dụng cú phạm truyên in terface vô danh
            @Override
            public void set(String x) {//khi sự kiên set sảy ra trong adapter thì
                productImageUrl = x;
                showProductImageBig();// hiển thị hình ảnh lớn
            }
        });
        productDetailAdapter.setList(getProductImageList());// cập nhật danh sách hình ảnh trong adapter theo dạng list
        LinearLayoutManager rcvProductLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);// theo hướng ngang, không đảo ngược thứ tự
        rcvProductImage.setLayoutManager(rcvProductLayout);// thieets lập Adapter cho recyclerView
        rcvProductImage.setAdapter(productDetailAdapter);// đối tượng adapter đc gán cho recyclerView và hiển thị lên
        showProductImageBig();
        // lưu trữ comment dưới từng loại sản pẩm
        commentAdapter = new CommentAdapter(DetailActivity.this);
        commentList = new ArrayList<>();
        LinearLayoutManager rcvCommentLayout = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        rcvComment.setLayoutManager(rcvCommentLayout);
        rcvComment.setAdapter(commentAdapter);
        getCommentList();// Lấy danh sách comment


        btnAddQuatity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(edtQuatity.getText().toString());// chuyê cái value về dạng số nguyên
                value +=1;
                edtQuatity.setText(String.valueOf(value));//cập nhật số lượng mới

            }
        });

        btnSubQuatity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int value = Integer.parseInt(edtQuatity.getText().toString());
                if(value >= 0) {
                    value -=1;
                }
                edtQuatity.setText(String.valueOf(value));

            }
        });
        //add vào giỏ hàng
        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, CartActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("productPrice", String.valueOf(productPrice));
                intent.putExtra("productQuality", edtQuatity.getText().toString());
                startActivity(intent);
            }
        });

        btnSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSendComment();
            }
        });

    }

    private void handleSendComment() {
        if(edtComment.length() == 0) {
            return ;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();// tương tác với firebase
        String userId = FirebaseAuth.getInstance().getUid();// lấy id người dùng
        Comment comment = new Comment(userId,
                productId, FieldValue.serverTimestamp(), edtComment.getText().toString());
        db.collection("comments")
                .add(comment)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                       getCommentList();// cập nhật lại danh sách bình luận
                    }
                })
                // thất bại
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
        edtComment.setText("");// cho về rỗng chờ lượt cmt tiếp
    }

    private void getCommentList() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("comments")
                .whereEqualTo("productId", productId)
                .orderBy("updateAt")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        commentList.clear();
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Comment comment = document.toObject(Comment.class);// đồng nhật kiểu
                                commentList.add(comment);//cập nhật lại danh sách cmt
                            }
                            commentAdapter.setList(commentList);// giaodieenjn người dùng hiển thị cmt mới
                        }
                       else {
                           // Làm gì đó;
                        }
                    }
                });
        db.collection("comments").whereEqualTo("productId", productId).count().get(AggregateSource.SERVER)// count lấy dữ liệu trên sever firebase đếm số lượng bt hợp lệ
                .addOnCompleteListener(new OnCompleteListener<AggregateQuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<AggregateQuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            AggregateQuerySnapshot snapshot = task.getResult();
                            tvDanhGia.setText("Đánh giá (" + String.valueOf(snapshot.getCount()) + ")");// trả về số lượng bình luận đc đếm
                        }
                    }
                });
    }

    private void showProductImageBig() {
        if(productImageUrl == "") {
            return ;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();// tham chiếu đến tệp hình hảnh đc chỉ định
        storageRef.child(productImageUrl).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {// tải hình ảnh xuống
            @Override
            public void onSuccess(Uri uri) {

                Glide.with(DetailActivity.this)
                        .load(uri)
                        .into(productImageBig);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.e("status", "false");

            }
        });
    }

    //tải hình ảnhsắp xếp thành list trong giở hàng
    private ArrayList<ProductDetail> getProductImageList() {
        if(productImages.size() == 0) {
            return null;
        }

        ArrayList<ProductDetail> list = new ArrayList<>();


        for(String productImage : productImages) {
            list.add(new ProductDetail(productImage));
        }
        if(list.size() > 0 ) {
            productImageUrl = list.get(0).getImg();// hình ảnh thành phần tử đầu tiên của list
        }
        return list;
    }

    void initUI() {
        productImageBig = findViewById(R.id.product_detail_big);
        tvDetailName = findViewById(R.id.product_detail_name);
        tvDetailPrice = findViewById(R.id.product_detail_price);
        tvDetailDesc = findViewById(R.id.txt_desc);
        rcvProductImage = findViewById(R.id.rcv_product_img);
        btnAddQuatity = findViewById(R.id.btn_incre_quatity);
        btnSubQuatity = findViewById(R.id.btn_desc_quatity);
        edtQuatity = findViewById(R.id.edtQuatity);
        rcvComment = findViewById(R.id.rcv_comment);
        edtComment = findViewById(R.id.edt_comment);
        btnSendComment = findViewById(R.id.btn_send_comment);
        btnAddToCart = findViewById(R.id.btn_add_cart);
        tvDanhGia = findViewById(R.id.tvDanhGia);

    }
}