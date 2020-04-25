package zadatak_11_2;

import java.util.Scanner;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

class Main {
	
	public static void main(String[] args) {
		System.out.println("Pocetak rada...\n");
		
		readIspitniRok();
		
		System.out.println("Zavrsetak rada.\n");
		HibernateUtil.getSessionFactory().close();
	}

	private static void readIspitniRok() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction TR = null;
        
        try (Scanner ulaz = new Scanner(System.in)) {
            TR = session.beginTransaction();
            
            System.out.println("Unesite godinu roka:");
            Integer godina = ulaz.nextInt();
            System.out.println("Unesite oznaku roka:");
            String oznaka = ulaz.next();
            
            // HQL upit za izdvajanje entiteta tipa IspitniRok
            // sa odredjenom godinom i oznakom.
            // Kao sto se u FROM klauzi navodi naziv KLASE, a ne TABELE,
            // tako se u WHERE klauzi navode ATRIBUTI, a ne KOLONE.
            String hql = "FROM IspitniRok WHERE id.godina = :godina AND id.oznaka = :oznaka";
            // Pripremanje upita
            Query<IspitniRok> upit = 
                    session.createQuery(hql, IspitniRok.class);
            // Postavljanje vrednosti za imenovane parametarske oznake
            upit.setParameter("godina", godina);
            upit.setParameter("oznaka", oznaka);
            // Izvrsavanje upita i listanje podataka
            IspitniRok ispitniRok = upit.getSingleResult();
            System.out.println(ispitniRok);

            TR.commit();
        } catch (Exception e) {
            System.err.println("Postoji problem sa ispisivanjem ispitnih rokova! Ponistavanje transakcije!");
        
            if (TR != null) {
                TR.rollback();
            }
        } finally {
            session.close();
        }
    }
	
}
