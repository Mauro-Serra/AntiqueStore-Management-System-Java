package entity;

// Classe che rappresenta una scultura, specializzazione di EntityProdotto
public class EntityScultura extends EntityProdotto {
    // Peso della scultura in kg
    private float peso;
    // Altezza della scultura in cm
    private float altezza;

    // Costruttore di default
    public EntityScultura() {
        super();
    }

    // Costruttore completo per inizializzare tutti i campi della scultura
    public EntityScultura(int idProdotto, String nome, String descrizione, float prezzo, String immagine1, String immagine2, String immagine3, String immagine4, int quantita,
                          float peso, float altezza) {
        super();
        setIdProdotto(idProdotto);
        setNome(nome);
        setDescrizione(descrizione);
        setPrezzo(prezzo);
        setImmagine1(immagine1);
        setImmagine2(immagine2);
        setImmagine3(immagine3);
        setImmagine4(immagine4);
        setQuantita(quantita);
        this.peso = peso;
        this.altezza = altezza;
    }

    // Getter e setter per i campi specifici della scultura
    public float getPeso() {
        return peso;
    }
    public void setPeso(float peso) {
        this.peso = peso;
    }
    public float getAltezza() {
        return altezza;
    }
    public void setAltezza(float altezza) {
        this.altezza = altezza;
    }

    // Override dei getter delle immagini per mantenere compatibilità
    public String getImmagine1() { return super.getImmagine1(); }
    public String getImmagine2() { return super.getImmagine2(); }
    public String getImmagine3() { return super.getImmagine3(); }
    public String getImmagine4() { return super.getImmagine4(); }

    // Getter alternativo per la quantità (compatibilità)
    public int getQuantità() {
        return getQuantita();
    }

    // Rappresentazione testuale della scultura, utile per la visualizzazione
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------\n");
        sb.append("ID:         ").append(getIdProdotto()).append("\n");
        sb.append("Nome:       ").append(getNome()).append("\n");
        sb.append("Descrizione:").append(getDescrizione()).append("\n");
        sb.append(String.format("Prezzo:     %.2f\n", getPrezzo()));
        sb.append("Quantità:   ").append(getQuantita()).append("\n");
        sb.append("Tipo:       scultura\n");
        sb.append("Peso:       ").append(peso).append(" kg\n");
        sb.append("Altezza:    ").append(altezza).append(" cm\n");
        sb.append("Immagini:   ").append(String.join(", ", getImmagine1(), getImmagine2(), getImmagine3(), getImmagine4())).append("\n");
        sb.append("-----------------------------");
        return sb.toString();
    }
}
