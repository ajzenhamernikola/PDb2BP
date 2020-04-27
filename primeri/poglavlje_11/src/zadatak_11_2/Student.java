package zadatak_11_2;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "dosije")
public class Student {
    // Primarni kljuc

    @Id
    @Column
    private Integer indeks;

    // Kolone od znacaja

    @Column(name = "ime", nullable = false)
    private String ime;

    @Column(name = "prezime", nullable = false)
    private String prezime;

    @Column(name = "mesto_rodjenja")
    private String mesto;


    // Kreiramo dvosmernu asocijativnu vezu izmedju klasa Smer i Student.
    // Posto tabela Dosije sadrzi strani kljuc id_smera koji referise na Smer
    // potrebno je da se u klasi Smer definise vrednost za opciju mappedBy.
    // Dodatno, zbog stranog kljuca moramo dodati anotaciju @JoinColumn kako
    // bismo ogranicili koriscenje ove reference na citanje.
    @ManyToOne
    @JoinColumn(name="id_smera", referencedColumnName="id_smera", insertable=false, updatable=false)
    private Smer smer;

    // Get/set metodi

    public Integer getIndeks() {
        return indeks;
    }

    public void setIndeks(Integer indeks) {
        this.indeks = indeks;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public Smer getSmer() {
		return smer;
	}

	public void setSmer(Smer smer) {
		this.smer = smer;
	}
}
