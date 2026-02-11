package control;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import entity.EntityProdotto;
import entity.EntityCarrello;
import entity.EntityClienteRegistrato;
import entity.EntityOrdine;
import entity.EntityPropostaVendita;
import database.ProdottoDAO;
import database.ClienteRegistratoDAO;
import database.OrdineDAO;
import database.PropostaVenditaDAO;
import database.CarrelloDAO;
import exception.DAOException;
import exception.DBConnectionException;

// Classe che gestisce la logica di business del negozio (singleton)
public class GestioneNegozio {
    // --- BCED methods for BoundaryCliente ---
    // Registra un nuovo cliente nel sistema
    public String registraClienteBCED(String nomeUtente, String password, String telefono, String carta) {
        try {
            if (nomeUtente == null || password == null || telefono == null || carta == null)
                return "Dati mancanti per la registrazione";
            if (!telefono.matches("\\d+"))
                return "Telefono non valido.";
            if (carta.length() != 16 || !carta.matches("\\d+"))
                return "Carta non valida.";
            EntityClienteRegistrato cliente = new EntityClienteRegistrato(nomeUtente, password, telefono, carta);
            ClienteRegistratoDAO.createCliente(cliente);
            return "Registrazione completata con successo!";
        } catch (DAOException e) {
            return "Errore di database: " + e.getMessage();
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    // Restituisce un cliente registrato dato il nome utente
    public EntityClienteRegistrato getClienteByUsernameBCED(String nomeUtente) {
        try {
            return ClienteRegistratoDAO.getClienteByUsername(nomeUtente);
        } catch (Exception e) {
            return null;
        }
    }

    // Effettua il login di un cliente
    public String loginClienteBCED(String nomeUtente, String password) {
        try {
            EntityClienteRegistrato cliente = ClienteRegistratoDAO.getClienteByUsername(nomeUtente);
            if (cliente != null && cliente.getPassword().equals(password)) {
                return "Accesso effettuato con successo!";
            } else {
                return "Credenziali errate o utente non registrato.";
            }
        } catch (Exception e) {
            return "Errore durante l'accesso: " + e.getMessage();
        }
    }

    // Restituisce la lista dei prodotti disponibili come stringhe
    public List<String> visualizzaProdottiBCED() {
        List<String> out = new ArrayList<>();
        try {
            List<EntityProdotto> prodotti = ProdottoDAO.getAllProdotti();
            for (EntityProdotto p : prodotti) {
                out.add(p.toString());
            }
        } catch (Exception e) {
            out.add("Errore di database: " + e.getMessage());
        }
        return out;
    }

    // Restituisce una rappresentazione formattata del carrello
    public List<String> mostraCarrelloFormattatoBCED(EntityCarrello carrello) {
        List<String> out = new ArrayList<>();
        for (EntityProdotto prod : carrello.getProdotti().keySet()) {
            int q = carrello.getProdotti().get(prod);
            out.add(prod.toString());
            out.add("Quantità nel carrello: " + q);
            out.add("-----------------------------");
        }
        return out;
    }

    // Aggiunge un prodotto al carrello dell'utente
    public String aggiungiAlCarrelloBCED(EntityCarrello carrello, int idProdotto, int quantita) {
        try {
            // 1. Validazione input
            if (quantita <= 0) return "Quantità non valida.";

            // 2. Recupera prodotto dal DB
            EntityProdotto prodotto = ProdottoDAO.getProdottoById(idProdotto);
            if (prodotto == null) return "Prodotto non trovato.";

            // 3. Controlla disponibilità effettiva
            int disponibile = prodotto.getQuantita();
            if (disponibile < quantita) {
                return "Quantità richiesta non disponibile. Disponibili: " + disponibile;
            }

            // 4. Associa carrello all'utente se necessario
            if (carrello.getNomeUtente() == null || carrello.getNomeUtente().isBlank()) {
                throw new IllegalStateException("Il carrello deve avere un nomeUtente associato per essere creato nel DB.");
            }

            // 5. Recupera o crea carrello nel DB
            CarrelloDAO dao = new CarrelloDAO();
            EntityCarrello carrelloDB = dao.getCarrelloByNomeUtente(carrello.getNomeUtente());
            if (carrelloDB != null) {
                carrello.setIdCarrello(carrelloDB.getIdCarrello());
            } else {
                dao.createCarrello(carrello);
            }
            int idCarrelloDB = carrello.getIdCarrello();

            // // 6. Aggiorna carrello nel DB (aggiungi/aggiorna prodotto)
            dao.aggiungiProdottoACarrello(idCarrelloDB, prodotto.getIdProdotto(), quantita);

            // 7. Aggiorna carrello in memoria (unifica per idProdotto)
            carrello.aggiungiProdotto(prodotto, quantita);

            return "Prodotto aggiunto al carrello e quantità aggiornata nel database.";
        } catch (Exception e) {
            return "Errore durante l'aggiunta al carrello: " + e.getMessage();
        }
    }

    // Rimuove un prodotto dal carrello dell'utente
    public String rimuoviDalCarrelloBCED(EntityCarrello carrello, int idProdotto, int quantita) {
        try {
            EntityProdotto prodotto = ProdottoDAO.getProdottoById(idProdotto);
            if (prodotto == null) return "Prodotto non trovato.";
            if (quantita <= 0) return "Quantità non valida.";
            CarrelloDAO dao = new CarrelloDAO();
            dao.rimuoviProdottoDaCarrello(carrello.getIdCarrello(), prodotto.getIdProdotto(), quantita);
            carrello.rimuoviProdotto(prodotto, quantita);
            EntityProdotto prodottoDB = ProdottoDAO.getProdottoById(prodotto.getIdProdotto());
            if (prodottoDB != null) {
                int nuovaQuantita = prodottoDB.getQuantita() + quantita;
                ProdottoDAO.aggiornaQuantitaProdotto(prodotto.getIdProdotto(), nuovaQuantita);
            }
            return "Prodotto rimosso dal carrello e quantità aggiornata nel database.";
        } catch (Exception e) {
            return "Errore durante la rimozione: " + e.getMessage();
        }
    }

    // Gestisce l'acquisto dei prodotti presenti nel carrello
    public String acquistaProdottoBCED(EntityCarrello carrello, EntityClienteRegistrato cliente, float prezzoComplessivo) {
        try {
            if (cliente == null) {
                return "Devi registrarti prima di acquistare.";
            }
            if (carrello.getProdotti().isEmpty()) {
                return "Carrello vuoto. Aggiungi prodotti prima.";
            }

            CarrelloDAO dao = new CarrelloDAO();
            System.out.println("ID Carrello: " + carrello.getIdCarrello());
            List<EntityProdotto> prodottiCarrello = dao.getProdottiByCarrelloId(carrello.getIdCarrello());
            if (prodottiCarrello.isEmpty()) {
                System.out.println("ID Carrello: " + carrello.getIdCarrello());
                return "Carrello vuoto. Aggiungi prodotti prima.";
            }

            for (EntityProdotto prodotto : prodottiCarrello) {
                EntityProdotto prodotto_negozio = ProdottoDAO.getProdottoById(prodotto.getIdProdotto());
                int quantita_richiesta = dao.getQuantitaProdottoInCarrello(carrello.getIdCarrello(), prodotto.getIdProdotto());
                int quantita_disponibile = prodotto_negozio.getQuantita();
                
                if (quantita_disponibile <= 0) {
                    return "Prodotto " + prodotto.getNome() + " non disponibile.";
                }

                if(quantita_richiesta > quantita_disponibile) {
                    return "Errore di sincronizzazione: quantità negativa.";
                }
            }

            // Effettua pagamento
            boolean pagamentoSuccesso = effettuaPagamento(carrello, cliente, prezzoComplessivo);
            if (!pagamentoSuccesso) {
                return "Pagamento non riuscito. Riprova.";
            }

            // Aggiorna la quantità del prodotto nel DB
            for(EntityProdotto prodotto : prodottiCarrello)
            {
                EntityProdotto prodotto_negozio = ProdottoDAO.getProdottoById(prodotto.getIdProdotto());
                int quantita_richiesta = dao.getQuantitaProdottoInCarrello(carrello.getIdCarrello(), prodotto.getIdProdotto());
                int quantita_disponibile = prodotto_negozio.getQuantita();
                int nuovaQuantita = quantita_disponibile - quantita_richiesta;

                if (nuovaQuantita < 0) {
                    return "Errore di sincronizzazione: quantità negativa.";
                } else if (nuovaQuantita == 0) {
                    ProdottoDAO.deleteProdotto(prodotto_negozio.getIdProdotto());
                } 
                else {
                    ProdottoDAO.aggiornaQuantitaProdotto(prodotto.getIdProdotto(), nuovaQuantita);
                 }
            }
            
            // Crea ordine
            EntityOrdine ordine = new EntityOrdine(0, java.time.LocalDate.now(), carrello, prezzoComplessivo);
            OrdineDAO.createOrdine(ordine);

            // Invia notifica
            String messaggio = "Pagamento andato a buon fine. Dettagli ordine:\n" + ordine.toString() + "\n----------------------------------------------\n";
            inviaNotifica(cliente.getTelefono(), messaggio);

            return "Acquisto completato! Riceverai una conferma via SMS.";
    } catch (DAOException e) {
        return "Errore di database: " + e.getMessage();
    } catch (Exception e) {
        return "Errore durante l'acquisto: " + e.getMessage();
    }
}

    // Aggiunge un nuovo prodotto al catalogo
    public String aggiungiProdottoBCED(String nome, String descrizione, float prezzo, String immagine1, String immagine2, String immagine3, String immagine4, int quantita, String tipo, String tecnica, String dimensioni, float peso, float altezza) {
        try {
            if (nome == null || nome.isBlank() || descrizione == null || descrizione.isBlank())
                return "Nome e descrizione obbligatori.";
            if (prezzo <= 0)
                return "Prezzo non valido.";
            if (quantita < 0)
                return "Quantità non valida.";
            entity.EntityProdotto prodotto;
            if ("dipinto".equalsIgnoreCase(tipo)) {
                prodotto = new entity.EntityDipinto(0, nome, descrizione, prezzo, immagine1, immagine2, immagine3, immagine4, quantita, tecnica, dimensioni);
            } else if ("scultura".equalsIgnoreCase(tipo)) {
                prodotto = new entity.EntityScultura(0, nome, descrizione, prezzo, immagine1, immagine2, immagine3, immagine4, quantita, peso, altezza);
            } else {
                prodotto = new entity.EntityProdotto(nome, descrizione, prezzo, immagine1, immagine2, immagine3, immagine4, quantita, tipo);
            }
            ProdottoDAO.createProdotto(prodotto);
            return "Prodotto aggiunto con successo.";
        } catch (exception.DAOException e) {
            return "Errore di database: " + e.getMessage();
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    // Modifica le informazioni di un prodotto esistente
    public String modificaProdottoBCED(int id, String nome, String descrizione, String prezzoStr, String quantitaStr, String tipo, String tecnica, String dimensioni, String pesoStr, String altezzaStr) {
        try {
            if (id <= 0)
                return "ID prodotto non valido.";
            float prezzo;
            int quantita;
            float peso = 0;
            float altezza = 0;
            try {
                prezzo = Float.parseFloat(prezzoStr);
                quantita = Integer.parseInt(quantitaStr);
                if (pesoStr != null && !pesoStr.isBlank()) peso = Float.parseFloat(pesoStr);
                if (altezzaStr != null && !altezzaStr.isBlank()) altezza = Float.parseFloat(altezzaStr);
            } catch (NumberFormatException e) {
                return "Prezzo, quantità, peso o altezza non validi.";
            }
            if (prezzo <= 0)
                return "Prezzo non valido.";
            if (quantita < 0)
                return "Quantità non valida.";
            entity.EntityProdotto prodotto;
            if ("dipinto".equalsIgnoreCase(tipo)) {
                prodotto = new entity.EntityDipinto(id, nome, descrizione, prezzo, null, null, null, null, quantita, tecnica, dimensioni);
            } else if ("scultura".equalsIgnoreCase(tipo)) {
                prodotto = new entity.EntityScultura(id, nome, descrizione, prezzo, null, null, null, null, quantita, peso, altezza);
            } else {
                prodotto = new entity.EntityProdotto(nome, descrizione, prezzo, null, null, null, null, quantita, tipo);
                prodotto.setIdProdotto(id);
            }
            ProdottoDAO.updateProdotto(prodotto);
            return "Prodotto modificato con successo.";
        } catch (exception.DAOException e) {
            return "Errore di database: " + e.getMessage();
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    // Rimuove un prodotto dal catalogo
    public String rimuoviProdottoBCED(int idProdotto) {
        try {
            if (idProdotto <= 0)
                return "ID prodotto non valido.";
            ProdottoDAO.deleteProdotto(idProdotto);
            return "Prodotto rimosso con successo.";
        } catch (DAOException e) {
            return "Errore di database: " + e.getMessage();
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    // Genera un report sugli acquisti dei clienti
    public String generaReportAcquistiBCED(int minAcquisti, String filePath) {
        try {
            if (minAcquisti < 0)
                return "Numero minimo di acquisti non valido.";
            if (filePath == null || filePath.isBlank())
                return "Nome file non valido.";
            ClienteRegistratoDAO.generaReportAcquisti(minAcquisti, filePath);
            return "Report acquisti generato con successo in " + filePath;
        } catch (DAOException e) {
            return "Errore di database: " + e.getMessage();
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    // Genera un report sulle vendite dei clienti
    public String generaReportVenditeBCED(int minVendite, String filePath) {
        try {
            if (minVendite < 0)
                return "Numero minimo di vendite non valido.";
            if (filePath == null || filePath.isBlank())
                return "Nome file non valido.";
            ClienteRegistratoDAO.generaReportVendite(minVendite, filePath);
            return "Report vendite generato con successo in " + filePath;
        } catch (DAOException e) {
            return "Errore di database: " + e.getMessage();
        } catch (Exception e) {
            return "Errore: " + e.getMessage();
        }
    }

    // Permette a un cliente di proporre la vendita di un prodotto
    public String proponiVenditaProdottoBCED(EntityClienteRegistrato venditore, String nome, String descrizione, String tipo, float prezzo, int quantita, String immagine1, String immagine2, String immagine3, String immagine4, String tecnica, String dimensioni, float peso, float altezza) {
        EntityPropostaVendita proposta = new EntityPropostaVendita(venditore, nome, descrizione, tipo, prezzo, quantita, immagine1, immagine2, immagine3, immagine4, tecnica, dimensioni, peso, altezza);
        proposta.stato = EntityPropostaVendita.Stato.IN_ATTESA;
        try {
            PropostaVenditaDAO.insertProposta(proposta);
            String messaggio = "Nuova proposta di vendita ricevuta: " + nome + " da " + venditore.getNomeUtente() + ". Prezzo proposto: " + prezzo + " euro.\n Vai nella sezione valuta proposte.\n----------------------------------------------\n";
            try {
                inviaNotifica("gestore", messaggio);
            } catch (DAOException | DBConnectionException e) {
                System.out.println("Errore durante l'invio della notifica: " + e.getMessage());
            }
            return "Proposta di vendita inviata al gestore. Attendi la valutazione.";
        } catch (SQLException e) {
            return "Errore database proposta: " + e.getMessage();
        }
    }

    // Accetta una controproposta di vendita
    public String accettaContropropostaVenditaBCED(EntityClienteRegistrato venditore, String nomeProdotto) {
        try {
            java.util.List<entity.EntityPropostaVendita> proposte = PropostaVenditaDAO.getProposteInAttesa();
            for (entity.EntityPropostaVendita p : proposte) {
                if (p.venditore != null && p.venditore.getNomeUtente().equals(venditore.getNomeUtente()) && p.nome.equals(nomeProdotto) && p.stato == entity.EntityPropostaVendita.Stato.CONTROPROPOSTA) {
                    try {
                        EntityProdotto prodotto;
                        if (p.tipo != null && p.tipo.equalsIgnoreCase("dipinto")) {
                            prodotto = new entity.EntityDipinto(0, p.nome, p.descrizione, p.prezzoGestore, p.immagine1, p.immagine2, p.immagine3, p.immagine4, p.quantita, p.tecnica, p.dimensioni);
                        } else if (p.tipo != null && p.tipo.equalsIgnoreCase("scultura")) {
                            prodotto = new entity.EntityScultura(0, p.nome, p.descrizione, p.prezzoGestore, p.immagine1, p.immagine2, p.immagine3, p.immagine4, p.quantita, p.peso, p.altezza);
                        } else {
                            prodotto = new entity.EntityProdotto(p.nome, p.descrizione, p.prezzoGestore, p.immagine1, p.immagine2, p.immagine3, p.immagine4, p.quantita, p.tipo);
                        }
                        ProdottoDAO.createProdotto(prodotto);
                        // Imposta lo stato su ACCETTATA e aggiorna la proposta nel DB
                        p.stato = entity.EntityPropostaVendita.Stato.ACCETTATA;
                        PropostaVenditaDAO.updateProposta(p);
                        String messaggio = "Proposta di vendita accettata: " + "Nome prodotto" + p.nome + "\nDescrizione: " + p.descrizione + "\nPrezzo: " + p.prezzoGestore +".\n----------------------------------------------\n";
                        inviaNotifica("gestore", messaggio);
                        return "Proposta accettata.";
                    } catch (Exception e) {
                        return "Errore inserimento prodotto: " + e.getMessage();
                    }
                }
            }
        } catch (SQLException | DAOException | DBConnectionException e) {
            return "Errore database proposta: " + e.getMessage();
        }
        return "Nessuna controproposta trovata o già gestita.";
    }

    // Annulla una proposta di vendita
    public void annullaPropostaVenditaBCED(EntityClienteRegistrato venditore, String nomeProdotto) {
        try {
            java.util.List<entity.EntityPropostaVendita> proposte = PropostaVenditaDAO.getProposteInAttesa();
            for (entity.EntityPropostaVendita p : proposte) {
                if (p.venditore != null && p.venditore.getNomeUtente().equals(venditore.getNomeUtente()) && p.nome.equals(nomeProdotto) && (p.stato == entity.EntityPropostaVendita.Stato.CONTROPROPOSTA || p.stato == entity.EntityPropostaVendita.Stato.IN_ATTESA)) {
                    String messaggio = "Proposta di vendita annullata: " + p.nome + ".\nDescrizione: " + p.descrizione + ".\n----------------------------------------------\n";
                    inviaNotifica("gestore", messaggio);
                    PropostaVenditaDAO.deleteProposta(p.venditore.getNomeUtente(), p.getNome());
                    break;
                }
            }
        } catch (SQLException | DAOException | DBConnectionException e) {
            // Log error or handle as needed
        }
    }

    // Valuta una proposta di vendita (accetta, rifiuta, controproponi)
    public String valutaPropostaVenditaBCED(String nomeVenditore, String nomeProdotto, String azione, Float nuovoPrezzo) {
        String messaggio;
        try {
            java.util.List<entity.EntityPropostaVendita> proposte = PropostaVenditaDAO.getProposteInAttesa();
            for (entity.EntityPropostaVendita p : proposte) {
                if (p.venditore == null || p.venditore.getNomeUtente() == null) {
                    return "Proposta non trovata o già gestita.";
                }
                if (p.venditore.getNomeUtente().equals(nomeVenditore) && p.nome.equals(nomeProdotto) && (p.stato == entity.EntityPropostaVendita.Stato.IN_ATTESA || p.stato == entity.EntityPropostaVendita.Stato.CONTROPROPOSTA)) {
                    switch (azione.toLowerCase()) {
                        case "accetta":
                            try {
                                EntityProdotto prodotto;
                                if (p.tipo != null && p.tipo.equalsIgnoreCase("dipinto")) {
                                    prodotto = new entity.EntityDipinto(0, p.nome, p.descrizione, p.prezzoGestore, p.immagine1, p.immagine2, p.immagine3, p.immagine4, p.quantita, p.tecnica, p.dimensioni);
                                } else if (p.tipo != null && p.tipo.equalsIgnoreCase("scultura")) {
                                    prodotto = new entity.EntityScultura(0, p.nome, p.descrizione, p.prezzoGestore, p.immagine1, p.immagine2, p.immagine3, p.immagine4, p.quantita, p.peso, p.altezza);
                                } else {
                                    prodotto = new entity.EntityProdotto(p.nome, p.descrizione, p.prezzoGestore, p.immagine1, p.immagine2, p.immagine3, p.immagine4, p.quantita, p.tipo);
                                }
                                ProdottoDAO.createProdotto(prodotto);
                                p.stato = entity.EntityPropostaVendita.Stato.ACCETTATA;
                                messaggio = "Proposta accettata: " + p.nome + ".\nDescrizione: " + p.descrizione + ".\nPrezzo: " + p.prezzoGestore + ".\n----------------------------------------------\n";
                                PropostaVenditaDAO.updateProposta(p);
                                inviaNotifica(p.venditore.getTelefono(), messaggio);
                                return "Proposta accettata e prodotto aggiunto al catalogo.";
                            } catch (Exception e) {
                                return "Errore inserimento prodotto: " + e.getMessage();
                            }
                        case "rifiuta":
                            messaggio = "Proposta di vendita rifiutata: " + p.nome + ".\nDescrizione: " + p.descrizione + ".\n----------------------------------------------\n";
                            PropostaVenditaDAO.deleteProposta(p.venditore.getNomeUtente(), p.getNome());
                            inviaNotifica(p.venditore.getTelefono(), messaggio);
                            return "Proposta rifiutata.";
                        case "controproponi":
                            if (nuovoPrezzo != null && nuovoPrezzo > 0) {
                                p.prezzoGestore = nuovoPrezzo;
                                p.stato = entity.EntityPropostaVendita.Stato.CONTROPROPOSTA;
                                PropostaVenditaDAO.updateProposta(p);
                                messaggio = "Controproposta per: "+ p.nome + "\nDescrizione: "+ p.descrizione + "\nNuovo prezzo: " + nuovoPrezzo + " euro.\n";
                                inviaNotifica(p.venditore.getTelefono(), messaggio);
                                return "Controproposta inviata al cliente: " + nuovoPrezzo + " euro";
                            } else {
                                return "Prezzo controproposto non valido.";
                            }
                        default:
                            return "Azione non valida. Usa: accetta, rifiuta, controproponi.";
                    }
                }
            }
        } catch (SQLException | DAOException | DBConnectionException e) {
            return "Errore database proposta: " + e.getMessage();
        }
        return "Proposta non trovata o già gestita.";
    }

    // --- Visualizza storico proposte di vendita per cliente ---
    // Visualizza lo storico delle controproposte per un cliente
    public java.util.List<String> visualizzacontropoposteBCED(EntityClienteRegistrato cliente) {
        java.util.List<String> out = new java.util.ArrayList<>();
        if (cliente == null) {
            out.add("Devi essere loggato per vedere le controproposte.");
            return out;
        }
        try {
            java.util.List<entity.EntityPropostaVendita> storico = PropostaVenditaDAO.getProposteByVenditore(cliente.getNomeUtente());
            if (storico.isEmpty()) {
                out.add("Nessuna controproposta trovata.");
            } else {
                out.add("--- Controproposte di vendita in attesa ---");
                for (entity.EntityPropostaVendita p : storico) {
                    if (p.stato == entity.EntityPropostaVendita.Stato.CONTROPROPOSTA) {
                        out.add(
                            "Prodotto: " + p.getNome() +
                            " | Descrizione: " + p.getDescrizione() +
                            " | Tipo: " + p.tipo +
                            " | Prezzo proposto: " + p.prezzoProposto +
                            " | Prezzo gestore: " + p.prezzoGestore +
                            " | Quantità: " + p.getQuantita() +
                            " | Stato: " + p.stato
                        );
                    }
                }
            }
        } catch (Exception e) {
            out.add("Errore database proposta: " + e.getMessage());
        }
        return out;
    }

    // --- Visualizza proposte di vendita pendenti per il gestore ---
    // Visualizza tutte le proposte di vendita pendenti per il gestore
    public java.util.List<String> visualizzaProposteVenditaBCED() {
        java.util.List<String> out = new java.util.ArrayList<>();
        try {
            java.util.List<entity.EntityPropostaVendita> proposte = PropostaVenditaDAO.getProposteInAttesa();
            if (proposte.isEmpty()) {
                out.add("Nessuna proposta di vendita in attesa.");
            } else {
                for (entity.EntityPropostaVendita p : proposte) {
                    out.add(
                        "Venditore: " + (p.venditore != null ? p.venditore.getNomeUtente() : "?") +
                        " | Prodotto: " + p.getNome() +
                        " | Tipo: " + p.tipo +
                        " | Prezzo proposto: " + p.prezzoProposto +
                        " | Prezzo gestore: " + p.prezzoGestore +
                        " | Quantità: " + p.getQuantita() +
                        " | Stato: " + p.stato +
                        (p.tipo != null && p.tipo.equalsIgnoreCase("dipinto") ? " | Tecnica: " + p.tecnica + " | Dimensioni: " + p.dimensioni : "") +
                        (p.tipo != null && p.tipo.equalsIgnoreCase("scultura") ? " | Peso: " + p.peso + " | Altezza: " + p.altezza : "")
                    );
                }
            }
        } catch (Exception e) {
            out.add("Errore database proposta: " + e.getMessage());
        }
        return out;
    }
    
    // Simula l'effettuazione di un pagamento (sempre positivo in questa versione)
    public boolean effettuaPagamento(EntityCarrello carrello, EntityClienteRegistrato cliente, float prezzoComplessivo) throws DAOException, DBConnectionException {
        System.out.println("Elaborazione pagamento di €" + String.format("%.2f", prezzoComplessivo) + " per il cliente " + (cliente != null ? cliente.getNomeUtente() : "sconosciuto") + "...");    
        return true;

    }  

    // Invia una notifica a un cliente o al gestore
    public void inviaNotifica(String telefono, String messaggio) throws DAOException, DBConnectionException {
        var cliente = ClienteRegistratoDAO.getClienteByTelefono(telefono);
        String nomeUtente;
        if (cliente == null && !telefono.equals("gestore")) {
            System.out.println("Cliente non trovato: " + cliente);
            return;
        } else if (telefono.equals("gestore")) {
            nomeUtente = "Gestore Negozio";
        } else {
            nomeUtente = cliente.getNomeUtente();
        }

        File cartellaNotifiche = new File("notifiche");
        if (!cartellaNotifiche.exists()) {
            cartellaNotifiche.mkdirs();      
        }

        File fileNotifica = new File(cartellaNotifiche, "notifica_" + nomeUtente + ".txt");
        String dataOra = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String riga = "[" + dataOra + "---Telefono: " + telefono +"]  " + messaggio;

        try {
            FileWriter writer = new FileWriter(fileNotifica, true); // true = append
            writer.write(riga + System.lineSeparator());
            writer.close();
            System.out.println("Notifica Inviata");
        } catch (IOException e) {
            System.out.println("Errore durante la scrittura: " + e.getMessage());
        }     
        
        System.out.println("Messaggio: " + messaggio);

    }

    // Restituisce la lista dei prodotti presenti nel carrello
    public List<EntityProdotto> controllaCarrello(EntityCarrello carrello) {
        return List.copyOf(carrello.getProdotti().keySet());
    }

    // Restituisce una mappa prodotto-quantità per il carrello
    public Map<EntityProdotto, Integer> prodottiCarrello(EntityCarrello carrello) {
        Map<EntityProdotto, Integer> prodotti_carrello = new HashMap<>();
        CarrelloDAO dao = new CarrelloDAO();
        try {
            List<EntityProdotto> prodottiCarrello = dao.getProdottiByCarrelloId(carrello.getIdCarrello());
        
            for (EntityProdotto prod : prodottiCarrello) {
                int quantita = dao.getQuantitaProdottoInCarrello(carrello.getIdCarrello(), prod.getIdProdotto());
                prodotti_carrello.put(prod, quantita);
            }
        } catch (DAOException | DBConnectionException e) {
            System.out.println("Errore durante la visualizzazione del carrello: " + e.getMessage());
        }

        return prodotti_carrello;
    }

    // Login cliente statico (usato per autenticazione diretta)
    public static EntityClienteRegistrato loginCliente(String username, String password) throws DAOException, DBConnectionException {
        EntityClienteRegistrato cliente = ClienteRegistratoDAO.getClienteByUsername(username);
        if (cliente != null && cliente.getPassword().equals(password)) {
            return cliente;
        }
        return null;
    }

    // Singleton: istanza unica della classe GestioneNegozio
    private static GestioneNegozio instance;
    private GestioneNegozio() {}
    public static synchronized GestioneNegozio getInstance() {
        if (instance == null) instance = new GestioneNegozio();
        return instance;
    }

    // Svuota completamente il carrello dell'utente
    public String svuotaCarrelloBCED(EntityCarrello carrello) {
        try {
            if (carrello.getIdCarrello() > 0) {
                CarrelloDAO.svuotaCarrello(carrello.getIdCarrello());
            }
            carrello.getProdotti().clear();
            carrello.setIdCarrello(0);
            return "Carrello svuotato completamente.";
        } catch (Exception e) {
            return "Errore durante lo svuotamento del carrello: " + e.getMessage();
        }
    }

    // Restituisce la quantità di un prodotto nel carrello
    public int getQuantitaProdottoInCarrelloBCED(int id_carrello, int idProdotto) {
        CarrelloDAO dao = new CarrelloDAO();
        try {
            return dao.getQuantitaProdottoInCarrello(id_carrello, idProdotto);
        } catch (DAOException | DBConnectionException e) {
            e.printStackTrace();
            return 0; // In caso di errore, restituisce 0
        }
    }

    // Calcola il prezzo totale dei prodotti nel carrello
    public float getPrezzoComplessivoBCED(int id_carrello, Collection<EntityProdotto> prodotti) {
        float prezzoTotale = 0f;
        for (EntityProdotto prod : prodotti) {
            int quantita = getQuantitaProdottoInCarrelloBCED(id_carrello, prod.getIdProdotto());
            prezzoTotale += prod.getPrezzo() * quantita;
        }
        return prezzoTotale;
    }

    // Restituisce il carrello associato a un nome utente
    public EntityCarrello getCarrelloByNomeUtenteBCED(String nomeUtente) {
        CarrelloDAO dao = new CarrelloDAO();
        try {
            EntityCarrello carrello = dao.getCarrelloByNomeUtente(nomeUtente);
            if (carrello != null) {
                carrello.setIdCarrello(carrello.getIdCarrello());
            } else {
                EntityCarrello carrello_new =  EntityCarrello.getInstance();
                carrello_new.setNomeUtente(nomeUtente); // Crea un nuovo carrello se non esiste
                dao.createCarrello(carrello_new);
            }
            return dao.getCarrelloByNomeUtente(nomeUtente);
        } catch (DAOException | DBConnectionException e) {
            e.printStackTrace();
            return null; // In caso di errore, restituisce null
        }
    }
}
