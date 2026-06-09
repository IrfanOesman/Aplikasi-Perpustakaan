package com.mycompany.perpustakaan;

import com.mycompany.perpustakaan.db.DBConnection;
import com.mycompany.perpustakaan.ui.ManagementBukuForm;

/**
 * Entry point aplikasi Perpustakaan.
 * Class ini yang dipanggil pertama saat program dijalankan.
 *
 * @author Irfan Oesman Asa
 */
public class Perpustakaan {

    public static void main(String[] args) {

        // 1. Set tampilan Nimbus (lebih modern dari default Java)
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info
                    : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nimbus tidak ada → pakai L&F default, tidak masalah
        }

        // 2. Tutup koneksi DB otomatis saat aplikasi ditutup
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DBConnection.tutupKoneksi();
            System.out.println("[App] Koneksi database ditutup. Sampai jumpa!");
        }));

        // 3. Buka form utama di Event Dispatch Thread (EDT)
        java.awt.EventQueue.invokeLater(() ->
            new ManagementBukuForm().setVisible(true)
        );
    }
}