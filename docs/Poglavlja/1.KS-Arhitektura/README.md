[Knjiga](../../README.md)

---

# 1. Klijentsko-serverska arhitektura

DB2 je sistem za upravljanje relacionim bazama podataka (RSUBP) koji nudi veoma moćne softverske alate za programiranje baza podataka. Ovi alati su korisni  kako administratorima baza podataka, tako i programerima aplikacija koje koriste mogućnosti relacionih baza podataka. Pod programiranjem baza podataka možemo smestiti naredne dve aktivnosti:

1. Programiranje na serveru - SQL rutine.
2. Programiranje na klijentu kroz više programske jezike.

Programiranje na klijentu (levo) podrazumeva korišćenje viših programskih jezika kao što su C, C++, Java, PHP i mnogih drugih. Često se ovi jezici nazivaju i *matični jezici* (engl. *host language*). Korišćenjem nekih od razvojnih okruženja, ti programski jezici se koriste za pristupanje interfejsu za programiranje aplikacija (engl. *application programming interface*, skr. *API*). Kroz ovaj interfejs se zatim, preko drajvera koji je dostupan programerima, aplikacija povezuje sa RSUBP i izvršava SQL upite. Ovo povezivanje sa bazom podataka se izvršava kroz mogućnosti operativnog sistema na kojem se klijentska aplikacija izvršava. 

Sa druge strane, programiranje na serveru obuhvata programiranje tzv. SQL *rutina* (engl. *routines*). SQL rutina može biti: *pohranjena procedura* (engl. *stored procedure*), *korisnički-definisana funkcija* (engl. *user-defined function*) ili *okidač* (engl. *trigger*). Svi ovi tipovi SQL rutina podrazumevaju programiranje operacija koje se čuvaju u samoj bazi podataka. Programeri zatim imaju mogućnost poziva ovih sačuvanih procedura, odn. njihovog izračunavanja.

!["Dva načina za programiranje baza podataka: programiranje na klijentu (levo) i programiranje na serveru (desno)."](./Slike/klijent-server-arhitektura.png "Dva načina za programiranje baza podataka: programiranje na klijentu (levo) i programiranje na serveru (desno).")

---

[Knjiga](../../README.md)
