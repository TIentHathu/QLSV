package com.example.formdangnhap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class DuyetDiemFragment extends Fragment {
    ListView lvDuyet;
    TextView txtChoDuyet;
    MyDatabaseHelper dbHelper;
    int teacherId;
    ArrayList<SinhVien> listSV;
    DuyetAdapter adapter;

    public static DuyetDiemFragment newInstance(int teacherId) {
        DuyetDiemFragment fragment = new DuyetDiemFragment();
        Bundle args = new Bundle();
        args.putInt("User_ID", teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_duyet_diem, container, false);

        teacherId = getArguments().getInt("User_ID");
        lvDuyet = view.findViewById(R.id.lvDuyetDiem);
        txtChoDuyet = view.findViewById(R.id.txtSoLuongChoDuyet);
        dbHelper = new MyDatabaseHelper(getContext());

        loadData();
        return view;
    }

    private void loadData() {
        listSV = new ArrayList<>();
        Cursor cursor = dbHelper.getSinhVienByGiaoVien(teacherId);
        int count = 0;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int diemHeThong = cursor.getInt(3);
                int diemTuDanhGia = cursor.getInt(4);
                // Hiển thị tất cả sinh viên
                listSV.add(new SinhVien(cursor.getString(0), cursor.getString(1),
                        cursor.getString(2), diemHeThong, diemTuDanhGia, cursor.getString(5)));

                // Đếm số lượng chờ duyệt (điểm hệ thống đang là 0)
                if (diemHeThong == 0) {
                    count++;
                }
            }
            cursor.close();
        }
        txtChoDuyet.setText("Số sinh viên đang chờ duyệt: " + count);
        adapter = new DuyetAdapter(getContext(), R.layout.item_duyet_diem, listSV);
        lvDuyet.setAdapter(adapter);
    }

    private class DuyetAdapter extends ArrayAdapter<SinhVien> {
        public DuyetAdapter(@NonNull android.content.Context context, int resource, @NonNull List<SinhVien> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_duyet_diem, parent, false);
            }
            SinhVien sv = getItem(position);
            ((TextView) convertView.findViewById(R.id.txtDuyetTen)).setText(sv.getHoTen());
            ((TextView) convertView.findViewById(R.id.txtDuyetInfo)).setText("Mã: " + sv.getMaSV() + " - Lớp: " + sv.getMaLop());

            TextView txtDiemHeThong = new TextView(getContext()); // Tạm thời tận dụng hoặc thêm vào layout
            String diemHTText = (sv.getDiemRenLuyen() == 0) ? "Cần cập nhật" : String.valueOf(sv.getDiemRenLuyen());
            ((TextView) convertView.findViewById(R.id.txtDuyetDiemTuCham)).setText("Điểm tự đánh giá: " + sv.getDiemTuDanhGia() + " | Hệ thống: " + diemHTText);

            convertView.findViewById(R.id.btnChapNhan).setOnClickListener(v -> {
                dbHelper.duyetDiem(sv.getMaSV(), sv.getDiemTuDanhGia());
                dbHelper.ghiNhatKy("Duyệt điểm", "Chấp nhận điểm cho SV " + sv.getMaSV());
                Toast.makeText(getContext(), "Đã cập nhật điểm hệ thống!", Toast.LENGTH_SHORT).show();
                loadData(); // Load lại để hiển thị điểm mới
            });

            convertView.findViewById(R.id.btnSuaDiem).setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putInt("User_ID", teacherId);
                bundle.putString("MaSV_Selected", sv.getMaSV());

                SuaThongTinSVFragment suaFrag = new SuaThongTinSVFragment();
                suaFrag.setArguments(bundle);

                getFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, suaFrag)
                        .commit();
            });

            return convertView;
        }
    }
}
