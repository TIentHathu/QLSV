package com.example.formdangnhap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public MyDatabaseHelper(Context context) {
        super(context, "Qlysv.db", null, 10);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tblUser_GiaoVien (" +
                "UserID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HoTen TEXT, " +
                "Email TEXT UNIQUE, " +
                "MatKhau TEXT, " +
                "SoDienThoai TEXT, " +
                "HocVi TEXT, " +
                "ChuyenMon TEXT, " +
                "NgaySinh TEXT, " +
                "DiaChi TEXT, " +
                "TrangThai INTEGER)");

        db.execSQL("CREATE TABLE tblLop (" +
                "MaLop TEXT PRIMARY KEY, " +
                "TenLop TEXT, " +
                "UserID INTEGER, " +
                "FOREIGN KEY(UserID) REFERENCES tblUser_GiaoVien(UserID))");

        db.execSQL("CREATE TABLE tblSinhVien (" +
                "MaSV TEXT PRIMARY KEY, " +
                "HoTen TEXT, " +
                "NgaySinh TEXT, " +
                "DiemRenLuyen INTEGER DEFAULT 0, " +
                "DiemTuDanhGia INTEGER DEFAULT 0, " +
                "MaLop TEXT, " +
                "FOREIGN KEY(MaLop) REFERENCES tblLop(MaLop))");

        db.execSQL("CREATE TABLE tblNhatKy (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HanhDong TEXT, " +
                "ThoiGian TEXT, " +
                "NoiDung TEXT)");

        // Dữ liệu mẫu Giao Vien với địa chỉ công tác mới
        db.execSQL("INSERT INTO tblUser_GiaoVien (HoTen, Email, MatKhau, DiaChi, TrangThai) VALUES ('Nguyễn Văn Tiến', 'tien@gmail.com', '123', '96 Định Công', 1)");
        db.execSQL("INSERT INTO tblUser_GiaoVien (HoTen, Email, MatKhau, DiaChi, TrangThai) VALUES ('Vũ Văn Huy', 'huy@gmail.com', '123', '301 Nguyễn Trãi', 1)");

        db.execSQL("INSERT INTO tblLop VALUES ('L01', 'Công nghệ thông tin 1', 1)");
        db.execSQL("INSERT INTO tblLop VALUES ('L02', 'Hệ thống thông tin 2', 2)");

        // Sinh viên mẫu (SV001 và SV006 để điểm 0 để test "Cần cập nhật")
        db.execSQL("INSERT INTO tblSinhVien (MaSV, HoTen, NgaySinh, DiemRenLuyen, DiemTuDanhGia, MaLop) VALUES ('SV001', 'Trần Văn Bình', '2004-05-20', 0, 90, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien (MaSV, HoTen, NgaySinh, DiemRenLuyen, DiemTuDanhGia, MaLop) VALUES ('SV002', 'Lê Thị Hoa', '2004-08-15', 45, 70, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien (MaSV, HoTen, NgaySinh, DiemRenLuyen, DiemTuDanhGia, MaLop) VALUES ('SV003', 'Nguyễn Huy Hoàng', '2004-01-15', 70, 80, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien (MaSV, HoTen, NgaySinh, DiemRenLuyen, DiemTuDanhGia, MaLop) VALUES ('SV006', 'Phạm Minh Đức', '2005-01-10', 0, 92, 'L02')");
        db.execSQL("INSERT INTO tblSinhVien (MaSV, HoTen, NgaySinh, DiemRenLuyen, DiemTuDanhGia, MaLop) VALUES ('SV007', 'Hoàng Thị Thu Thủy', '2005-03-22', 95, 95, 'L02')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tblNhatKy");
        db.execSQL("DROP TABLE IF EXISTS tblSinhVien");
        db.execSQL("DROP TABLE IF EXISTS tblLop");
        db.execSQL("DROP TABLE IF EXISTS tblUser_GiaoVien");
        onCreate(db);
    }

    public void ghiNhatKy(String hanhDong, String noiDung) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("HanhDong", hanhDong);
        values.put("ThoiGian", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put("NoiDung", noiDung);
        db.insert("tblNhatKy", null, values);
    }

    public Cursor getAllNhatKy() {
        return getReadableDatabase().rawQuery("SELECT * FROM tblNhatKy ORDER BY _id DESC", null);
    }

    public Cursor getSinhVienByGiaoVien(int userId) {
        String sql = "SELECT s.* FROM tblSinhVien s JOIN tblLop l ON s.MaLop = l.MaLop WHERE l.UserID = ?";
        return getReadableDatabase().rawQuery(sql, new String[]{String.valueOf(userId)});
    }

    public void duyetDiem(String maSV, int diemMoi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("DiemRenLuyen", diemMoi);
        db.update("tblSinhVien", values, "MaSV = ?", new String[]{maSV});
    }

    public void deleteSinhVien(String maSV) {
        getWritableDatabase().delete("tblSinhVien", "MaSV = ?", new String[]{maSV});
    }

    public void updateSinhVien(String maSV, String ten, String ngaySinh, int diem, String maLop) {
        ContentValues v = new ContentValues();
        v.put("HoTen", ten);
        v.put("NgaySinh", ngaySinh);
        v.put("DiemRenLuyen", diem);
        v.put("MaLop", maLop);
        getWritableDatabase().update("tblSinhVien", v, "MaSV = ?", new String[]{maSV});
    }

    public Cursor getTenGiaovien(int UserID) {
        return getReadableDatabase().rawQuery("SELECT * FROM tblUser_GiaoVien WHERE UserID = ?", new String[]{String.valueOf(UserID)});
    }
}
