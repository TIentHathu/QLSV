package com.example.formdangnhap;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class humbergerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_humberger);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
// mac dinh mo man hinh sinh vien

         if (savedInstanceState == null) {
             navigationView.setCheckedItem(R.id.nav_view_students);
         }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_view_students) {
            Toast.makeText(this, "Mở: Xem danh sách sinh viên", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, DanhSachSVActivity.class);
            // startActivity(intent);
        } else if (id == R.id.nav_edit_student) {
            Toast.makeText(this, "Mở: Sửa thông tin sinh viên", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, SuaThongTinSVActivity.class);
            // startActivity(intent);
        } else if (id == R.id.nav_assess_points) {
            Toast.makeText(this, "Mở: Xét điểm rèn luyện", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, XetDiemRLActivity.class);
            // startActivity(intent);
        } else if (id == R.id.nav_stats_points) {
            Toast.makeText(this, "Mở: Thống kê điểm rèn luyện", Toast.LENGTH_SHORT).show();
            // Intent intent = new Intent(this, ThongKeDiemActivity.class);
            // startActivity(intent);
        } else if (id == R.id.nav_logout) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


}