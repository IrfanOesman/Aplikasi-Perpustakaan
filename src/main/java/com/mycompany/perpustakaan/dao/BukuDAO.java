package com.mycompany.perpustakaan.dao;

import com.mycompany.perpustakaan.db.DBConnection;
import com.mycompany.perpustakaan.model.Buku;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BukuDAO — semua operasi CRUD untuk tabel `buku`.
 * Koneksi diambil fresh setiap operasi agar auto-reconnect bekerja.
 */
public class BukuDAO {

    // ── Helper: ambil koneksi ─────────────────────────────────────────────
    private Connection getConn() {
        return DBConnection.getConnection();
    }

    // ── READ: semua buku ─────────────────────────────────────────────────
    public List<Buku> getAllBuku() {
        List<Buku> list = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null) return list;

        String sql = "SELECT id, judul, pengarang, isbn, kategori, status "
                   + "FROM buku ORDER BY id";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(buatDariRS(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── READ: cari buku (judul / pengarang / isbn / kategori) ────────────
    public List<Buku> cariBuku(String keyword) {
        List<Buku> list = new ArrayList<>();
        Connection conn = getConn();
        if (conn == null || keyword == null || keyword.isBlank()) return list;

        String sql = "SELECT id, judul, pengarang, isbn, kategori, status "
                   + "FROM buku "
                   + "WHERE judul     LIKE ? "
                   + "   OR pengarang LIKE ? "
                   + "   OR isbn      LIKE ? "
                   + "   OR kategori  LIKE ? "
                   + "ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String k = "%" + keyword.trim() + "%";
            ps.setString(1, k);
            ps.setString(2, k);
            ps.setString(3, k);
            ps.setString(4, k);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(buatDariRS(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── CREATE: tambah buku baru ─────────────────────────────────────────
    public boolean tambahBuku(Buku buku) {
        Connection conn = getConn();
        if (conn == null) return false;

        String sql = "INSERT INTO buku (judul, pengarang, isbn, kategori, status) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, buku.getJudul());
            ps.setString(2, buku.getPengarang());
            ps.setString(3, buku.getIsbn());
            ps.setString(4, buku.getKategori());
            ps.setString(5, buku.getStatus());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UPDATE: perbarui data buku ────────────────────────────────────────
    public boolean updateBuku(Buku buku) {
        Connection conn = getConn();
        if (conn == null) return false;

        String sql = "UPDATE buku "
                   + "SET judul=?, pengarang=?, isbn=?, kategori=?, status=? "
                   + "WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, buku.getJudul());
            ps.setString(2, buku.getPengarang());
            ps.setString(3, buku.getIsbn());
            ps.setString(4, buku.getKategori());
            ps.setString(5, buku.getStatus());
            ps.setInt(6, buku.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── DELETE: hapus buku berdasarkan ID ────────────────────────────────
    public boolean hapusBuku(int id) {
        Connection conn = getConn();
        if (conn == null) return false;

        String sql = "DELETE FROM buku WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ── UTIL: cek apakah buku sedang dipinjam ────────────────────────────
    public boolean isBukuDipinjam(int idBuku) {
        Connection conn = getConn();
        if (conn == null) return false;

        String sql = "SELECT COUNT(*) FROM peminjaman "
                   + "WHERE id_buku=? AND status='Dipinjam'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idBuku);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            // Tabel peminjaman mungkin belum ada — abaikan
        }
        return false;
    }

    // ── Helper: buat objek Buku dari ResultSet ───────────────────────────
    private Buku buatDariRS(ResultSet rs) throws SQLException {
        return new Buku(
            rs.getInt("id"),
            rs.getString("judul"),
            rs.getString("pengarang"),
            rs.getString("isbn"),
            rs.getString("kategori"),
            rs.getString("status")
        );
    }
}