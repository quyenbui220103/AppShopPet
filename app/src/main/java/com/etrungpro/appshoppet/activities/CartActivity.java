package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.adapters.CartAdapter;
import com.etrungpro.appshoppet.models.DetailCart;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartActivity extends AppCompatActivity {

    String productId;
    int productPrice;
    int productQuality;
    //
    int existPIndex = -1;
    //danh sách sản phẩm đã chọn
    ArrayList<DetailCart> products;
    ArrayList<String> selectList = new ArrayList<>();
    CartAdapter cartAdapter;
    RecyclerView rcvCart;
    TextView tvTongTien;
    TextView cartTitle;
    Button btnThanhToan;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //thiết lập giao diện cho activity
        setContentView(R.layout.activity_cart);
        //liên kết các thành phần giao diện
        initUI();
        //Lấy dữ liệu từ Intent được gọi để chuyển đến activity này và lưu trữ chúng vào các biến
        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");//lấy 1 extra kiểu chuyển từ dối tượng inten và lưu vào 1 biến
        if(intent.getStringExtra("productPrice") != null) {
            productPrice = Integer.parseInt(intent.getStringExtra("productPrice"));// nếu tồn tại giá thì chuyển sang số nguyên
        }
        if(intent.getStringExtra("productQuality") != null) {
            productQuality = Integer.parseInt(intent.getStringExtra("productQuality"));//nếu tồn tại thì chuyyeenr sang số nguyên
        }
        //khởi tạo cartAdapter
        cartAdapter = new CartAdapter(CartActivity.this, new CartAdapter.ICartEvent() {
            @Override
            public void add(String productId) {//thêm sp vào giỏ hàng
                selectList.add(productId);
                calTotalPrice();
            }

            @Override
            public void remove(String productId) {
                selectList.remove(productId);//cập nhật giá lại giỏ hàng
                calTotalPrice();
            }

            @Override
            public void updateTotalPrice(DetailCart detailCart, int position) {// chỉnh sửa
                products.set(position, detailCart);
                calTotalPrice();
            }

            @Override
            public void delete(int position) {//xóa
                products.remove(position);
                cartAdapter.setList(products);
                cartTitle.setText("Giỏ hàng (" + String.valueOf(products.size()) + ")");// hiện thị số lượng sản phẩm sau khi xóa
                calTotalPrice();
            }

        });
        //khởi tạo danh sách rỗng của đối tượng DetailCart để lưu trữ các sản phẩm trong giỏ hàng.
        products = new ArrayList<>();
        //quản lý và hiển thị các view trong rcvCart(recycler)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(// sử dụng linear  layout để quản  cách hiển thị các hình ảnh trong giỏ hàng
                CartActivity.this, LinearLayoutManager.VERTICAL, false);// vertical xắp sếp thèo chiều dọc và false là ko đao ngược item
        rcvCart.setLayoutManager(linearLayoutManager);
        //ẩn rcvCart
        rcvCart.setVisibility(View.GONE);// khi không có dữ liệu trong giỏ hàng thì ẩn các vùng chứa layout item
        rcvCart.setAdapter(cartAdapter);//dùng apdapter để liên kết dữ liệu vào giao diện
        // lấy danh sách sản phẩm trong giỏ hàng và hiển thị chúng lên rcvCart.
        getCartProductList();
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(products.size() == 0) {
                    Toast.makeText(CartActivity.this, "Không có sản phẩm nào cả", Toast.LENGTH_SHORT).show();
                    return ;
                }
                Intent intent = new Intent(CartActivity.this, EditOrderActivity.class);
                //đóng gói dữ liệu để chuyển đến EditOrderActivity thông qua Intent
                Bundle bundle = new Bundle();
                //khởi tạo một ArrayList rỗng để lưu trữ danh sách các sản phẩm chi tiết đã chọn để thanh toán
                ArrayList<DetailCart> detailCarts = new ArrayList<>();
                for(DetailCart detailCart : products) {
                    for(String id : selectList) {
                        if(detailCart.getId().equals(id)) {
                            detailCarts.add(detailCart);
                            selectList.remove(id);
                            break;
                        }
                    }
                }
                //đóng gói dữ liệu truyền qua editOrder
                //putSerializable() để đưa danh sách detailCarts của các sản phẩm trong giỏ hàng sang dưới dạng một đối tượng Serializable
                bundle.putSerializable("detailCarts",detailCarts);
                // để đưa tổng giá trị của giỏ hàng tvTongTien qua Activity mới.
                bundle.putString("totalPrice", tvTongTien.getText().toString());//đưa giá trị của text view giá sản phẩm vào trong bundle
                intent.putExtras(bundle);//đẩu bundle vào intent
                startActivity(intent);//
            }
        });


    }

    private void initUI() {
        rcvCart = findViewById(R.id.rcv_cart);
        tvTongTien = findViewById(R.id.tv_tongtien);
        btnThanhToan = findViewById(R.id.btn_thanh_toan);
        cartTitle = findViewById(R.id.cart_title);
    }

    //lấy danh sách sản phẩm trong giỏ hàng của người dùng.
    private void getCartProductList() {
        //lấy thông tin người dùng hiện tại
        FirebaseAuth user = FirebaseAuth.getInstance();
        //lấy ra user id của người dùng hiện tại
        String userId = user.getUid();
        //truy vấn đến cart trên firebase và chỉ lấy ra những sản phẩm có userid bằng với userId đã lấy ở trên
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("carts")
                .whereEqualTo("userid", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {// kiểm tra truy vấn thành công hay chưa
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {//duyệt qua các tài liệu trong firebase
                                String cartId = queryDocumentSnapshot.getId();//lấy id của tài liệu để gán vào giỏ hàng
                                //truy vấn và so sánh để lấy detailCarts có trườn cartId =cảId
                                db.collection("detailCarts")
                                    .whereEqualTo("cartId", cartId)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            // nếu truy vấn thành công
                                            if(task.isSuccessful()) {

                                                int i = 0;
                                                for(QueryDocumentSnapshot document : task.getResult()) {
                                                    DetailCart detailCart = document.toObject(DetailCart.class);// chuyển đổi từ dữ liệu trên firebase thành đối tượng
                                                    detailCart.setId(document.getId());//cho id của tài liệu trên firebase vào id của đội tượng
                                                    products.add(detailCart);
                                                    //kiểm tra xem sảm phẩm đã tồn tại trong giỏ hàng hay chưa. Nếu đã tồn tại, thì chúng ta có thể cập nhật số lượng sản phẩm bằng cách thêm số lượng mới vào số lượng cũ của sản phẩm tương ứng. Nếu không tồn tại, chúng ta sẽ thêm sản phẩm mới vào giỏ hàng.
                                                    if(productId != null && !productId.isEmpty() && detailCart.getProductId().equals(productId)) {
                                                        existPIndex = i;//mục sa phẩm đã tồn tại trong danh sách
                                                    }
                                                    i+=1;
                                                }
                                                //khi sản phẩm được thêm vào giỏ hàng, danh sách giỏ hàng sẽ được hiển thị trên màn hình của người dùng.
                                                rcvCart.setVisibility(View.VISIBLE);// hiển thị
                                                //số lượng giỏ hàng
                                                if(productId == null || productId.isEmpty()) {
                                                    cartAdapter.setList(products);// gọi dến adapter danh sách sản phẩm
                                                    cartTitle.setText("Giỏ hàng (" + String.valueOf(products.size()) + ")");// lấy ra danh sách trong giở hàng kèm vs số lượng
                                                    calTotalPrice();//gọi hàm tính tổng tiền
                                                    return ;
                                                }

                                                Map<String, Object> newDetailProduct = new HashMap<>();// tạo một kiểu map có key là string value là object
                                                //ktra xem có đối tượng nào có trong giỏ hàng không với existPIndex = -1 là không có trong giỏ hàng
                                                if(existPIndex == -1) {
                                                    //add chi tiết sản phẩm vào chi tiết giỏ hàng
                                                    newDetailProduct.put("cartId", cartId);
                                                    newDetailProduct.put("productId", productId);
                                                    newDetailProduct.put("productPrice", productPrice);
                                                    newDetailProduct.put("quatity",productQuality);
                                                    // lưu trũ dữ liệu vào firebase
                                                    db.collection("detailCarts")
                                                    .add(newDetailProduct)
                                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                //onSuccess chúng ta lấy ID của document mới được thêm vào Firestore thông qua đối tượng documentReference.getId().
                                                                @Override
                                                                public void onSuccess(DocumentReference documentReference) {
                                                                    String id = documentReference.getId();
                                                                    products.add(new DetailCart(id,cartId, productId, productQuality, productPrice));
                                                                    cartAdapter.setList(products);
                                                                    //cập nhật số lượng sản phẩm của giỏ hàng
                                                                    cartTitle.setText("Giỏ hàng (" + String.valueOf(products.size()) + ")");
                                                                    //tính giá giỏ hàng
                                                                    calTotalPrice();
                                                                }
                                                            });
                                                }
                                                else {
                                                    Log.e("status", "go to 2.2");// có thể ghi lại thông tin quan trọng or gỡ lỗi trong quá trình thực hiện
                                                    //lấy ra đối tượng
                                                    DetailCart existDetailCart = products.get(existPIndex);
                                                    //lấy số lượng sản phẩm hiện tại trong giỏ hàng của sản phẩm đã tồn tại trước đó.
                                                    int quality = existDetailCart.getQuatity();
                                                    //cập nhật lại số lượng sản phẩm của sản phẩm đã tồn tại trong giỏ hàng bằng cách thêm số lượng sản phẩm mới vào số lượng sản phẩm đã có.
                                                    existDetailCart.setQuatity(quality + productQuality);
                                                    //cập nhật lại thông tin sản phẩm đã tồn tại trong danh sách sản phẩm trong giỏ hàng bằng cách ghi đè sản phẩm cũ bằng sản phẩm mới đã được cập nhật số lượng.
                                                     products.set(existPIndex, existDetailCart);
                                                     newDetailProduct.put("cartId", cartId);
                                                     newDetailProduct.put("productId", productId);
                                                     newDetailProduct.put("productPrice", productPrice);
                                                     newDetailProduct.put("quatity",existDetailCart.getQuatity());
                                                     db.collection("detailCarts")
                                                          .document(existDetailCart.getId()).set(newDetailProduct);
                                                     //cập nhật lại danh sách sản phẩm trong giỏ hàng trên giao diện.
                                                    cartAdapter.setList(products);
                                                    //cập nhật lại số lượng sản phẩm trong giỏ hàng trên tiêu đề của trang.
                                                    cartTitle.setText("Giỏ hàng (" + String.valueOf(products.size()) + ")");
                                                    //tính lại tổng tiền sản phẩm
                                                    calTotalPrice();
                                                }

                                            }
                                        }});
                            }
                        }
                    }
                });

    }
    //hàm tính tổng tiền
    private void calTotalPrice() {
        int totalPrice = 0;// khởi tạo biến để lưu giá của tất cả sản pẩm
        int tChoiotalPricece = 0;// khởi tạo biên để lưu giá của sản phẩm đc chọn
        for(DetailCart detailCart : products) {
            int val = detailCart.getQuatity() * detailCart.getProductPrice();
            totalPrice += val;
            //Nếu sản phẩm có trong danh sách sản phẩm đã chọn (selectList), thì cộng giá tiền của sản phẩm đó vào totalPriceChoice.
            if(selectList.contains(detailCart.getId())) {
                totalPrice += val;
            }
        }
        //Nếu danh sách sản phẩm đã chọn là rỗng thì hiển thị tổng giá tiền của tất cả các sản phẩm. Nếu danh sách sản phẩm đã chọn không rỗng thì hiển thị tổng giá tiền của các sản phẩm đã chọn.
        if(selectList.size() == 0) {
            tvTongTien.setText(String.valueOf(totalPrice));
        } else {
            tvTongTien.setText(String.valueOf(totalPrice));// Cập nhật giá tiền của sản phẩm thông qua kích thước list
        }
    }
}