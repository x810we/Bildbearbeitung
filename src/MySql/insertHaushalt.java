package MySql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Date;
import java.text.SimpleDateFormat;

public class insertHaushalt {

    public static void main(String[] args) throws Exception {



        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);
        System.out.println(date);



        Date zeitstempel = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("Datum: " + simpleDateFormat.format(zeitstempel));


        Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/HausHalt", "x810we", "soswind22");

        String insertQuery = "INSERT INTO Medien (idMedien,Datum,ZStand) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            pstmt.setInt(1, 1);
            pstmt.setDate(2, date);
            pstmt.setInt(3, 2000);
            pstmt.executeUpdate();
        }

    }




}
