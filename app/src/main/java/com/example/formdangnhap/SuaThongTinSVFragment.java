package com.example.formdangnhap;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SuaThongTinSVFragment extends Fragment {

    // ── Sửa / Xóa ──────────────────────────────────────────────────────────
    EditText edtTen, edtNS, edtDiem, edtLop;
    Spinner spnMaSV;
    Button btnCapNhat, btnXoa;
    ArrayList<String> listMaSV;
    ArrayAdapter<String> adapterMaSV;

    // ── Thêm mới ────────────────────────────────────────────────────────────
    EditText edtThemMa, edtThemTen, edtThemNS;
    Spinner spnThemLop;
    Button btnThem;
    TextView txtMaError, txtTenError, txtNSError;  // Hiển thị lỗi inline
    ArrayList<String> listMaLop;    // "L01 – Tên lớp"  (hiển thị)
    ArrayList<String> listMaLopId;  // "L01"             (dùng insert DB)
    ArrayAdapter<String> adapterLop;

    // ── Chung ───────────────────────────────────────────────────────────────
    MyDatabaseHelper dbHelper;
    int teacherId = -1;
    String maSV_Transfer = null;

    // ════════════════════════════════════════════════════════════════════════
    public static SuaThongTinSVFragment newInstance(int teacherId) {
        SuaThongTinSVFragment f = new SuaThongTinSVFragment();
        Bundle args = new Bundle();
        args.putInt("User_ID", teacherId);
        f.setArguments(args);
        return f;
    }

    // ════════════════════════════════════════════════════════════════════════
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_sua_sv, container, false);

        // Ẩn toolbar khi chạy trong Fragment
        View toolbar = view.findViewById(R.id.toolbarSua);
        if (toolbar != null) toolbar.setVisibility(View.GONE);

        // Nhận tham số
        if (getArguments() != null) {
            teacherId = getArguments().getInt("User_ID");
            maSV_Transfer = getArguments().getString("MaSV_Selected");
        }

        dbHelper = new MyDatabaseHelper(getContext());

        // ── Ánh xạ: Sửa / Xóa ──────────────────────────────────────────────
        spnMaSV = view.findViewById(R.id.spnMaSV);
        edtTen = view.findViewById(R.id.edtSuaTen);
        edtNS = view.findViewById(R.id.edtSuaNS);
        edtDiem = view.findViewById(R.id.edtSuaDiem);
        edtLop = view.findViewById(R.id.edtSuaLop);
        btnCapNhat = view.findViewById(R.id.btnCapNhat);
        btnXoa = view.findViewById(R.id.btnXoa);

        // ── Ánh xạ: Thêm mới ────────────────────────────────────────────────
        edtThemMa = view.findViewById(R.id.edtThemMa);
        edtThemTen = view.findViewById(R.id.edtThemTen);
        edtThemNS = view.findViewById(R.id.edtThemNS);
        spnThemLop = view.findViewById(R.id.spnThemLop);
        btnThem = view.findViewById(R.id.btnThem);
        txtMaError = view.findViewById(R.id.txtMaError);
        txtTenError = view.findViewById(R.id.txtTenError);
        txtNSError = view.findViewById(R.id.txtNSError);

        // ── Load dữ liệu ban đầu ────────────────────────────────────────────
        loadSpinnerMaSV();
        loadSpinnerLop();

        // Nếu được truyền MaSV từ màn hình Duyệt điểm → chọn sẵn
        if (maSV_Transfer != null) {
            int pos = listMaSV.indexOf(maSV_Transfer);
            if (pos >= 0) spnMaSV.setSelection(pos);
        }

        // ── Spinner chọn SV cần sửa ─────────────────────────────────────────
        spnMaSV.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int i, long l) {
                loadStudentDetail(listMaSV.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> p) {
            }
        });

        // ── Gán sự kiện các nút ─────────────────────────────────────────────
        btnCapNhat.setOnClickListener(v -> xuLyCapNhat());
        btnXoa.setOnClickListener(v -> xuLyXoa());
        btnThem.setOnClickListener(v -> xuLyThemSinhVien());

        // Xóa lỗi realtime khi người dùng bắt đầu gõ
        edtThemMa.addTextChangedListener(clearError(txtMaError));
        edtThemTen.addTextChangedListener(clearError(txtTenError));
        edtThemNS.addTextChangedListener(clearError(txtNSError));

        return view;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  XỬ LÝ THÊM SINH VIÊN
    // ════════════════════════════════════════════════════════════════════════
    private void xuLyThemSinhVien() {
        String ma = edtThemMa.getText().toString().trim();
        String ten = edtThemTen.getText().toString().trim();
        String ns = edtThemNS.getText().toString().trim();
        boolean hasError = false;

        // ── BƯỚC 1: VALIDATE từng trường, hiển thị lỗi ngay dưới ô nhập ────

        // Validate Mã SV
        if (ma.isEmpty()) {
            showError(txtMaError, "Vui lòng nhập mã sinh viên");
            hasError = true;
        } else if (!ma.matches("[A-Za-z0-9]+")) {
            showError(txtMaError, "Mã SV chỉ được chứa chữ và số, không dấu cách");
            hasError = true;
        }

        // Validate Họ tên
        if (ten.isEmpty()) {
            showError(txtTenError, "Vui lòng nhập họ và tên");
            hasError = true;
        } else if (ten.length() < 2) {
            showError(txtTenError, "Họ tên quá ngắn");
            hasError = true;
        }

        // Validate Ngày sinh
        if (ns.isEmpty()) {
            showError(txtNSError, "Vui lòng nhập ngày sinh");
            hasError = true;
        } else if (!ns.matches("\\d{4}-\\d{2}-\\d{2}")) {
            showError(txtNSError, "Sai định dạng – phải là YYYY-MM-DD (VD: 2004-05-20)");
            hasError = true;
        } else if (!isNgaySinhHopLe(ns)) {
            showError(txtNSError, "Ngày sinh không hợp lệ (tháng hoặc ngày không đúng)");
            hasError = true;
        }

        // Nếu có lỗi thì dừng lại, không insert
        if (hasError) return;

        // ── BƯỚC 2: Lấy mã lớp từ Spinner ───────────────────────────────────
        if (spnThemLop.getSelectedItem() == null || listMaLopId.isEmpty()) {
            Toast.makeText(getContext(), "Không có lớp nào để thêm sinh viên!", Toast.LENGTH_SHORT).show();
            return;
        }
        String maLop = listMaLopId.get(spnThemLop.getSelectedItemPosition());

        // ── BƯỚC 3: INSERT vào database ──────────────────────────────────────
        boolean success = dbHelper.insertSinhVien(ma, ten, ns, 0, maLop);

        if (success) {
            dbHelper.ghiNhatKy("Thêm SV", "GV " + teacherId + " thêm SV " + ma + " vào lớp " + maLop);
            Toast.makeText(getContext(), "✅ Thêm sinh viên " + ma + " thành công!", Toast.LENGTH_SHORT).show();
            clearThemFields();
            loadSpinnerMaSV(); // Cập nhật spinner sửa/xóa có thêm SV mới
        } else {
            // Mã SV đã tồn tại trong DB
            showError(txtMaError, "Mã SV \"" + ma + "\" đã tồn tại trong hệ thống");
            edtThemMa.requestFocus();
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  XỬ LÝ CẬP NHẬT
    // ════════════════════════════════════════════════════════════════════════
    private void xuLyCapNhat() {
        if (spnMaSV.getSelectedItem() == null) return;

        String ma = spnMaSV.getSelectedItem().toString();
        String ten = edtTen.getText().toString().trim();
        String ns = edtNS.getText().toString().trim();
        String diemStr = edtDiem.getText().toString().trim();
        String lop = edtLop.getText().toString().trim();

        if (ten.isEmpty() || ns.isEmpty() || diemStr.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        int diem;
        try {
            diem = Integer.parseInt(diemStr);
            if (diem < 0 || diem > 100) {
                Toast.makeText(getContext(), "Điểm rèn luyện phải từ 0 đến 100!", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Điểm không hợp lệ!", Toast.LENGTH_SHORT).show();
            return;
        }

        dbHelper.updateSinhVien(ma, ten, ns, diem, lop);
        dbHelper.ghiNhatKy("Cập nhật", "GV " + teacherId + " sửa SV " + ma);
        Toast.makeText(getContext(), "✅ Đã cập nhật thông tin sinh viên!", Toast.LENGTH_SHORT).show();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  XỬ LÝ XÓA
    // ════════════════════════════════════════════════════════════════════════
    private void xuLyXoa() {
        if (spnMaSV.getSelectedItem() == null) return;
        String ma = spnMaSV.getSelectedItem().toString();

        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa sinh viên " + ma + "?\nThao tác này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    dbHelper.deleteSinhVien(ma);
                    dbHelper.ghiNhatKy("Xóa", "GV " + teacherId + " xóa SV " + ma);
                    Toast.makeText(getContext(), "Đã xóa sinh viên " + ma, Toast.LENGTH_SHORT).show();
                    loadSpinnerMaSV();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  CÁC HÀM HELPER
    // ════════════════════════════════════════════════════════════════════════

    private void loadSpinnerMaSV() {
        listMaSV = new ArrayList<>();
        Cursor cursor = dbHelper.getSinhVienByGiaoVien(teacherId);
        if (cursor != null) {
            while (cursor.moveToNext()) listMaSV.add(cursor.getString(0));
            cursor.close();
        }
        adapterMaSV = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, listMaSV);
        adapterMaSV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnMaSV.setAdapter(adapterMaSV);
        if (listMaSV.isEmpty()) clearSuaFields();
    }

    private void loadSpinnerLop() {
        listMaLop = new ArrayList<>();
        listMaLopId = new ArrayList<>();
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT MaLop, TenLop FROM tblLop WHERE UserID = ?",
                new String[]{String.valueOf(teacherId)});
        if (cursor != null) {
            while (cursor.moveToNext()) {
                listMaLopId.add(cursor.getString(0));
                listMaLop.add(cursor.getString(0) + " – " + cursor.getString(1));
            }
            cursor.close();
        }
        adapterLop = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, listMaLop);
        adapterLop.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnThemLop.setAdapter(adapterLop);
    }

    private void loadStudentDetail(String ma) {
        Cursor cursor = dbHelper.getReadableDatabase()
                .rawQuery("SELECT * FROM tblSinhVien WHERE MaSV = ?", new String[]{ma});
        if (cursor.moveToFirst()) {
            edtTen.setText(cursor.getString(1));
            edtNS.setText(cursor.getString(2));
            edtDiem.setText(String.valueOf(cursor.getInt(3)));
            edtLop.setText(cursor.getString(5));
        }
        cursor.close();
    }

    /**
     * Kiểm tra tháng (1-12) và ngày (1-31) có hợp lệ không.
     * ns phải đúng format YYYY-MM-DD trước khi gọi hàm này.
     */
    private boolean isNgaySinhHopLe(String ns) {
        try {
            String[] p = ns.split("-");
            int thang = Integer.parseInt(p[1]);
            int ngay = Integer.parseInt(p[2]);
            if (thang < 1 || thang > 12) return false;
            if (ngay < 1 || ngay > 31) return false;
            // Tháng 30 ngày
            if ((thang == 4 || thang == 6 || thang == 9 || thang == 11) && ngay > 30) return false;
            // Tháng 2 tối đa 29 ngày
            if (thang == 2 && ngay > 29) return false;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void showError(TextView txtError, String message) {
        if (txtError == null) return;
        txtError.setText(message);
        txtError.setVisibility(View.VISIBLE);
    }

    /**
     * TextWatcher: tự ẩn thông báo lỗi khi người dùng bắt đầu gõ
     */
    private TextWatcher clearError(final TextView txtError) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int st, int c, int a) {
            }

            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (txtError != null) txtError.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
    }

    private void clearSuaFields() {
        edtTen.setText("");
        edtNS.setText("");
        edtDiem.setText("");
        edtLop.setText("");
    }

    private void clearThemFields() {
        edtThemMa.setText("");
        edtThemTen.setText("");
        edtThemNS.setText("");
        if (!listMaLopId.isEmpty()) spnThemLop.setSelection(0);
        if (txtMaError != null) txtMaError.setVisibility(View.GONE);
        if (txtTenError != null) txtTenError.setVisibility(View.GONE);
        if (txtNSError != null) txtNSError.setVisibility(View.GONE);
    }
}