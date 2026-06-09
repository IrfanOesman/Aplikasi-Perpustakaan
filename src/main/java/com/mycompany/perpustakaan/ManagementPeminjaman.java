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
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Irfan Oesman Asa
 */
public class ManagementPeminjaman extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ManagementPeminjaman.class.getName());
    private Connection conn;
    private void load_table() {
    DefaultTableModel model = new DefaultTableModel();
    
    model.addColumn("Anggota");
    model.addColumn("Buku");
    model.addColumn("Jatuh Tempo");
    model.addColumn("Status");

    
    try {
        String sql = "SELECT a.nama, "
           + " b.judul,"
           + "p.tanggal_jatuh_tempo,"
           + "p.status "
           + "FROM peminjaman p "
           + "INNER JOIN anggota a ON p.id_anggota = a.id_anggota "
           + "INNER JOIN buku b ON p.id_buku = b.id_buku";

        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();
        
        while(rs.next()) {
            model.addRow(new Object[]{
                rs.getString("nama"),
                rs.getString("judul"),
                rs.getDate("tanggal_jatuh_tempo"),
                rs.getString("status")
            });
        }
        
        jTable1.setModel(model);
        panelDenda.setVisible(false);
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }
}public void buttonSimpan(){
    // 1. Validasi seleksi ComboBox Anggota
    if (cmbAnggota.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Silakan pilih anggota terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Ambil ID Anggota langsung dari teks ComboBox terpilih
    String selectedAnggota = cmbAnggota.getSelectedItem().toString();
    String noAnggota = selectedAnggota.split(" — ")[0].trim();
    
    // 2. Ambil ID Buku dari ComboBox Buku
    if (cmbPilihBuku.getSelectedIndex() <= 0) {
        JOptionPane.showMessageDialog(this, "Silakan pilih buku terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }
    String selectedBuku = cmbPilihBuku.getSelectedItem().toString();
    String idBuku = selectedBuku.split(" — ")[0].trim(); 
    
    String status = "dipinjam";

    // 3. Pengolahan Data JDateChooser
    String tanggalPinjam = "";
    String jatuhTempo = "";
    
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    if (jdTanggalPinjam.getDate() != null && jdTanggalJatuhTempo.getDate() != null) {
        tanggalPinjam = sdf.format(jdTanggalPinjam.getDate());
        jatuhTempo = sdf.format(jdTanggalJatuhTempo.getDate()); 
    } else {
        JOptionPane.showMessageDialog(this, "Tanggal Pinjam dan Jatuh Tempo harus diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // 4. Query SQL
    String sqlInsert = "INSERT INTO peminjaman (id_buku, id_anggota, tanggal_pinjam, tanggal_jatuh_tempo, status, created_at) VALUES (?, ?, ?, ?, ?, NOW())";
    // Sesuaikan nama kolom (misal: 'status') dan nama tabel (misal: 'buku') dengan database Anda
    String sqlUpdateBuku = "UPDATE buku SET status = 'dipinjam' WHERE id_buku = ?"; 

    try {
        // MENONAKTIFKAN Auto-Commit untuk memulai transaksi database (lebih aman)
        conn.setAutoCommit(false);
        
        // --- PROSES 1: INSERT KE TABEL PEMINJAMAN ---
        PreparedStatement pstInsert = conn.prepareStatement(sqlInsert);
        pstInsert.setString(1, idBuku);
        pstInsert.setString(2, noAnggota);
        pstInsert.setString(3, tanggalPinjam);
        pstInsert.setString(4, jatuhTempo);
        pstInsert.setString(5, status);
        int rowsInsert = pstInsert.executeUpdate();

        // --- PROSES 2: UPDATE STATUS DI TABEL BUKU ---
        PreparedStatement pstUpdate = conn.prepareStatement(sqlUpdateBuku);
        pstUpdate.setString(1, idBuku);
        int rowsUpdate = pstUpdate.executeUpdate();

        // Validasi: Jika kedua proses berhasil, maka komit data ke database
        if (rowsInsert > 0 && rowsUpdate > 0) {
            conn.commit(); // Data disimpan permanen secara bersamaan
            
            JOptionPane.showMessageDialog(this, "Peminjaman Berhasil Disimpan dan Status Buku Diperbarui!");
            
            // Mengosongkan form input setelah berhasil
            bersih(); 
            
            // Catatan: Jika Anda punya fungsi untuk refresh tabel atau combonbox buku, panggil di sini
            // tampilkanDataBuku(); 
        } else {
            // Jika salah satu gagal, batalkan semua perubahan
            conn.rollback();
            JOptionPane.showMessageDialog(this, "Gagal memproses peminjaman.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (SQLException e) {
        try {
            // Jika terjadi error, batalkan semua transaksi agar tidak ada data yang 'menggantung'
            if (conn != null) {
                conn.rollback();
            }
        } catch (SQLException ex) {
            System.out.println("Rollback gagal: " + ex.getMessage());
        }
        JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error Penyimpanan", JOptionPane.ERROR_MESSAGE);
    } finally {
        try {
            // Kembalikan ke setelan awal auto-commit
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
    private void buttonCari(){
    String keyword = fCari.getText().trim();
    
    if (keyword.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan ID Peminjaman atau Nama Anggota terlebih dahulu!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        fCari.setText("");
        
        return;
    }

    String sql = "SELECT p.*, a.nama, b.judul " +
                 "FROM peminjaman p " +
                 "JOIN anggota a ON p.id_anggota = a.id_anggota " +
                 "JOIN buku b ON p.id_buku = b.id_buku " +
                 "WHERE p.status = 'dipinjam' AND (p.id_peminjam = ? OR a.nama LIKE ?)";

    try {
       
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, keyword);
        pst.setString(2, "%" + keyword + "%");
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            
            // 1. Ambil data dari database
            String nama = rs.getString("nama");
            String idAnggota = rs.getString("id_anggota");
            String judul = rs.getString("judul");
            String tglPinjam = rs.getString("tanggal_pinjam");
            String tglTempo = rs.getString("tanggal_jatuh_tempo");
            
            // Format tanggal kembali (Hari ini)
            java.text.SimpleDateFormat sdfTampilan = new java.text.SimpleDateFormat("dd MMMM yyyy", new java.util.Locale("id", "ID"));
            String tglKembaliHariIni = sdfTampilan.format(new java.util.Date());
            
            // 2. Hitung Denda & Keterlambatan
            java.sql.Date jatuhTempo = rs.getDate("tanggal_jatuh_tempo");
            java.util.Date hariIni = new java.util.Date();
            
            long diffInMillies = hariIni.getTime() - jatuhTempo.getTime();
            long diffInDays = diffInMillies / (1000 * 60 * 60 * 24);
            
            String statusTeks = "";
            if (diffInDays > 0) {
                statusTeks = "Terlambat " + diffInDays + " hari";
                
                // Update panel denda merah di bawahnya
                long totalDenda = diffInDays * 1000;
                lblTotalDenda.setText("Rp " + String.format("%,d", totalDenda));
                lblKeteranganDenda.setText(diffInDays + " hari × Rp 1.000/hari");
                panelDenda.setVisible(true);
            } else {
                statusTeks = "Tepat Waktu";
                panelDenda.setVisible(false);
            }

            // 3. Susun teks terformat untuk JTextArea
            // Menggunakan pola string yang rapi dengan baris baru (\n)
            String detailTeks = "DETAIL PEMINJAMAN DITEMUKAN:\n"
                    + "=========================================\n"
                    + "Peminjam\t: " + nama + " (" + idAnggota + ")\n"
                    + "Buku\t\t: " + judul + "\n"
                    + "Tanggal Pinjam\t: " + tglPinjam + "\n"
                    + "Jatuh Tempo\t: " + tglTempo + "\n"
                    + "Tanggal Kembali\t: " + tglKembaliHariIni + "\n"
                    + "Status\t\t: " + statusTeks + "\n"
                    + "=========================================";
            
            // Set teks ke JTextArea kamu
            areaPengembalian.setText(detailTeks);
            areaPengembalian.setEditable(false); // Pastikan tidak bisa diedit user
            
        } else {
            JOptionPane.showMessageDialog(this, "Data peminjaman tidak ditemukan atau sudah dikembalikan!", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            areaPengembalian.setText("");
            panelDenda.setVisible(false);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error load data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    private void buttonPengembalian(){
    String keyword = fCari.getText().trim();
    
    // Validasi apakah user sudah melakukan pencarian data terlebih dahulu
    if (keyword.isEmpty() || areaPengembalian.getText().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Silakan cari data peminjaman terlebih dahulu sebelum memproses!", "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String sql = "UPDATE peminjaman SET status = 'dikembalikan', tanggal_kembali = CURDATE() " +
                 "WHERE status = 'dipinjam' AND (id_peminjam = ? OR id_anggota = (SELECT id_anggota FROM anggota WHERE nama = ? LIMIT 1))";

    try {
  
        PreparedStatement pst = conn.prepareStatement(sql);
        pst.setString(1, keyword);
        pst.setString(2, keyword);

        int rows = pst.executeUpdate();
        if (rows > 0) {
            JOptionPane.showMessageDialog(this, "Buku Berhasil Diproses dan Dikembalikan!");
            
            // Reset Form Pengembalian setelah berhasil
            fCari.setText("");
            areaPengembalian.setText("");
            panelDenda.setVisible(false);
            
            
           
        } else {
            JOptionPane.showMessageDialog(this, "Gagal memproses pengembalian data.", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memproses: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    }
    public void loadBuku() {
    // Kosongkan combo box terlebih dahulu agar tidak double saat di-refresh
    cmbPilihBuku.removeAllItems();
    cmbPilihBuku.addItem("- Pilih Buku -");

    // Query untuk mengambil ID, Judul, dan Pengarang dari tabel buku
    // Kamu juga bisa memfilter hanya buku yang statusnya 'Tersedia'
    String sql = "SELECT id_buku, judul, pengarang FROM buku WHERE status = 'Tersedia'";

    try {
        
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            // Gabungkan ID dan Judul dengan tanda pemisah (misal: " — ")
            String idBuku = rs.getString("id_buku");
            String judulBuku = rs.getString("judul");
            String pengarang = rs.getString("pengarang");
            
            // Format tampilan: "1 — Clean Code — Robert C. Martin"
            String itemTampilan = idBuku + " — " + judulBuku + " — (" + pengarang + ")";
            
            // Masukkan string gabungan ini ke dalam ComboBox
            cmbPilihBuku.addItem(itemTampilan);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
    }
}
    public void loadAnggota() {
    cmbAnggota.removeAllItems();
    cmbAnggota.addItem("- Pilih Anggota -");

    String sql = "SELECT id_anggota, nama FROM anggota";

    try {
        // Menggunakan objek 'conn' global yang sudah kamu deklarasikan
        PreparedStatement pst = conn.prepareStatement(sql);
        ResultSet rs = pst.executeQuery();

        while (rs.next()) {
            String idAnggota = rs.getString("id_anggota");
            String namaAnggota = rs.getString("nama");
            
            // Format item tampilan: "A001 — Irfan Oesman"
            String itemTampilan = idAnggota + " — " + namaAnggota;
            
            cmbAnggota.addItem(itemTampilan);
        }
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data anggota: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    public void hitungDurasiPeminjaman() {
    // Validasi: jalankan perhitungan hanya jika kedua tanggal sudah dipilih
    if (jdTanggalPinjam.getDate() != null && jdTanggalJatuhTempo.getDate() != null) {
        
        // 1. Konversi java.util.Date dari JDateChooser ke java.time.LocalDate
        java.time.LocalDate datePinjam = jdTanggalPinjam.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDate dateTempo = jdTanggalJatuhTempo.getDate().toInstant()
                .atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        
        // 2. Hitung selisih hari menggunakan ChronoUnit
        long totalHari = java.time.temporal.ChronoUnit.DAYS.between(datePinjam, dateTempo);
        
        // Cek jika user salah pilih tanggal (tanggal tempo mendahului tanggal pinjam)
        if (totalHari < 0) {
            areaPeminjaman.setText("Peringatan:\nTanggal jatuh tempo tidak boleh sebelum tanggal pinjam!");
            return;
        }
        
        // 3. Kalkulasi simulasi denda per hari (Misal standar Rp 1.000)
        long dendaPerHari = 1000;
        
        // 4. Susun teks rangkuman untuk JTextArea sesuai desain mockup kamu
        String infoTeks = "RINGKASAN DURASI & TARIF PEMINJAMAN:\n"
                + "=========================================\n"
                + "Durasi Pinjam\t: " + totalHari + " hari\n"
                + "Denda per Hari\t: Rp " + String.format("%,d", dendaPerHari) + "\n"
                + "Status Buku\t: Tersedia\n"
                + "=========================================\n"
                + "* Pastikan pengembalian sebelum tanggal jatuh tempo.";
        
        // Set ke JTextArea
        areaPeminjaman.setText(infoTeks);
    } else {
        // Jika salah satu atau kedua tanggal kosong, bersihkan JTextArea
        areaPeminjaman.setText("");
    }
}
    public void bersih() {
    // 1. Kosongkan JTextField untuk nomor/ID anggota (jika ada)
    fNomor.setText("");
    areaPeminjaman.setText("");
    jdTanggalJatuhTempo.setDate(null);
    jdTanggalPinjam.setDate(null);
    
    // 2. Kembalikan JComboBox Anggota dan Buku ke pilihan pertama ("- Pilih -")
    if (cmbAnggota.getItemCount() > 0) {
        cmbAnggota.setSelectedIndex(0);
    }
    if (cmbPilihBuku.getItemCount() > 0) {
        cmbPilihBuku.setSelectedIndex(0);
    }
    
        
}
    /**
     * Creates new form ManajemenAnggota
     */
    public ManagementPeminjaman() {
         initComponents();
         try {
        String url ="jdbc:mysql://localhost:3307/db_perpustakaan";
        String user="root";
        String pass="";
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(url,user,pass);
    } catch (Exception e) {
         e.printStackTrace();
    }
       
        load_table();
        loadBuku();    
        loadAnggota();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        fNomor = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        cmbPilihBuku = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        areaPeminjaman = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jdTanggalJatuhTempo = new com.toedter.calendar.JDateChooser();
        jdTanggalPinjam = new com.toedter.calendar.JDateChooser();
        cmbAnggota = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        fCari = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        panelDenda = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblTotalDenda = new javax.swing.JLabel();
        lblKeteranganDenda = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        areaPengembalian = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel6.setText("Nama Anggota");

        fNomor.setEditable(false);

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel7.setText("No Anggota");

        cmbPilihBuku.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel8.setText("Tanggal Pinjam");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel9.setText("Jatuh Tempo");

        areaPeminjaman.setColumns(20);
        areaPeminjaman.setRows(5);
        jScrollPane1.setViewportView(areaPeminjaman);

        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setText("Simpan");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton2.setText("Batal");
        jButton2.addActionListener(this::jButton2ActionPerformed);

        jdTanggalJatuhTempo.addPropertyChangeListener(this::jdTanggalJatuhTempoPropertyChange);

        jdTanggalPinjam.addPropertyChangeListener(this::jdTanggalPinjamPropertyChange);

        cmbAnggota.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbAnggota.addActionListener(this::cmbAnggotaActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(cmbPilihBuku, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
                                .addGap(137, 137, 137)
                                .addComponent(jLabel6)
                                .addGap(75, 75, 75))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jdTanggalPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addComponent(jdTanggalJatuhTempo, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(fNomor, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(113, 113, 113)
                                .addComponent(cmbAnggota, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addGap(95, 95, 95))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel2Layout.createSequentialGroup()
                    .addGap(56, 56, 56)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(490, Short.MAX_VALUE)))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel6))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fNomor, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                    .addComponent(cmbAnggota))
                .addGap(35, 35, 35)
                .addComponent(cmbPilihBuku, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdTanggalJatuhTempo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jdTanggalPinjam, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(41, 41, 41))
            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                    .addContainerGap(514, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(36, 36, 36)))
        );

        jTabbedPane1.addTab("Peminjaman", jPanel2);

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel10.setText("Cari ID Peminjaman/ Nama Anggota ");

        jLabel1.setText("Detail Peminjaman : ");

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton3.setText("Batal");

        jButton4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton4.setText("Proses Pengembalian");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jButton5.setText("Cari");
        jButton5.addActionListener(this::jButton5ActionPerformed);

        jLabel2.setText("Denda Keterlambatan");

        lblTotalDenda.setText("4.000");

        lblKeteranganDenda.setText("1000 x 14 hari");

        javax.swing.GroupLayout panelDendaLayout = new javax.swing.GroupLayout(panelDenda);
        panelDenda.setLayout(panelDendaLayout);
        panelDendaLayout.setHorizontalGroup(
            panelDendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDendaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panelDendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelDendaLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                        .addGap(16, 16, 16))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDendaLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblTotalDenda, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelDendaLayout.createSequentialGroup()
                        .addComponent(lblKeteranganDenda, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        panelDendaLayout.setVerticalGroup(
            panelDendaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelDendaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalDenda, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblKeteranganDenda)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        areaPengembalian.setColumns(20);
        areaPengembalian.setRows(5);
        jScrollPane4.setViewportView(areaPengembalian);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(302, 302, 302))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(69, 69, 69)
                                        .addComponent(jButton4))
                                    .addComponent(fCari, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(47, 47, 47)
                                .addComponent(jButton5)))
                        .addContainerGap(105, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 395, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(panelDenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel10)
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fCari, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5))
                .addGap(44, 44, 44)
                .addComponent(jLabel1)
                .addGap(35, 35, 35)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelDenda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pengembalian", jPanel3);

        jLabel4.setText("Dafta Buku Yang Sedang Dipinjam");

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
        jScrollPane3.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(185, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel4)
                .addGap(45, 45, 45)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Riwayat Aktif", jPanel4);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 667, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(410, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 628, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(76, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
      buttonSimpan();
      loadBuku();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
     buttonPengembalian();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
    buttonCari();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void cmbAnggotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAnggotaActionPerformed
                                             
    // Cek memastikan ada item yang dipilih dan bukan index default "- Pilih Anggota -"
    if (cmbAnggota.getSelectedIndex() > 0) {
        String selectedAnggota = cmbAnggota.getSelectedItem().toString();
        // Memotong string untuk mengambil ID-nya saja (bagian sebelum tanda " — ")
        String idAnggota = selectedAnggota.split(" — ")[0].trim();
        
        // Set ke JTextField target kamu
        fNomor.setText(idAnggota);
    } else {
        fNomor.setText("");
    }

    }//GEN-LAST:event_cmbAnggotaActionPerformed

    private void jdTanggalPinjamPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdTanggalPinjamPropertyChange
    hitungDurasiPeminjaman();
    }//GEN-LAST:event_jdTanggalPinjamPropertyChange

    private void jdTanggalJatuhTempoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jdTanggalJatuhTempoPropertyChange
     hitungDurasiPeminjaman();
    }//GEN-LAST:event_jdTanggalJatuhTempoPropertyChange

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
    bersih();
    }//GEN-LAST:event_jButton2ActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ManagementPeminjaman().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea areaPeminjaman;
    private javax.swing.JTextArea areaPengembalian;
    private javax.swing.JComboBox<String> cmbAnggota;
    private javax.swing.JComboBox<String> cmbPilihBuku;
    private javax.swing.JTextField fCari;
    private javax.swing.JTextField fNomor;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private com.toedter.calendar.JDateChooser jdTanggalJatuhTempo;
    private com.toedter.calendar.JDateChooser jdTanggalPinjam;
    private javax.swing.JLabel lblKeteranganDenda;
    private javax.swing.JLabel lblTotalDenda;
    private javax.swing.JPanel panelDenda;
    // End of variables declaration//GEN-END:variables
}
