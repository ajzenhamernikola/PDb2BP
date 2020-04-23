package zadatak_10_1;

import org.hibernate.Session;
import org.hibernate.Transaction;

public class Main {

	public static void main(String[] args) {
		System.out.println("Pocetak rada\n");
		
		readSmer();
		updateSmer();
		readSmer();
		
		System.out.println("Zavrsetak rada\n");

		HibernateUtil.getSessionFactory().close();
	}
	
	private static void readSmer() {
	
		Session session = HibernateUtil.getSessionFactory().openSession();
		Smer s = new Smer();
			
		s = session.get(Smer.class, 2020);

		if (s != null) {
			System.out.println(s);
		}
		else {
			System.out.println("Ne postoji zadati ispitni rok!");
		}

		session.close();
    }
	
	private static void updateSmer() {
		try (Session session = HibernateUtil.getSessionFactory().openSession();) {

			Smer smer = new Smer();
			
			Transaction TR = null;
			
			try {
				TR = session.beginTransaction();
				session.load(smer, 2020);

				smer.setNaziv("Ovo je nov naziv");
				
				TR.commit();
				System.out.println("Smer je azuriran!");	
			}
			catch (Exception e) {
				System.err.println("Azuriranje smera nije uspelo!");
				
				if (TR != null) {
					TR.rollback();
				}
			}
		}
	}
}
