package com.yashodha.imagebot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yashodha.Model.FoodItems;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageView img = findViewById(R.id.imageView3);
        RecyclerView recyclerView = findViewById(R.id.rv_food);


        // Create instances of FoodItems
        FoodItems food1 = new FoodItems("Pizza", R.drawable.pizza, 1110.99);
        FoodItems food2 = new FoodItems("Burger", R.drawable.burger, 410.49);
        FoodItems food3 = new FoodItems("Sandwich", R.drawable.sand, 280.49);
        FoodItems food4 = new FoodItems("Hot dog.", R.drawable.pizza, 300.00);
        FoodItems food5 = new FoodItems("Burger", R.drawable.pizza, 8.49);
        FoodItems food6 = new FoodItems("Burger", R.drawable.pizza, 8.49);
        FoodItems food7 = new FoodItems("Burger", R.drawable.pizza, 8.49);
        FoodItems food8 = new FoodItems("Burger", R.drawable.pizza, 8.49);
        FoodItems food9= new FoodItems("Burger", R.drawable.pizza, 8.49);
        FoodItems food10= new FoodItems("Burger", R.drawable.pizza, 8.49);

        // Create a list to hold your FoodItem objects
        List<FoodItems> foodItemList = new ArrayList<>();
        foodItemList.add(food1);
        foodItemList.add(food2);
        foodItemList.add(food3);
        foodItemList.add(food4);
        foodItemList.add(food5);
        foodItemList.add(food6);
        foodItemList.add(food7);
        foodItemList.add(food8);
        foodItemList.add(food9);
        foodItemList.add(food10);
        // Add more items as needed

        // Create an instance of your custom adapter and pass the list of FoodItem objects
        CustomAdapter adapter = new CustomAdapter(this, foodItemList);

        // Set a layout manager for your RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        // Set the adapter for your RecyclerView
        recyclerView.setAdapter(adapter);
    }
}
