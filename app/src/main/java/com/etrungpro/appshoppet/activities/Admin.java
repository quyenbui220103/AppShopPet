package com.etrungpro.appshoppet.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.etrungpro.appshoppet.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.etrungpro.appshoppet.databinding.ActivityAdminBinding;

public class Admin extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityAdminBinding binding;

    @Override
    //gọi phương thức onCreate thực hiện các thao tác khởi tạo và thiết lập ban đầu cho Activity, chẳng hạn như thiết lập layout, các thành phần giao diện như Button, TextView,... hay các đối tượng quản lý dữ liệu và xử lý logic cho Activity.
    protected void onCreate(Bundle savedInstanceState) {//Khi Activity được khởi tạo lại, Bundle savedInstanceState sẽ được truyền lại vào phương thức onCreate() để bạn có thể phục hồi trạng thái của Activity.
        super.onCreate(savedInstanceState);

        binding = ActivityAdminBinding.inflate(getLayoutInflater()); //chuyển đổi một file layout XML thành một đối tượng View
        setContentView(binding.getRoot());//setContentView(binding.getRoot()) được sử dụng để hiển thị nội dung của đối tượng View được tạo ra từ tệp layout XML lên màn hình.

        setSupportActionBar(binding.appBarAdmin.toolbar); //thiết lập toolbar trong layout làm ActionBar của activity, để thêm các nút chức năng
        binding.appBarAdmin.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Thay thế bằng hành động của riêng bạn", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;//Ánh xạ các phần tử của giao diện người dùng từ tệp layout xml vào mã java của activity
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow) // Chuyển từng ID menu dưới dạng một tập hợp Id vì mỗi ID
                .setOpenableLayout(drawer)  // menu nên được coi là điểm đến cấp cao nhất
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin2);
        //NavController để điều hướng các mục điều hướng trong NavigationView.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//Sử dụng NavigationUI để thiết lập ActionBar và NavigationView để hiển thị và cập nhật các mục điều hướng được chọn khi người dùng điều hướng.
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Thổi phồng menu ; thao tác này sẽ thêm các mục vào thanh hành động nếu có.
        getMenuInflater().inflate(R.menu.admin, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_admin2);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}