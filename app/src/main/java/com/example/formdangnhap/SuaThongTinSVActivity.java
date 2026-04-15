package com.example.formdangnhap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class SuaThongTinSVActivity extends AppCompatActivity {
    EditText edtTen, edtNS, edtDiem, edtLop;
    Spinner spnMaSV;
    Button btnCapNhat, btnXoa;
    MyDatabaseHelper dbHelper;
    int teacherId = -1;
    ArrayList<String> listMaSV;
    ArrayAdapter<String> adapterMaSV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_sv);

        teacherId = getIntent().getIntExtra("User_ID", -1);

        Toolbar toolbar = findViewById(R.id.toolbarSua);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        spnMaSV = findViewById(R.id.spnMaSV);
        edtTen = findViewById(R.id.edtSuaTen);
        edtNS = findViewById(R.id.edtSuaNS);
        edtDiem = findViewById(R.id.edtSuaDiem);
        edtLop = findViewById(R.id.edtSuaLop);
        btnCapNhat = findViewById(R.id.btnCapNhat);
        btnXoa = findViewById(R.id.btnXoa);
        dbHelper = new MyDatabaseHelper(this);

        loadSpinnerData();

        spnMaSV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String maSelected = listMaSV.get(i);
                loadStudentDetail(maSelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        btnCapNhat.setOnClickListener(v -> {
            if (spnMaSV.getSelectedItem() == null) return;
            String ma = spnMaSV.getSelectedItem().toString();
            String ten = edtTen.getText().toString().trim();
            String ns = edtNS.getText().toString().trim();
            String diemStr = edtDiem.getText().toString().trim();
            String lop = edtLop.getText().toString().trim();

            dbHelper.updateSinhVien(ma, ten, ns, Integer.parseInt(diemStr), lop);
            dbHelper.ghiNhatKy("Cập nhật", "GV " + teacherId + " sửa SV " + ma);
            Toast.makeText(this, "Đã cập nhật!", Toast.LENGTH_SHORT).show();
        });

        btnXoa.setOnClickListener(v -> {
            if (spnMaSV.getSelectedItem() == null) return;
            String ma = spnMaSV.getSelectedItem().toString();

            dbHelper.deleteSinhVien(ma);
            dbHelper.ghiNhatKy("Xóa", "GV " + teacherId + " xóa SV " + ma);
            Toast.makeText(this, "Đã xóa!", Toast.LENGTH_SHORT).show();

            loadSpinnerData(); // Cập nhật lại danh sách Spinner sau khi xóa
        });
    }

    private void loadSpinnerData() {
        listMaSV = new ArrayList<>();
        Cursor cursor = dbHelper.getSinhVienByGiaoVien(teacherId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                listMaSV.add(cursor.getString(0)); // Lấy MaSV
            }
            cursor.close();
        }

        adapterMaSV = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listMaSV);
        adapterMaSV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMaSV.setAdapter(adapterMaSV);

        if (listMaSV.isEmpty()) {
            clearFields();
        }
    }

    private void loadStudentDetail(String ma) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tblSinhVien WHERE MaSV = ?", new String[]{ma});
        if (cursor.moveToFirst()) {
            edtTen.setText(cursor.getString(1));
            edtNS.setText(cursor.getString(2));
            edtDiem.setText(String.valueOf(cursor.getInt(3)));
            edtLop.setText(cursor.getString(4));
        }
        cursor.close();
    }

    private void clearFields() {
        edtTen.setText("");
        edtNS.setText("");
        edtDiem.setText("");
        edtLop.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
