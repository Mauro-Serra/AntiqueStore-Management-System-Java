package entity;

// Classe che rappresenta un prodotto generico nel negozio
public class EntityProdotto {
    // Identificativo univoco del prodotto
    private int idProdotto;
    // Nome del prodotto
    private String nome;
    // Descrizione del prodotto
    private String descrizione;
    // Prezzo del prodotto
    private float prezzo;
    // URL o percorso delle immagini associate al prodotto
    private String immagine1;
    private String immagine2;
    private String immagine3;
    private String immagine4;
    // Quantità disponibile in magazzino
    protected int quantita;
    // Tipo di prodotto (es: dipinto, scultura, ecc.)
    private String tipo; 


    // Costruttore di default
    public EntityProdotto() {
        super();
    }

    // Costruttore base (senza specializzazione)
    public EntityProdotto(String nome, String descrizione, float prezzo,
                        String immagine1, String immagine2, String immagine3, String immagine4,
                        int quantita, String tipo) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo = prezzo;
        this.immagine1 = immagine1;
        this.immagine2 = immagine2;
        this.immagine3 = immagine3;
        this.immagine4 = immagine4;
        this.quantita = quantita;
        this.tipo = tipo;
    }

    // Getter e Setter per tutti i campi
    public int getIdProdotto() { return idProdotto; }
    public void setIdProdotto(int idProdotto) { this.idProdotto = idProdotto; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }
    public float getPrezzo() { return prezzo; }
    public void setPrezzo(float prezzo) { this.prezzo = prezzo; }
    public String getImmagine1() { return immagine1; }
    public void setImmagine1(String immagine1) { this.immagine1 = immagine1; }
    public String getImmagine2() { return immagine2; }
    public void setImmagine2(String immagine2) { this.immagine2 = immagine2; }
    public String getImmagine3() { return immagine3; }
    public void setImmagine3(String immagine3) { this.immagine3 = immagine3; }
    public String getImmagine4() { return immagine4; }
    public void setImmagine4(String immagine4) { this.immagine4 = immagine4; }
    public int getQuantita() { return quantita; }
    public void setQuantita(int quantita) { this.quantita = quantita; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    // Restituisce una rappresentazione testuale del prodotto, utile per la visualizzazione
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------\n");
        sb.append("ID:         ").append(idProdotto).append("\n");
        sb.append("Nome:       ").append(nome).append("\n");
        sb.append("Descrizione:").append(descrizione).append("\n");
        sb.append(String.format("Prezzo:     %.2f\n", prezzo));
        sb.append("Quantità:   ").append(quantita).append("\n");
        sb.append("Tipo:       ").append(tipo).append("\n");
        sb.append("Immagini:   ").append(String.join(", ", immagine1, immagine2, immagine3, immagine4)).append("\n");
        sb.append("-----------------------------");
        return sb.toString();
    }
}
