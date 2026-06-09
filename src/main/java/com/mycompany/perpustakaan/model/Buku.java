package com.mycompany.perpustakaan.model;

/**
 * Model class untuk entitas Buku.
 * Mencerminkan struktur tabel `buku` di database perpustakaan.
 */
public class Buku {

    private int    id;
    private String judul;
    private String pengarang;
    private String isbn;
    private String kategori;
    private String status;

    // ── Constructor penuh: dipakai saat baca data dari DB ─────────────────
    public Buku(int id, String judul, String pengarang,
                String isbn, String kategori, String status) {
        this.id        = id;
        this.judul     = judul;
        this.pengarang = pengarang;
        this.isbn      = isbn;
        this.kategori  = kategori;
        this.status    = status;
    }

    // ── Constructor tanpa ID: dipakai saat tambah buku baru ke DB ─────────
    public Buku(String judul, String pengarang,
                String isbn, String kategori, String status) {
        this(0, judul, pengarang, isbn, kategori, status);
    }

    // ── Getters ───────────────────────────────────────────────────────────
    public int    getId()        { return id; }
    public String getJudul()     { return judul; }
    public String getPengarang() { return pengarang; }
    public String getIsbn()      { return isbn; }
    public String getKategori()  { return kategori; }
    public String getStatus()    { return status; }

    // ── Setters ───────────────────────────────────────────────────────────
    public void setId(int id)               { this.id = id; }
    public void setJudul(String judul)      { this.judul = judul; }
    public void setPengarang(String p)      { this.pengarang = p; }
    public void setIsbn(String isbn)        { this.isbn = isbn; }
    public void setKategori(String k)       { this.kategori = k; }
    public void setStatus(String status)    { this.status = status; }

    @Override
    public String toString() {
        return "Buku{id=" + id + ", judul='" + judul + "', status='" + status + "'}";
    }
}