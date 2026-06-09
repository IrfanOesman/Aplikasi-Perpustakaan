/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.perpustakaan.Model;

/**
 *
 * @author DHANI
 */
public class Anggota {
    private int idAnggota;
    private String nama;
    private String noAnggota;
    private String email;
    private String noTelpon;
    private String alamat;
    private String status; 

    // Constructor kosong
    public Anggota() {}

    // Constructor lengkap
    public Anggota(int idAnggota, String nama, String noAnggota,
                   String email, String noTelpon, String alamat,
                   String status) {
        this.idAnggota = idAnggota;
        this.nama = nama;
        this.noAnggota = noAnggota;
        this.email = email;
        this.noTelpon = noTelpon;
        this.alamat = alamat;
        this.status = status;
    }

    // Constructor tanpa id (untuk INSERT baru)
    public Anggota(String nama, String noAnggota, String email, 
                   String noTelpon, String alamat, String status) {
        this.nama = nama;
        this.noAnggota = noAnggota;
        this.email = email;
        this.noTelpon = noTelpon;
        this.alamat = alamat;
        this.status = status;
    }

    // Getter
    public int getIdAnggota() { return idAnggota; }
    public String getNama() { return nama; }
    public String getNoAnggota() { return noAnggota; }
    public String getEmail() { return email; }
    public String getNoTelpon() { return noTelpon; }
    public String getAlamat() { return alamat; }
    public String getStatus() { return status; }

    // Setter
    public void setIdAnggota(int idAnggota) { this.idAnggota = idAnggota; }
    public void setNama(String nama) { this.nama = nama; }
    public void setNoAnggota(String noAnggota) { this.noAnggota = noAnggota; }
    public void setEmail(String email) { this.email = email; }
    public void setNoTelpon(String noTelpon) { this.noTelpon = noTelpon; }
    public void setAlamat(String alamat) { this.alamat = alamat; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return noAnggota + " - " + nama;
    }
}
