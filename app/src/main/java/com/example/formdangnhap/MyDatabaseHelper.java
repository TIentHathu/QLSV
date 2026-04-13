package com.example.formdangnhap;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public MyDatabaseHelper(Context context) {
        // Tăng version lên 3 để cập nhật cấu trúc bảng mới
        super(context, "Qlysv.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Bảng Giáo viên (Hợp nhất các thuộc tính)
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

        // 2. Bảng Lớp (UserID là khóa ngoại liên kết với bảng Giáo viên)
        db.execSQL("CREATE TABLE tblLop (" +
                "MaLop TEXT PRIMARY KEY, " +
                "TenLop TEXT, " +
                "UserID INTEGER, " +
                "FOREIGN KEY(UserID) REFERENCES tblUser_GiaoVien(UserID))");

        // 3. Bảng Sinh viên (Sinh viên thuộc về lớp)
        db.execSQL("CREATE TABLE tblSinhVien (" +
                "MaSV TEXT PRIMARY KEY, " +
                "HoTen TEXT, " +
                "NgaySinh TEXT, " +
                "DiemRenLuyen INTEGER, " +
                "MaLop TEXT, " +
                "FOREIGN KEY(MaLop) REFERENCES tblLop(MaLop))");

        // --- CHÈN DỮ LIỆU MẪU ---

        // Giáo viên 1: Nguyễn Văn Tiến
        db.execSQL("INSERT INTO tblUser_GiaoVien (HoTen, Email, MatKhau, TrangThai) " +
                "VALUES ('Nguyễn Văn Tiến', 'tien@gmail.com', '123', 1)");

        // Giáo viên 2: Vũ Văn Huy
        db.execSQL("INSERT INTO tblUser_GiaoVien (HoTen, Email, MatKhau, TrangThai) " +
                "VALUES ('Vũ Văn Huy', 'huy@gmail.com', '123', 1)");

        // Thầy Tiến (ID 1) quản lý lớp L01
        db.execSQL("INSERT INTO tblLop VALUES ('L01', 'Công nghệ thông tin 1', 1)");
        
        // Thầy Huy (ID 2) quản lý lớp L02
        db.execSQL("INSERT INTO tblLop VALUES ('L02', 'Hệ thống thông tin 2', 2)");

        // Chèn sinh viên vào lớp L01
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV001', 'Trần Văn Bình', '2004-05-20', 85, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV002', 'Lê Thị Hoa', '2004-08-15', 45, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV003', 'Nguyen huy hoang', '2004-01-15', 45, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV004', 'Lê Thôi Nôi', '2004-02-15', 45, 'L01')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV005', 'Hoàng Minh Đức', '2004-03-15', 45, 'L01')");
        
        // Chèn sinh viên vào lớp L02
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV006', 'Phạm Minh Đức', '2005-01-10', 90, 'L02')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV007', 'Thái', '2005-02-10', 70, 'L02')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV008', 'Thành', '2005-03-10', 90, 'L02')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV009', 'Nguyễn Văn Long', '2005-04-10', 92, 'L02')");
        db.execSQL("INSERT INTO tblSinhVien VALUES ('SV010', 'Hoàng Thị Thu Thủy', '2005-05-10', 92, 'L02')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tblSinhVien");
        db.execSQL("DROP TABLE IF EXISTS tblLop");
        db.execSQL("DROP TABLE IF EXISTS tblUser_GiaoVien");
        onCreate(db);
    }

    public Cursor getTenGiaovien(int UserID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM tblUser_GiaoVien WHERE UserID = ?",
                new String[]{String.valueOf(UserID)});
    }
}
