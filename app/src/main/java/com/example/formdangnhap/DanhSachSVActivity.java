package com.example.formdangnhap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class DanhSachSVActivity extends AppCompatActivity {
    ListView lvSinhVien;
    ArrayList<SinhVien> arrayList;
    SinhVienAdapter adapter;
    MyDatabaseHelper dbHelper;
    int teacherId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_sv);

        teacherId = getIntent().getIntExtra("User_ID", -1);

        Toolbar toolbar = findViewById(R.id.toolbarDanhSach);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        lvSinhVien = findViewById(R.id.lvSinhVien);
        dbHelper = new MyDatabaseHelper(this);
        arrayList = new ArrayList<>();

        loadData();

        lvSinhVien.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                showDetailDialog(arrayList.get(i));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadData() {
        arrayList.clear();
        Cursor cursor = dbHelper.getSinhVienByGiaoVien(teacherId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String ma = cursor.getString(0);
                String ten = cursor.getString(1);
                String ns = cursor.getString(2);
                int diem = cursor.getInt(3);
                String lop = cursor.getString(4);
                arrayList.add(new SinhVien(ma, ten, ns, diem, lop));
            }
            cursor.close();
        }
        adapter = new SinhVienAdapter(this, R.layout.item_sinhvien, arrayList);
        lvSinhVien.setAdapter(adapter);
    }

    private void showDetailDialog(SinhVien sv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_sv_detail, null);
        builder.setView(dialogView);

        TextView txtMa = dialogView.findViewById(R.id.txtDetailMa);
        TextView txtTen = dialogView.findViewById(R.id.txtDetailTen);
        TextView txtNS = dialogView.findViewById(R.id.txtDetailNS);
        TextView txtDiem = dialogView.findViewById(R.id.txtDetailDiem);
        TextView txtLop = dialogView.findViewById(R.id.txtDetailLop);
        android.widget.Button btnDong = dialogView.findViewById(R.id.btnDongDetail);

        txtMa.setText("Mã SV: " + sv.getMaSV());
        txtTen.setText("Họ tên: " + sv.getHoTen());
        txtNS.setText("Ngày sinh: " + sv.getNgaySinh());
        txtDiem.setText("Điểm rèn luyện: " + sv.getDiemRenLuyen());
        txtLop.setText("Mã lớp: " + sv.getMaLop());

        AlertDialog dialog = builder.create();
        btnDong.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
