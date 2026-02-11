package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.EntityClienteRegistrato;
import exception.DAOException;
import exception.DBConnectionException;
import java.io.FileWriter;

// Classe DAO per la gestione dei clienti registrati nel database
public class ClienteRegistratoDAO {
    // CREATE: inserisce un nuovo cliente nel database
    public static void createCliente(EntityClienteRegistrato cliente) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "INSERT INTO ClienteRegistrato (nomeUtente, password, telefono, cartaDiCredito) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, cliente.getNomeUtente());
                stmt.setString(2, cliente.getPassword());
                stmt.setString(3, cliente.getTelefono());
                stmt.setString(4, cliente.getCartaDiCredito());
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore inserimento cliente: " + e.getMessage());
        }
    }

    // READ: recupera un cliente tramite nome utente
    public static EntityClienteRegistrato getClienteByNomeUtente(String nomeUtente) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM ClienteRegistrato WHERE nomeUtente = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeUtente);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new EntityClienteRegistrato(
                        rs.getString("nomeUtente"),
                        rs.getString("password"),
                        rs.getString("telefono"),
                        rs.getString("cartaDiCredito")
                    );
                } else {
                    return null;
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura cliente: " + e.getMessage());
        }
    }

    // Recupera un cliente tramite numero di telefono
    public static EntityClienteRegistrato getClienteByTelefono(String Telefono) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM ClienteRegistrato WHERE Telefono = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Telefono);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new EntityClienteRegistrato(
                        rs.getString("nomeUtente"),
                        rs.getString("password"),
                        rs.getString("telefono"),
                        rs.getString("cartaDiCredito")
                    );
                } else {
                    return null;
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura cliente: " + e.getMessage());
        }
    }

    // Permette di ottenere un cliente tramite username (alias di getClienteByNomeUtente)
    public static EntityClienteRegistrato getClienteByUsername(String username) throws DAOException, DBConnectionException {
        return getClienteByNomeUtente(username);
    }

    // UPDATE: aggiorna i dati di un cliente esistente
    public static void updateCliente(EntityClienteRegistrato cliente) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "UPDATE ClienteRegistrato SET password=?, telefono=?, cartaDiCredito=? WHERE nomeUtente=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, cliente.getPassword());
                stmt.setString(2, cliente.getTelefono());
                stmt.setString(3, cliente.getCartaDiCredito());
                stmt.setString(4, cliente.getNomeUtente());
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento cliente: " + e.getMessage());
        }
    }

    // DELETE: elimina un cliente dal database
    public static void deleteCliente(String nomeUtente) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "DELETE FROM ClienteRegistrato WHERE nomeUtente=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, nomeUtente);
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore eliminazione cliente: " + e.getMessage());
        }
    }

    // LIST ALL: restituisce la lista di tutti i clienti registrati
    public static List<EntityClienteRegistrato> getAllClienti() throws DAOException, DBConnectionException {
        List<EntityClienteRegistrato> clienti = new ArrayList<>();
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM ClienteRegistrato";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    EntityClienteRegistrato c = new EntityClienteRegistrato(
                        rs.getString("nomeUtente"),
                        rs.getString("password"),
                        rs.getString("telefono"),
                        rs.getString("cartaDiCredito")
                    );
                    clienti.add(c);
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura clienti: " + e.getMessage());
        }
        return clienti;
    }

    // Genera un report dei clienti che hanno effettuato almeno N acquisti e salva su file CSV
    public static void generaReportAcquisti(int minAcquisti, String filePath) throws DAOException, DBConnectionException {
        String query = "select nomeutente, count(*) as num_ordini, SUM(PrezzoComplessivo) as importo_totale from Ordine  group by nomeutente  having count(*) >= ? order by num_ordini desc";
        try (Connection conn = database.DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, minAcquisti);
            ResultSet rs = stmt.executeQuery();
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("nomeUtente,numeroAcquisti,importoTotale\n");
                while (rs.next()) {
                    writer.write(rs.getString("nomeutente") + "," +
                                 rs.getInt("num_ordini") + "," +
                                 rs.getFloat("importo_totale") + "\n");
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore generazione report acquisti: " + e.getMessage());
        }
    }

    // Genera un report dei clienti che hanno effettuato almeno N vendite (proposte accettate) e salva su file CSV
    public static void generaReportVendite(int minVendite, String filePath) throws DAOException, DBConnectionException {
        String query = "SELECT c.nomeUtente, COUNT(pv.id) AS numeroVendite " +
                "FROM ClienteRegistrato c " +
                "JOIN proposta_vendita pv ON c.nomeUtente = pv.venditore " +
                "WHERE pv.stato = 'ACCETTATA' " +
                "GROUP BY c.nomeUtente " +
                "HAVING COUNT(pv.id) >= ?";
        try (Connection conn = database.DBManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, minVendite);
            ResultSet rs = stmt.executeQuery();
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write("nomeUtente,numeroVendite\n");
                while (rs.next()) {
                    writer.write(rs.getString("nomeUtente") + "," +
                                 rs.getInt("numeroVendite") + "\n");
                }
            }
        } catch (Exception e) {
            throw new DAOException("Errore generazione report vendite: " + e.getMessage());
        }
    }
}
