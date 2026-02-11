package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.EntityCarrello;
import entity.EntityProdotto;
import exception.DAOException;
import exception.DBConnectionException;

// Classe DAO per la gestione delle operazioni sul carrello nel database
public class CarrelloDAO {
    // Costante non utilizzata (può essere rimossa)
    public static final String OrdineDAO = null;

    // CREATE: crea un carrello solo se non esiste già per quell'utente
    public void createCarrello(EntityCarrello carrello) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "INSERT INTO Carrello (nomeutente, quantita) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, carrello.getNomeUtente());
                stmt.setInt(2, carrello.getQuantita());
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    carrello.setIdCarrello(rs.getInt(1));
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore inserimento carrello: " + e.getMessage());
        }
    }

    // READ: recupera carrello per nomeutente
    public EntityCarrello getCarrelloByNomeUtente(String nomeUtente) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM Carrello WHERE nomeutente = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeUtente);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    EntityCarrello c = EntityCarrello.getInstance();
                    c.setIdCarrello(rs.getInt("idCarrello"));
                    c.setNomeUtente(rs.getString("nomeutente"));
                    c.getProdotti().clear();
                    for (EntityProdotto p : getProdottiByCarrelloId(c.getIdCarrello())) {
                        c.getProdotti().put(p, p.getQuantita());
                    }
                    return c;
                } else {
                    return null;
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura carrello per nomeUtente: " + e.getMessage());
        }
    }

    // UPDATE: aggiorna la quantità totale del carrello
    public void aggiornaQuantitaTotaleCarrello(int idCarrello, int nuovaQuantita) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "UPDATE Carrello SET quantita=? WHERE idCarrello=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, nuovaQuantita);
                stmt.setInt(2, idCarrello);
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento quantita totale carrello: " + e.getMessage());
        }
    }

    // AGGIUNGI/AGGIORNA prodotto nel carrello_prodotto
    public void aggiungiProdottoACarrello(int idCarrello, int codice, int quantita) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String select = "SELECT quantita FROM Carrello_Prodotto WHERE idCarrello=? AND codice=?";
            try (PreparedStatement stmtSel = conn.prepareStatement(select)) {
                stmtSel.setInt(1, idCarrello);
                stmtSel.setInt(2, codice);
                ResultSet rs = stmtSel.executeQuery();
                if (rs.next()) {
                    int nuovaQuantita = rs.getInt("quantita") + quantita;
                    String update = "UPDATE Carrello_Prodotto SET quantita=? WHERE idCarrello=? AND codice=?";
                    try (PreparedStatement stmtUpd = conn.prepareStatement(update)) {
                        stmtUpd.setInt(1, nuovaQuantita);
                        stmtUpd.setInt(2, idCarrello);
                        stmtUpd.setInt(3, codice);
                        stmtUpd.executeUpdate();
                    }
                } else {
                    String insert = "INSERT INTO Carrello_Prodotto (idCarrello, codice, quantita) VALUES (?, ?, ?)";
                    try (PreparedStatement stmtIns = conn.prepareStatement(insert)) {
                        stmtIns.setInt(1, idCarrello);
                        stmtIns.setInt(2, codice);
                        stmtIns.setInt(3, quantita);
                        stmtIns.executeUpdate();
                    }
                }
            }
            // Aggiorna la quantità totale del carrello
            EntityCarrello carrello = getCarrelloById(idCarrello);
            aggiornaQuantitaTotaleCarrello(idCarrello, carrello.getQuantita());
            DBManager.closeConnection();
        } catch (SQLException e) {
            throw new DAOException("Errore aggiunta prodotto al carrello: " + e.getMessage());
        }
    }

    // RIMUOVI o aggiorna prodotto in carrello_prodotto
    public void rimuoviProdottoDaCarrello(int idCarrello, int codice, int quantita) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String select = "SELECT quantita FROM Carrello_Prodotto WHERE idCarrello=? AND codice=?";
            try (PreparedStatement stmtSel = conn.prepareStatement(select)) {
                stmtSel.setInt(1, idCarrello);
                stmtSel.setInt(2, codice);
                ResultSet rs = stmtSel.executeQuery();
                if (rs.next()) {
                    int attuale = rs.getInt("quantita");
                    int nuovaQuantita = attuale - quantita;
                    if (nuovaQuantita > 0) {
                        String update = "UPDATE Carrello_Prodotto SET quantita=? WHERE idCarrello=? AND codice=?";
                        try (PreparedStatement stmtUpd = conn.prepareStatement(update)) {
                            stmtUpd.setInt(1, nuovaQuantita);
                            stmtUpd.setInt(2, idCarrello);
                            stmtUpd.setInt(3, codice);
                            stmtUpd.executeUpdate();
                        }
                    } else {
                        String delete = "DELETE FROM Carrello_Prodotto WHERE idCarrello=? AND codice=?";
                        try (PreparedStatement stmtDel = conn.prepareStatement(delete)) {
                            stmtDel.setInt(1, idCarrello);
                            stmtDel.setInt(2, codice);
                            stmtDel.executeUpdate();
                        }
                    }
                }
            }
            // Aggiorna la quantità totale del carrello
            EntityCarrello carrello = getCarrelloById(idCarrello);
            aggiornaQuantitaTotaleCarrello(idCarrello, carrello.getQuantita());
            DBManager.closeConnection();
        } catch (SQLException e) {
            throw new DAOException("Errore rimozione prodotto dal carrello: " + e.getMessage());
        }
    }

    // READ: recupera carrello per id
    public EntityCarrello getCarrelloById(int id) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM Carrello WHERE idCarrello = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    EntityCarrello c = EntityCarrello.getInstance();
                    c.setIdCarrello(rs.getInt("idCarrello"));
                    c.setQuantita(rs.getInt("quantita"));
                    c.getProdotti().clear();
                    for (EntityProdotto p : getProdottiByCarrelloId(id)) {
                        c.getProdotti().put(p, p.getQuantita());
                    }
                    return c;
                } else {
                    return null;
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura carrello: " + e.getMessage());
        }
    }

    // DELETE: elimina un carrello dal database
    public void deleteCarrello(int id) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "DELETE FROM Carrello WHERE idCarrello=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore eliminazione carrello: " + e.getMessage());
        }
    }

    // Svuota completamente il carrello dal database (elimina prodotti e carrello)
    public static void svuotaCarrello(int idCarrello) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            // Prima elimina le associazioni prodotti-carrello (se esistono)
            String deleteProdotti = "DELETE FROM Carrello_Prodotto WHERE idCarrello=?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteProdotti)) {
                stmt.setInt(1, idCarrello);
                stmt.executeUpdate();
            }
            // Poi elimina il carrello
            String deleteCarrello = "DELETE FROM Carrello WHERE idCarrello=?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteCarrello)) {
                stmt.setInt(1, idCarrello);
                stmt.executeUpdate();
            }
            DBManager.closeConnection();
        } catch (SQLException e) {
            throw new DAOException("Errore svuotamento carrello: " + e.getMessage());
        }
    }

    // LIST ALL: restituisce la lista di tutti i carrelli
    public List<EntityCarrello> getAllCarrelli() throws DAOException, DBConnectionException {
        List<EntityCarrello> carrelli = new ArrayList<>();
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM Carrello";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    EntityCarrello c = EntityCarrello.getInstance();
                    c.setIdCarrello(rs.getInt("idCarrello"));
                    c.setQuantita(rs.getInt("quantita"));
                    c.getProdotti().clear();
                    for (EntityProdotto p : getProdottiByCarrelloId(c.getIdCarrello())) {
                        c.getProdotti().put(p, p.getQuantita());
                    }
                    carrelli.add(c);
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura carrelli: " + e.getMessage());
        }
        return carrelli;
    }

    // Restituisce la lista dei prodotti presenti in un carrello dato l'id
    public List<EntityProdotto> getProdottiByCarrelloId(int idCarrello) throws DAOException, DBConnectionException {
        List<EntityProdotto> prodotti = new ArrayList<>();
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT p.*, cp.quantita as quantitaNelCarrello FROM Prodotto p JOIN Carrello_Prodotto cp ON p.codice = cp.codice WHERE cp.idCarrello = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idCarrello);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    EntityProdotto p = new EntityProdotto();
                    p.setIdProdotto(rs.getInt("codice"));
                    p.setNome(rs.getString("nome"));
                    p.setDescrizione(rs.getString("descrizione"));
                    p.setPrezzo(rs.getFloat("prezzo"));
                    p.setImmagine1(rs.getString("immagine1"));
                    p.setImmagine2(rs.getString("immagine2"));
                    p.setImmagine3(rs.getString("immagine3"));
                    p.setImmagine4(rs.getString("immagine4"));
                    // Imposta la quantità del prodotto nel carrello
                    p.setQuantita(rs.getInt("quantitaNelCarrello"));
                    prodotti.add(p);
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura prodotti del carrello: " + e.getMessage());
        }
        return prodotti;
    }

    // Restituisce la quantità di un prodotto specifico nel carrello
    public int getQuantitaProdottoInCarrello(int idCarrello, int codiceProdotto) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT quantita FROM Carrello_Prodotto WHERE idCarrello = ? AND codice = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idCarrello);
                stmt.setInt(2, codiceProdotto);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("quantita");
                } else {
                    return 0; // Prodotto non trovato
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura quantità prodotto nel carrello: " + e.getMessage());
        }
    }
}
