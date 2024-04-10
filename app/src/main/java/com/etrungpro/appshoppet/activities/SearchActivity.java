package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.adapters.ProductAdapter;
import com.etrungpro.appshoppet.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    TextView txtSearchSize;
    SearchView searchView;
    RecyclerView rcvSearch;
    ArrayList<Product> productList;
    ProductAdapter productAdapter;

    //    androidx.appcompat.widget.SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);//đặt giao diện người dùng cho activity
        initUI(); //khởi tạo, thiết lập các thành phần SearchView, RecyclerView, và ProductAdapter.

        String previousSearch = getIntent().getStringExtra("searchValue");
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this);
        //thiết lập layout cho GridLayoutManager vs 2 cột
        GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchActivity.this, 2);
        rcvSearch.setLayoutManager(gridLayoutManager);
        rcvSearch.setAdapter(productAdapter);
        getSearchResult(previousSearch);// lấy kết quả tìm kiếm từ db, cập nhật dsach sp trong RecyclerView theo kq tìm kiếm

        //listen dl nhập từ user
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getSearchResult(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    //tìm kiếm sp trong db dựa trên gt searchValue
    private void getSearchResult(String searchValue) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //kiểm tra gt searchValue tồn tại chưa
        if (searchValue.length() == 0) {//=0 là k có gt đã tìm kiếm
            return;
        }
        //nếu k rỗng, sd orderBy sxep kq trả về theo key
        db.collection("products")
                .orderBy("key")
                .startAt(searchValue).endAt(searchValue + "\uf8ff")////tìm sp có gt key bắt đầu, kthuc = searchValue
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    // get() để lấy kết quả trả về và thêm sản phẩm vào dsach bằng vòng lặp for-each
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            productList.clear(); //nếu thành công thì xóa dsach
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Product product = document.toObject(Product.class);
                                productList.add(product);
                            }//lấy dl từ dl trả về từ tìm kiếm và chuyển đổi thành đtg Product
                            productAdapter.setData(productList);//cập nhập dl sp
                        }
                    }
                });
    }


    private void initUI() {
        txtSearchSize = findViewById(R.id.result_size);//hiển thị kq slg sp tìm đc
        rcvSearch = findViewById(R.id.rcv_search);//hiển thị dsach sp
        searchView = findViewById(R.id.search_view);//cho user nhập vào từ khóa cần tìm
    }
}