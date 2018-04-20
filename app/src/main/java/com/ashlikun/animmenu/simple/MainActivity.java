package com.ashlikun.animmenu.simple;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ashlikun.animmenu.AnimMenu;
import com.ashlikun.animmenu.OnMenuItemClickListener;

public class MainActivity extends AppCompatActivity {
    AnimMenu animMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        animMenu = findViewById(R.id.animMenu);
        animMenu.addView(animMenu.getDefaultItem()
                .strokeWidth(3)
                .strokeColor(Color.BLACK)
                .iconId(R.drawable.add));
        animMenu.addView(animMenu.getDefaultItem()
                .strokeWidth(3)
                .strokeColor(Color.BLACK)
                .badge("1")
                .iconId(R.drawable.like));

        animMenu.setItemClickListener(new OnMenuItemClickListener() {


            @Override
            public void onItemClick(int index, String tag) {

            }
        });
    }

    public void onClick(View view) {
        if (!animMenu.isOpen()) {
            animMenu.openMenu();
        } else {
            animMenu.closeMenu();
        }
    }
}