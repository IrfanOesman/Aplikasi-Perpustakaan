package com.mycompany.perpustakaan.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class DBConnection {

    private static final String HOST     = "localhost";
    private static final String PORT     = "3307";
    private static final String DB_NAME  = "perpustakaan";
    private static final String USER     = "root";
    private static final String PASSWORD = "";

    private static final String URL =
        "jdbc:mysql://" + HOST + ":" + PORT + "/" + DB_NAME
        + "?useSSL=false&serverTimezone=Asia/Jakarta&autoReconnect=true"
        + "&useUnicode=true&characterEncoding=UTF-8"
        + "&createDatabaseIfNotExist=true";

    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DBConnection] Koneksi berhasil ke " + DB_NAME);
                createTablesIfNotExist(connection);
            }
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null,
                "Driver MySQL tidak ditemukan!\n\nTambahkan dependency mysql-connector-j ke pom.xml",
                "Error: Driver Tidak Ada", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                "Gagal terhubung ke database!\n\nPesan: " + e.getMessage()
                + "\n\nPastikan XAMPP MySQL sudah aktif.",
                "Error: Koneksi Gagal", JOptionPane.ERROR_MESSAGE);
        }
        return connection;
    }

    private static void createTablesIfNotExist(Connection conn) {
        String sql = "CREATE TABLE IF NOT EXISTS `buku` ("
                   + "  `id`        INT(11)      NOT NULL AUTO_INCREMENT,"
                   + "  `judul`     VARCHAR(200) NOT NULL,"
                   + "  `pengarang` VARCHAR(100) NOT NULL,"
                   + "  `isbn`      VARCHAR(30)  DEFAULT NULL,"
                   + "  `kategori`  VARCHAR(50)  DEFAULT NULL,"
                   + "  `status`    VARCHAR(20)  NOT NULL DEFAULT 'Tersedia',"
                   + "  PRIMARY KEY (`id`)"
                   + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 "
                   + "  COLLATE=utf8mb4_unicode_ci";
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(sql);
            System.out.println("[DBConnection] Tabel 'buku' siap.");
        } catch (SQLException e) {
            System.err.println("[DBConnection] Gagal buat tabel: " + e.getMessage());
        }
    }

    public static void tutupKoneksi() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("[DBConnection] Koneksi ditutup.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean cekKoneksi() {
        return getConnection() != null;
    }
}