package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.etrungpro.appshoppet.models.Product;
import com.etrungpro.appshoppet.adapters.ProductAdapter;
import com.etrungpro.appshoppet.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    TextView tvDanhMuc;
    RecyclerView rcvDanhMuc;
    ProductAdapter productAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);// liên kết vs layout
        initUI();

        String categoryName = getIntent().getStringExtra("categoryName"); //Lấy giá trị của chuỗi tên danh mục từ Intent
        tvDanhMuc.setText(categoryName);//hien thi
        productAdapter = new ProductAdapter(CategoryActivity.this);//tạo adapter để truyền tham số
        GridLayoutManager gridLayoutManager = new GridLayoutManager(CategoryActivity.this, 2);//quản lý danh mục hiện thị theo dạng lưới 2 cột
        rcvDanhMuc.setLayoutManager(gridLayoutManager);
        rcvDanhMuc.setAdapter(productAdapter);//cung cấp dữ liệu cho danh sách sản phẩm và xử lý việc tạo hiển thị
        getProductCategoryList(categoryName);// cập nhật danh sách vào adapter
    }

    private void getProductCategoryList(String category) { //lấy danh sách các sản phẩm có trong một danh mục (category) cụ thể.

        ArrayList<Product> products = new ArrayList<>();// tạo 1 líst mơi đê lưu sản phẩm
        FirebaseFirestore db = FirebaseFirestore.getInstance(); //truy vấn dữ liệu từ tập con (collection) "products" trong cơ sở dữ liệu
        db.collection("products")
                .whereEqualTo("category", category)//lấy các sản phẩm có thuộc tính "category" bằng với giá trị được truyền vào phương thức.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                product.setId(document.getId());
                                products.add(product);
                            }
                            productAdapter.setData(products);// cập nhật danh sách sản phẩm trong adapter vs sản phẩm ms đc truy vấn hiển thị trên giao diện người dùng
                        } else {
                            Toast.makeText(CategoryActivity.this, "False to get collection!", Toast.LENGTH_SHORT).show();//
                        }
                    }
                });

    }

    void initUI() {
        tvDanhMuc = findViewById(R.id.txt_danhmuc);
        rcvDanhMuc = findViewById(R.id.rcv_danhmuc);
    }
}