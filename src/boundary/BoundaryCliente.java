package boundary;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import control.GestioneNegozio;
import entity.EntityClienteRegistrato;
import entity.EntityCarrello;
import entity.EntityProdotto;

// Classe di interfaccia utente per il cliente (Boundary)
// Permette la registrazione, login, gestione carrello, acquisto e vendita prodotti tramite terminale
public class BoundaryCliente {
    // Scanner per input da terminale
    static Scanner scan = new Scanner(System.in);
    // Carrello associato all'utente loggato
    static EntityCarrello carrello = EntityCarrello.getInstance();
    // Telefono dell'utente loggato
    static String utenteTelefono = null;
    // Gestore delle logiche di business
    static GestioneNegozio gestore = GestioneNegozio.getInstance();
    // Utente attualmente loggato
    static EntityClienteRegistrato clienteLoggato = null;

    // Avvia il terminale per il cliente loggato
    public static void startTerminal(EntityClienteRegistrato cliente) {
        System.out.println("Benvenuto " + cliente.getNomeUtente() + "! (operazioni da terminale)");
        clienteLoggato = cliente;
        utenteTelefono = cliente.getTelefono();
        carrello.setNomeUtente(cliente.getNomeUtente());
        carrello = gestore.getCarrelloByNomeUtenteBCED(cliente.getNomeUtente());
        mainLoop();
    }

    // Ciclo principale delle operazioni disponibili per il cliente
    private static void mainLoop() {
        boolean exit = false;
        while (!exit) {
            System.out.println("Benvenuto nel negozio online!");
            System.out.println("1. Visualizza prodotti");
            System.out.println("2. Gestisci carrello");
            System.out.println("3. Acquista prodotti");
            System.out.println("4. Vendi un prodotto");
            System.out.println("5. Visualizza controproposte in attesa");
            System.out.println("6. Logout");
            System.out.print("Scelta: ");
            String op = scan.nextLine();
            switch (op) {
                case "1":
                    visualizzaProdotto();
                    break;
                case "2":
                    gestisciCarrello();
                    break;
                case "3":
                    acquistaProdotto();
                    break;
                case "4":
                    vendi();
                    break;
                case "5":
                    visualizzaControproposteInAttesa();
                    break;
                case "6":
                    clienteLoggato = null;
                    utenteTelefono = null;
                    System.out.println("Logout effettuato.");
                    exit = true;
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        }
        System.out.println("Arrivederci!");
    }

    // Metodo per la registrazione di un nuovo cliente
    private static void registrazione() {
        System.out.println("Inserisci nome utente:");
        String nomeUtente = scan.nextLine();
        System.out.println("Inserisci numero di telefono:");
        String telefono = scan.nextLine();
        System.out.println("Inserisci password:");
        String password = scan.nextLine();
        System.out.println("Inserisci numero carta di credito (16 cifre):");
        String carta = scan.nextLine();
        String messaggio = gestore.registraClienteBCED(nomeUtente, password, telefono, carta);
        System.out.println(messaggio);
        if (messaggio.startsWith("Registrazione completata")) {
            utenteTelefono = telefono;
            clienteLoggato = gestore.getClienteByUsernameBCED(nomeUtente);
            if (clienteLoggato != null) {
                carrello.setNomeUtente(clienteLoggato.getNomeUtente());
            }
        }
    }

    // Metodo per l'accesso di un cliente già registrato
    private static void accesso() {
        System.out.println("Inserisci nome utente:");
        String nomeUtente = scan.nextLine();
        System.out.println("Inserisci password:");
        String password = scan.nextLine();
        String messaggio = gestore.loginClienteBCED(nomeUtente, password);
        System.out.println(messaggio);
        if (messaggio.startsWith("Accesso effettuato")) {
            clienteLoggato = gestore.getClienteByUsernameBCED(nomeUtente);
            utenteTelefono = clienteLoggato.getTelefono();
            if (clienteLoggato != null) {
                carrello.setNomeUtente(clienteLoggato.getNomeUtente());
            }
        }
    }

    // Mostra la lista dei prodotti disponibili e permette di aggiungerli al carrello
    private static void visualizzaProdotto() {
        List<String> prodotti = gestore.visualizzaProdottiBCED();
        if (prodotti.isEmpty()) {
            System.out.println("Nessun prodotto disponibile.");
            return;
        }
        for (String p : prodotti) {
            System.out.println(p);
        }
        System.out.print("Vuoi aggiungere un prodotto al carrello? (s/n): ");
        String risposta = scan.nextLine();
        if (risposta.equalsIgnoreCase("s")) {
            System.out.print("Inserisci l'ID del prodotto da aggiungere: ");
            int idAggiungi = Integer.parseInt(scan.nextLine());
            System.out.print("Quantità da aggiungere: ");
            int quantitaAggiungi = Integer.parseInt(scan.nextLine());
            String messaggio = gestore.aggiungiAlCarrelloBCED(carrello, idAggiungi, quantitaAggiungi);
            System.out.println(messaggio);
            mostraCarrelloFormattato(carrello);
        }
    }

    // Stampa il contenuto del carrello in modo formattato
    private static void mostraCarrelloFormattato(EntityCarrello carrello) {
        Map<EntityProdotto, Integer> prodotti_carrello = gestore.prodottiCarrello(carrello);
        
        if(prodotti_carrello.isEmpty()) {
            System.out.println("Carrello vuoto.");
            return;
        }
        System.out.println("\nCarrello attuale:");
        for (Map.Entry<EntityProdotto, Integer> entry : prodotti_carrello.entrySet()) {
            EntityProdotto prod = entry.getKey();
            int quantita = entry.getValue();
            System.out.println("-----------------------------");
            System.out.println("ID:         " + prod.getIdProdotto());
            System.out.println("Nome:       " + prod.getNome());
            System.out.println("Descrizione:" + prod.getDescrizione());
            System.out.printf("Prezzo:     %.2f\n", prod.getPrezzo());
            System.out.println("Quantità:   " + quantita);
            System.out.println("Tipo:       " + prod.getTipo());
            System.out.println("Immagini:   " + prod.getImmagine1() + ", " + prod.getImmagine2() + ", " + prod.getImmagine3() + ", " + prod.getImmagine4());
            System.out.println("-----------------------------");
        }
        // Stampa il totale del carrello
        float prezzo_complessivo = gestore.getPrezzoComplessivoBCED(carrello.getIdCarrello(), new ArrayList<>(prodotti_carrello.keySet()));
        System.out.printf("Totale carrello: %.2f €\n", prezzo_complessivo);
        System.out.println("-----------------------------");
        
    }

    // Permette di aggiungere, visualizzare o rimuovere prodotti dal carrello
    private static void gestisciCarrello() {
        System.out.println("1. Aggiungi prodotto al carrello");
        System.out.println("2. Visualizza carrello");
        System.out.println("3. Rimuovi prodotto dal carrello");
        System.out.print("Scelta: ");
        String scelta = scan.nextLine();
        switch (scelta) {
            case "1":
                System.out.print("Inserisci ID prodotto da aggiungere: ");
                int idAggiungi = Integer.parseInt(scan.nextLine());
                System.out.print("Quantità: ");
                int quantitaAggiungi = Integer.parseInt(scan.nextLine());
                String messaggioAggiungi = gestore.aggiungiAlCarrelloBCED(carrello, idAggiungi, quantitaAggiungi);
                System.out.println(messaggioAggiungi);
                mostraCarrelloFormattato(carrello);
                break;
            case "2":
                mostraCarrelloFormattato(carrello);
                break;
            case "3":
                System.out.print("Inserisci ID prodotto da rimuovere: ");
                int idRimuovi = Integer.parseInt(scan.nextLine());
                System.out.print("Quantità da rimuovere: ");
                int quantitaRimuovi = Integer.parseInt(scan.nextLine());
                String messaggioRimuovi = gestore.rimuoviDalCarrelloBCED(carrello, idRimuovi, quantitaRimuovi);
                System.out.println(messaggioRimuovi);
                mostraCarrelloFormattato(carrello);
                break;
            default:
                System.out.println("Scelta non valida.");
        }
    }

    // Gestisce la procedura di acquisto dei prodotti presenti nel carrello
    private static void acquistaProdotto() {
        if (utenteTelefono == null || clienteLoggato == null) {
            System.out.println("Devi registrarti prima di acquistare.");
            return;
        }
        if (carrello.getProdotti().isEmpty()) {
            System.out.println("Carrello vuoto. Aggiungi prodotti prima.");
            return;
        }

        Map<EntityProdotto, Integer> prodotti_carrello = gestore.prodottiCarrello(carrello);
        
        if(prodotti_carrello.isEmpty()) {
            System.out.println("Carrello vuoto.");
            return;
        }

        float prezzo_complessivo = gestore.getPrezzoComplessivoBCED(carrello.getIdCarrello(), new ArrayList<>(prodotti_carrello.keySet()));
        String messaggio = gestore.acquistaProdottoBCED(carrello, clienteLoggato, prezzo_complessivo);
        System.out.println(messaggio);
        gestore.svuotaCarrelloBCED(carrello);
    }

    // Permette al cliente di proporre la vendita di un nuovo prodotto
    private static void vendi() {
        System.out.println("--- Proposta di vendita prodotto ---");
        System.out.print("Nome prodotto: ");
        String nome = scan.nextLine();
        System.out.print("Descrizione: ");
        String descrizione = scan.nextLine();
        System.out.print("Tipo (es: dipinto, scultura): ");
        String tipo = scan.nextLine().trim().toLowerCase();
        String tecnica = "";
        String dimensioni = "";
        float peso = 0;
        float altezza = 0;
        if (tipo.equals("scultura")) {
            System.out.print("Peso (kg): ");
            while (true) {
                String pesoInput = scan.nextLine();
                try {
                    peso = Float.parseFloat(pesoInput);
                    if (peso <= 0) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    System.out.print("Inserisci un peso valido (>0): ");
                }
            }
            System.out.print("Altezza (cm): ");
            while (true) {
                String altezzaInput = scan.nextLine();
                try {
                    altezza = Float.parseFloat(altezzaInput);
                    if (altezza <= 0) throw new NumberFormatException();
                    break;
                } catch (NumberFormatException e) {
                    System.out.print("Inserisci un'altezza valida (>0): ");
                }
            }
        } else if (tipo.equals("dipinto")) {
            System.out.print("Tecnica d'arte: ");
            tecnica = scan.nextLine();
            System.out.print("Dimensioni quadro (es: 50x70 cm): ");
            dimensioni = scan.nextLine();
        }
        // Chiedi gli URL delle immagini
        System.out.print("URL Immagine 1: ");
        String immagine1 = scan.nextLine();
        System.out.print("URL Immagine 2: ");
        String immagine2 = scan.nextLine();
        System.out.print("URL Immagine 3: ");
        String immagine3 = scan.nextLine();
        System.out.print("URL Immagine 4: ");
        String immagine4 = scan.nextLine();

        System.out.print("Prezzo proposto: ");
        float prezzo = 0;
        while (true) {
            String prezzoInput = scan.nextLine();
            try {
                prezzo = Float.parseFloat(prezzoInput);
                if (prezzo <= 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.print("Inserisci un prezzo valido (>0): ");
            }
        }
        System.out.print("Quantità: ");
        int quantita = 0;
        while (true) {
            String quantitaInput = scan.nextLine();
            try {
                quantita = Integer.parseInt(quantitaInput);
                if (quantita <= 0) throw new NumberFormatException();
                break;
            } catch (NumberFormatException e) {
                System.out.print("Inserisci una quantità valida (>0): ");
            }
        }

        String risposta = gestore.proponiVenditaProdottoBCED(clienteLoggato, nome, descrizione, tipo, prezzo, quantita, immagine1, immagine2, immagine3, immagine4, tecnica, dimensioni, peso, altezza);
        System.out.println(risposta);

        // Gestione controproposta
        while (risposta.startsWith("Controproposta")) {
            String prezzoControproposta = estraiPrezzoControproposta(risposta);
            System.out.println("Il gestore propone il prezzo: " + prezzoControproposta + " euro");
            System.out.print("Accetti la controproposta? (s/n): ");
            String accetta = scan.nextLine();
            if (accetta.equalsIgnoreCase("s")) {
                String conferma = gestore.accettaContropropostaVenditaBCED(clienteLoggato, nome);
                System.out.println(conferma);
                break;
            } else {
                System.out.println("Compravendita annullata.");
                gestore.annullaPropostaVenditaBCED(clienteLoggato, nome);
                break;
            }
        }
    }

    // Estrae il prezzo dalla stringa di risposta della controproposta
    private static String estraiPrezzoControproposta(String risposta) {
        String[] parts = risposta.split(":");
        if (parts.length > 1) {
            return parts[1].replaceAll("[^0-9.,]", "").trim();
        }
        return "";
    }

    // Visualizza le controproposte di vendita in attesa e permette di accettarle o rifiutarle
    private static void visualizzaControproposteInAttesa() {
        List<String> storico = gestore.visualizzacontropoposteBCED(clienteLoggato);
        boolean contropropostaPresente = false;
        String nomeProdottoControproposta = null;
        for (String s : storico) {
            System.out.println(s);
            if (s.contains("Stato: CONTROPROPOSTA")) {
                contropropostaPresente = true;
                // Estrai il nome prodotto dalla stringa
                int idx = s.indexOf("Prodotto: ");
                int idx2 = s.indexOf(" |", idx);
                if (idx != -1 && idx2 != -1) {
                    nomeProdottoControproposta = s.substring(idx + 10, idx2).trim();
                }
            }
        }
        if (contropropostaPresente && nomeProdottoControproposta != null) {
            System.out.print("Hai una controproposta in attesa. Vuoi accettarla? (s/n): ");
            String scelta = scan.nextLine();
            if (scelta.equalsIgnoreCase("s")) {
                String conferma = gestore.accettaContropropostaVenditaBCED(clienteLoggato, nomeProdottoControproposta);
                System.out.println(conferma);
            } else {
                gestore.annullaPropostaVenditaBCED(clienteLoggato, nomeProdottoControproposta);
                System.out.println("Proposta rifiutata.");
            }
        }
    }

    // Metodo main: gestisce il menu iniziale di registrazione, accesso o uscita
    public static void main(String[] args) {
        boolean exit = false;
        boolean loggedIn = false;
        while (!exit) {
            if (!loggedIn) {
                System.out.println("Benvenuto nel negozio online!");
                System.out.println("1. Registrazione");
                System.out.println("2. Accesso");
                System.out.println("3. Esci");
                System.out.print("Scelta: ");
                String op = scan.nextLine();
                switch (op) {
                    case "1":
                        registrazione();
                        if (clienteLoggato != null) {
                            loggedIn = true;
                            mainLoop();
                            loggedIn = false;
                        }
                        break;
                    case "2":
                        accesso();
                        if (clienteLoggato != null) {
                            loggedIn = true;
                            mainLoop();
                            loggedIn = false;
                        }
                        break;
                    case "3":
                        exit = true;
                        break;
                    default:
                        System.out.println("Opzione non valida. Riprova.");
                }
            }
        }
    }
}
