package com.example.formdangnhap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class trangchu extends AppCompatActivity {
    ImageButton btnHome, btnSetting, btnuser;
    MaterialCardView cardViewQLSV, cardViewLicsu;
    MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(this);
    int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu);

        // Nhận ID người dùng từ Intent
        currentUserId = getIntent().getIntExtra("User_ID", -1);

        btnHome = findViewById(R.id.btnHome);
        btnuser = findViewById(R.id.btncaNhan);
        btnSetting = findViewById(R.id.btnsetting);
        cardViewQLSV = findViewById(R.id.cardViewQLSV);
        cardViewLicsu = findViewById(R.id.cardViewHistory);

        // Mở nhật ký hoạt động
        cardViewLicsu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHistoryDialog();
            }
        });

        // Mở dialog cá nhân
        btnuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(trangchu.this);
                View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_profile, null);
                builder.setView(dialogView);

                Button btnThoat = dialogView.findViewById(R.id.btnSignout);
                TextView txtten = dialogView.findViewById(R.id.txtten);
                TextView txtemail = dialogView.findViewById(R.id.txtemail);
                TextView txtDiaChi = dialogView.findViewById(R.id.diachicongtac);

                // Lấy thông tin dựa trên currentUserId
                Cursor cursor = myDatabaseHelper.getTenGiaovien(currentUserId);

                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        int tenIndex = cursor.getColumnIndex("HoTen");
                        int emailIndex = cursor.getColumnIndex("Email");
                        int diaChiIndex = cursor.getColumnIndex("DiaChi");
                        if (tenIndex != -1 && emailIndex != -1) {
                            txtemail.setText(cursor.getString(emailIndex));
                            txtten.setText(cursor.getString(tenIndex));
                        }
                        if (diaChiIndex != -1) {
                            txtDiaChi.setText(cursor.getString(diaChiIndex));
                        }
                    }
                    cursor.close();
                }

                AlertDialog dialog = builder.create();

                btnThoat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showLogoutDialog();
                    }
                });
                dialog.show();
            }
        });

        cardViewQLSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(trangchu.this, humbergerActivity.class);
                intent.putExtra("User_ID", currentUserId); // Truyền ID giáo viên sang
                startActivity(intent);
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingDialog();
            }
        });
    }

    private void showHistoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_history, null);
        builder.setView(dialogView);

        ListView lvHistory = dialogView.findViewById(R.id.lvHistory);
        Button btnDong = dialogView.findViewById(R.id.btnDongHistory);

        ArrayList<String> historyList = new ArrayList<>();
        Cursor cursor = myDatabaseHelper.getAllNhatKy();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String action = cursor.getString(cursor.getColumnIndexOrThrow("HanhDong"));
                String time = cursor.getString(cursor.getColumnIndexOrThrow("ThoiGian"));
                String content = cursor.getString(cursor.getColumnIndexOrThrow("NoiDung"));
                historyList.add("[" + action + "] " + time + "\n" + content);
            }
            cursor.close();
        }

        if (historyList.isEmpty()) {
            historyList.add("Chưa có hoạt động nào.");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                historyList
        );

        lvHistory.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        btnDong.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(trangchu.this);
        View diaglogView = getLayoutInflater().inflate(R.layout.layout_setting_dialog, null);
        builder.setView(diaglogView);
        
        Button btnLuu_trang_thai = diaglogView.findViewById(R.id.btnluusetting);
        SwitchCompat swDarkMode = diaglogView.findViewById(R.id.switchDarkMode);
        SwitchCompat swThongbao = diaglogView.findViewById(R.id.switchThongbao);

        SharedPreferences pref = getSharedPreferences("Settings", MODE_PRIVATE);
        swDarkMode.setChecked(pref.getBoolean("dark_mode", false));
        swThongbao.setChecked(pref.getBoolean("notify", true));

        AlertDialog dialog = builder.create();
        btnLuu_trang_thai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isDark = swDarkMode.isChecked();
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean("dark_mode", isDark);
                editor.putBoolean("notify", swThongbao.isChecked());
                editor.apply();

                if (isDark) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                dialog.dismiss();
                Toast.makeText(trangchu.this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(trangchu.this);
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có muốn đăng xuất không?");
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performLogout();
            }
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void performLogout() {
        Intent intent = new Intent(trangchu.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }
}
