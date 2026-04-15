package com.example.formdangnhap;

public class SinhVien {
    private String maSV;
    private String hoTen;
    private String ngaySinh;
    private int diemRenLuyen;
    private int diemTuDanhGia;
    private String maLop;

    public SinhVien(String maSV, String hoTen, String ngaySinh, int diemRenLuyen, int diemTuDanhGia, String maLop) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.diemRenLuyen = diemRenLuyen;
        this.diemTuDanhGia = diemTuDanhGia;
        this.maLop = maLop;
    }

    public SinhVien(String ma, String ten, String ns, int diem, String lop) {
    }

    public String getMaSV() {
        return maSV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public int getDiemRenLuyen() {
        return diemRenLuyen;
    }

    public int getDiemTuDanhGia() {
        return diemTuDanhGia;
    }

    public String getMaLop() {
        return maLop;
    }
}
