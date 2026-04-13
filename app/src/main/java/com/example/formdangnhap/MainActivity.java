package com.example.formdangnhap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;


public class MainActivity extends AppCompatActivity {

    TextView txtDangnhap;
    EditText edtTen, edtMK;
    Button btnDangnhap;
    CheckBox cBghinho;
    SQLiteDatabase sqLiteDatabase;

    SQLiteDatabase mySQL;
    MyDatabaseHelper myHelper;

    // Khai báo SharedPreferences
    SharedPreferences sharedPreferences;

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
        myHelper = new MyDatabaseHelper(MainActivity.this);

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("LoginData", MODE_PRIVATE);

        // Lấy dữ liệu đã lưu và hiển thị lên giao diện
        loadSavedLoginData();

        btnDangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtTen.getText().toString().isEmpty() || edtMK.getText().toString().isEmpty()) {
                    ThongBao(view, "Vui lòng nhập đầy đủ thông tin");
                } else {
                    String tendangnhap = edtTen.getText().toString().trim();
                    String mk = edtMK.getText().toString().trim();

                    mySQL = myHelper.getReadableDatabase();

                    try {
                        String sql = "SELECT * FROM tblUser_GiaoVien WHERE Email = ? AND MatKhau = ?";
                        Cursor cursor = mySQL.rawQuery(sql, new String[]{tendangnhap, mk});

                        if (cursor != null && cursor.moveToFirst()) {
                            // Xử lý ghi nhớ đăng nhập khi đăng nhập thành công
                            saveLoginData(tendangnhap, mk, cBghinho.isChecked());

                            int idUserDTbase = cursor.getInt(cursor.getColumnIndex("UserID"));
                            Intent Mh2 = new Intent(MainActivity.this, trangchu.class);
                            Mh2.putExtra("User_ID", idUserDTbase);
                            startActivity(Mh2);
                            cursor.close();
                        } else {
                            ThongBao(view, "Tên đăng nhập hoặc mật khẩu không chính xác");
                            if (cursor != null) cursor.close();
                        }
                    } catch (Exception e) {
                        ThongBao(view, "Lỗi kết nối cơ sở dữ liệu");
                    }
                }
            }
        });

        cBghinho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cBghinho.isChecked()) {
                    ThongBao(view, "Đã chọn ghi nhớ đăng nhập");

                }
            }
        });
    }

    // Hàm lưu thông tin đăng nhập
    private void saveLoginData(String user, String pass, boolean isChecked) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isChecked) {
            editor.putString("username", user);
            editor.putString("password", pass);
            editor.putBoolean("remember", true);
        } else {
            editor.clear(); // Xóa hết nếu không chọn ghi nhớ
        }
        editor.apply();
    }

    // Hàm load thông tin đã lưu
    private void loadSavedLoginData() {
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        if (isRemembered) {
            edtTen.setText(sharedPreferences.getString("username", ""));
            edtMK.setText(sharedPreferences.getString("password", ""));
            cBghinho.setChecked(true);
        }
    }

    public void ThongBao(View v, String n) {
        Snackbar sn = Snackbar.make(v, "" + n, Snackbar.LENGTH_SHORT);
        sn.setActionTextColor(Color.YELLOW);
        View snv = sn.getView();
        snv.setBackgroundColor(Color.RED);
        sn.show();
    }
}