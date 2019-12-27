package zadatak_9_1;

import java.sql.*;
import java.util.Scanner;

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
            
            // Iskljucujemo automatsko potvrdjivanje izmena
            con.setAutoCommit(false);

            Integer indeks = pronadji_najveci_indeks(con);
            System.out.println("1. Najveci indeks u tabeli ISPIT je " + indeks);
            obrisi_ispite(con, indeks);
            indeks = pronadji_najveci_indeks(con);
            System.out.println("3. Najveci indeks u tabeli ISPIT je " + indeks);
            potvrdi_ili_ponisti_izmene(con);
            indeks = pronadji_najveci_indeks(con);
            System.out.println("5. Najveci indeks u tabeli ISPIT je " + indeks);
            
            // S obzirom da je sve proslo kako treba ako se doslo do ove tacke,
            // potvrdjujemo izmene pre raskidanje konekcije
            con.commit();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();

            System.out.println("SQLCODE: " + e.getErrorCode() + "\n" + "SQLSTATE: " + e.getSQLState() + "\n"
                    + "PORUKA: " + e.getMessage());

            // U slucaju neuspeha, ponistavamo eventualne izmene i zatvaramo konekciju.
            // Pozivi metoda rollback() i close() ispod mogu da izbace SQLException,
            // ali njih ignorisemo, te je zato catch blok prazan.
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

    private static Integer pronadji_najveci_indeks(Connection con) throws SQLException, Exception {
        String sql = 
            "SELECT MAX(INDEKS)" +
            "FROM   ISPIT";
        Statement stmt = con.createStatement();
        ResultSet kursor = stmt.executeQuery(sql);
        
        Integer indeks = null;
        boolean imaRezultata = kursor.next();
        if (imaRezultata) {
            indeks = kursor.getInt(1);
        }
        else {
            throw new Exception("Nema ispita u bazi");
        }
        
        kursor.close();
        stmt.close();
        
        return indeks;
    }

    private static void obrisi_ispite(Connection con, Integer indeks) throws SQLException {
        String sql = 
            "DELETE FROM ISPIT " +
            "WHERE  INDEKS = ?";
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, indeks);
        
        stmt.executeUpdate();
        
        System.out.println("2. Obrisani su ispiti za studenta sa indeksom " + indeks);
    }

    private static void potvrdi_ili_ponisti_izmene(Connection con) throws SQLException {
        System.out.println("4. Da li zelite da potvrdite izmene [y] ili da ponistite izmene [n]?");
        try (Scanner ulaz = new Scanner(System.in)) {
            String odgovor = ulaz.next();
            if (odgovor.equalsIgnoreCase("y")) {
                // Potvrdjivanje izmena
                con.commit();
                System.out.println("Izmene su potvrdjene!");
            }
            else {
                // Ponistavanje izmena
                con.rollback();
                System.out.println("Izmene su ponistene!");
            }
        }
    }
}