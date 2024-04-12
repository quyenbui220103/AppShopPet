package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.ProductDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProductDetailAdapter extends RecyclerView.Adapter<ProductDetailAdapter.ProductDetailViewHolder> {

    ArrayList<ProductDetail> mList;
    Context mContext;

    // Interface để xử lý sự kiện khi người dùng chọn hình ảnh sản phẩm
    public interface IProductDetailEvent {
        void set(String productImageUrl);
    }

    private IProductDetailEvent iProductEvent;

    public ProductDetailAdapter(Context mContext, IProductDetailEvent iProductEvent) {
        this.mContext = mContext;
        this.iProductEvent = iProductEvent;
    }

    // Phương thức này được sử dụng để cập nhật dữ liệu danh sách chi tiết sản phẩm
    public void setList(ArrayList<ProductDetail> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout item_detail_img.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_img, parent, false);
        return new ProductDetailViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDetailViewHolder holder, int position) {
        // Lấy ra đối tượng ProductDetail tại vị trí position trong danh sách chi tiết sản phẩm
        ProductDetail productDetail = mList.get(position);

        // Khởi tạo FirebaseStorage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Tham chiếu đến Firebase Storage
        StorageReference storageRef = storage.getReference();

        // Tải hình ảnh từ Firebase Storage và hiển thị bằng Glide
        storageRef.child(productDetail.getImg()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(mContext)
                        .load(uri)
                        .into(holder.productImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xử lý khi có lỗi xảy ra trong quá trình tải hình ảnh
                Log.e("status", "false");
            }
        });

        // Xử lý sự kiện khi người dùng nhấn vào hình ảnh sản phẩm
        holder.productImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gọi phương thức set() của interface để thông báo về sự kiện chọn hình ảnh
                iProductEvent.set(productDetail.getImg());
            }
        });
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng phần tử trong danh sách chi tiết sản phẩm, nếu danh sách không tồn tại, trả về 0
        if(mList != null) {
            return mList.size();
        }
        return 0;
    }

    // Class ViewHolder để giữ các thành phần giao diện cho mỗi item trong RecyclerView
    public class ProductDetailViewHolder extends RecyclerView.ViewHolder {

        ImageView productImg;

        public ProductDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần giao diện từ layout item_detail_img.xml
            productImg = itemView.findViewById(R.id.product_detail_sm);
        }
    }
}
