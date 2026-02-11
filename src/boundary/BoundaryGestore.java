package boundary;

import java.util.List;
import java.util.Scanner;
import control.GestioneNegozio;

// Classe di interfaccia utente per il gestore del negozio
// Permette la gestione dei prodotti, la generazione di report e la valutazione delle proposte di vendita tramite terminale
public class BoundaryGestore {
    // Scanner per input da terminale
    static Scanner scan = new Scanner(System.in);
    // Gestore delle logiche di business
    static GestioneNegozio gestore = GestioneNegozio.getInstance();

    // Metodo main: ciclo principale delle operazioni disponibili per il gestore
    public static void main(String[] args) {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n--- Gestione Negozio ---");
            System.out.println("1. Aggiungi Prodotto");
            System.out.println("2. Visualizza Prodotti");
            System.out.println("3. Modifica Prodotto");
            System.out.println("4. Rimuovi Prodotto");
            System.out.println("5. Genera Report");
            System.out.println("6. Valuta Proposte");
            System.out.println("7. Esci");
            System.out.print("Scelta: ");

            String op = scan.nextLine();

            switch (op) {
                case "1":
                    aggiungiProdotto(); // Inserisce un nuovo prodotto nel catalogo
                    break;
                case "2":
                    visualizzaProdotto(); // Mostra tutti i prodotti disponibili
                    break;
                case "3":
                    modificaProdotto(); // Modifica le informazioni di un prodotto esistente
                    break;
                case "4":
                    rimuoviProdotto(); // Rimuove un prodotto dal catalogo
                    break;
                case "5":
                    generaReport(); // Genera report su acquisti o vendite
                    break;
                case "6":
                    valutaProposta(); // Valuta le proposte di vendita dei clienti
                    break;
                case "7":
                    exit = true;
                    break;
                default:
                    System.out.println("Opzione non valida. Riprova.");
            }
        }

        System.out.println("Arrivederci!");
    }

    // Permette di aggiungere un nuovo prodotto al catalogo
    private static void aggiungiProdotto() {
        System.out.print("Nome: ");
        String nome = scan.nextLine();
        System.out.print("Descrizione: ");
        String descrizione = scan.nextLine();
        System.out.print("Prezzo: ");
        float prezzo = Float.parseFloat(scan.nextLine());
        System.out.print("URL Immagine 1: ");
        String immagine1 = scan.nextLine();
        System.out.print("URL Immagine 2: ");
        String immagine2 = scan.nextLine();
        System.out.print("URL Immagine 3: ");
        String immagine3 = scan.nextLine();
        System.out.print("URL Immagine 4: ");
        String immagine4 = scan.nextLine();
        System.out.print("Quantità: ");
        int quantita = Integer.parseInt(scan.nextLine());
        System.out.print("Tipo (dipinto/scultura/altro): ");
        String tipo = scan.nextLine().trim().toLowerCase();
        String tecnica = "";
        String dimensioni = "";
        float peso = 0;
        float altezza = 0;
        if (tipo.equals("scultura")) {
            System.out.print("Peso (kg): ");
            peso = Float.parseFloat(scan.nextLine());
            System.out.print("Altezza (cm): ");
            altezza = Float.parseFloat(scan.nextLine());
        } else if (tipo.equals("dipinto")) {
            System.out.print("Tecnica: ");
            tecnica = scan.nextLine();
            System.out.print("Dimensioni: ");
            dimensioni = scan.nextLine();
        }
        String messaggio = gestore.aggiungiProdottoBCED(nome, descrizione, prezzo, immagine1, immagine2, immagine3, immagine4, quantita, tipo, tecnica, dimensioni, peso, altezza);
        System.out.println(messaggio);
    }

    // Mostra la lista dei prodotti disponibili
    private static void visualizzaProdotto() {
        List<String> prodotti = gestore.visualizzaProdottiBCED();
        if (prodotti.isEmpty()) {
            System.out.println("Nessun prodotto disponibile.");
            return;
        }
        for (String p : prodotti) {
            System.out.println(p);
        }
    }

    // Permette di modificare le informazioni di un prodotto esistente
    private static void modificaProdotto() {
        System.out.print("ID prodotto da modificare: ");
        int id = Integer.parseInt(scan.nextLine());
        System.out.print("Nuovo nome: ");
        String nome = scan.nextLine();
        System.out.print("Nuova descrizione: ");
        String descrizione = scan.nextLine();
        System.out.print("Nuovo prezzo: ");
        String prezzoStr = scan.nextLine();
        System.out.print("Nuova quantità: ");
        String quantitaStr = scan.nextLine();
        System.out.print("Tipo (dipinto/scultura/altro): ");
        String tipo = scan.nextLine().trim().toLowerCase();
        String tecnica = "";
        String dimensioni = "";
        String pesoStr = "";
        String altezzaStr = "";
        if (tipo.equals("scultura")) {
            System.out.print("Nuovo peso (kg): ");
            pesoStr = scan.nextLine();
            System.out.print("Nuova altezza (cm): ");
            altezzaStr = scan.nextLine();
        } else if (tipo.equals("dipinto")) {
            System.out.print("Nuova tecnica: ");
            tecnica = scan.nextLine();
            System.out.print("Nuove dimensioni: ");
            dimensioni = scan.nextLine();
        }
        String messaggio = gestore.modificaProdottoBCED(id, nome, descrizione, prezzoStr, quantitaStr, tipo, tecnica, dimensioni, pesoStr, altezzaStr);
        System.out.println(messaggio);
    }

    // Permette di rimuovere un prodotto dal catalogo
    private static void rimuoviProdotto() {
        System.out.print("ID prodotto da rimuovere: ");
        int id = Integer.parseInt(scan.nextLine());
        String messaggio = gestore.rimuoviProdottoBCED(id);
        System.out.println(messaggio);
    }

    // Permette di generare report su acquisti o vendite dei clienti
    private static void generaReport() {
        System.out.println("1. Report acquisti clienti");
        System.out.println("2. Report vendite clienti");
        System.out.print("Scegli il tipo di report: ");
        String scelta = scan.nextLine();
        switch (scelta) {
            case "1":
                System.out.print("Numero minimo di acquisti: ");
                int minAcquisti = Integer.parseInt(scan.nextLine());
                System.out.print("Nome file CSV di output (es: report_acquisti.csv): ");
                String filePathA = scan.nextLine();
                String msgA = gestore.generaReportAcquistiBCED(minAcquisti, filePathA);
                System.out.println(msgA);
                break;
            case "2":
                System.out.print("Numero minimo di vendite: ");
                int minVendite = Integer.parseInt(scan.nextLine());
                System.out.print("Nome file CSV di output (es: report_vendite.csv): ");
                String filePathV = scan.nextLine();
                String msgV = gestore.generaReportVenditeBCED(minVendite, filePathV);
                System.out.println(msgV);
                break;
            default:
                System.out.println("Scelta non valida.");
        }
    }

    // Permette di visualizzare e valutare le proposte di vendita dei clienti
    private static void valutaProposta() {
        List<String> proposte = gestore.visualizzaProposteVenditaBCED();
        if (proposte.isEmpty() || (proposte.size() == 1 && proposte.get(0).toLowerCase().contains("nessuna"))) {
            System.out.println("Nessuna proposta in attesa.");
            return;
        }
        System.out.println("Proposte di vendita pendenti:");
        for (String p : proposte) {
            System.out.println(p);
        }
        System.out.println("NOTA: Le proposte accettate, rifiutate o annullate vengono rimosse dalla tabella e non saranno più visibili qui.");
        System.out.print("Inserisci nome venditore della proposta da valutare: ");
        String nomeVenditore = scan.nextLine();
        System.out.print("Inserisci nome prodotto della proposta da valutare: ");
        String nomeProdotto = scan.nextLine();
        System.out.print("Azione (accetta/rifiuta/controproponi): ");
        String azione = scan.nextLine().trim().toLowerCase();
        Float nuovoPrezzo = null;
        if (azione.equals("controproponi")) {
            System.out.print("Prezzo controproposto: ");
            String prezzoInput = scan.nextLine();
            try {
                nuovoPrezzo = Float.parseFloat(prezzoInput);
                if (nuovoPrezzo <= 0) {
                    System.out.println("Il prezzo deve essere maggiore di zero. Operazione annullata.");
                    return;
                }
            } catch (NumberFormatException e) {
                System.out.println("Prezzo non valido. Operazione annullata.");
                return;
            }
        } else if (!azione.equals("accetta") && !azione.equals("rifiuta")) {
            System.out.println("Azione non valida. Operazione annullata.");
            return;
        }
        String risultato = gestore.valutaPropostaVenditaBCED(nomeVenditore, nomeProdotto, azione, nuovoPrezzo);
        System.out.println(risultato);
    }

    // Avvia il terminale per il gestore (richiama il main)
    public static void startTerminal() {
        System.out.println("Benvenuto Gestore! (operazioni da terminale)");
        main(new String[]{});
    }
}
