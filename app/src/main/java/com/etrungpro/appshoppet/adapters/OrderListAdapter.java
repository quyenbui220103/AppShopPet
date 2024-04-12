package com.etrungpro.appshoppet.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.etrungpro.appshoppet.R;
import com.etrungpro.appshoppet.activities.DetailOrderActivity;
import com.etrungpro.appshoppet.models.Order;

import java.util.ArrayList;
import java.util.Date;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {

    Context mContext;
    ArrayList<Order> mList;

    // Constructor để khởi tạo adapter với context
    public OrderListAdapter(Context mContext) {
        this.mContext = mContext;
    }

    // Phương thức này được sử dụng để cập nhật dữ liệu danh sách đơn hàng
    public void setList(ArrayList<Order> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    // Phương thức này tạo ViewHolder cho mỗi item
    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tạo một View từ layout item_order.xml
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderListViewHolder(v);
    }

    // Phương thức này được gọi để hiển thị dữ liệu tại vị trí đã chỉ định từ danh sách đơn hàng
    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int position) {
        // Lấy ra đối tượng Order tại vị trí position trong danh sách đơn hàng
        Order order = mList.get(position);

        // Đặt các giá trị của đơn hàng lên các TextView
        holder.tvOrderId.setText("ID: " + order.getId());
        holder.tvOrderOverview.setText("Overview: " + order.getOverview());
        holder.tvOrderAddress.setText("Address: " + order.getAddress());
        holder.tvOrderTotalPrice.setText("Tổng tiền: " + String.valueOf(order.getTotalPrice()) + "đ");

        // Định dạng và đặt ngày tạo đơn hàng lên TextView
        Date date = order.getCreateAt().toDate();
        holder.tvOrderDate.setText("Date: " + date.toString());

        // Xử lý sự kiện khi người dùng nhấn vào item đơn hàng
        holder.layoutOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở DetailOrderActivity khi nhấn vào item
                Intent intent = new Intent(mContext, DetailOrderActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    // Phương thức này trả về số lượng item trong danh sách đơn hàng
    @Override
    public int getItemCount() {
        if(mList != null) {
            return mList.size();
        }
        return 0;
    }

    // Class ViewHolder để giữ các thành phần giao diện cho mỗi item trong RecyclerView
    public class OrderListViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderId;
        TextView tvOrderOverview;
        TextView tvOrderAddress;
        TextView tvOrderDate;
        TextView tvOrderTotalPrice;
        LinearLayout layoutOrder;

        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các thành phần giao diện từ layout item_order.xml
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderOverview = itemView.findViewById(R.id.tv_order_overview);
            tvOrderAddress = itemView.findViewById(R.id.tv_order_address);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderTotalPrice = itemView.findViewById(R.id.tv_order_total_price);
            layoutOrder = itemView.findViewById(R.id.layout_order_item);
        }
    }
}
