package entity;

// Classe che rappresenta una proposta di vendita fatta da un cliente
public class EntityPropostaVendita {
    // Cliente che propone la vendita
    public EntityClienteRegistrato venditore;
    // Nome del prodotto proposto
    public String nome;
    // Descrizione del prodotto
    public String descrizione;
    // Tipo di prodotto (es: dipinto, scultura)
    public String tipo;
    // Prezzo proposto dal venditore
    public float prezzoProposto;
    // Prezzo eventualmente controproposto dal gestore
    public float prezzoGestore;
    // Quantità proposta
    public int quantita;
    // Immagini del prodotto
    public String immagine1, immagine2, immagine3, immagine4;
    // ID prodotto (se già presente a catalogo)
    public int idProdotto;
    // Prezzo effettivo (per compatibilità con EntityProdotto)
    public float prezzo;

    // Stato della proposta (in attesa, controproposta, accettata, rifiutata, annullata)
    public Stato stato;
    public enum Stato { IN_ATTESA, CONTROPROPOSTA, ACCETTATA, RIFIUTATA, ANNULLATA }
    // Campi specifici per dipinto
    public String tecnica; // solo per dipinto
    public String dimensioni; // solo per dipinto
    // Campi specifici per scultura
    public float peso; // solo per scultura
    public float altezza; // solo per scultura

    // Costruttore completo
    public EntityPropostaVendita(EntityClienteRegistrato v, String n, String d, String t, float p, int q, String i1, String i2, String i3, String i4, String tecnica, String dimensioni, float peso, float altezza) {
        venditore = v; nome = n; descrizione = d; tipo = t; prezzoProposto = p; prezzoGestore = p; quantita = q;
        immagine1 = i1; immagine2 = i2; immagine3 = i3; immagine4 = i4; stato = Stato.IN_ATTESA;
        this.tecnica = tecnica;
        this.dimensioni = dimensioni;
        this.peso = peso;
        this.altezza = altezza;
        prezzo = p;
        idProdotto = -1;
    }
    // Getter e Setter per compatibilità con EntityProdotto
    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int id) { this.idProdotto = id; }
    public String getNome() { return nome; }
    public void setNome(String n) { this.nome = n; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String d) { this.descrizione = d; }
    public float getPrezzo() { return prezzo; }
    public void setPrezzo(float p) { this.prezzo = p; }
    public String getImmagine1() { return immagine1; }
    public void setImmagine1(String i) { this.immagine1 = i; }
    public String getImmagine2() { return immagine2; }
    public void setImmagine2(String i) { this.immagine2 = i; }
    public String getImmagine3() { return immagine3; }
    public void setImmagine3(String i) { this.immagine3 = i; }
    public String getImmagine4() { return immagine4; }
    public void setImmagine4(String i) { this.immagine4 = i; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int q) { this.quantita = q; }

    public Stato getStato() { return stato; }
    public void setStato(Stato s) { this.stato = s; }

    // Getter e Setter per i nuovi campi specifici
    public String getTecnica() { return tecnica; }
    public void setTecnica(String t) { this.tecnica = t; }
    public String getDimensioni() { return dimensioni; }
    public void setDimensioni(String d) { this.dimensioni = d; }
    public float getPeso() { return peso; }
    public void setPeso(float p) { this.peso = p; }
    public float getAltezza() { return altezza; }
    public void setAltezza(float a) { this.altezza = a; }
}