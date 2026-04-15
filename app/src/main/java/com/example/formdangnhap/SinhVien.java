package com.example.formdangnhap;

public class SinhVien {
    private String maSV;
    private String hoTen;
    private String ngaySinh;
    private int diemRenLuyen;
    private String maLop;

    public SinhVien(String maSV, String hoTen, String ngaySinh, int diemRenLuyen, String maLop) {
        this.maSV = maSV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.diemRenLuyen = diemRenLuyen;
        this.maLop = maLop;
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

    public String getMaLop() {
        return maLop;
    }
}
