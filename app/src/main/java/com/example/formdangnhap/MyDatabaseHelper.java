package com.example.formdangnhap;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public MyDatabaseHelper(Context context) {
        super(context, "Qlysv.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng
        db.execSQL("CREATE TABLE tblUser_GiaoVien (" +
                "UserID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "HoTen TEXT, Email TEXT UNIQUE, MatKhau TEXT, TrangThai INTEGER)");

        // Chèn dữ liệu mẫu ngay khi tạo xong database
        db.execSQL("INSERT INTO tblUser_GiaoVien (HoTen, Email, MatKhau, TrangThai) " +
                "VALUES ('Nguyễn Văn Tiến', 'tien@gmail.com', '123', 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tblUser_GiaoVien");
        onCreate(db);
    }
}