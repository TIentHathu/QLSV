package com.example.formdangnhap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class trangchu extends AppCompatActivity {
ImageButton btnHome,btnSetting,btnuser;
MaterialCardView cardViewQLSV,cardViewLicsu;
    MyDatabaseHelper myDatabaseHelper= new MyDatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trangchu);
        btnHome= findViewById(R.id.btnHome);
        btnuser =findViewById(R.id.btncaNhan);
        btnSetting=findViewById(R.id.btnsetting);
        cardViewQLSV=findViewById(R.id.cardViewQLSV);
        cardViewLicsu= findViewById(R.id.cardViewHistory);


// mo dialog ca nhan
        btnuser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder =new AlertDialog.Builder(trangchu.this);
                //get thong tin giao vien
                View dialogView =getLayoutInflater().inflate(R.layout.layout_dialog_profile,null);
                builder.setView(dialogView);
                //anh xa
                Button  btnThoat = dialogView.findViewById(R.id.btnSignout);
                TextView txtten =dialogView.findViewById(R.id.txtten);
                TextView txtemail =dialogView.findViewById(R.id.txtemail);
                Cursor cursor = myDatabaseHelper.getTenGiaovien(1);

                if(cursor!=null){
                    if(cursor.moveToFirst()){
                        int ten = cursor.getColumnIndex("HoTen");
                        int email= cursor.getColumnIndex("Email");
                        if (ten !=-1 &&email!=-1){
                            txtemail.setText(cursor.getString(email));
                            txtten.setText(cursor.getString(ten));
                        }
                    }

                }
                AlertDialog dialog= builder.create();//
// thoat ve dang nhap
               btnThoat.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       AlertDialog.Builder builder1=new AlertDialog.Builder(trangchu.this);
                       builder1.setTitle("ban muốn thoát");
                       builder1.setMessage("bạn muốn kết thúc phiên làm việc này");
                       builder1.setPositiveButton("có", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               performLogout();
                               dialogInterface.dismiss();
                               finish();
                           }
                       });
                       builder1.setNegativeButton("huy", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialogInterface, int i) {
                               dialogInterface.dismiss();
                           }
                       });
                       builder1.show();
                   }
               });
                dialog.show();
            }
        });
        //chuyen man hinh sang quan ly sinh vien
        cardViewQLSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(trangchu.this,humbergerActivity.class);
                startActivity(intent);
            }
        });
        //viết chương trình setting dialog
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                AlertDialog.Builder builder=new AlertDialog.Builder(trangchu.this);
                View diaglogView =getLayoutInflater().inflate(R.layout.layout_setting_dialog,null);
                builder.setView(diaglogView);
                Button btnLuu_trang_thai =diaglogView.findViewById(R.id.btnluusetting);
                SwitchCompat swDarkMode= diaglogView.findViewById(R.id.switchDarkMode);
                SwitchCompat swThongbao= diaglogView.findViewById(R.id.switchThongbao);
// laays trang thai


                AlertDialog dialog =builder.create();
                btnLuu_trang_thai.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        boolean isDark = swDarkMode.isChecked();
                        boolean isNotify = swThongbao.isChecked();
                        SharedPreferences pref = getSharedPreferences("Settings", MODE_PRIVATE);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("dark_mode", isDark);
                        editor.putBoolean("notify", isNotify);
                        editor.apply(); // Quan trọng: Phải có dòng này mới lưu được
                        if (isDark) {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        } else {
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        }
                        // 4. Đóng dialog và thông báo
                        dialog.dismiss();
                        Toast.makeText(trangchu.this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
                    }

                });




                dialog.show();
            }
        });


    }

    //ngoài hàm onCreate
    // chuyen man hinh ve ca nhan va xoa thong tin
    private void performLogout() {
        Intent intent = new Intent(trangchu.this, MainActivity.class);
        // ham flag ho tro xoa man hinh xoa cac du lieu lam moi nhu khi banj vua chay no
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        }

        //ham nay se goi khi bam nut back o dien thoai
        @Override
    public void onBackPressed(){
        AlertDialog.Builder builder =new AlertDialog.Builder(trangchu.this);
        builder.setTitle("bạn muốn thoát");
        builder.setMessage("bạn muốn kết thúc phiên làm việc ở đây");
        builder.setPositiveButton("có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                performLogout();
                dialogInterface.dismiss();
                finish();
            }
        });
        builder.setNegativeButton("hủy ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.show();
        }




}