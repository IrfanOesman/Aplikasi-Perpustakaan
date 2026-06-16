/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.perpustakaan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Irfan Oesman Asa
 */
public class Buku extends javax.swing.JFrame {
     private int idLogin;
    private String namaAdmin;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Buku.class.getName());
    DefaultTableModel model;
    private Connection conn;
    
    private void tampilData() {
      
    model.setRowCount(0); // Reset data tabel di UI
    try {
        
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM buku");
//         Object[] kolom = {"ID Buku", "Judul", "Pengarang", "ISBN", "Kategori", "Tahun Terbit", "Status"};
        while(rs.next()) {
            Object[] data = {
                rs.getString("id_buku"),
                rs.getString("judul"),
                rs.getString("pengarang"),
                rs.getString("isbn"),
                rs.getString("kategori"),
                rs.getString("tahun_terbit"),
                rs.getString("status")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }
}
    private void buttonTanbah(){
    try {
        String sql = "INSERT INTO buku (judul, pengarang, isbn, kategori, tahun_terbit, status) VALUES (?, ?, ?, ?, ?, ?)";
       
        PreparedStatement pst = conn.prepareStatement(sql);
        
        pst.setString(1, fJudul.getText());
        pst.setString(2, fPengarang.getText());
        pst.setString(3, fISBN.getText());
        pst.setString(4, fKategori.getText());
        pst.setInt(5, jYearChooser1.getYear());
        pst.setString(6, cmbStatus.getSelectedItem().toString().toLowerCase()); // Menyesuaikan enum database (lowercase)
        
        pst.executeUpdate();
        JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
         debug.setText(String.valueOf(jYearChooser1.getYear()));
        tampilData();
        bersihkanForm();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }
    private void buttonEdit(){
    int baris = tabel.getSelectedRow();
    if (baris == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data pada tabel terlebih dahulu!");
        return;
    }

    String id_buku = model.getValueAt(baris, 0).toString();
    try {
        String sql = "UPDATE buku SET judul=?, pengarang=?, isbn=?, kategori=?, tahun_terbit=?, status=? WHERE id_buku=?";
       
        PreparedStatement pst = conn.prepareStatement(sql);

        pst.setString(1, fJudul.getText());
        pst.setString(2, fPengarang.getText());
        pst.setString(3, fISBN.getText());
        pst.setString(4, fKategori.getText());
        pst.setInt(5, jYearChooser1.getYear()); // Update tahun dari JYearChooser
        pst.setString(6, cmbStatus.getSelectedItem().toString().toLowerCase());
        pst.setString(7, id_buku);

        pst.executeUpdate();
        JOptionPane.showMessageDialog(null, "Data Berhasil Diubah");
        tampilData();
        bersihkanForm();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }
    private void buttonHapus(){
                                              
    int baris = tabel.getSelectedRow();
    if (baris == -1) {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus!");
        return;
    }
    
    int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
    if(konfirmasi == JOptionPane.YES_OPTION) {
        String id_buku = model.getValueAt(baris, 0).toString();
        try {
            String sql = "DELETE FROM buku WHERE id_buku=?";
           
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, id_buku);
            
            pst.executeUpdate();
            JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
            tampilData();
            bersihkanForm();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
    }
    private void buttonCari(){
                                      
    model.setRowCount(0);
    String cari = fCari.getText();
    try {
        String sql = "SELECT * FROM buku WHERE judul LIKE ? OR pengarang LIKE ? OR isbn LIKE ?";
       
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%" + cari + "%");
        pst.setString(2, "%" + cari + "%");
        pst.setString(3, "%" + cari + "%");
        
        ResultSet rs = pst.executeQuery();
        while(rs.next()) {
            Object[] data = {
                rs.getString("id_buku"),
                rs.getString("judul"),
                rs.getString("pengarang"),
                rs.getString("isbn"),
                rs.getString("kategori"),
                rs.getString("tahun_terbit"),
                rs.getString("status")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
}
    
    private void bersihkanForm() {
    fJudul.setText("");
    fPengarang.setText("");
    fISBN.setText("");
    fKategori.setText("");
    int tahunSekarang = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
    jYearChooser1.setYear(tahunSekarang);
    cmbStatus.setSelectedIndex(0);
    fJudul.requestFocus();
}
    public Buku(int idLogin, String namaUser) {
        initComponents();
        this.setLocationRelativeTo(null);
        lblNama.setText(namaUser);
        this.idLogin = idLogin;
        this.namaAdmin = namaAdmin;
         Koneksi db = new Koneksi();
        this.conn = db.connect();
        if (this.conn == null) {
            JOptionPane.showMessageDialog(this, "Aplikasi gagal terhubung ke database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Di dalam Constructor (bawah initComponents)
        Object[] kolom = {"ID Buku", "Judul", "Pengarang", "ISBN", "Kategori", "Tahun Terbit", "Status"};
        model = new DefaultTableModel(null, kolom);
        tabel.setModel(model);
        tampilData();
      }
            
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        fCari = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        fJudul = new javax.swing.JTextField();
        fISBN = new javax.swing.JTextField();
        fPengarang = new javax.swing.JTextField();
        jYearChooser1 = new com.toedter.calendar.JYearChooser();
        fKategori = new javax.swing.JTextField();
        cmbStatus = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        lblNama = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        debug = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton3.setBackground(new java.awt.Color(0, 0, 102));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Tambah");
        jButton3.addActionListener(this::jButton3ActionPerformed);

        jButton4.setBackground(new java.awt.Color(0, 0, 102));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Edit");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jButton5.setBackground(new java.awt.Color(0, 0, 102));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("Hapus");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jButton6.setBackground(new java.awt.Color(0, 0, 102));
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("Bersihkan");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        fCari.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fCari.addActionListener(this::fCariActionPerformed);

        jLabel1.setBackground(new java.awt.Color(204, 204, 204));
        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel1.setText("Cari:");

        jLabel2.setText("Judul");

        jLabel3.setText("Pengarang");

        jLabel4.setText("Kategori");

        jLabel5.setText("Tahun");

        jLabel6.setText("ISBN");

        jLabel7.setText("Status");

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tersedia", "Dipinjam" }));

        tabel.setModel(new javax.swing.table.DefaultTableModel(
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
        tabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabelMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel);

        jButton1.setText("Cari");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/account 32px.png"))); // NOI18N

        lblNama.setText("Kelompok Perpustakaan");

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/dots 16px.png"))); // NOI18N
        jLabel12.setText("Online");

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

        jMenuItem3.setText("Dashboard");
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
                        .addGap(44, 44, 44)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel7))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(fKategori)
                                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jYearChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(fJudul)
                                            .addComponent(fCari)
                                            .addComponent(fISBN)
                                            .addComponent(fPengarang, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButton1))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(jButton3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton4)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton5)
                                        .addGap(18, 18, 18)
                                        .addComponent(jButton6)))
                                .addGap(80, 80, 80)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(lblNama)
                                            .addComponent(jLabel12)))
                                    .addComponent(debug, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addComponent(jLabel1))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fCari, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jButton1))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblNama)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jLabel11)))))
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(fJudul, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(fPengarang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(debug)))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(31, 31, 31)
                        .addComponent(jLabel6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jYearChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(fISBN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(fKategori, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(jButton4)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton5)
                        .addComponent(jButton6)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        buttonTanbah();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        buttonEdit();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        buttonHapus();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        bersihkanForm();
        tampilData();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void fCariActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fCariActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fCariActionPerformed

    private void tabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelMouseClicked
    int baris = tabel.getSelectedRow();
    if (baris != -1) {
        // ID Buku disimpan di variabel temporary atau bisa langsung ambil dari tabel jika dibutuhkan
        fJudul.setText(model.getValueAt(baris, 1).toString());
        fPengarang.setText(model.getValueAt(baris, 2).toString());
        fISBN.setText(model.getValueAt(baris, 3).toString());
        fKategori.setText(model.getValueAt(baris, 4).toString());
        // Ambil string tahun dari tabel, ubah jadi Integer, lalu set ke JYearChooser
        int tahunBuku = Integer.parseInt(model.getValueAt(baris, 5).toString());
        jYearChooser1.setYear(tahunBuku);
        String status = model.getValueAt(baris, 6).toString();
        // Menyesuaikan item ComboBox (Tersedia / Dipinjam)
        if(status.equalsIgnoreCase("tersedia")) cmbStatus.setSelectedIndex(0);
        else cmbStatus.setSelectedIndex(1);
    }
    }//GEN-LAST:event_tabelMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      buttonCari();
    }//GEN-LAST:event_jButton1ActionPerformed

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
        new LaporanDashboard(idLogin,namaAdmin).setVisible(true);
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
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JLabel debug;
    private javax.swing.JTextField fCari;
    private javax.swing.JTextField fISBN;
    private javax.swing.JTextField fJudul;
    private javax.swing.JTextField fKategori;
    private javax.swing.JTextField fPengarang;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JYearChooser jYearChooser1;
    private javax.swing.JLabel lblNama;
    private javax.swing.JTable tabel;
    // End of variables declaration//GEN-END:variables
}
