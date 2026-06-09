/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.perpustakaan;

import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import com.mycompany.perpustakaan.Model.Anggota;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author DHANI
 */
public class Manajemen_Anggota extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Manajemen_Anggota.class.getName());
        private Connection conn;
            DefaultTableModel model;

    private String hashPassword(String password) {
    try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(password.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    } catch (Exception ex) {
        throw new RuntimeException(ex);
    }
}
    private void buttonTambah(){
    // Validasi input kosong terlebih dahulu
    if(txtNama.getText().isEmpty() || txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama, Email, dan Password tidak boleh kosong!");
        return;
    }

    try {
        String sql = "INSERT INTO anggota (nama, email, password, role, no_telpon, alamat, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(sql);
        
        pst.setString(1, txtNama.getText());
        pst.setString(2, txtEmail.getText());
        
        // HASH PASSWORD SEBELUM MASUK DATABASE
        String encryptedPassword = hashPassword(String.valueOf(txtPassword.getPassword()));
        pst.setString(3, encryptedPassword);
        
        pst.setString(4, cmbRole.getSelectedItem().toString()); // 'Admin' atau 'Anggota'
        pst.setString(5, txtHp.getText());
        pst.setString(6, areaAlamat.getText());
        pst.setString(7, cmbStatus.getSelectedItem().toString().toLowerCase()); // 'aktif' atau 'nonaktif'
        
        pst.executeUpdate();
        JOptionPane.showMessageDialog(null, "Anggota Baru Berhasil Ditambahkan!");
        tampilData();
        bersihkanForm();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal Menambah Data: " + e.getMessage());
    }
    }
    private void buttonEdit(){
    if (txtId.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih data pada tabel terlebih dahulu!");
        return;
    } 
    try { 
            String sql = "UPDATE anggota SET nama=?, email=?, role=?, no_telpon=?, alamat=?, status=? WHERE id_anggota=?";    
        PreparedStatement pst = conn.prepareStatement(sql);       
        pst.setString(1, txtNama.getText());
        pst.setString(2, txtEmail.getText());

            pst.setString(3, cmbRole.getSelectedItem().toString());
            pst.setString(4, txtHp.getText());
            pst.setString(5, areaAlamat.getText());
            pst.setString(6, cmbStatus.getSelectedItem().toString().toLowerCase());
            pst.setString(7, txtId.getText());

        pst.executeUpdate();
        JOptionPane.showMessageDialog(null, "Data Anggota Berhasil Diperbarui");
        tampilData();
        bersihkanForm();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }
    private void buttonHapus(){
    if (txtId.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih data anggota yang ingin dihapus!");
        return;
    }
    
    int konfirmasi = JOptionPane.showConfirmDialog(this, "Hapus data anggota dengan ID: " + txtId.getText() + "?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
    if(konfirmasi == JOptionPane.YES_OPTION) {
        try {
            String sql = "DELETE FROM anggota WHERE id_anggota=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, txtId.getText());
            
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
    String keyword = txtNama.getText(); // mengambil teks pengetikan dari field Nama
    try {
        String sql = "SELECT * FROM anggota WHERE nama LIKE ? OR id_anggota LIKE ? OR no_telpon LIKE ?";
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, "%" + keyword + "%");
        pst.setString(2, "%" + keyword + "%");
        pst.setString(3, "%" + keyword + "%");
        
        ResultSet rs = pst.executeQuery();
        while(rs.next()) {
            Object[] data = {
                rs.getString("id_anggota"),
                rs.getString("nama"),
                rs.getString("email"),
                rs.getString("role"),
                rs.getString("no_telpon"),
                rs.getString("alamat"),
                rs.getString("status")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, e.getMessage());
    }
    }
    private void tampilData() {
    model.setRowCount(0);
    try {
        Statement st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT * FROM anggota");
        
        while(rs.next()) {
            Object[] data = {
                rs.getString("id_anggota"),
                rs.getString("nama"),
                rs.getString("email"),
                rs.getString("role"),
                rs.getString("no_telpon"),
                rs.getString("alamat"),
                rs.getString("status")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data: " + e.getMessage());
    }
}
    private void bersihkanForm() {
    txtId.setText("");
    txtNama.setText("");
    txtHp.setText("");
    txtEmail.setText("");
    txtPassword.setText("");
    areaAlamat.setText("");
    cmbStatus.setSelectedIndex(0); // Aktif
    cmbRole.setSelectedIndex(0);   // Anggota
    txtNama.requestFocus();
}
    
    public Manajemen_Anggota() {
        initComponents();
        Koneksi db = new Koneksi();
        this.conn = db.connect();
        if (this.conn == null) {
            JOptionPane.showMessageDialog(this, "Aplikasi gagal terhubung ke database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        // Inisialisasi kolom JTable sesuai database Anda
        Object[] kolom = {"ID Anggota", "Nama", "Email", "Role", "No Telpon", "Alamat", "Status"};
        model = new DefaultTableModel(null, kolom);
        tabel.setModel(model);

        // ID Anggota biasanya AUTO_INCREMENT, sebaiknya di-disable agar user tidak mengisi manual
        txtId.setEditable(false); 

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

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jTextField4 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        areaAlamat = new javax.swing.JTextArea();
        txtNama = new javax.swing.JTextField();
        txtId = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtHp = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        cmbStatus = new javax.swing.JComboBox<>();
        cmbRole = new javax.swing.JComboBox<>();
        txtPassword = new javax.swing.JPasswordField();
        jLabel14 = new javax.swing.JLabel();

        jLabel1.setText("jLabel1");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        jTextField4.setText("jTextField4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton3.setText("Hapus");
        jButton3.addActionListener(this::jButton3ActionPerformed);
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 180, 110, 22));

        jLabel18.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Password");
        getContentPane().add(jLabel18, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 210, -1, -1));

        areaAlamat.setColumns(20);
        areaAlamat.setRows(5);
        jScrollPane3.setViewportView(areaAlamat);

        getContentPane().add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 300, 310, -1));
        getContentPane().add(txtNama, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 60, 185, -1));
        getContentPane().add(txtId, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 90, 184, -1));

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Role");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 240, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Nama");
        jLabel2.setAutoscrolls(true);
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, 50, 20));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ID Anggota");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 90, 90, 20));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("No Hp");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 120, 60, 20));

        txtHp.addActionListener(this::txtHpActionPerformed);
        getContentPane().add(txtHp, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 120, 184, -1));

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
        jScrollPane2.setViewportView(tabel);

        getContentPane().add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 430, 380, 200));

        jButton6.setText("Clear");
        jButton6.addActionListener(this::jButton6ActionPerformed);
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 150, 110, 22));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Data Anggota");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 10, -1, -1));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Alamat");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 310, 60, -1));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Status");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 150, 60, 20));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("Email");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 180, 50, 20));
        getContentPane().add(txtEmail, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 180, 184, -1));

        jButton5.setText("Cari");
        jButton5.addActionListener(this::jButton5ActionPerformed);
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 120, 110, 22));

        jButton2.setText("Edit");
        jButton2.addActionListener(this::jButton2ActionPerformed);
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 90, 110, -1));

        jButton1.setText("Tambah");
        jButton1.addActionListener(this::jButton1ActionPerformed);
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 60, 110, -1));

        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/plus.png"))); // NOI18N
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 60, -1, -1));

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/pen.png"))); // NOI18N
        getContentPane().add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 90, -1, -1));

        jLabel11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/search.png"))); // NOI18N
        getContentPane().add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 120, -1, -1));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/loading-arrow.png"))); // NOI18N
        getContentPane().add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 150, -1, -1));

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/clear.png"))); // NOI18N
        getContentPane().add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 180, -1, -1));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/biru.jpeg"))); // NOI18N
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 120, 600));

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/biru.jpeg"))); // NOI18N
        getContentPane().add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 520, 50));

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "aktif", "nonaktif", " " }));
        getContentPane().add(cmbStatus, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 150, 180, -1));

        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Anggota", "Admin", " " }));
        getContentPane().add(cmbRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 240, 184, -1));
        getContentPane().add(txtPassword, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 210, 180, -1));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cukulat.jpeg"))); // NOI18N
        getContentPane().add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 50, 400, 600));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    //Tombol Hapus
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        buttonHapus();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        buttonCari();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        bersihkanForm();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        buttonTambah();
    }//GEN-LAST:event_jButton1ActionPerformed

    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        buttonEdit();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void txtHpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtHpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtHpActionPerformed

    private void tabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabelMouseClicked
    int baris = tabel.getSelectedRow();
    if (baris != -1) {
        txtId.setText(model.getValueAt(baris, 0).toString());
        txtNama.setText(model.getValueAt(baris, 1).toString());
        txtEmail.setText(model.getValueAt(baris, 2).toString());
        
        // Password dikosongkan saat klik tabel demi alasan keamanan
        txtPassword.setText(""); 
        
        String role = model.getValueAt(baris, 3).toString();
        cmbRole.setSelectedItem(role);
        
        txtHp.setText(model.getValueAt(baris, 4).toString());
        areaAlamat.setText(model.getValueAt(baris, 5).toString());
        
        String status = model.getValueAt(baris, 6).toString();
        if(status.equalsIgnoreCase("aktif")) cmbStatus.setSelectedIndex(0);
        else cmbStatus.setSelectedIndex(1);
    }
    }//GEN-LAST:event_tabelMouseClicked

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
        java.awt.EventQueue.invokeLater(() -> new Manajemen_Anggota().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaAlamat;
    private javax.swing.JComboBox<String> cmbRole;
    private javax.swing.JComboBox<String> cmbStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTable tabel;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHp;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNama;
    private javax.swing.JPasswordField txtPassword;
    // End of variables declaration//GEN-END:variables
}
