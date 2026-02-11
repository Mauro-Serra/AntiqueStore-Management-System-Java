package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Classe di utility per la gestione della connessione al database MySQL
public class DBManager {
    // Connessione singleton condivisa
    private static Connection conn = null;
    
    // Blocco statico per il caricamento del driver MySQL
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("Driver MySQL non trovato nel classpath");
        }
    }
    
    // Costruttore privato per evitare istanziazione
    private DBManager() {}

    // Restituisce la connessione al database, creandola se necessario
    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/negozioantiquariato?serverTimezone=UTC", "root", ""
                );
            }
        } catch (SQLException e) {
            // Stampa dettagliata dell'errore SQL
            System.err.println("SQLException: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("VendorError: " + e.getErrorCode());
            throw new SQLException("Errore nella connessione al database", e);
        }
        return conn;
    }
    
    // Chiude la connessione al database se aperta
    public static synchronized void closeConnection() {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                // Log dell'errore o gestione custom
                e.printStackTrace();
            } finally {
                conn = null;
            }
        }
    }
}
