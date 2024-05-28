package com.example.toolbardos;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        toolbar1 = findViewById(R.id.toolbarNew);
        setSupportActionBar(toolbar1);

        getSupportFragmentManager().beginTransaction().add(R.id.mnfragment1, new fragment1()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.mnfragment2, new fragment2()).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.mnfragment3, new fragment3()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.mnfragment1)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.mnfragment1, new fragment1()).commit();
            return true;
        }
        if(item.getItemId() == R.id.mnfragment2)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.mnfragment2, new fragment1()).commit();
            return true;
        }
        if(item.getItemId() == R.id.mnfragment3)
        {
            getSupportFragmentManager().beginTransaction().replace(R.id.mnfragment3, new fragment1()).commit();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}