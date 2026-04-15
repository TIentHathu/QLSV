package com.example.formdangnhap;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ThongKeFragment extends Fragment {
    TextView txtTong, txtXuatSac, txtGioi, txtKha, txtYeu, txtTyLe;
    MyDatabaseHelper dbHelper;
    int teacherId;

    public static ThongKeFragment newInstance(int teacherId) {
        ThongKeFragment fragment = new ThongKeFragment();
        Bundle args = new Bundle();
        args.putInt("User_ID", teacherId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_thong_ke, container, false);

        teacherId = getArguments().getInt("User_ID");
        txtTong = view.findViewById(R.id.txtTongSV);
        txtXuatSac = view.findViewById(R.id.txtXuatSac);
        txtGioi = view.findViewById(R.id.txtGioi);
        txtKha = view.findViewById(R.id.txtKha);
        txtYeu = view.findViewById(R.id.txtYeu);
        txtTyLe = view.findViewById(R.id.txtTyLe);
        dbHelper = new MyDatabaseHelper(getContext());

        thongKe();
        return view;
    }

    private void thongKe() {
        int tong = 0, xs = 0, gioi = 0, kha = 0, yeu = 0, trenTB = 0;
        Cursor cursor = dbHelper.getSinhVienByGiaoVien(teacherId);

        if (cursor != null) {
            tong = cursor.getCount();
            while (cursor.moveToNext()) {
                int diem = cursor.getInt(3); // DiemRenLuyen
                if (diem >= 86) xs++;
                else if (diem >= 76) gioi++;
                else if (diem >= 65) kha++;
                else if (diem < 50) yeu++;

                if (diem >= 50) trenTB++;
            }
            cursor.close();
        }

        txtTong.setText(String.valueOf(tong));
        txtXuatSac.setText("Xuất sắc (>=86): " + xs);
        txtGioi.setText("Giỏi (76-85): " + gioi);
        txtKha.setText("Khá (65-75): " + kha);
        txtYeu.setText("Yếu (<50): " + yeu);

        if (tong > 0) {
            int tyLe = (trenTB * 100) / tong;
            txtTyLe.setText(tyLe + "%");
        } else {
            txtTyLe.setText("0%");
        }
    }
}
