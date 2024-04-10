package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.models.Product;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.DetailCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    //lưu trữ thông tin về trạng thái của ứng dụng và cung cấp các phương thức để truy cập tài nguyên của hệ thống.
    Context mContext;
    //khởi tạo một danh sách các sản phẩm chi tiết trong giỏ hàng được lưu trữ trong ArrayList kiểu DetailCart.
    ArrayList<DetailCart> mList;

    public interface ICartEvent {
        void add(String productId);
        void remove(String productId);
        void updateTotalPrice(DetailCart detailCart, int position);
        void delete(int position);
    }

    private ICartEvent iCartEvent;

    public CartAdapter(Context mContext) {
        this.mContext = mContext;//cho phép lớp cartAdapter c quyền truy cập vào đối tượng Context trong activity
    }
    public CartAdapter(Context mContext , ICartEvent iCartEvent) {
        this.iCartEvent = iCartEvent;
        this.mContext = mContext;
    }
    //thiết lập danh sách các mục trong giỏ hàng
    public void setList(ArrayList<DetailCart> mList) {
        this.mList = mList;
        notifyDataSetChanged();//thông báo cho adapter dữ liệu đã thay đổi
    }
    //mỗi item trong danh sách sẽ được hiển thị dưới dạng một View với đầy đủ các thành phần và giá trị của nó
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);//khởi tạo layout
        return new CartViewHolder(v);
    }
    //hiển thị dữ liệu lên viewHolder
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        DetailCart detailCart = mList.get(position);//Lấy danh sách đối tượng DetailCart từ danh sách mList tại chức năng
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products")
                .document(detailCart.getProductId())// đổi từ kiểu document thành product
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            Product product = documentSnapshot.toObject(Product.class);
                            holder.tvProductCartName.setText(product.getName());
                            holder.tvProductCartPrice.setText(String.valueOf(product.getPrice()));
                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            //lấy tham chiếu tới thư mục gốc của Firebase Storage,
                            StorageReference storageRef = storage.getReference();
                            //tham chiếu đến tệp hình ảnh
                            storageRef.child(product.getImgBig()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                //hiển thị ảnh lấy được từ url
                                public void onSuccess(Uri uri) {
                                    Glide.with(mContext)// tải hình ảnh từ url hiện thị vào holder
                                            .load(uri)
                                            .into(holder.ProductCartImage);
                                }
                            }).addOnFailureListener(new OnFailureListener() {// không thành công tải dử liệu
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }

                    }
                });
        //Dòng lệnh này được sử dụng để hiển thị số lượng sản phẩm trong giỏ hàng lên trên giao diện người dùng
        holder.tvProductCartQuality.setText(String.valueOf(detailCart.getQuatity()));
        //xử lý sự kiện cho check box
        holder.cbProductCartSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()) {
                    iCartEvent.add(detailCart.getId()); // thêm vào danh sách sản phẩm có trong giở hàng
                } else {
                    iCartEvent.remove(detailCart.getId());// loại bỏ ra khỏi danh sách sp trong giỏ hàng
                }
            }
        });
        //xử lý sự kiện tăng số lượng sản phẩm
        holder.btnPlusQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuality = Integer.parseInt(holder.tvProductCartQuality.getText().toString());// lấy số lượng từ textview trong viewHolder đôi thành kiểu số nguyên
                currentQuality += 1 ;
                holder.tvProductCartQuality.setText(String.valueOf(currentQuality));//cập nhật textview với giá trị mới
                detailCart.setQuatity(currentQuality);//cập nhật số lượng vào đối tượng detail
                updateCartQuality(detailCart, holder.getAdapterPosition());
            }
        });
        //xử lý sự kiện giảm số lượng sản phẩm
        holder.btnSubQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentQuality = Integer.parseInt(holder.tvProductCartQuality.getText().toString()); // chuyển số lượng ở bên text view về số nguyên
                currentQuality -= 1 ;
                holder.tvProductCartQuality.setText(String.valueOf(currentQuality));// cập nhật text view vs giá trị mới cuae curentQality
                detailCart.setQuatity(currentQuality);//cập nhật số lượng vào đối tượng detail
                updateCartQuality(detailCart, holder.getAdapterPosition());// cập nhật thjk hợp giữa detail với holder
            }
        });
        //xử lý sự kiện xóa sản phẩm
        holder.tvDeleteProductCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               db.collection("detailCarts")//xóa tài liệu trong bộ suw tập detail của cơ sở dữ liệu firebase vs id tương ứng vs detailCart
                       .document(detailCart.getId())
                       .delete()
                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                           @Override
                           public void onSuccess(Void unused) {
                               iCartEvent.delete(holder.getAdapterPosition());// thông báo sựu kiện xóa tại vị trí của viewHolder
                           }
                       });
            }
        });
    }
    //cập nhật số lượng của một sản phẩm trong giỏ hàng của người dùng.Đầu vào của phương thức là một đối tượng DetailCart
    // //và vị trí của sản phẩm trong danh sách hiển thị.
    //việc cập nhật số lượng sản phẩm và tính toán lại tổng giá tiền của giỏ hàng được thực hiện trên máy chủ Firebase,
    // //sau đó gửi kết quả về cho ứng dụng Android thông qua các callback (như OnCompleteListener).
    private void updateCartQuality(DetailCart detailCart, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();// truy cập vào firebase thực hiện lưu trữ id của détail
        db.collection("detailCarts")
                .document(detailCart.getId())
                .set(detailCart)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            iCartEvent.updateTotalPrice(detailCart, position); //nếu thành công thì update lại giá tiền
                        }
                    }
                });
    }
    //xác định số phần tử hiển thị
    @Override
    public int getItemCount() {

        if(mList != null) {
            return mList.size(); // số phần tử hiển thi tương ứng với size
        }
        return 0;
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        TextView tvProductCartName;
        TextView tvProductCartPrice;
        TextView btnPlusQuality;
        TextView btnSubQuality;
        ImageView ProductCartImage;
        TextView tvProductCartQuality;
        TextView tvDeleteProductCart;
        CheckBox cbProductCartSelect;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductCartName = itemView.findViewById(R.id.product_cart_name);
            ProductCartImage = itemView.findViewById(R.id.product_cart_img);
            tvProductCartQuality = itemView.findViewById(R.id.product_cart_quatity);
            tvProductCartPrice = itemView.findViewById(R.id.product_cart_price);
            cbProductCartSelect = itemView.findViewById(R.id.cb_item_cart);
            btnPlusQuality = itemView.findViewById(R.id.btn_cart_plus);
            btnSubQuality = itemView.findViewById(R.id.btn_cart_sub);
            tvDeleteProductCart = itemView.findViewById(R.id.tv_delete_p_cart);
        }
    }
}
