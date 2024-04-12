package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.activities.DetailActivity;
import com.etrungpro.appshoppet.models.Product;
import com.etrungpro.appshoppet.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.CategoryViewHolder>  {

    ArrayList<Product> mList;
    Context mContext;

    public ProductAdapter(Context context) {
        this.mContext = context;
    }

    // Phương thức này được sử dụng để cập nhật dữ liệu danh sách sản phẩm
    public void setData(ArrayList<Product> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout item_product.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Lấy ra đối tượng Product tại vị trí position trong danh sách sản phẩm
        Product product = mList.get(position);
        // Kiểm tra xem Product có tồn tại không
        if(product == null) {
            Log.e("value", "null");
            return ;
        }

        // Khởi tạo FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Tham chiếu đến Firebase Storage
        StorageReference storageRef = storage.getReference();
        // Lấy đường dẫn tới hình ảnh sản phẩm từ Firebase Storage và hiển thị bằng Glide
        storageRef.child(product.getImgBig()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext)
                        .load(uri)
                        .into(holder.imgProduct);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xử lý khi có lỗi xảy ra trong quá trình tải hình ảnh
                Log.e("status", "false");
            }
        });

        // Đặt tên sản phẩm lên TextView
        holder.tvProductName.setText(product.getName());
        // Đặt giá sản phẩm lên TextView
        holder.tvProductPrice.setText(String.valueOf(product.getPrice()) + " đ");

        // Xử lý sự kiện khi người dùng nhấn vào tên sản phẩm
        holder.tvProductName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo Intent để chuyển đến DetailActivity
                Intent intent = new Intent(mContext, DetailActivity.class);
                // Đặt dữ liệu sản phẩm vào Bundle và chuyển qua DetailActivity
                Bundle bundle = new Bundle();
                bundle.putString("productName", product.getName());
                bundle.putInt("productPrice", product.getPrice());
                bundle.putString("productDesc", product.getDescription());
                bundle.putStringArrayList("productImages", product.getImg());
                bundle.putString("productId", product.getId());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng phần tử trong danh sách sản phẩm, nếu danh sách không tồn tại, trả về 0
        if(mList != null) return mList.size();
        return 0;
    }

    // Class ViewHolder để giữ các thành phần giao diện cho mỗi item trong RecyclerView
    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgProduct;
        private TextView tvProductName;
        private TextView tvProductPrice;

        public CategoryViewHolder(@NonNull View itemView)
        {
            super(itemView);
            // Ánh xạ các thành phần giao diện từ layout item_product.xml
            imgProduct = itemView.findViewById(R.id.product_img);
            tvProductName = itemView.findViewById(R.id.product_name);
            tvProductPrice = itemView.findViewById(R.id.product_price);
        }
    }
}
