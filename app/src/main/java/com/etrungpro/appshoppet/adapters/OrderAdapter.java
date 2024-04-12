package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.models.Product;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.DetailCart;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    ArrayList<DetailCart> mList;

    // Constructor để khởi tạo adapter với context
    public OrderAdapter(Context context) {
        this.context = context;
    }

    // Phương thức này được sử dụng để cập nhật dữ liệu danh sách chi tiết đơn hàng
    public void setList(ArrayList<DetailCart> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    // Phương thức này tạo ViewHolder cho mỗi item
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout item_product_order.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_order, parent, false);
        return new OrderViewHolder(v);
    }

    // Phương thức này được gọi để hiển thị dữ liệu tại vị trí đã chỉ định từ danh sách chi tiết đơn hàng
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        // Lấy ra đối tượng DetailCart tại vị trí position trong danh sách chi tiết đơn hàng
        DetailCart detailCart = mList.get(position);

        // Truy vấn Firestore để lấy thông tin sản phẩm dựa trên ID của sản phẩm trong chi tiết đơn hàng
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .document(detailCart.getProductId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            // Nếu tài liệu tồn tại, chuyển đổi nó thành đối tượng Product
                            Product product = documentSnapshot.toObject(Product.class);
                            // Đặt tên sản phẩm và giá của sản phẩm lên TextViews tương ứng
                            holder.tvProductOrderName.setText(product.getName());
                            holder.tvProductOrderPrice.setText(String.valueOf(product.getPrice()));

                            // Tạo một tham chiếu đến Firebase Storage để tải ảnh sản phẩm từ URL được cung cấp
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();

                            // Tải ảnh sản phẩm từ Firebase Storage và hiển thị nó trong ImageView bằng Glide
                            storageRef.child(product.getImgBig()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(context)
                                            .load(uri)
                                            .into(holder.productOrderImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Xử lý lỗi nếu không tải được ảnh
                                }
                            });
                        }
                    }
                });

        // Đặt số lượng sản phẩm lên TextView
        holder.tvProductOrderQuatity.setText("Số lượng: " + String.valueOf(detailCart.getQuatity()));
    }

    // Phương thức này trả về số lượng item trong danh sách chi tiết đơn hàng
    @Override
    public int getItemCount() {
        if(mList != null) {
            return mList.size();
        }
        return 0;
    }

    // Class ViewHolder để giữ các thành phần giao diện cho mỗi item trong RecyclerView
    public class OrderViewHolder extends RecyclerView.ViewHolder {

        ImageView productOrderImage;
        TextView tvProductOrderName;
        TextView tvProductOrderQuatity;
        TextView tvProductOrderPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần giao diện từ layout item_product_order.xml
            productOrderImage = itemView.findViewById(R.id.product_order_img);
            tvProductOrderName = itemView.findViewById(R.id.product_order_name);
            tvProductOrderQuatity = itemView.findViewById(R.id.product_order_quatity);
            tvProductOrderPrice = itemView.findViewById(R.id.product_order_price);
        }
    }
}
