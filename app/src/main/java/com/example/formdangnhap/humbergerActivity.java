package com.example.formdangnhap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class humbergerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private int teacherId = -1;
    private ImageButton btnBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humberger);

        teacherId = getIntent().getIntExtra("User_ID", -1);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        btnBackToHome = findViewById(R.id.btnBackToHome);
        btnBackToHome.setOnClickListener(v -> {
            Intent intent = new Intent(humbergerActivity.this, trangchu.class);
            intent.putExtra("User_ID", teacherId);
            startActivity(intent);
            finish();
        });

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            replaceFragment(DanhSachSVFragment.newInstance(teacherId));
            navigationView.setCheckedItem(R.id.nav_view_students);
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_view_students) {
            replaceFragment(DanhSachSVFragment.newInstance(teacherId));
        } else if (id == R.id.nav_edit_student) {
            replaceFragment(SuaThongTinSVFragment.newInstance(teacherId));
        } else if (id == R.id.nav_assess_points) {
            replaceFragment(DuyetDiemFragment.newInstance(teacherId));
        } else if (id == R.id.nav_stats_points) {
            replaceFragment(ThongKeFragment.newInstance(teacherId));
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
