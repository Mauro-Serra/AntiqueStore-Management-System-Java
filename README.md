# NegozioAntiquariato

Sistema gestionale completo per un negozio di antiquariato, sviluppato in Java, con architettura multilivello, gestione database relazionale MySQL, interfaccia utente (CLI/GUI), e ampio supporto a test automatici.

---

## Sommario

- [Descrizione Generale](#descrizione-generale)
- [Architettura e Struttura del Progetto](#architettura-e-struttura-del-progetto)
- [Funzionalità Dettagliate](#funzionalità-dettagliate)
- [Dipendenze e Requisiti](#dipendenze-e-requisiti)
- [Installazione e Configurazione](#installazione-e-configurazione)
- [Esecuzione](#esecuzione)
- [Testing](#testing)
- [Documentazione delle Classi e dei Package](#documentazione-delle-classi-e-dei-package)
- [Gestione Database](#gestione-database)
- [Estendibilità e Personalizzazione](#estendibilità-e-personalizzazione)
- [Autori e Licenza](#autori-e-licenza)

---

## Descrizione Generale

NegozioAntiquariato è un sistema gestionale modulare per la gestione di un negozio di antiquariato. Permette la gestione completa di prodotti (dipinti, sculture, ecc.), clienti, ordini, carrelli, proposte di vendita, notifiche e autenticazione. Il sistema è progettato per essere facilmente estendibile e manutenibile, con una chiara separazione tra livelli (entity, control, boundary, database, exception).

---

## Architettura e Struttura del Progetto

- **src/**: Codice sorgente Java
  - **Main.java**: Entry point dell'applicazione, avvia la boundary principale.
  - **boundary/**: Interfacce utente (GUI Swing e CLI)
    - `BoundaryCliente.java`: Interfaccia per clienti registrati
    - `BoundaryGestore.java`: Interfaccia per gestore del negozio
    - `BoundaryLogin.java`: Gestione autenticazione
    - `graphics/`: Componenti grafici custom
  - **control/**: Logica di business e coordinamento
    - `GestioneNegozio.java`: Gestione centrale di prodotti, ordini, proposte, clienti
  - **database/**: Data Access Object (DAO) e gestione connessione
    - `DBManager.java`: Gestione connessione e pool DB
    - `ProdottoDAO.java`, `OrdineDAO.java`, `PropostaVenditaDAO.java`, `CarrelloDAO.java`, `ClienteRegistratoDAO.java`: CRUD e query custom
  - **entity/**: Modelli dati (POJO)
    - `EntityProdotto.java`, `EntityDipinto.java`, `EntityScultura.java`, `EntityClienteRegistrato.java`, `EntityOrdine.java`, `EntityPropostaVendita.java`, ecc.
  - **exception/**: Eccezioni custom
    - `DAOException.java`, `DBConnectionException.java`, `OperationException.java`
  - **Test/**: Test di unità e integrazione (JUnit 5, Mockito)
    - `control/GestioneNegozioTest.java`, ecc.

- **bin/**: File compilati (.class)
- **lib/**: Librerie esterne (JAR)
- **notifiche/**: File di notifica di esempio
- **mysql-connector-j-9.3.0.jar**: Driver JDBC per MySQL

---

## Funzionalità Dettagliate

- **Gestione Prodotti**
  - Inserimento, modifica, eliminazione, visualizzazione prodotti
  - Supporto a tipologie specializzate (Dipinto, Scultura) con attributi specifici (tecnica, dimensioni, peso, altezza, ecc.)
- **Gestione Clienti**
  - Registrazione, autenticazione, modifica dati personali
  - Visualizzazione storico ordini e proposte
- **Gestione Ordini**
  - Creazione ordini da carrello
  - Visualizzazione dettagli ordine, stato, storico
- **Carrello**
  - Aggiunta/rimozione prodotti, gestione quantità
  - Calcolo totale, svuotamento carrello
- **Proposte di Vendita**
  - Invio proposta da parte del cliente (con immagini, descrizione, prezzo proposto)
  - Valutazione proposta da parte del gestore (accetta/rifiuta/annulla, prezzo gestore)
  - Tracciamento stato proposta (IN_ATTESA, ACCETTATA, RIFIUTATA, ANNULLATA)
- **Gestione Notifiche**
  - Notifiche automatiche per cambi stato ordine/proposta
  - Notifiche personalizzate gestore-cliente
- **Gestione Accessi**
  - Login/logout per clienti e gestore
  - Controllo ruoli e permessi
- **Interfaccia Utente**
  - GUI Swing moderna (KGradientPanel, SVG)
  - CLI per testing rapido
- **Gestione Errori**
  - Eccezioni custom per errori DB, operazioni non valide, ecc.

---

## Dipendenze e Requisiti

- **Java 17** o superiore
- **MySQL** (server attivo, database configurato)
- **Librerie esterne** (in `lib/`):
  - `mysql-connector-j-9.3.0.jar` (driver JDBC)
  - `junit-jupiter-api`, `junit-jupiter-engine`, `mockito-core`, ecc. (per i test)
  - `KGradientPanel.jar`, `svgSalamander-1.1.4.jar` (per la GUI)

---

## Installazione e Configurazione

1. **Clona o scarica il progetto**
2. **Configura il database MySQL**
   - Crea un database (es: `negozio_antiquariato`)
   - Importa la struttura e i dati iniziali tramite script SQL forniti (se presenti)
   - Aggiorna i parametri di connessione in `DBManager.java`:
     ```java
     private static final String URL = "jdbc:mysql://localhost:3306/negozio_antiquariato";
     private static final String USER = "root";
     private static final String PASSWORD = "";
     ```
3. **Aggiungi le librerie al classpath**
   - Assicurati che tutti i JAR in `lib/` siano inclusi nel classpath di compilazione ed esecuzione
4. **Compila il progetto**
   - Da terminale, nella cartella `src/`:
     ```pwsh
     javac -cp "../lib/*" -d ../bin Main.java
     ```
5. **Esegui l'applicazione**
   - Da terminale, nella cartella `bin/`:
     ```pwsh
     java -cp ".;../lib/*" Main
     ```
   - Su Linux/Mac sostituisci `;` con `:` nel classpath
6. **Esegui i test**
   - Utilizza JUnit 5 e le librerie in `lib/` per eseguire i test in `src/Test/`

---

## Esecuzione

- Lanciare `Main.java` per avviare l'applicazione.
- Seguire le istruzioni a schermo per login, gestione prodotti, ordini, proposte, ecc.
- In caso di errori di connessione, verificare i parametri in `DBManager.java` e la presenza del driver JDBC.

---

## Testing

- I test sono scritti con JUnit 5 e Mockito.
- Per eseguire i test:
  - Assicurarsi che le librerie di test siano nel classpath
  - Eseguire i test tramite IDE o da terminale:
    ```pwsh
    java -jar ../lib/junit-platform-console-standalone-1.13.1.jar --class-path . --scan-class-path
    ```
- I test coprono logica di business, DAO, casi limite e gestione errori.

---

## Documentazione delle Classi e dei Package

### Package principali

- **entity/**: Modelli dati (POJO) con campi, costruttori, getter/setter, toString, e Javadoc dettagliato
- **database/**: DAO per ogni entità, con metodi CRUD, query custom, gestione transazioni, e gestione errori
- **control/**: Logica di business centralizzata, orchestrazione operazioni tra DAO e boundary
- **boundary/**: Interfacce utente (GUI/CLI), gestione input/output, validazione dati
- **exception/**: Eccezioni custom per errori di accesso dati, connessione, operazioni non valide
- **Test/**: Test automatici, mock, test di integrazione

### Classi principali (con Javadoc nel codice sorgente)

- **EntityProdotto, EntityDipinto, EntityScultura**: Modelli per prodotti, con attributi generici e specifici
- **EntityClienteRegistrato**: Modello cliente, con dati anagrafici, credenziali, storico ordini/proposte
- **EntityOrdine**: Modello ordine, con prodotti, quantità, stato, data
- **EntityPropostaVendita**: Modello proposta di vendita, con stato, immagini, prezzi, riferimenti
- **GestioneNegozio**: Logica centrale, gestione prodotti, ordini, proposte, clienti
- **DAO**: CRUD e query custom per ogni entità, gestione errori e transazioni
- **BoundaryCliente, BoundaryGestore**: Interfacce utente per clienti e gestore, flussi operativi
- **DBManager**: Gestione connessione, pool, chiusura risorse

---

## Gestione Database

- Struttura tabelle: prodotti, clienti, ordini, proposte_vendita, carrelli, ecc.
- Script SQL forniti per creazione e popolamento (se presenti)
- Gestione integrità referenziale tramite chiavi esterne
- Gestione transazioni nei DAO
- Gestione errori SQL tramite eccezioni custom

---

## Estendibilità e Personalizzazione

- Per aggiungere nuove tipologie di prodotto:
  - Creare nuova classe in `entity/`
  - Aggiornare DAO e logica in `control/`
  - Aggiornare interfacce in `boundary/`
- Per aggiungere nuove funzionalità:
  - Seguire pattern MVC e separazione livelli
  - Documentare con Javadoc e commenti dettagliati

---

## Autori

- Progetto sviluppato per il corso di Ingegneria del Software
- Autori: [Passaro Andrea, Serra Mauro, Moccia Paolo, Traditi Roberto]
