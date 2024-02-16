package com.yashodha.imagebot;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public ImageView foodImageView;
        public TextView feeTextView;
        public Button addButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.txtTitle);
            foodImageView = itemView.findViewById(R.id.imgFood);
            feeTextView = itemView.findViewById(R.id.fee);
            addButton = itemView.findViewById(R.id.btnAdd);
        }
    }


