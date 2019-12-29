package zadatak_10_3;

import org.hibernate.Session;
import org.hibernate.Transaction;

class Main {
	
	public static void main(String[] args) {
		System.out.println("Pocetak rada\n");
		
		insertSmer();
		deleteSmer();
		insertIspitniRok();
		
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
		
		// Kreiramo praznu instancu objekta ispitnog roka 
		// koju cemo popuniti vrednostima koje treba sacuvati
		IspitniRok ir = new IspitniRok();
		
		// Kreiramo prvo identifikator, tj. slozeni kljuc,
		// a zatim i ostale podatke
		IspitniRokId id = new IspitniRokId(2019, "jun");
		ir.setId(id);
		ir.setNaziv("Jun 2019");
		ir.setPocetak("6/1/2019");
		ir.setKraj("6/22/2018");
		// Ne moramo da podesimo jer ce biti izabrana podrazumevana vrednost
		// ir.setTip("B");
		
		// Procedura za cuvanje je ista kao i do sada
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

}
