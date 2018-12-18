package com.ashlikun.animmenu.simple;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.ashlikun.animmenu.AnimMenu;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    AnimMenu animMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animMenu = findViewById(R.id.animMenu);
        animMenu.setAutoOpen(true);
        animMenu.setClickable(true);
        animMenu.addView(animMenu.getDefaultItem()
                .strokeWidth(3)
                .strokeColor(Color.BLACK)
                .iconId(R.drawable.add));

    }

    public void onClick(View view) {
        if (animMenu.isOpen()) {
            animMenu.closeMenu();
        } else {
            animMenu.openMenu();
        }
    }
}
