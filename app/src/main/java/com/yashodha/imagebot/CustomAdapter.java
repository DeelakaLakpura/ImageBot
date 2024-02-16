package com.yashodha.imagebot;
import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.yashodha.Model.FoodItems;
import java.util.List;


public class CustomAdapter extends RecyclerView.Adapter<ViewHolder> {
    private Context context;
    private List<FoodItems> dataList;

    public CustomAdapter(Context context, List<FoodItems> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.food_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItems data = dataList.get(position);
        holder.titleTextView.setText(data.getTitle());
        holder.foodImageView.setImageResource(data.getImageResource());
        holder.feeTextView.setText((data.getFee()) + " LKR");


        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, FoodViewActivity.class);
                intent.putExtra("foodName", data.getTitle());
                intent.putExtra("imageResource", data.getImageResource());
                intent.putExtra("foodPrice", data.getFee());


                context.startActivity(intent);
            }

        });

        // Set other data to other views
        // For example:
        // holder.foodImageView.setImageResource(data.getImageResource());
        // holder.feeTextView.setText(data.getFee());
        // Set click listeners, etc.
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
