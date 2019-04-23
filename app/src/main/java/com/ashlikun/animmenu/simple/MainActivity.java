package com.ashlikun.animmenu.simple;

import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.ashlikun.animmenu.AnimMenu;

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
                .strokeWidth(0)
                .strokeColor(Color.BLACK)
                .iconId(R.mipmap.ic_launcher_round));

    }

    public void onClick(View view) {
        if (animMenu.isOpen()) {
            animMenu.closeMenu();
        } else {
            animMenu.openMenu();
        }
    }
}
