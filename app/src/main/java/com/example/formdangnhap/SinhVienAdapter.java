package com.example.formdangnhap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SinhVienAdapter extends ArrayAdapter<SinhVien> {
    private Context context;
    private int resource;
    private List<SinhVien> objects;

    public SinhVienAdapter(@NonNull Context context, int resource, @NonNull List<SinhVien> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        SinhVien sv = objects.get(position);

        TextView txtTen = convertView.findViewById(R.id.txtTenSV);
        TextView txtMa = convertView.findViewById(R.id.txtMaSV);

        txtTen.setText(sv.getHoTen());
        txtMa.setText("Mã SV: " + sv.getMaSV());

        return convertView;
    }
}
