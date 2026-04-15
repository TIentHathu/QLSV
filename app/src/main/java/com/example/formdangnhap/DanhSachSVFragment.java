package com.example.formdangnhap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class DanhSachSVFragment extends Fragment {
    ListView lvSinhVien;
    ArrayList<SinhVien> arrayList;
    SinhVienAdapter adapter;
    MyDatabaseHelper dbHelper;
    int teacherId;

    public static DanhSachSVFragment newInstance(int teacherId) {
        DanhSachSVFragment fragment = new DanhSachSVFragment();
        Bundle args = new Bundle();
        args.putInt("User_ID", teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_danh_sach_sv, container, false);

        View toolbar = view.findViewById(R.id.toolbarDanhSach);
        if (toolbar != null) toolbar.setVisibility(View.GONE);

        if (getArguments() != null) {
            teacherId = getArguments().getInt("User_ID");
        }

        lvSinhVien = view.findViewById(R.id.lvSinhVien);
        dbHelper = new MyDatabaseHelper(getContext());
        arrayList = new ArrayList<>();

        loadData();

        lvSinhVien.setOnItemClickListener((adapterView, v, i, l) -> showDetailDialog(arrayList.get(i)));

        return view;
    }

    private void loadData() {
        arrayList.clear();
        Cursor cursor = dbHelper.getSinhVienByGiaoVien(teacherId);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // Lấy dữ liệu theo đúng thứ tự cột mới:
                // 0: MaSV, 1: HoTen, 2: NgaySinh, 3: DiemRenLuyen, 4: DiemTuDanhGia, 5: MaLop
                String ma = cursor.getString(0);
                String ten = cursor.getString(1);
                String ns = cursor.getString(2);
                int diemRL = cursor.getInt(3);
                int diemTuCham = cursor.getInt(4);
                String lop = cursor.getString(5);

                arrayList.add(new SinhVien(ma, ten, ns, diemRL, diemTuCham, lop));
            }
            cursor.close();
        }
        if (getContext() != null) {
            adapter = new SinhVienAdapter(getContext(), R.layout.item_sinhvien, arrayList);
            lvSinhVien.setAdapter(adapter);
        }
    }

    private void showDetailDialog(SinhVien sv) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_sv_detail, null);
        builder.setView(dialogView);

        ((TextView) dialogView.findViewById(R.id.txtDetailMa)).setText("Mã SV: " + sv.getMaSV());
        ((TextView) dialogView.findViewById(R.id.txtDetailTen)).setText("Họ tên: " + sv.getHoTen());
        ((TextView) dialogView.findViewById(R.id.txtDetailNS)).setText("Ngày sinh: " + sv.getNgaySinh());

        String diemText = (sv.getDiemRenLuyen() == 0) ? "Cần cập nhật" : String.valueOf(sv.getDiemRenLuyen());
        ((TextView) dialogView.findViewById(R.id.txtDetailDiem)).setText("Điểm RL: " + diemText);
        ((TextView) dialogView.findViewById(R.id.txtDetailLop)).setText("Mã lớp: " + sv.getMaLop());

        AlertDialog dialog = builder.create();
        dialogView.findViewById(R.id.btnDongDetail).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}
