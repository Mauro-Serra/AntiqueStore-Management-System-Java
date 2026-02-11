package entity;

// Classe che rappresenta un dipinto, specializzazione di EntityProdotto
public class EntityDipinto extends EntityProdotto {
    // Tecnica artistica utilizzata per il dipinto (es: olio, acquerello)
    private String tecnica;
    // Dimensioni della tela del dipinto (es: 50x70 cm)
    private String dimensionetela;

    // Costruttore di default
    public EntityDipinto() {
        super();
    }

    // Costruttore completo per inizializzare tutti i campi del dipinto
    public EntityDipinto(int idProdotto, String nome, String descrizione, float prezzo, String immagine1, String immagine2, String immagine3, String immagine4, int quantita,
                         String tecnica, String dimensionetela) {
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
        this.tecnica = tecnica;
        this.dimensionetela = dimensionetela;
    }

    // Getter e setter per i campi specifici del dipinto
    public String getTecnica() {
        return tecnica;
    }
    public void setTecnica(String tecnica) {
        this.tecnica = tecnica;
    }
    public String getDimensionetela() {
        return dimensionetela;
    }
    public void setDimensionetela(String dimensionetela) {
        this.dimensionetela = dimensionetela;
    }

    // Metodo di utilità per compatibilità con il toString esistente
    public String getImmagine1() {
        return super.getImmagine1();
    }
    public String getImmagine2() {
        return super.getImmagine2();
    }
    public String getImmagine3() {
        return super.getImmagine3();
    }
    public String getImmagine4() {
        return super.getImmagine4();
    }
    public int getQuantità() {
        return getQuantita();
    }

    // Rappresentazione testuale del dipinto, utile per la visualizzazione
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-----------------------------\n");
        sb.append("ID:         ").append(getIdProdotto()).append("\n");
        sb.append("Nome:       ").append(getNome()).append("\n");
        sb.append("Descrizione:").append(getDescrizione()).append("\n");
        sb.append(String.format("Prezzo:     %.2f\n", getPrezzo()));
        sb.append("Quantità:   ").append(getQuantita()).append("\n");
        sb.append("Tipo:       dipinto\n");
        sb.append("Tecnica:    ").append(tecnica).append("\n");
        sb.append("Dimensioni: ").append(dimensionetela).append("\n");
        sb.append("Immagini:   ").append(String.join(", ", getImmagine1(), getImmagine2(), getImmagine3(), getImmagine4())).append("\n");
        sb.append("-----------------------------");
        return sb.toString();
    }
}
