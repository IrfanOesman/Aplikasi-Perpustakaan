/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.perpustakaan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Irfan Oesman Asa
 */
public class Koneksi {
     private Connection koneksi;
    
    public Connection connect() {
        // 1. Load Driver MySQL
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Menyesuaikan ke driver mysql-connector-j terbaru
            System.out.println("Berhasil Load Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Gagal Load Driver: " + ex);
        }
        
        // 2. Koneksi ke Database 'mahasiswa'
        String url = "jdbc:mysql://localhost:3306/mahasiswa";
        try {
            koneksi = DriverManager.getConnection(url, "root", "");
            System.out.println("Berhasil Koneksi Database");
        } catch (SQLException ex) {
            System.out.println("Gagal Koneksi Database: " + ex);
        }
        
        return koneksi;
    }
}
