package zadatak_8_2;

import java.sql.*;

public class Main {
    static {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {
        Connection con = null;
        String url = "jdbc:db2://localhost:50001/vstud";

        try {
            con = DriverManager.getConnection(url, "student", "abcdef");
            con.setAutoCommit(false);

            // Zelimo da izvrsimo INSERT naredbu
            // u kojoj su sve informacije poznate.
            // To znaci da mozemo koristiti kombinaciju
            // interfejsa Statement i metoda executeUpdate().
            String sql = 
                "INSERT INTO PREDMET " +
                "VALUES (20001, 'Pred1', 'Predmet 1', 1, 6)";

            Statement stmt = con.createStatement();

            System.out.println("Unosim podatke u tabelu PREDMET...");

            // Metod executeUpdate() se koristi ne samo za azuriranje,
            // vec i za unos, brisanje, i slicne naredbe.
            int insertCount = stmt.executeUpdate(sql);

            System.out.println("Broj unetih redova: " + insertCount);

            stmt.close();

            con.commit();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();

            System.out.println("SQLCODE: " + e.getErrorCode() + "\n" + "SQLSTATE: " + e.getSQLState() + "\n"
                    + "PORUKA: " + e.getMessage());

            try {
                if (null != con) {
                    con.rollback();
                    con.close();
                }
            } catch (SQLException e2) {
            }

            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (null != con) {
                    con.rollback();
                    con.close();
                }
            } catch (SQLException e2) {
            }

            System.exit(2);
        }
    }
}