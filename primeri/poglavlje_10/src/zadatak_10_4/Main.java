package zadatak_10_4;

import org.hibernate.Session;
import org.hibernate.Transaction;

class Main {
	
	public static void main(String[] args) {
		System.out.println("Pocetak rada\n");
		
		deleteIspitniRok();
		
		System.out.println("Zavrsetak rada\n");
		HibernateUtil.getSessionFactory().close();
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

}
