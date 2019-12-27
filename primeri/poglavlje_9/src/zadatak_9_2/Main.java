package zadatak_9_2;

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
            con.setAutoCommit(false);

            // Postavljanje isteka vremena za katance.
            Statement lockStmt = con.createStatement();
            lockStmt.execute("SET CURRENT LOCK TIMEOUT 5");
            
            obradiPredmete(con);
            
            // Vracanje podrazumevane vrednosti za istek vremena
            lockStmt.execute("SET CURRENT LOCK TIMEOUT NULL");
            lockStmt.close();
            
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

    private static void obradiPredmete(Connection con) throws SQLException {
        try (Scanner ulaz = new Scanner(System.in)) {
            String sql = 
                "SELECT ID_PREDMETA, " + 
                "       NAZIV, " + 
                "       BODOVI " +
                "FROM   PREDMET " +
                "WHERE  ID_PREDMETA IN ( " +
                "           SELECT  ID_PREDMETA " +
                "           FROM    OBAVEZAN_PREDMET " +
                "           WHERE   ID_SMERA = 201 " +
                "       ) AND " +
                "       ID_PREDMETA NOT IN ( " + 
                "           SELECT  ID_PREDMETA " +
                "           FROM    OBRADJENI_PREDMETI" +
                "       )";
                
            Statement stmt = con.createStatement(
                ResultSet.TYPE_FORWARD_ONLY, 
                ResultSet.CONCUR_UPDATABLE,
                // Kursor deklarisemo sa opcijom HOLD_CURSORS_OVER_COMMIT
                // da bi ostao otvoren prilikom izvrsavanja COMMIT naredbe.
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
            
            ResultSet kursor = otvoriKursor(stmt, sql);
            
            // Citanje reda moze dovesti do problema zbog S ili U katanaca,
            // te moramo poziv metoda next() obraditi zasebno,
            // pa zato ide unutar petlje za obradu.
            boolean ima_redova = true;
            while(true) {
                // S ili U katanac
                try {
                    ima_redova = kursor.next();
                }
                catch (SQLException e) {
                    // Obrada katanaca
                    if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
                        kursor.close();
                        kursor = obradiCekanje("FETCH", con, stmt, sql);
                        continue;
                    }
                    throw e;
                }
                
                // Izlaz iz beskonacne petlje
                // ukoliko vise nema redova u kursoru
                if (!ima_redova) {
                    break;
                }
                
                // Inace, dohvatamo podatke
                int id_predmeta = kursor.getInt(1);
                String naziv = kursor.getString(2);
                short bodovi = kursor.getShort(3);
                
                System.out.printf("\nPredmet %s ima %d bodova\n", naziv.trim(), bodovi);
                System.out.println("Da li zelite da uvecate broj bodova za 1? [da/ne]");
                
                String odgovor = ulaz.next();
                if (odgovor.equalsIgnoreCase("da")) {
                    // X katanac
                    try {
                        // Ovde koristimo metode updateXXX i updateRow za azuriranje podataka.
                        // Za vezbu uraditi zadatak pozicionirajucom UPDATE naredbom.
                        kursor.updateShort(3, (short) (bodovi + 1));
                        kursor.updateRow();
                    }
                    catch (SQLException e) {
                        if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
                            kursor.close();
                            kursor = obradiCekanje("UPDATE", con, stmt, sql);
                            continue;
                        }
                        throw e;
                    }
                    
                    System.out.println("Uspesno su azurirani bodovi za tekuci predmet!");
                }
                
                // Evidentiranje obrade predstavlja INSERT naredbu
                // koja takodje moze dovesti do problema visekorisnickog okruzenja,
                // tako da moramo obraditi katance i u ovom slucaju
                try {
                    evidentirajPredmetKaoObradjen(con, id_predmeta);
                } catch (SQLException e) {
                    if (e.getErrorCode() == -911 || e.getErrorCode() == -913) {
                        kursor.close();
                        kursor = obradiCekanje("INSERT", con, stmt, sql);
                        continue;
                    }
                    throw e;
                }
                
                
                // Zavrsavamo jednu transakciju
                con.commit();
                
                System.out.println("Da li zelite da zavrsite sa obradom? [da/ne]");
                odgovor = ulaz.next();
                
                if (odgovor.equalsIgnoreCase("da")) {
                    break;
                }
            }
            
            kursor.close();
            stmt.close();
        }
    }
    
    private static void evidentirajPredmetKaoObradjen(Connection con, int id_predmeta) throws SQLException {
        String sql = 
            "INSERT INTO OBRADJENI_PREDMETI " +
            "VALUES (?)";
        PreparedStatement stmt = con.prepareStatement(sql);
        
        stmt.setInt(1, id_predmeta);
        stmt.executeUpdate();
        
        stmt.close();
    }

    private static ResultSet otvoriKursor(Statement stmt, String sql) throws SQLException {
        ResultSet kursor = stmt.executeQuery(sql);
        return kursor;
    }
    
    private static ResultSet obradiCekanje(String codeHint, Connection con, Statement stmt, String sql) throws SQLException {
        System.out.printf("[%s] Objekat je zakljucan od strane druge transakcije!\n" +
                "Molimo sacekajte!\n", codeHint);
        
        try {
            con.rollback();
        } catch (SQLException e) {
        }
        
        return otvoriKursor(stmt, sql);
    }
}