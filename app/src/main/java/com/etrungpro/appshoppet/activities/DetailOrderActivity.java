package com.etrungpro.appshoppet.activities;

import androidx.appcompat.app.AppCompatActivity;
//Class Bundle được sử dụng để đóng gói và truyền dữ liệu giữa các thành phần của ứng dụng Android
import android.os.Bundle;

import com.etrungpro.appshoppet.R;

public class DetailOrderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_order);
    }
}
//hiển thị thông tin chi tiết đơn hàng đã đặt trong phần order