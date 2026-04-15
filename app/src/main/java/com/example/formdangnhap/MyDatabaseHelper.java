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
        super(context, "Qlysv.db", null, 6);
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
                "DiemRenLuyen INTEGER, " +
                "MaLop TEXT, " +
                "FOREIGN KEY(MaLop) REFERENCES tblLop(MaLop))");

        db.execSQL("CREATE TABLE tblNhatKy (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HanhDong TEXT, " +
                "ThoiGian TEXT, " +
                "NoiDung TEXT)");

        db.execSQL("INSERT INTO tblUser_GiaoVien (HoTen, Email, MatKhau, TrangThai) VALUES ('Nguyễn Văn Tiến', 'tien@gmail.com', '123', 1)");
        db.execSQL("INSERT INTO tblUser_GiaoVien (HoTen, Email, MatKhau, TrangThai) VALUES ('Vũ Văn Huy', 'huy@gmail.com', '123', 1)");

        db.execSQL("INSERT INTO tblLop VALUES ('L01', 'Công nghệ thông tin 1', 1)");
        db.execSQL("INSERT INTO tblLop VALUES ('L02', 'Hệ thống thông tin 2', 2)");

        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV001', 'Trần Văn Bình', '2004-05-20', 85, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV002', 'Lê Thị Hoa', '2004-08-15', 45, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV003', 'Nguyễn Huy Hoàng', '2004-01-15', 70, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV004', 'Nguyễn Thị Thắm', '2004-01-15', 70, 'L01')");

        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV006', 'Phạm Minh Đức', '2005-01-10', 90, 'L02')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV007', 'Hoàng Thị Thu Thủy', '2005-03-22', 95, 'L02')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV008', 'Nguyễn Thế Vinh', '2005-07-12', 88, 'L02')");
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
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put("HanhDong", hanhDong);
        values.put("ThoiGian", currentTime);
        values.put("NoiDung", noiDung);
        db.insert("tblNhatKy", null, values);
    }

    public Cursor getAllNhatKy() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tblNhatKy ORDER BY _id DESC", null);
    }

    public Cursor getSinhVienByGiaoVien(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT s.* FROM tblSinhVien s " +
                "JOIN tblLop l ON s.MaLop = l.MaLop " +
                "WHERE l.UserID = ?";
        return db.rawQuery(sql, new String[]{String.valueOf(userId)});
    }

    public boolean checkSinhVienQuanLy(String maSV, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT 1 FROM tblSinhVien s " +
                "JOIN tblLop l ON s.MaLop = l.MaLop " +
                "WHERE s.MaSV = ? AND l.UserID = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{maSV, String.valueOf(userId)});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        return exists;
    }

    public void deleteSinhVien(String maSV) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tblSinhVien", "MaSV = ?", new String[]{maSV});
    }

    public void updateSinhVien(String maSV, String ten, String ngaySinh, int diem, String maLop) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("HoTen", ten);
        values.put("NgaySinh", ngaySinh);
        values.put("DiemRenLuyen", diem);
        values.put("MaLop", maLop);
        db.update("tblSinhVien", values, "MaSV = ?", new String[]{maSV});
    }

    public Cursor getTenGiaovien(int UserID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tblUser_GiaoVien WHERE UserID = ?", new String[]{String.valueOf(UserID)});
    }
}
