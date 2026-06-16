/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.perpustakaan;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sajin
 */
public class LaporanDashboard extends javax.swing.JFrame {
    DefaultTableModel modelDipinjam;
    DefaultTableModel modelTopBooks;
    private int idLogin;
    private String namaAdmin;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LaporanDashboard.class.getName());
    private Connection conn;
    
    private void loadTabelDipinjam() {// PERBAIKAN 1: Bersihkan model global sebelum memuat ulang data agar tidak duplikat
        modelDipinjam.setRowCount(0);
        
        try {
            // Menggunakan klausa WHERE p.status = 'dipinjam' agar sesuai fungsi tab
            String sql = "SELECT p.id_peminjam, a.nama, b.judul, " +
                         "p.tanggal_pinjam, p.tanggal_jatuh_tempo, p.status " +
                         "FROM peminjaman p " +
                         "INNER JOIN anggota a ON p.id_anggota = a.id_anggota " +
                         "INNER JOIN buku b ON p.id_buku = b.id_buku " +
                         "WHERE p.status = 'dipinjam'";
            
            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet res = pst.executeQuery();

            while (res.next()) {
                modelDipinjam.addRow(new Object[]{
                    res.getString("id_peminjam"), // Disesuaikan dengan nama kolom database asli 'id_peminiam'
                    res.getString("nama"), 
                    res.getString("judul"),   
                    res.getString("tanggal_pinjam"),     
                    res.getString("tanggal_jatuh_tempo"), 
                    res.getString("status")
                });
            }
            
            tabelBukuSedangDipinjam.setModel(modelDipinjam);
            
        } catch (SQLException e) {
            javax.swing.JOptionPane.showMessageDialog(this, "Error Load Data: " + e.getMessage());
        }
    }
    private void loadTableTopBooks(){// PERBAIKAN 2: Bersihkan model global top books sebelum memuat ulang data
        modelTopBooks.setRowCount(0);

        try {
            String sql = "SELECT b.judul, b.pengarang, b.kategori, COUNT(p.id_buku) AS total_dipinjam " +
                         "FROM peminjaman p " +
                         "INNER JOIN buku b ON p.id_buku = b.id_buku " +
                         "GROUP BY p.id_buku, b.judul, b.pengarang, b.kategori " +
                         "ORDER BY total_dipinjam DESC";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            int ranking = 1;
            while (rs.next()) {
                modelTopBooks.addRow(new Object[]{
                    ranking++,
                    rs.getString("judul"),
                    rs.getString("pengarang"),
                    rs.getString("kategori"),
                    rs.getInt("total_dipinjam")
                });
            }

            tabelTopBooks.setModel(modelTopBooks);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
    private void loadDashboard(){
        try {
       String sql = "SELECT COUNT(*) AS total_dipinjam " +
                 "FROM peminjaman WHERE status = 'Dipinjam'";

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            int total = rs.getInt("total_dipinjam");
            jLabel8.setText(String.valueOf(total));
        }
    } catch (Exception e) {
         javax.swing.JOptionPane.showMessageDialog(this, "Error Load Data: " + e.getMessage());
    }
    try {
        String sql = "SELECT b.judul, COUNT(*) AS total_peminjaman " +
             "FROM peminjaman p " +
             "INNER JOIN buku b ON p.id_buku = b.id_buku " +
             "GROUP BY b.id_buku, b.judul " +
             "ORDER BY total_peminjaman DESC " +
             "LIMIT 1";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String judul = rs.getString("judul");
                jLabel17.setText(judul);
            }
    } catch (Exception e) {
         javax.swing.JOptionPane.showMessageDialog(this, "Error Load Data: " + e.getMessage());
    }
    try {
        String sql = "SELECT COUNT(*) AS total_dikembalikan " +
             "FROM peminjaman " +
             "WHERE status = 'dikembalikan'";

            PreparedStatement pst = conn.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                jLabel13.setText(rs.getString("total_dikembalikan"));
}
    } catch (Exception e) {
    }
    }
    private void buttonCari(){String keyword = fCari.getText().trim();
        int tabAktif = tabPane.getSelectedIndex(); // Mendapatkan tab yang sedang dibuka user
        System.out.println("Keyword dicari: " + keyword + " | Tab Aktif: " + tabAktif);
//        if (keyword.isEmpty()) {
//            loadTabelDipinjam();
//            loadTableTopBooks();
//            return;
//        }
        
        try {
            if (tabAktif == 0) { 
                // ISI LOGIKA SEARCH UNTUK TAB 1 (Buku Sedang Dipinjam)
                modelDipinjam.setRowCount(0);
                
                // PERBAIKAN 3: Menambahkan p.status pada klausa SELECT agar rs.getString("status") terbaca sempurna
                String sql = "SELECT p.id_peminjam, a.nama, b.judul, p.tanggal_pinjam, p.tanggal_jatuh_tempo, p.status " +
                             "FROM peminjaman p " +
                             "INNER JOIN anggota a ON p.id_anggota = a.id_anggota " +
                             "INNER JOIN buku b ON p.id_buku = b.id_buku " +
                             "WHERE p.status = 'dipinjam' AND (b.judul LIKE ? OR a.nama LIKE ?)";
                             
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, "%" + keyword + "%");
                pst.setString(2, "%" + keyword + "%");
                ResultSet rs = pst.executeQuery();
                
                while(rs.next()) {
                    modelDipinjam.addRow(new Object[]{
                        rs.getString("id_peminjam"), 
                        rs.getString("nama"), 
                        rs.getString("judul"),   
                        rs.getString("tanggal_pinjam"),     
                        rs.getString("tanggal_jatuh_tempo"), 
                        rs.getString("status")
                    });
                }
                
                // PERBAIKAN UTAMA 4: Pindahkan setModel ke LUAR loop while agar JTable visual dipaksa refresh walau data kosong
                tabelBukuSedangDipinjam.setModel(modelDipinjam);
                
            } else if (tabAktif == 1) { 
                // ISI LOGIKA SEARCH UNTUK TAB 2 (Top Books)
                modelTopBooks.setRowCount(0);
                String sql = "SELECT b.judul, b.pengarang, b.kategori, COUNT(p.id_buku) AS total_dipinjam " +
                             "FROM peminjaman p " +
                             "INNER JOIN buku b ON p.id_buku = b.id_buku " +
                             "WHERE b.judul LIKE ? OR b.kategori LIKE ? " +
                             "GROUP BY p.id_buku, b.judul, b.pengarang, b.kategori " +
                             "ORDER BY total_dipinjam DESC";
                             
                PreparedStatement pst = conn.prepareStatement(sql);
                pst.setString(1, "%" + keyword + "%");
                pst.setString(2, "%" + keyword + "%");
                ResultSet rs = pst.executeQuery();
                
                int ranking = 1;
                while(rs.next()) {
                    modelTopBooks.addRow(new Object[]{
                        ranking++,
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getString("kategori"),
                        rs.getInt("total_dipinjam")
                    });
                }
                
                // PERBAIKAN UTAMA 5: Pindahkan setModel ke LUAR loop while demi konsistensi data visual
                tabelTopBooks.setModel(modelTopBooks);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saat mencari: " + e.getMessage());
        }
    }
    
    /**
     * Creates new form LaporanDashboard
     */
    public LaporanDashboard(int idLogin, String namaUser) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.idLogin = idLogin;
        this.namaAdmin = namaUser;
        lblNama.setText(namaUser);
        try {
        String url ="jdbc:mysql://localhost:3307/db_perpustakaan";
        String user="root";
        String pass="";
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url,user,pass);
    } catch (Exception e) {
         e.printStackTrace();
    }
        // Setup Tab 1: Buku Sedang Dipinjam
        Object[] kolomSedang = {"ID Pinjam", "Nama Anggota", "Judul Buku", "Tgl Pinjam", "Jatuh Tempo", "Status"};
        modelDipinjam = new DefaultTableModel(null, kolomSedang);
        tabelBukuSedangDipinjam.setModel(modelDipinjam);

        // Setup Tab 2: Top Books
        Object[] kolomTop = {"Peringkat", "Judul Buku", "Pengarang", "Kategori", "Total Dipinjam"};
        modelTopBooks = new DefaultTableModel(null, kolomTop);
        tabelTopBooks.setModel(modelTopBooks);
        loadDashboard();
        loadTabelDipinjam();
        loadTableTopBooks();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel9 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        tabPane = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabelBukuSedangDipinjam = new javax.swing.JTable();
        jTextField4 = new javax.swing.JTextField();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jButton16 = new javax.swing.JButton();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabelTopBooks = new javax.swing.JTable();
        fCari = new javax.swing.JTextField();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        lblNama = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/WhatsApp Image 2026-05-20 at 20.04.07 (1).jpeg"))); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 255));
        jLabel1.setText("DASHBOARD & LAPORAN PERPUSTAKAAN");

        jPanel1.setBackground(new java.awt.Color(255, 0, 51));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/star 64px.png"))); // NOI18N

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setText("Buku Paling Sering Dipinjam :");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel17.setText("Laskar Pelangi");

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setText("Top Borrowed Book");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel15)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16)
                    .addComponent(jLabel18))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel18))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel15)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(0, 204, 0));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Buku Sedang Dipinjam :");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel8.setText("248");

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/open-book 64px.png"))); // NOI18N

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Total Buku Aktif");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel10)
                    .addComponent(jLabel8)
                    .addComponent(jLabel2))
                .addGap(26, 26, 26))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(0, 10, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel9)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 0));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/wall-clock 64px.png"))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel12.setText("Total Riwayat Peminjaman : ");

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel13.setText("1.532");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Transaksi Selesai");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel14)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 6, Short.MAX_VALUE)
                        .addComponent(jLabel12))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jLabel11)))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        jLabel3.setText("---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");

        jPanel10.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        tabelBukuSedangDipinjam.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane4.setViewportView(tabelBukuSedangDipinjam);

        jButton10.setText("Search");

        jButton11.setText("Refresh");
        jButton11.addActionListener(this::jButton11ActionPerformed);

        jButton12.setText("Print");
        jButton12.addActionListener(this::jButton12ActionPerformed);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton12, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jButton11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        tabPane.addTab("Buku Sedang Dipinjam", jPanel7);

        jPanel11.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jButton16.setText("Print");

        tabelTopBooks.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane6.setViewportView(tabelTopBooks);

        jButton17.setText("Search");
        jButton17.addActionListener(this::jButton17ActionPerformed);

        jButton18.setText("Refresh");
        jButton18.addActionListener(this::jButton18ActionPerformed);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton18, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(fCari, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jButton18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton16))
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        tabPane.addTab("Top Books", jPanel8);

        jLabel20.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/WhatsApp Image 2026-05-20 at 20.04.07 (1).jpeg"))); // NOI18N

        lblNama.setText("Kelompok Perpustakaan");

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/dots 16px.png"))); // NOI18N
        jLabel6.setText("Online");

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/account 32px.png"))); // NOI18N

        jMenu1.setText("Akun");

        jMenuItem4.setText("Logout");
        jMenuItem4.addActionListener(this::jMenuItem4ActionPerformed);
        jMenu1.add(jMenuItem4);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Menu");

        jMenuItem1.setText("Peminjaman");
        jMenuItem1.addActionListener(this::jMenuItem1ActionPerformed);
        jMenu2.add(jMenuItem1);

        jMenuItem2.setText("Manajemen Anggota");
        jMenuItem2.addActionListener(this::jMenuItem2ActionPerformed);
        jMenu2.add(jMenuItem2);

        jMenuItem3.setText("Manajemen Buku");
        jMenuItem3.addActionListener(this::jMenuItem3ActionPerformed);
        jMenu2.add(jMenuItem3);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 917, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNama)
                            .addComponent(jLabel6))
                        .addGap(16, 16, 16))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(29, 29, 29)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblNama)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel6))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addComponent(jLabel7)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addComponent(jLabel3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tabPane, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
      loadDashboard();
      loadTabelDipinjam();
      loadTableTopBooks();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton18ActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        buttonCari();
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
    int tabAktif = tabPane.getSelectedIndex();
    MessageFormat header;
    MessageFormat footer = new MessageFormat("Halaman {0,number,integer}");
    boolean complete = false;
    
    try {
        if (tabAktif == 0) {
            if(tabelBukuSedangDipinjam.getRowCount() == 0) return;
            header = new MessageFormat("Laporan Daftar Buku Sedang Dipinjam");
            complete = tabelBukuSedangDipinjam.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        } else {
            if(tabelTopBooks.getRowCount() == 0) return;
            header = new MessageFormat("Laporan Statistik Buku Terpopuler (Top Books)");
            complete = tabelTopBooks.print(JTable.PrintMode.FIT_WIDTH, header, footer);
        }
        
        if (complete) {
            JOptionPane.showMessageDialog(this, "Cetak dokumen berhasil.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal mencetak: " + e.getMessage());
    }
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
    this.dispose();
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
    new ManagementPeminjaman(idLogin,namaAdmin).setVisible(true);
    this.dispose();
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
    new Manajemen_Anggota(idLogin,namaAdmin).setVisible(true);
    this.dispose();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
      new Buku(idLogin,namaAdmin).setVisible(true);
    this.dispose();
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField fCari;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JLabel lblNama;
    private javax.swing.JTabbedPane tabPane;
    private javax.swing.JTable tabelBukuSedangDipinjam;
    private javax.swing.JTable tabelTopBooks;
    // End of variables declaration//GEN-END:variables
}