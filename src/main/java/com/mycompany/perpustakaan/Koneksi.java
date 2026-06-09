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


 * @author sajin
 */
public class Koneksi {
    
    private Connection koneksi;
    
    public Connection connect(){
        // 1. Load Driver MySQL
        try {
            // Menggunakan driver MySQL cj (disarankan untuk MySQL versi baru)
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException ex){
            System.out.println("Driver tidak ditemukan: " + ex);
        }
        
        // 2. Hubungkan ke Database
        String url = "jdbc:mysql://localhost:3307/db_perpustakaan";
        try {
            koneksi = DriverManager.getConnection(url, "root", "");
            System.out.println("Koneksi Database Berhasil!");
        } catch (SQLException ex){
            // Koreksi pesan: ini adalah kondisi GAGAL
            System.out.println("Koneksi Database GAGAL: " + ex); 
        }
        
        return koneksi;
    }   
}

