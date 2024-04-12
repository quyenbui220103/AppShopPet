package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.etrungpro.appshoppet.activities.CategoryActivity;
import com.etrungpro.appshoppet.models.Product;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.Category;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context mContext;
    private ArrayList<Category> mListCategory;


    public CategoryAdapter(Context mContext) {
        this.mContext = mContext;
    }

    // Phương thức này được sử dụng để cập nhật dữ liệu danh sách danh mục
    public void setData(ArrayList<Category> list) {
        this.mListCategory = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout item_category.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);

        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Lấy ra đối tượng Category tại vị trí position trong danh sách danh mục
        Category category = mListCategory.get(position);
        // Kiểm tra xem Category có tồn tại không
        if(category == null) {
            return ;
        }
        // Đặt tên danh mục lên TextView
        holder.categoryName.setText(category.getName());

        // Tạo một LinearLayoutManager cho RecyclerView sản phẩm
        LinearLayoutManager linearLayout = new LinearLayoutManager(mContext, RecyclerView.HORIZONTAL, false);
        holder.rcvProduct.setLayoutManager(linearLayout);

        // Tạo một Adapter cho RecyclerView sản phẩm và đặt dữ liệu sản phẩm vào Adapter
        ProductAdapter productAdapter = new ProductAdapter(mContext);
        productAdapter.setData(category.getProducts());
        holder.rcvProduct.setAdapter(productAdapter);

        // Xử lý sự kiện khi nút "Xem tất cả" được nhấn
        holder.btnShowALl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo một Intent để chuyển đến CategoryActivity
                Intent intent = new Intent(mContext, CategoryActivity.class);
                // Đặt tên của danh mục vào Intent để truyền sang CategoryActivity
                intent.putExtra("categoryName", category.getName());
                // Bắt đầu Activity mới bằng Intent
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        // Trả về số lượng phần tử trong danh sách danh mục, nếu danh sách không tồn tại, trả về 0
        if(mListCategory != null) return mListCategory.size();
        return 0;
    }

    // Class ViewHolder để giữ các thành phần giao diện cho mỗi item trong RecyclerView
    public class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryName;
        private RecyclerView rcvProduct;
        private MaterialButton btnShowALl;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần giao diện từ layout item_category.xml
            categoryName = itemView.findViewById(R.id.category_name);
            rcvProduct = itemView.findViewById(R.id.rcv_product);
            btnShowALl = itemView.findViewById(R.id.btn_show_all);

        }
    }
}
