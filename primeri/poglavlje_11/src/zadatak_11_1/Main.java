package zadatak_11_1;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

class Main {
	
	public static void main(String[] args) {
		System.out.println("Pocetak rada\n");
		
		insertSmer();
		deleteSmer();
		insertIspitniRok();
		deleteIspitniRok();
		readIspitniRokovi();
		
		System.out.println("Zavrsetak rada\n");
		HibernateUtil.getSessionFactory().close();
	}

	private static void insertSmer() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Smer smer = new Smer();
		
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
			TR = session.beginTransaction();
			
			session.save(smer);
			TR.commit();
			
			System.out.println("Smer je sacuvan");
		} catch (Exception e) {
			System.out.println("Cuvanje smera nije uspelo! Transakcija se ponistava!");
			
			if (TR != null) {
				TR.rollback();
			}
		} finally {
			session.close();
		}
	}
	
	private static void deleteSmer(){
		Session session = HibernateUtil.getSessionFactory().openSession();
		Smer smer = new Smer();
		
		Transaction TR = null;
		try {
			TR = session.beginTransaction();
			
			session.load(smer, 300); 
			session.delete(smer);
			
			System.out.println("Smer obrisan");
			
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
	
	private static void insertIspitniRok() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		IspitniRok ir = new IspitniRok();
		
		IspitniRokId id = new IspitniRokId(2019, "jun");
		ir.setId(id);
		ir.setNaziv("Jun 2019");
		ir.setPocetak("6/1/2019");
		ir.setKraj("6/22/2018");
		
		Transaction TR = null;
		try {
			TR = session.beginTransaction();
			
			session.save(ir);
			
			System.out.println("Ispitni rok je sacuvan");
			TR.commit();
		} catch (Exception e) {
			System.err.println("Cuvanje ispitnog roka nije uspelo! Ponistavanje transakcije!");
			
			if (TR != null) {
				TR.rollback();
			}
		} finally {
			session.close();
		}
	}
	
	private static void deleteIspitniRok() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		
		IspitniRok ir = new IspitniRok();
		IspitniRokId id = new IspitniRokId(2019, "jun");
		
		Transaction TR = null;
		try {
			TR = session.beginTransaction();
			
			session.load(ir, id);
			session.delete(ir);
			
			System.out.println("Ispitni rok je obrisan");
			TR.commit();
		} catch (Exception e) {
			System.err.println("Brisanje ispitnog roka nije uspelo! Ponistavanje transakcije!");
		
			if (TR != null) {
				TR.rollback();
			}
		} finally {
			session.close();
		}		
	}
	
	private static void readIspitniRokovi() {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction TR = null;
        
        try {
            TR = session.beginTransaction();
            
            // HQL upit za izdvajanje svih entiteta tipa IspitniRok
            String hql = "FROM IspitniRok";
            // Kreiranje objekta koji sadrzi informacije o HQL upitu.
            // Obratiti paznju da se klasa Query nalazi u paketu org.hibernate.query!!!
            // Takodje, pored samog HQL upita, 
            // metodu createQuery prosledjujemo klasu koja predstavlja entitet rezultata.
            // Drugim recima, kazemo Hibernate-u da zelimo da dohvatimo listu ispitnih rokova.
            org.hibernate.query.Query<IspitniRok> upit = 
                    session.createQuery(hql, IspitniRok.class);
            // Pozivom metoda list() dohvatamo zeljeni rezultat
            List<IspitniRok> ispitniRokovi = upit.list();
            // Iteriranje kroz listu
            for(IspitniRok ir : ispitniRokovi) {
                System.out.println(ir.getNaziv());
            }

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
