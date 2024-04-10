package com.etrungpro.appshoppet.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.etrungpro.appshoppet.ui.FeedFragment;
import com.etrungpro.appshoppet.ui.HomeFragment;
import com.etrungpro.appshoppet.ui.OrderFragment;
import com.etrungpro.appshoppet.ui.UserFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    //constructor
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    //Khi tạo một ViewPagerAdapter với constructor này,
    // nó sẽ kết nối trực tiếp với FragmentActivity đó.
    public ViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }
    //được sử dụng trong một Fragment để chứa các Fragment khác.
    public ViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        //Lifecycle để adapter tự động cập nhật khi một Fragment được tạo hoặc xóa.
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 :
                return new HomeFragment();
            case 1 :
                return new FeedFragment();
            case 2 :
                return new OrderFragment();
            case 3 :
                return new UserFragment();
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
    //trả về slg phần tử cần hiển thị trong ViewPager2, là 4 fragment
}