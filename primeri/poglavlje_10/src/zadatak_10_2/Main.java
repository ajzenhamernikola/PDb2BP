package zadatak_10_2;

import org.hibernate.Session;
import org.hibernate.Transaction;

class Main {

    public static void main(String[] args) {
        System.out.println("Pocetak rada\n");

        deleteSmer();

        System.out.println("Zavrsetak rada\n");
        HibernateUtil.getSessionFactory().close();
    }

    private static void deleteSmer() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Smer smer = new Smer();

        Transaction TR = null;
        try {
            TR = session.beginTransaction();

            // Ucitavanje (dohvatanje) smera na osnovu primarnog kljuca
            session.load(smer, 300);
            // Brisanje ucitanog smera iz baze
            session.delete(smer);

            System.out.println("Smer obrisan");

            // Potvrdjivanje i zavrsavanje transakcije
            TR.commit();
        } catch (Exception e) {
            System.err.println("Brisanje smera nije uspelo! Ponistavanje transakcije!");

            if (TR != null) {
                TR.rollback();
            }
        } finally {
            session.close();
        }
    }

}
