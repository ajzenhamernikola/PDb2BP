package zadatak_10_1;

import org.hibernate.Session;
import org.hibernate.Transaction;

class Main {
    
    public static void main(String[] args) {
        System.out.println("Pocetak rada\n");
        
        insertSmer();
        
        System.out.println("Zavrsetak rada\n");
        
        // Zatvaranje fabrike sesija
        HibernateUtil.getSessionFactory().close();
    }
    
    private static void insertSmer() {
        // Otvaranje sesije
        Session session = HibernateUtil.getSessionFactory().openSession();
        // Kreiranje objekta klase Smer.
        // U ovom objektu ce biti zapisane sve informacije o novom smeru,
        // koje ce zatim biti skladistene u bazi podataka.
        Smer smer = new Smer();
        
        // Postavljanje odgovarajucih vrednosti za smer
        smer.setId_smera(300);
        smer.setOznaka("MATF_2019");
        smer.setNaziv("Novi MATF smer u 2019. godini");
        smer.setSemestara(8);
        smer.setBodovi(240);
        smer.setNivo(110);
        smer.setZvanje("Diplomirani informaticar");
        smer.setOpis("Novi smer na Matematickom fakultetu");
        
        Transaction TR = null;
        try {
            // Zapocinjemo novu transakciju
            TR = session.beginTransaction();
            
            // Skladistimo kreirani smer u tabelu SMER u bazi podataka
            session.save(smer);
            // Pohranjivanje izmena i zavrsavanje transakcije
            TR.commit();
            
            System.out.println("Smer je sacuvan");
        } catch (Exception e) {
            // Doslo je do greske: ponistavamo izmene u transakciji
            System.out.println("Cuvanje smera nije uspelo! Transakcija se ponistava!");
            
            if (TR != null) {
                TR.rollback();
            }
        } finally {
            // Bilo da je doslo do uspeha ili do neuspeha,
            // duzni smo da zatvorimo sesiju
            session.close();
        }
    }
}
