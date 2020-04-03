SELECT ID_PREDMETA,  
       NAZIV,  
       BODOVI 
FROM   PREDMET 
WHERE  ID_PREDMETA IN ( 
           SELECT  ID_PREDMETA 
           FROM    OBAVEZAN_PREDMET 
           WHERE   ID_SMERA = 201 
       ) AND 
       ID_PREDMETA NOT IN (  
           SELECT  ID_PREDMETA 
           FROM    OBRADJENI_PREDMETI
       )