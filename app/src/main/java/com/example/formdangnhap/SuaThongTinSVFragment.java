package com.example.formdangnhap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SuaThongTinSVFragment extends Fragment {
    EditText edtTen, edtNS, edtDiem, edtLop;
    Spinner spnMaSV;
    Button btnCapNhat, btnXoa;
    MyDatabaseHelper dbHelper;
    int teacherId = -1;
    String maSV_Transfer = null;
    ArrayList<String> listMaSV;
    ArrayAdapter<String> adapterMaSV;

    public static SuaThongTinSVFragment newInstance(int teacherId) {
        SuaThongTinSVFragment fragment = new SuaThongTinSVFragment();
        Bundle args = new Bundle();
        args.putInt("User_ID", teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sua_sv, container, false);

        View toolbar = view.findViewById(R.id.toolbarSua);
        if (toolbar != null) toolbar.setVisibility(View.GONE);

        if (getArguments() != null) {
            teacherId = getArguments().getInt("User_ID");
            maSV_Transfer = getArguments().getString("MaSV_Selected");
        }

        spnMaSV = view.findViewById(R.id.spnMaSV);
        edtTen = view.findViewById(R.id.edtSuaTen);
        edtNS = view.findViewById(R.id.edtSuaNS);
        edtDiem = view.findViewById(R.id.edtSuaDiem);
        edtLop = view.findViewById(R.id.edtSuaLop);
        btnCapNhat = view.findViewById(R.id.btnCapNhat);
        btnXoa = view.findViewById(R.id.btnXoa);
        dbHelper = new MyDatabaseHelper(getContext());

        loadSpinnerData();

        // Nếu được chuyển từ màn hình Duyệt điểm sang, tự động chọn mã SV đó
        if (maSV_Transfer != null) {
            int pos = listMaSV.indexOf(maSV_Transfer);
            if (pos >= 0) {
                spnMaSV.setSelection(pos);
            }
        }

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
            Toast.makeText(getContext(), "Đã cập nhật!", Toast.LENGTH_SHORT).show();
        });

        btnXoa.setOnClickListener(v -> {
            if (spnMaSV.getSelectedItem() == null) return;
            String ma = spnMaSV.getSelectedItem().toString();
            dbHelper.deleteSinhVien(ma);
            dbHelper.ghiNhatKy("Xóa", "GV " + teacherId + " xóa SV " + ma);
            Toast.makeText(getContext(), "Đã xóa!", Toast.LENGTH_SHORT).show();
            loadSpinnerData();
        });

        return view;
    }

    private void loadSpinnerData() {
        listMaSV = new ArrayList<>();
        Cursor cursor = dbHelper.getSinhVienByGiaoVien(teacherId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                listMaSV.add(cursor.getString(0));
            }
            cursor.close();
        }
        adapterMaSV = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, listMaSV);
        adapterMaSV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMaSV.setAdapter(adapterMaSV);
        if (listMaSV.isEmpty()) clearFields();
    }

    private void loadStudentDetail(String ma) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM tblSinhVien WHERE MaSV = ?", new String[]{ma});
        if (cursor.moveToFirst()) {
            edtTen.setText(cursor.getString(1));
            edtNS.setText(cursor.getString(2));
            edtDiem.setText(String.valueOf(cursor.getInt(3)));
            edtLop.setText(cursor.getString(5)); // Chú ý index 5 vì đã thêm DiemTuDanhGia vào giữa
        }
        cursor.close();
    }

    private void clearFields() {
        edtTen.setText("");
        edtNS.setText("");
        edtDiem.setText("");
        edtLop.setText("");
    }
}
