/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;


public class koneksi {
    Connection connect;
    Statement sttmnt;
    ResultSet rslt;
    private static Connection MySQLConfig;
    public static Connection configDB(){
        try{
            String url="jdbc:mysql://localhost:3306/toko";
            String user="root";
            String pass="";
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            MySQLConfig=DriverManager.getConnection(url, user, pass);
            
        }catch (SQLException e){
           JOptionPane.showMessageDialog(null, "koneksi gagal "+e.getMessage());
        }
        return MySQLConfig;
    }
}
