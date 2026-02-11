package entity;

// Classe che rappresenta un cliente registrato nel sistema
public class EntityClienteRegistrato {
    // Nome utente del cliente
    private String nomeUtente;
    // Password del cliente
    private String password;
    // Numero di telefono del cliente
    private String telefono;
    // Numero della carta di credito del cliente
    private String cartaDiCredito;

    // Costruttore vuoto (necessario per alcune operazioni di serializzazione/deserializzazione)
    public EntityClienteRegistrato() {
        super();
    }

    // Costruttore completo
    public EntityClienteRegistrato(String nomeUtente, String password, String telefono, String cartaDiCredito) {
        super();
        this.nomeUtente = nomeUtente;
        this.password = password;
        this.telefono = telefono;
        this.cartaDiCredito = cartaDiCredito;
    }
    // Costruttore con solo nome utente e password (per login o ricerche rapide)
    public EntityClienteRegistrato(String nomeUtente, String password) {
        this.nomeUtente = nomeUtente;
        this.password = password;
        this.telefono = null;
        this.cartaDiCredito = null;
    }

    // Getter e setter per tutti i campi
    public String getNomeUtente() {
        return nomeUtente;
    }
    public void setNomeUtente(String nomeUtente) {
        this.nomeUtente = nomeUtente;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getCartaDiCredito() {
        return cartaDiCredito;
    }
    public void setCartaDiCredito(String cartaDiCredito) {
        this.cartaDiCredito = cartaDiCredito;
    }
}
