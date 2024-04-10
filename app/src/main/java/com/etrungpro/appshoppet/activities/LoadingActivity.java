package com.etrungpro.appshoppet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.etrungpro.appshoppet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadingActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);//thiết lập giao diện cho activity_loading
//
        //cho phép tạm dừng hoạt động hiện tại và chuyển sang hoạt động (activity) khác sau khoảng thời gian nhất định (ở đây là 2 giây).
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextActivity();
            }
        }, 2000);//Phương thức nextActivity() sẽ được gọi sau khi khoảng thời gian 2 giây kể từ khi phương thức postDelayed() được gọi.
    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();//lấy thông tin người dùng hiện tại đã đăng nhập
        Intent intent; //đối tượng Intent được khởi tạo để chứa thông tin về màn hình chính tiếp theo của ứng dụng.
        if(user == null) {
            //Nếu người dùng chưa đăng nhập, đối tượng Intent sẽ được khởi tạo để chuyển đến LoginActivity (màn hình đăng nhập).
            intent = new Intent(this, LoginActivity.class);
        }
        else {
            //nếu người dùng đã đăng nhập, đối tượng Intent sẽ được khởi tạo để chuyển đến MainActivity (màn hình chính).
            intent = new Intent(this, MainActivity.class);
        }
        startActivity(intent);
    }
}