package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.adapters.OrderAdapter;
import com.etrungpro.appshoppet.models.DetailCart;
import com.etrungpro.appshoppet.models.Order;
import com.etrungpro.appshoppet.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

//hien thi thong tin chi tiet san pham duoc chon trong gio hang va cho phep nhap thong tin ng dung de dat hang
public class EditOrderActivity extends AppCompatActivity {

    RecyclerView rcvCartOrder;
    TextView tvSoLuongSp;
    EditText edtEmailOrder;
    EditText edtFullNameOrder;
    EditText edtAddressOrder;
    MaterialButton btnThanhToan;
    String productNames = "";
    String totalPrice;
    ArrayList<DetailCart> detailCarts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);
        //tham chiếu các view qua id
        initUI();
        Bundle bundle = getIntent().getExtras();

        // Lấy danh sách sản phẩm từ giỏ hàng đã chọn
        detailCarts = (ArrayList<DetailCart>) bundle.get("detailCarts");
        totalPrice = (String) bundle.get("totalPrice");

        //hiển thị danh sách sản phẩm trong giỏ hàng của đơn hàng.
        OrderAdapter orderAdapter = new OrderAdapter(EditOrderActivity.this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                EditOrderActivity.this, LinearLayoutManager.VERTICAL, false);
        rcvCartOrder.setLayoutManager(linearLayoutManager);
        rcvCartOrder.setAdapter(orderAdapter);
        tvSoLuongSp.setText("Đơn hàng (" + String.valueOf(detailCarts.size()) + ")");
        orderAdapter.setList(detailCarts);
        //bat loi su kien thanh toan
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtEmailOrder.length() == 0) {
                    edtEmailOrder.setError("Bạn chưa nhập email");
                    edtEmailOrder.setFocusable(true);
                    return;
                }
                if(edtFullNameOrder.length() == 0) {
                    edtFullNameOrder.setError("Bạn chưa họ tên");
                    edtFullNameOrder.setFocusable(true);
                    return;
                }
                if(edtAddressOrder.length() == 0) {
                    edtAddressOrder.setError("Bạn chưa nhập địa chỉ");
                    edtAddressOrder.setFocusable(true);
                    return;
                }

                //thêm thông tin của đơn hàng vào Firestore database của Firebase
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                //truy vấn dữ liệu từ tất cả các bảng trong products của csdl
                db.collection("products")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
//                           //để kiểm tra xem sản phẩm nào trong detailCarts được đặt hàng và tạo một chuỗi tên các sản phẩm được đặt hàng trong biến productNames.
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        for(DetailCart detailCart : detailCarts) {
                                            if(document.getId().equals(detailCart.getProductId()))
                                                productNames += (String)document.get("name") + " ";
                                        }
                                    }
                                    //tạo một đối tượng order để chứa thông tin về đơn hàng và đẩy nó lên csdl
                                    Map<String, Object> order = new HashMap<>();
                                    order.put("overview", productNames);
                                    order.put("address", edtAddressOrder.getText().toString());
                                    String userId = FirebaseAuth.getInstance().getUid();
                                    order.put("userId", userId);
                                    order.put("totalPrice", Integer.parseInt(totalPrice));
                                    order.put("createAt", Timestamp.now());
                                    //nếu việc đẩy đối tượng order lên cơ sở dữ liệu Firebase thành công, nó sẽ hiển thị một thông báo "Đặt hàng thành công", chuyển sang màn hình MainActivity và kết thúc activity hiện tại thông qua phương thức finish().
                                    db.collection("orders")
                                            .add(order)
                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(EditOrderActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(EditOrderActivity.this, MainActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            }
                        });



            }
        });
    }

    // Tham chiếu các view qua id
    void initUI() {
        rcvCartOrder = findViewById(R.id.rcv_cart_order);
        edtEmailOrder = findViewById(R.id.edt_email_order);
        edtFullNameOrder = findViewById(R.id.edt_fullname_order);
        btnThanhToan = findViewById(R.id.btn_thanh_toan);
        tvSoLuongSp = findViewById(R.id.tv_don_hang_sl);
        edtAddressOrder = findViewById(R.id.edt_address_order);

    }
//truy xuất dữ liệu trong CSDL
    void getCurrentInfo() {
        //khởi tạo csdl
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //lấy ID của người dùng hiện tại từ Firebase Authentication bằng cách sử dụng phương thức getUid() của đối tượng FirebaseAuth.
        String userId = FirebaseAuth.getInstance().getUid();
        //trỏ đến bảng users đến user id get yêu cầu lấy dữ liệu của bảng
        db.collection("users")
                .document(userId)
                .get()
        //xử lý dữ liệu trả về
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            //chuyển đổi dữ liệu trả về từ Firestore thành một đối tượng User bằng cách sử dụng phương thức toObject() của đối tượng DocumentSnapshot.
                            User user = documentSnapshot.toObject(User.class);
                            //đặt văn bản của EditTextedtEmailOrder thành địa chỉ email của người dùng.
                            edtEmailOrder.setText(user.getEmail());
                            //đặt văn bản của EditText edtFullNameOrder thành tên đầy đủ của người dùng bằng cách sử dụng phương thức getFirstName() và getLastName() của đối tượng User.
                            edtFullNameOrder.setText(user.getFirstName() + " " + user.getLastName());
                        }
                    }
                });
    }
}