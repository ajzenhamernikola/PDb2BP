package zadatak_8_1;

import java.sql.*;

public class Main {
    // Staticki blok koji sluzi za ucitavanje JDBC DB2 drajvera.
    // Izvrsava se kada se prvi put instancira objekat ove klase,
    // ili kada se prvi put pozove staticki metod.
    static {
        try {
            Class.forName("com.ibm.db2.jcc.DB2Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String argv[]) {
        // Objekat koji ce sadrzati konekciju
        Connection con = null;
        // URL za JDBC konekciju tipa 4
        String url = "jdbc:db2://localhost:50001/vstud";

        try {
            // Kreiramo konekciju na bazi podataka zadatoj u promenljivoj url,
            // koriscenjem metoda DriverManager.getConnection.
            // Argumenti za korisnicko ime i lozinku su obavezni!
            con = DriverManager.getConnection(url, "student", "abcdef");

            // Iskljucujemo automatsko potvrdjivanje izmena
            con.setAutoCommit(false);

            // Kreiramo objekat naredbe (Statement)
            Statement stmt = con.createStatement();

            // SQL upit ili naredbu koju zelimo da izvrsimo
            // zapisujemo kao nisku <-> DINAMICKI SQL
            String queryStr = 
                "SELECT SIFRA, " + 
                "       NAZIV " + 
                "FROM   PREDMET " + 
                "WHERE  BODOVI > 20";
            // Za izvrsavanje upita koristimo Statement.executeQuery metod.
            // Ovaj metod vraca objekat klase ResultSet,
            // koji sadrzi rezultate upita (kursor).
            ResultSet rs = stmt.executeQuery(queryStr);

            // Ispisujemo zaglavlje
            System.out.printf("%-15s %-50S\n\n", "SIFRA", "NAZIV");

            // Metodom ResultSet.next pozicioniramo kursor na naredni red.
            // Ako vise nema redova, metod ce vratiti vrednost false.
            // Inace, vratice true, pa mozemo da ga koristimo kao uslov u petlji
            while (rs.next()) {
                // Izdvajamo podatke koriscenjem familije metoda
                // ResultSet.getXXX,
                // gde je XXX neki tip podataka,
                // a argument je broj kolone koja se dohvata iz projekcije
                // upita.
                String sifra = rs.getString(1);
                String naziv = rs.getString(2);

                System.out.printf("%-15s %-50S\n", sifra.trim(), naziv.trim());
            }

            // Zatvaramo kursor
            rs.close();
            // Zatvaramo naredbu
            stmt.close();

            // S obzirom da je sve proslo kako treba ako se doslo do ove tacke,
            // potvrdjujemo izmene pre raskidanje konekcije
            con.commit();
            con.close();
        }
        // Obrada SQL gresaka
        catch (SQLException e) {
            // Ispisujemo sve informacije na standardni izlaz
            e.printStackTrace();

            System.out.println("SQLCODE: " + e.getErrorCode() + "\n" + "SQLSTATE: " + e.getSQLState() + "\n"
                    + "PORUKA: " + e.getMessage());

            // U slucaju neuspeha, ponistavamo eventualne izmene i zatvaramo
            // konekciju.
            // Pozivi metoda rollback() i close() ispod mogu da izbace
            // SQLException,
            // ali njih ignorisemo, te je zato catch blok prazan.
            try {
                if (null != con) {
                    con.rollback();
                    con.close();
                }
            } catch (SQLException e2) {
            }

            // Signaliziramo neuspesan zavrsetak programa
            System.exit(1);
        }
        // Obrada drugih gresaka
        catch (Exception e) {
            e.printStackTrace();

            // U slucaju neuspeha, ponistavamo eventualne izmene i zatvaramo
            // konekciju.
            // Pozivi metoda rollback() i close() ispod mogu da izbace
            // SQLException,
            // ali njih ignorisemo, te je zato catch blok prazan.
            try {
                if (null != con) {
                    con.rollback();
                    con.close();
                }
            } catch (SQLException e2) {
            }

            // Signaliziramo neuspesan zavrsetak programa
            System.exit(2);
        }
    }
}