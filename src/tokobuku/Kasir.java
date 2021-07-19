/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tokobuku;

import java.awt.HeadlessException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import koneksi.koneksi;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author LEGION5
 */
public class Kasir extends javax.swing.JFrame {

    DefaultTableModel table = new DefaultTableModel();

    /**
     * Creates new form TampilanKasir
     */
    public Kasir() {
        initComponents();
        koneksi.configDB();
        totalnya();
        tanggal();

        tbKeranjang.setModel(table);
        table.addColumn("ID Transaksi");
        table.addColumn("ID Buku");
        table.addColumn("Judul");
        table.addColumn("Harga");
        table.addColumn("Jumlah");
        table.addColumn("Total Harga");
        table.addColumn("Tanggal");


        tampilData();
    }

    public void tanggal() {
        Date now = new Date();
        tanggal.setDate(null);
    }

    private void tampilData() {
        //untuk mengahapus baris setelah input
        int row = tbKeranjang.getRowCount();
        for (int a = 0; a < row; a++) {
            table.removeRow(0);
        }

        String query = "SELECT  `id_transaksi`, `id`, `judul`, `harga`, `jumlah`, `total_harga`, `tgl_transaksi`,`nama` FROM tb_keranjang ";
        String procedures = "CALL `total_harga_transaksi`()";

        try {
            Connection connect = koneksi.configDB();//memanggil koneksi
            Statement sttmnt = connect.createStatement();//membuat statement
            ResultSet rslt = sttmnt.executeQuery(query);//menjalanakn query

            while (rslt.next()) {
                //menampung data sementara

                String kode = rslt.getString("id_transaksi");
                String buku = rslt.getString("id");
                String jdl = rslt.getString("judul");
                String harga = rslt.getString("harga");
                String jumlah = rslt.getString("jumlah");
                String total = rslt.getString("total_harga");
                String tgl = rslt.getString("tgl_transaksi");
                String nama = rslt.getString("nama");

                //masukan semua data kedalam array
                String[] data = {kode, buku, jdl, harga, jumlah, total, tgl, nama};
                //menambahakan baris sesuai dengan data yang tersimpan diarray
                table.addRow(data);
            }
            //mengeset nilai yang ditampung agar muncul di table
            tbKeranjang.setModel(table);

        } catch (Exception e) {
            System.out.println(e);
        }

    }

    private void clear() {
        nmpembeli.setText(null);
        buku.setText(null);
        judul.setText(null);
        txt_totalharga.setText(null);
        harga1.setText(null);
        txt_totalharga.setText(null);
        tanggal.setDate(null);
        jumlah.setText(null);
    }

    private void keranjang() {
        String idbuku = buku.getText();
        String cus = nmpembeli.getText();
        String jdl = judul.getText();
        String harga = harga1.getText();
        String jum = jumlah.getText();
        String total = txt_totalharga.getText();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");
        String tgl = String.valueOf(date.format(tanggal.getDate()));

        //panggil koneksi
        Connection connect = koneksi.configDB();
        //query untuk memasukan data
        String query = "INSERT INTO `transaksi`(`tgl_transaksi`, `id_transaksi`, `id`, `judul`, `harga`, `jumlah_barang`, `total_harga`, `nama`) "
                + "VALUES ( '"+tgl +"' , NULL ,'" + idbuku + "', '" + jdl + "', '" + harga + "', '" + jum + "', '" + total + "', '" + cus + "')";

        try {
            //menyiapkan statement untuk di eksekusi
            PreparedStatement ps = (PreparedStatement) connect.prepareStatement(query);
            ps.executeUpdate(query);
            JOptionPane.showMessageDialog(null, "Data Masuk Ke-Keranjang");

        } catch (SQLException | HeadlessException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Data Gagal Disimpan");

        } finally {
            tampilData();
            clear();

        }
        totalnya();
    }

    private void hapusData() {
        //ambill data no pendaftaran
        int i = tbKeranjang.getSelectedRow();

        String kode = table.getValueAt(i, 0).toString();

        Connection connect = koneksi.configDB();

        String query = "DELETE FROM `tb_keranjang` WHERE `tb_keranjang`.`id_transaksi` = '" + kode + "' ";
        try {
            PreparedStatement ps = (PreparedStatement) connect.prepareStatement(query);
            ps.execute();
        } catch (SQLException | HeadlessException e) {
            System.out.println(e);
            JOptionPane.showMessageDialog(null, "Data Gagal Dihapus");
        } finally {
            tampilData();
            clear();
        }
        totalnya();
    }

    private void totalnya() {
        String procedures = "CALL `total_harga_transaksi`()";

        try {
            Connection connect = koneksi.configDB();//memanggil koneksi
            Statement sttmnt = connect.createStatement();//membuat statement
            ResultSet rslt = sttmnt.executeQuery(procedures);//menjalanakn query\
            while (rslt.next()) {
                txt_totalharga2.setText(rslt.getString(1));
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void total() {
        String hrg = harga1.getText();
        String jum = jumlah.getText();

        int hargaa = Integer.parseInt(hrg);
        try {
            int jumlahh = Integer.parseInt(jum);

            int total = hargaa * jumlahh;
            String total_harga = Integer.toString(total);

            txt_totalharga.setText(total_harga);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Only Number");
            jumlah.setText(null);
        }
    }

    private void reset() {
        txt_uang.setText(null);
    }

    private void kembalian() {
        String total = txt_totalharga2.getText();
        String uang = txt_uang.getText();

        int totals = Integer.parseInt(total);
        try {
            int uangs = Integer.parseInt(uang);
            int kembali = (uangs - totals);
            String fix = Integer.toString(kembali);
            kembalian.setText(fix);
            JOptionPane.showMessageDialog(null, "Transaksi Berhasil!");
        } catch (NumberFormatException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, "Invalid Payment");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        findbuku = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        add = new javax.swing.JButton();
        harga1 = new javax.swing.JTextField();
        buku = new javax.swing.JTextField();
        judul = new javax.swing.JTextField();
        txt_totalharga = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        tanggal = new com.toedter.calendar.JDateChooser();
        jButton3 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbKeranjang = new javax.swing.JTable();
        delete = new javax.swing.JButton();
        payment = new javax.swing.JButton();
        kembalian = new javax.swing.JTextField();
        txt_totalharga2 = new javax.swing.JTextField();
        back = new javax.swing.JButton();
        txt_uang = new javax.swing.JTextField();
        reset = new javax.swing.JButton();
        print = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jumlah = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        nmpembeli = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(102, 102, 102));

        jPanel1.setBackground(new java.awt.Color(102, 102, 102));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        findbuku.setBackground(new java.awt.Color(153, 153, 153));
        findbuku.setForeground(new java.awt.Color(255, 255, 255));
        findbuku.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel4.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText("TUNAI");
        findbuku.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 300, 110, 20));

        jLabel5.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel5.setText("Nama");
        findbuku.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 80, 130, 30));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel6.setText("Kode Buku");
        findbuku.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 30, 130, 30));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel7.setText("JUDUL");
        findbuku.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 140, 130, 30));

        jLabel8.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel8.setText("Total Harga");
        findbuku.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 320, 130, 30));

        add.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/add.png"))); // NOI18N
        add.setText("ADD");
        add.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addActionPerformed(evt);
            }
        });
        findbuku.add(add, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, 410, -1));
        findbuku.add(harga1, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 200, 260, 30));
        findbuku.add(buku, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 30, 260, 30));
        findbuku.add(judul, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 140, 260, 30));

        txt_totalharga.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txt_totalhargaMouseReleased(evt);
            }
        });
        findbuku.add(txt_totalharga, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 320, 260, 30));

        jLabel9.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel9.setText("HARGA");
        findbuku.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 200, 130, 30));
        findbuku.add(tanggal, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 380, 260, 30));

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/src.png"))); // NOI18N
        jButton3.setText("FIND");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        findbuku.add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 30, 120, 30));

        tbKeranjang.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tbKeranjang);

        findbuku.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 30, 550, 140));

        delete.setText("DELETE");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        findbuku.add(delete, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 40, 80, -1));

        payment.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/bill.png"))); // NOI18N
        payment.setText("PAYMENT");
        payment.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paymentActionPerformed(evt);
            }
        });
        findbuku.add(payment, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 350, 550, 30));

        kembalian.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        kembalian.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        findbuku.add(kembalian, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 440, 560, 40));

        txt_totalharga2.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        txt_totalharga2.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        txt_totalharga2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                txt_totalharga2MouseReleased(evt);
            }
        });
        txt_totalharga2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_totalharga2ActionPerformed(evt);
            }
        });
        findbuku.add(txt_totalharga2, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 210, 440, 40));

        back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/bs.png"))); // NOI18N
        back.setText("BACK");
        back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backActionPerformed(evt);
            }
        });
        findbuku.add(back, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 540, 110, 30));

        txt_uang.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        txt_uang.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        findbuku.add(txt_uang, new org.netbeans.lib.awtextra.AbsoluteConstraints(730, 290, 440, 40));

        reset.setText("RESET");
        reset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetActionPerformed(evt);
            }
        });
        findbuku.add(reset, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 90, 80, -1));

        print.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/p1.png"))); // NOI18N
        print.setText("PRINT");
        print.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printActionPerformed(evt);
            }
        });
        findbuku.add(print, new org.netbeans.lib.awtextra.AbsoluteConstraints(1180, 540, 110, 30));

        jLabel11.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel11.setText("JUMLAH");
        findbuku.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 260, 130, 30));

        jumlah.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jumlahKeyReleased(evt);
            }
        });
        findbuku.add(jumlah, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 260, 260, 30));

        jLabel10.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel10.setText("TANGGAL");
        findbuku.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 380, 130, 30));

        nmpembeli.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                nmpembeliMouseReleased(evt);
            }
        });
        findbuku.add(nmpembeli, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 80, 260, 30));

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/src.png"))); // NOI18N
        jButton1.setText("LIHAT DATA");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        findbuku.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 80, -1, 30));

        jLabel14.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel14.setText("KEMBALIAN");
        findbuku.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 410, 120, 20));

        jLabel17.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel17.setText("TOTAL");
        findbuku.add(jLabel17, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 210, 100, 40));

        jPanel1.add(findbuku, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 100, 1340, 600));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/m1.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1090, 0, -1, -1));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Gambar/B1 (2).png"))); // NOI18N
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 0, -1, -1));

        jLabel3.setFont(new java.awt.Font("Times New Roman", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("SARI ANGGREK");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 10, 860, -1));

        jLabel12.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Sari Anggrek Jl. Jendral Sudirman Bukittinggi, Sumatra Barat");
        jPanel1.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 40, 455, -1));

        jLabel13.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Telp.(0752)223 37 ");
        jPanel1.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 60, 354, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void paymentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paymentActionPerformed
        // TODO add your handling code here:
        kembalian();
    }//GEN-LAST:event_paymentActionPerformed

    private void backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backActionPerformed
        // TODO add your handling code here:
        new menu().setVisible(true);
        dispose();
    }//GEN-LAST:event_backActionPerformed

    private void printActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printActionPerformed
        // TODO add your handling code here:
//        try{
//            InputStream is = Kasir.class.getResourceAsStream("/Struk/struk.jasper");
//            JasperPrint jsPrint = JasperFillManager.fillReport(is, null, koneksi.configDB());
//            JasperViewer.viewReport(jsPrint, false);
//        }catch(Exception e){
//            JOptionPane.showMessageDialog(null, "Gagal mencetak laporan karena: "+
//                    e.getMessage(),"cetak laporan",JOptionPane.ERROR_MESSAGE);
//        }       
 try{
            String file = "/Struk/struk.jasper";
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            HashMap param = new HashMap();
            
            param.put("total",txt_totalharga2.getText());
            param.put("uang",txt_uang.getText());
            param.put("kembalian",kembalian.getText());
            
            JasperPrint print = JasperFillManager.fillReport(getClass().getResourceAsStream(file),param,koneksi.configDB());
            JasperViewer.viewReport(print, false);
            
        }catch(ClassNotFoundException | InstantiationException | IllegalAccessException | JRException e){
            System.out.println(e);
        }
    }//GEN-LAST:event_printActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        new DataBuku().setVisible(true);
//        this.setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void txt_totalhargaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_totalhargaMouseReleased
        // TODO add your handling code here:

    }//GEN-LAST:event_txt_totalhargaMouseReleased

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        // TODO add your handling code here:
        hapusData();
        txt_uang.setText(null);
        kembalian.setText(null);
    }//GEN-LAST:event_deleteActionPerformed

    private void resetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetActionPerformed
        // TODO add your handling code here:
        try {
            String clear = "TRUNCATE `tb_keranjang`";
            Connection connect = koneksi.configDB();
            PreparedStatement ps = (PreparedStatement) connect.prepareStatement(clear);
            ps.execute();
//            keranjang();

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            tampilData();
            totalnya();
            txt_uang.setText(null);
            kembalian.setText(null);
        }
    }//GEN-LAST:event_resetActionPerformed

    private void addActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addActionPerformed
        // TODO add your handling code here:
        keranjang();
    }//GEN-LAST:event_addActionPerformed

    private void txt_totalharga2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txt_totalharga2MouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalharga2MouseReleased

    private void txt_totalharga2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_totalharga2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txt_totalharga2ActionPerformed

    private void jumlahKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jumlahKeyReleased
        // TODO add your handling code here:
        total();
    }//GEN-LAST:event_jumlahKeyReleased

    private void nmpembeliMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nmpembeliMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_nmpembeliMouseReleased

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        new DataCus().setVisible(true);
//        this.setVisible(false);
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Kasir.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Kasir().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton add;
    private javax.swing.JButton back;
    public javax.swing.JTextField buku;
    private javax.swing.JButton delete;
    private javax.swing.JPanel findbuku;
    public javax.swing.JTextField harga1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTextField judul;
    private javax.swing.JTextField jumlah;
    private javax.swing.JTextField kembalian;
    private javax.swing.JTextField nmpembeli;
    private javax.swing.JButton payment;
    private javax.swing.JButton print;
    private javax.swing.JButton reset;
    private com.toedter.calendar.JDateChooser tanggal;
    private javax.swing.JTable tbKeranjang;
    private javax.swing.JTextField txt_totalharga;
    private javax.swing.JTextField txt_totalharga2;
    private javax.swing.JTextField txt_uang;
    // End of variables declaration//GEN-END:variables
}
