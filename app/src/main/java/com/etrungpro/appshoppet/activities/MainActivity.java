package com.etrungpro.appshoppet.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.adapters.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager2;
    BottomNavigationView bottomNav;
    SearchView searchView;
    ImageView btnMess;
    ImageView btnCart;
    TextView tvDontSeen;
    @Override
    protected void onCreate(Bundle savedInstanceState) { //onCreate là 1 phương thức được gọi khi activity được khởi tạo, sd để thiết lập giao diện cho activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//thiết lập giao diện cho activity

        viewPager2 = findViewById(R.id.view_pager);
        bottomNav = findViewById(R.id.bottom_nav);
        searchView = findViewById(R.id.search_view);
        btnMess = findViewById(R.id.mess_btn);
        btnCart = findViewById(R.id.cart_btn);
        tvDontSeen = findViewById(R.id.tv_dont_seen);

        tvDontSeen.setVisibility(View.GONE);//phương thức setVisibility(View.GONE) làm cho đối tượng TextView tvDontSeen đc đặt là Gone-View sẽ không hiển thị trên màn hình khi activity được hiển thị
        setUpViewPager(); //thiết lập ViewPager-hiển thị các trang (phần tử) dọc theo 1 trục

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() { //NavigationBarView Là thanh điều hướng
            //thiết lập một sự kiện lắng nghe (listener) cho sự kiện chọn một mục trong thanh điều hướng NavigationBar
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        viewPager2.setCurrentItem(0); //setCurrentItem để chuyển đổi giữa các fragment tương ứng của mỗi mục
                        break;
                    case R.id.action_feed:
                        viewPager2.setCurrentItem(1);
                        break;case R.id.action_order:
                        viewPager2.setCurrentItem(2);
                        break;
                    case R.id.action_user:
                        viewPager2.setCurrentItem(3);
                        break;
                }
                return false; //trả về giá trị false để đảm bảo rằng sự kiện chọn mục xử lý đúng đắn
            }
        });

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() { //thiết lập một sự kiện lắng nghe (listener) cho hành động tìm kiếm của người dùng trên thanh tìm kiếm
            @Override
            public boolean onQueryTextSubmit(String query) { //phương thức đc gọi khi user nhấn nút tìm kiếm
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("searchValue", query); //giá trị tìm kiếm đc đưa vào intent dưới dạng key-value
                startActivity(intent); // chuyển hướng người dùng đến màn hình tìm kiếm vs kq tìm kiếm đc hiển thị
                return false; //true khi sự kiện tìm kiếm đã được xử lý và không thực hiện tìm kiếm nữa
            }

            @Override
            public boolean onQueryTextChange(String newText) { //được gọi khi user thay đổi nội dung trong thanh search
                return false; //0 có hđ nào được thực hiện khi user thay đổi nội dung trên thanh tìm kiếm
            }
        });

        btnMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //phương thức OnClick ,
                // trc khi chuyển sang màn hình mess, nội dung thông báo chưa xem của user đc đặt ẩn đi = cách đặt thuộc tính Visibility = GONE
                tvDontSeen.setVisibility(View.GONE);
                Intent intent = new Intent(MainActivity.this, ConversationActivity.class);
                //Intent được tạo ra để mở màn hình mess
                startActivity(intent); //chuyển hướng người dùng sang màn hình mess
            }
        });

        btnCart.setOnClickListener(new View.OnClickListener() { //nút giỏ hàng
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

    }

    private void handleCatchNewMessage() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {//khi có sự thay đổi trong dl phthuc onEvent sẽ đc gọi, code sẽ ktra nếu có ít nhất 1 sự thay đổi thì tvDontSeen sẽ hiển thị, thông báo cho user có tin nhắn mới chưa đọc
                if(value.getDocumentChanges().size() > 0) {
                    //trả về dsach các thay đổi trong dl
                    tvDontSeen.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //thiết lập ViewPager2
    private void setUpViewPager() {
        // tạo một đối tượng ViewPagerAdapter và đặt nó làm adapter cho ViewPager2 bằng phương thức setAdapter()
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) { //listen sự kiện khi user chuyển đổi giữa các fragment
                super.onPageSelected(position); //xác định fragment hiện tại đang hiển thị
                switch (position) {
                    case 0:
                        bottomNav.getMenu().findItem(R.id.action_home).setChecked(true);
                        break;
                    case 1:
                        bottomNav.getMenu().findItem(R.id.action_feed).setChecked(true);
                        break;
                    case 2:
                        bottomNav.getMenu().findItem(R.id.action_order).setChecked(true);
                        break;
                    case 3:
                        bottomNav.getMenu().findItem(R.id.action_user).setChecked(true);
                        break;

                }
            }
        });
    }
}