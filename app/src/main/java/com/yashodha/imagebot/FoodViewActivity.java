package com.yashodha.imagebot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class FoodViewActivity extends AppCompatActivity {

    private TextView foodNameTextView,food_price;
    private ImageView foodImageView;

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_view);
        Intent intent = getIntent();
        foodImageView = findViewById(R.id.imgFoodDetails);
        foodNameTextView =findViewById(R.id.txtFoodName);
        food_price =findViewById(R.id.txtFoodPrice);
        btn = findViewById(R.id.btn_check);

        // Retrieve data from intent
        String foodName = intent.getStringExtra("foodName");
        double foodPrice = intent.getIntExtra("foodPrice",1);
        int imageResource = intent.getIntExtra("imageResource", 1);
        foodNameTextView.setText(foodName);
        food_price.setText(foodPrice+"");
        foodImageView.setImageResource(imageResource);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodViewActivity.this, MainActivity.class);
                intent.putExtra("foodimage", imageResource);
                startActivity(intent);
            }
        });



    }
}