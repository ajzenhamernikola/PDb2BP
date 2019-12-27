package zadatak_9_3;

import java.io.File;
import java.io.FileNotFoundException;
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
        String urlVstud = "jdbc:db2://localhost:50001/vstud";
        Connection conVstud = null;

        String urlMstud = "jdbc:db2://localhost:50001/mstud";
        Connection conMstud = null;

        try {
            System.out.println("Povezivanje na VSTUD...");
            conVstud = DriverManager.getConnection(urlVstud, "student", "abcdef");
            conVstud.setAutoCommit(false);
            System.out.println("Uspesno je ostvarena konekcija!");

            System.out.println("Povezivanje na MSTUD...");
            conMstud = DriverManager.getConnection(urlMstud, "student", "abcdef");
            conMstud.setAutoCommit(false);
            System.out.println("Uspesno je ostvarena konekcija!");

            try (Scanner ulaz = new Scanner(System.in)) {
                // Program redom:
                // Zahteva od korisnika da unese broj bodova B.

                System.out.println("Unesite broj bodova B:");
                short brojBodova = ulaz.nextShort();

                // Iz baze MSTUD izdvaja indeks, ime i prezime studenata
                // koji su polozili sve predmete koji nose vise od B bodova.

                izlistajStudenteMstud(conMstud, brojBodova);

                // Zatim, zahteva od korisnika da unese ocenu O (ceo broj od 6
                // do 10).

                System.out.println("Unesite ocenu O:");
                short ocena = ulaz.nextShort();

                // Iz baze VSTUD izlistava indeks, naziv, ocenu, godinu i oznaku
                // ispitnog roka
                // za sve studente koji nikada nisu dobili ocenu manju nego sto
                // je ocena O.

                izlistajPolaganjaVstud(conVstud, ocena);

                // Nakon ispisivanja tih podataka, u bazi MSTUD, iz tabele ISPIT
                // brise sva polaganja za studenta sa maksimalnim brojem indeksa
                // I
                // iz DOSIJE, i vraca I.

                int indeks = obrisiPolaganjaIVratiIndeksMstud(conMstud);

                // Na kraju, u bazi VSTUD, u tabeli PREDMET
                // za sve predmete koje je polozio student sa brojem indeksa I,
                // uvecava broj bodova za jedan (osim ako je broj bodova veci od
                // 10,
                // tada ostavlja nepromenjeno stanje).

                uvecajBodoveZaPredmeteVstud(conVstud, indeks);
            }

            // Potvrdjivanje izmena i zatvaranje konekcije
            // mora da se vrsi nad obe baze!
            conVstud.commit();
            conVstud.close();

            conMstud.commit();
            conMstud.close();
        } catch (SQLException e) {
            e.printStackTrace();

            System.out.println("SQLCODE: " + e.getErrorCode() + "\n" + "SQLSTATE: " + e.getSQLState() + "\n"
                    + "PORUKA: " + e.getMessage());

            try {
                // Ponistavanje izmena i zatvaranje konekcije
                // mora da se vrsi nad obe baze!
                if (null != conVstud) {
                    conVstud.rollback();
                    conVstud.close();
                }
                if (null != conMstud) {
                    conMstud.rollback();
                    conMstud.close();
                }
            } catch (SQLException e2) {
            }

            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (null != conVstud) {
                    conVstud.rollback();
                    conVstud.close();
                }
                if (null != conMstud) {
                    conMstud.rollback();
                    conMstud.close();
                }
            } catch (SQLException e2) {
            }

            System.exit(2);
        }
    }

    private static void izlistajStudenteMstud(Connection con, short brojBodova)
            throws SQLException, FileNotFoundException {
        String sql = ucitajSqlIzDatoteke("izlistajStudenteMstud.sql");
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setShort(1, brojBodova);
        ResultSet rez = stmt.executeQuery();

        System.out.println("\n\nStudenti koji su polozili sve predmete od " + brojBodova + " bodova\n");
        while (rez.next()) {
            System.out.println("Indeks: " + rez.getInt(1) + ", " + "Ime: " + rez.getString(2).trim() + ", "
                    + "Prezime: " + rez.getString(3).trim() + ", ");
        }

        rez.close();
        stmt.close();
    }

    private static void izlistajPolaganjaVstud(Connection con, short ocena) throws SQLException, FileNotFoundException {
        String sql = ucitajSqlIzDatoteke("izlistajPolaganjaVstud.sql");
        PreparedStatement stmt = con.prepareStatement(sql);

        stmt.setShort(1, ocena);
        ResultSet rez = stmt.executeQuery();

        System.out.println("Polozeni ispiti studenata koji nemaju ocenu manju od " + ocena);
        while (rez.next()) {
            System.out.println("Indeks: " + rez.getInt(1) + ", " + "Naziv: " + rez.getString(2).trim() + ", "
                    + "Ocena: " + rez.getInt(3) + ", " + "Godina roka: " + rez.getInt(4) + ", " + "Oznaka roka: "
                    + rez.getString(5).trim());
        }

        rez.close();
        stmt.close();
    }

    private static int obrisiPolaganjaIVratiIndeksMstud(Connection con) throws Exception {
        int indeks = 0;
        Statement stmt = con.createStatement();
        ResultSet rez = stmt.executeQuery(
            "SELECT MAX(INDEKS) " + 
            "FROM   DOSIJE");

        boolean dohvacenIndeks = rez.next();
        if (!dohvacenIndeks) {
            stmt.close();
            throw new Exception("Ne postoji nijedan indeks u bazi podataka");
        }

        indeks = rez.getInt(1);
        rez.close();

        int brojObrisanih = stmt.executeUpdate(
            "DELETE FROM ISPIT " + 
            "WHERE  INDEKS = (SELECT MAX(INDEKS) FROM DOSIJE)");        
        System.out.println("Broj obrisanih redova: " + brojObrisanih);
        
        stmt.close();
        return indeks;
    }

    private static void uvecajBodoveZaPredmeteVstud(Connection con, int indeks) 
            throws SQLException, FileNotFoundException {
        String sql = ucitajSqlIzDatoteke("uvecajBodoveZaPredmeteVstud.sql");
        PreparedStatement stmt = con.prepareStatement(sql);
        stmt.setInt(1, indeks);

        int brojAzuriranih = stmt.executeUpdate();
        System.out.println("Broj azuriranih redova: " + brojAzuriranih);
        
        stmt.close();
    }

    private static String ucitajSqlIzDatoteke(String nazivDatoteke) throws FileNotFoundException {
        String putanja = "./bin/zadatak_9_3/" + nazivDatoteke;
        StringBuilder sql = new StringBuilder("");
        String linija = null;

        try (Scanner skenerFajla = new Scanner(new File(putanja), "utf-8")) {
            while (skenerFajla.hasNextLine()) {
                linija = skenerFajla.nextLine();
                sql.append(linija);
                sql.append("\n");
            }
        }

        return sql.toString();
    }
}