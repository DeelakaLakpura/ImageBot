package com.yashodha.imagebot;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yashodha.Model.FoodItems;
import com.yashodha.imagebot.User.LoginActivity;
import com.yashodha.imagebot.User.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);



        ImageView img = findViewById(R.id.imageView3);
        RecyclerView recyclerViewBurgers = findViewById(R.id.rv_burgers);
        RecyclerView recyclerViewPizza = findViewById(R.id.rv_pizza);
        RecyclerView recyclerViewsoft = findViewById(R.id.rv_drinks);
        ImageView frogot = findViewById(R.id.imageView3);
        TextView profile = findViewById(R.id.pro_name);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            profile.setText("HI! "+email);

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }

        // Create instances of FoodItems
        FoodItems food1 = new FoodItems("Hamburgers\n", R.drawable.b, 510.99);
        FoodItems food2 = new FoodItems("Stacked Burgers", R.drawable.st, 410.49);
        FoodItems food3 = new FoodItems("Green Chile Cheeseburgers", R.drawable.g, 680.49);
        FoodItems food4 = new FoodItems("Neapolitan Pizza", R.drawable.p1, 1280.49);
        FoodItems food5 = new FoodItems("Chicago Pizza\n", R.drawable.p2, 2280.49);
        FoodItems food6 = new FoodItems("Chicken Pizza\n", R.drawable.p3, 1680.49);
        FoodItems food7 = new FoodItems("Coca-Cola\n", R.drawable.coc, 280.49);
        FoodItems food8 = new FoodItems("Sprite\n", R.drawable.sprite, 280.49);
        FoodItems food9 = new FoodItems("Fanta\n", R.drawable.fanta, 280.49);

        frogot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent i = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(i);
            }
        });

        // Create a list to hold your FoodItem objects
        List<FoodItems> burgerList = new ArrayList<>();
        burgerList.add(food1);
        burgerList.add(food2);
        burgerList.add(food3);

        List<FoodItems> pizzaList = new ArrayList<>();
        pizzaList.add(food4);
        pizzaList.add(food5);
        pizzaList.add(food6);

        List<FoodItems> softDrinks = new ArrayList<>();
        softDrinks.add(food7);
        softDrinks.add(food8);
        softDrinks.add(food9);

        // Create an instance of your custom adapter and pass the list of FoodItem objects
        CustomAdapter adapterBurgers = new CustomAdapter(this, burgerList);
        CustomAdapter adapterPizza = new CustomAdapter(this, pizzaList);
        CustomAdapter adapterDrinks = new CustomAdapter(this, softDrinks);

        // Set a layout manager for your RecyclerView
        recyclerViewBurgers.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewPizza.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerViewsoft.setLayoutManager(new GridLayoutManager(this, 3));

        // Set the adapter for your RecyclerView
        recyclerViewBurgers.setAdapter(adapterBurgers);
        recyclerViewPizza.setAdapter(adapterPizza);
        recyclerViewsoft.setAdapter(adapterDrinks);
    }
}
