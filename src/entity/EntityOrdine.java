package entity;

import java.time.LocalDate;

// Classe che rappresenta un ordine effettuato da un cliente
public class EntityOrdine {

    // Identificativo univoco dell'ordine
    private int idOrdine;
    // Data in cui è stato effettuato l'ordine
    private LocalDate dataOrdine;
    // Carrello associato all'ordine (prodotti acquistati)
    private EntityCarrello carrello;
    // Prezzo totale dell'ordine
    private float prezzoComplessivo;

    // Costruttore per inizializzare tutti i campi dell'ordine
    public EntityOrdine(int idOrdine, LocalDate dataOrdine, EntityCarrello carrello, float prezzoComplessivo) {
        this.idOrdine = idOrdine;
        this.dataOrdine = dataOrdine;
        this.carrello = carrello;
        this.prezzoComplessivo = prezzoComplessivo;
    }

    // Getter e Setter per tutti i campi
    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public EntityCarrello getCarrello() {
        return carrello;
    }

    public void setCarrello(EntityCarrello carrello) {
        this.carrello = carrello;
    }

    public float getPrezzoComplessivo() {
        return prezzoComplessivo;
    }

    public void setPrezzoComplessivo(float prezzoComplessivo) {
        this.prezzoComplessivo = prezzoComplessivo;
    }

    // Restituisce una rappresentazione testuale dell'ordine, utile per la visualizzazione
    @Override
    public String toString() {
        return "Ordine #" + idOrdine + " | Data: " + dataOrdine + "\nContenuto carrello:\n" + carrello.toString_Prodotti() + " \n Prezzo complessivo: " + prezzoComplessivo;
    }
}
