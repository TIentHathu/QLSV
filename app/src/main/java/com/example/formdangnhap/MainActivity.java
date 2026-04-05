package com.example.formdangnhap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.sql.SQLData;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextView txtDangnhap;
    EditText edtTen, edtMK;
    Button btnDangnhap;
    CheckBox cBghinho;
    SQLiteDatabase sqLiteDatabase;
    ArrayList<String> myList;
    ArrayAdapter myadapter;

    SQLiteDatabase mySQL;
    MyDatabaseHelper myHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtTen = (EditText) findViewById(R.id.ten);
        edtMK = (EditText) findViewById(R.id.MK);
        btnDangnhap = findViewById(R.id.btndannhap);
        cBghinho = findViewById(R.id.ghinho);
        txtDangnhap = findViewById(R.id.Dangnhap);
        myList = new ArrayList<>();
        myadapter = new ArrayAdapter(MainActivity.this, android.R.layout.activity_list_item, myList);
        myHelper = new MyDatabaseHelper(MainActivity.this);




        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTen.getText().toString().isEmpty() || edtMK.getText().toString().isEmpty()) {

                    ThongBao(view, "Vui lòng nhập đầy đủ thông tin");
                } else {
                    String tendangnhap = edtTen.getText().toString().trim();
                    String mk = edtMK.getText().toString().trim();


                    mySQL = openOrCreateDatabase("Qlysv.db", MODE_PRIVATE, null);

                    try {

                        mySQL = myHelper.getReadableDatabase();

                        // 1. Viết câu lệnh truy vấn có tham số ? để chống SQL Injection
                        String sql = "SELECT * FROM tblUser_GiaoVien WHERE Email = ? AND MatKhau = ?";


                        // 2. Truyền giá trị vào mảng String để thay thế cho các dấu ?
                        Cursor cursor = mySQL.rawQuery(sql, new String[]{tendangnhap, mk});

                        // 3. Kiểm tra kết quả
                        if (cursor != null && cursor.moveToFirst()) {


                            Intent Mh2 = new Intent(MainActivity.this, trangchu.class);
                            startActivity(Mh2);
                        } else if(cursor!=null) {
                            cursor.close();
                        }


                    } catch (Exception e) {
                        ThongBao(view, "ko tim thay ten dang nhap");
                    }
                }
            }
        });
        cBghinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cBghinho.isChecked()) {
                    //SharedPreferences.Editor editor= sharedPreferences.Editor();

                    ThongBao(view, "tài khoản đã ghi nhớ đăng nhập");
                }
            }
        });


    }

    public void ThongBao(View v, String n) {
        Snackbar sn = Snackbar.make(v, "" + n, Snackbar.LENGTH_SHORT);
        sn.setActionTextColor(Color.YELLOW);
        View snv = sn.getView();
        snv.setBackgroundColor(Color.RED);
        sn.show();
    }

}