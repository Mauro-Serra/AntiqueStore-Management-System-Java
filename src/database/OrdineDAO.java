package database;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import entity.EntityOrdine;
import entity.EntityCarrello;
import exception.DAOException;
import exception.DBConnectionException;

// Classe DAO per la gestione degli ordini nel database
public class OrdineDAO {
    // CREATE: inserisce un nuovo ordine nel database
    public static void createOrdine(EntityOrdine ordine) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "INSERT INTO Ordine (dataOrdine, nomeutente, idCarrello, PrezzoComplessivo) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDate(1, Date.valueOf(ordine.getDataOrdine()));
                stmt.setString(2, ordine.getCarrello().getNomeUtente());
                stmt.setInt(3, ordine.getCarrello().getIdCarrello());
                stmt.setFloat(4, ordine.getPrezzoComplessivo());
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    ordine.setIdOrdine(rs.getInt(1));
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore inserimento ordine: " + e.getMessage());
        }
    }

    // READ: recupera un ordine tramite id
    public static EntityOrdine getOrdineById(int idOrdine) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM Ordine WHERE idOrdine = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idOrdine);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    LocalDate data = rs.getDate("dataOrdine").toLocalDate();
                    int idCarrello = rs.getInt("idCarrello");
                    CarrelloDAO carrelloDAO = new CarrelloDAO();
                    EntityCarrello carrello = carrelloDAO.getCarrelloById(idCarrello);
                    float prezzoComplessivo = rs.getFloat("PrezzoComplessivo");
                    return new EntityOrdine(idOrdine, data, carrello, prezzoComplessivo);
                } else {
                    return null;
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura ordine: " + e.getMessage());
        }
    }

    // UPDATE: aggiorna i dati di un ordine esistente
    public static void updateOrdine(EntityOrdine ordine) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "UPDATE Ordine SET dataOrdine=?, idCarrello=? WHERE idOrdine=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setDate(1, Date.valueOf(ordine.getDataOrdine()));
                stmt.setInt(2, ordine.getCarrello().getIdCarrello());
                stmt.setInt(3, ordine.getIdOrdine());
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento ordine: " + e.getMessage());
        }
    }

    // DELETE: elimina un ordine dal database
    public static void deleteOrdine(int idOrdine) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "DELETE FROM Ordine WHERE idOrdine=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, idOrdine);
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore eliminazione ordine: " + e.getMessage());
        }
    }

    // LIST ALL: restituisce la lista di tutti gli ordini
    public static List<EntityOrdine> getAllOrdini() throws DAOException, DBConnectionException {
        List<EntityOrdine> ordini = new ArrayList<>();
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM Ordine";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idOrdine = rs.getInt("idOrdine");
                    LocalDate data = rs.getDate("dataOrdine").toLocalDate();
                    int idCarrello = rs.getInt("idCarrello");
                    CarrelloDAO carrelloDAO = new CarrelloDAO();
                    EntityCarrello carrello = carrelloDAO.getCarrelloById(idCarrello);
                    float prezzoComplessivo = rs.getFloat("PrezzoComplessivo");
                    ordini.add(new EntityOrdine(idOrdine, data, carrello, prezzoComplessivo));
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura ordini: " + e.getMessage());
        }
        return ordini;
    }
}
