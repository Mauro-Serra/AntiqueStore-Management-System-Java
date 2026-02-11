package entity;

import java.util.HashMap;
import java.util.Map;

public class EntityCarrello {
    // Istanza privata statica
    private static EntityCarrello instance;

    private int idCarrello;
    // Mappa prodotto -> quantità nel carrello
    private Map<EntityProdotto, Integer> prodotti;
    private String nomeUtente;

    // Costruttore privato
    private EntityCarrello() {
        this.prodotti = new HashMap<>();
    }

    // Metodo pubblico per ottenere l'istanza
    public static synchronized EntityCarrello getInstance() {
        if (instance == null) {
            instance = new EntityCarrello();
        }
        return instance;
    }

    // Restituisce la quantità totale di prodotti nel carrello (somma di tutte le quantità)
    public int getQuantita() {
        // La quantità complessiva è la somma delle quantità di tutti i prodotti
        return prodotti.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Restituisce la mappa prodotto -> quantità
    public Map<EntityProdotto, Integer> getProdotti() {
        return prodotti;
    }

    // Aggiunge una quantità di un prodotto al carrello
    public void aggiungiProdotto(EntityProdotto prodotto, int quantita) {
        // Cerca se esiste già un prodotto con lo stesso idProdotto
        EntityProdotto prodottoEsistente = null;
        for (EntityProdotto p : prodotti.keySet()) {
            if (p.getIdProdotto() == prodotto.getIdProdotto()) {
                prodottoEsistente = p;
                break;
            }
        }
        if (prodottoEsistente != null) {
            prodotti.put(prodottoEsistente, prodotti.get(prodottoEsistente) + quantita);
        } else {
            prodotti.put(prodotto, quantita);
        }
    }

    // Rimuove una quantità di un prodotto dal carrello
    public void rimuoviProdotto(EntityProdotto prodotto, int quantita) {
        // Cerca se esiste già un prodotto con lo stesso idProdotto
        EntityProdotto prodottoEsistente = null;
        for (EntityProdotto p : prodotti.keySet()) {
            if (p.getIdProdotto() == prodotto.getIdProdotto()) {
                prodottoEsistente = p;
                break;
            }
        }
        if (prodottoEsistente != null) {
            int attuale = prodotti.get(prodottoEsistente);
            if (attuale > quantita) {
                prodotti.put(prodottoEsistente, attuale - quantita);
            } else {
                prodotti.remove(prodottoEsistente);
            }
        }
    }

    // Rimuove completamente un prodotto dal carrello
    public void rimuoviProdottoCompletamente(EntityProdotto prodotto) {
        prodotti.remove(prodotto);
    }

    public void setQuantita(int quantita) {
        // Non è possibile impostare una quantità totale, ma solo aggiungere o rimuovere prodotti
        throw new UnsupportedOperationException("Non è possibile impostare una quantità totale. Usa aggiungiProdotto o rimuoviProdotto.");
    }

    public float getPrezzoComplessivo() {
        float totale = 0f;
        for (Map.Entry<EntityProdotto, Integer> entry : prodotti.entrySet()) {
            // Usa la quantità della mappa prodotti (cioè la quantità nel carrello)
            totale += entry.getKey().getPrezzo() * entry.getValue();
        }
        return totale;
    }

    // Getter e setter per nomeUtente
    public String getNomeUtente() {
        return nomeUtente;
    }

    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }

    public int getIdCarrello() {
        return idCarrello;
    }

    public void setIdCarrello(int idCarrello) {
        this.idCarrello = idCarrello;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Carrello di ").append(nomeUtente).append(" (ID: ").append(idCarrello).append(")\n");
        if (prodotti.isEmpty()) {
            sb.append("Il carrello è vuoto.");
        } else {
            for (Map.Entry<EntityProdotto, Integer> entry : prodotti.entrySet()) {
                sb.append(entry.getKey().getNome()).append(" - Quantità: ").append(entry.getValue()).append("\n");
            }
            sb.append("Prezzo complessivo: ").append(getPrezzoComplessivo()).append("€");
        }
        return sb.toString();
    }

    public String toString_Prodotti() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<EntityProdotto, Integer> entry : prodotti.entrySet()) {
            sb.append(entry.getKey().getNome()).append(" - Quantità: ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}