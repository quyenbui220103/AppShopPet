package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.models.Slide;

import java.util.List;

public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.PhotoViewHolder> {

    private Context mContext;
    private List<Slide> mListPhoto;

    public SlideAdapter(Context mContext, List<Slide> mListPhoto) {
        this.mContext = mContext; //Tham chiếu đến context của ứng dụng.
        this.mListPhoto = mListPhoto; //Một danh sách (List) các đối tượng Slide.
    }

    @NonNull
    @Override
    //đtg PhotoViewHolder
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //parent: là một đối tượng ViewGroup (lớp cha) chứa các item trong danh sách.
        //viewType: là loại item (kiểu view) được hiển thị trong danh sách.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slide_item, parent, false);
        //LayoutInflater để tạo ra một đối tượng View từ layout slide_item
        //truyền parent vào để có thể thiết lập kích thước và các thuộc tính khác cho view
        return new PhotoViewHolder(view);
        //tạo ra một đối tượng PhotoViewHolder mới, truyền vào đối tượng View vừa tạo, và trả về đối tượng này.
    }

    //hiển thị dữ liệu (hình ảnh) lên View của một item trong danh sách.
    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        //holder: Đối tượng PhotoViewHolder để hiển thị dữ liệu.
        //position: Vị trí (index) của item trong danh sách.
        Slide slide = mListPhoto.get(position); //truyền đtg của  item tại vị trí position trong danh sách.
        if (slide == null) {//null thì k lamgi
            return;
        }

        Glide.with(mContext).load(slide.getResourceId()).into(holder.slidePhoto);
        //thư viện Glide để tải hình ảnh từ tài nguyên được chỉ định bởi slide.getResourceId()
        // và hiển thị lên ImageView trong đối tượng PhotoViewHolder (holder.slidePhoto).
    }

    // Lấy số lượng item trong danh sách.
    @Override
    public int getItemCount() {
        if(mListPhoto != null) { // không null, trả về slg
            return mListPhoto.size();
        }
        return 0;//null trả về 0
    }

    public static class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView slidePhoto;
        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);//Constructor sử dụng để ánh xạ các thành phần View của item vào các biến thành viên của đối tượng ViewHolder.

            slidePhoto = itemView.findViewById(R.id.slide_photo);
            //findViewById để tìm ImageView slidePhoto trong itemView và gán nó cho biến thành viên của đối tượng ViewHolder.
        }
    }

}