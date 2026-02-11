package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.EntityProdotto;
import exception.DAOException;
import exception.DBConnectionException;

public class ProdottoDAO {

    // CREATE
    public static void createProdotto(EntityProdotto eP) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "INSERT INTO Prodotto (nome, descrizione, prezzo, immagine1, immagine2, immagine3, immagine4, quantitadisp, tipo, tecnica, dimensioni, peso, altezza) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, eP.getNome());
                stmt.setString(2, eP.getDescrizione());
                stmt.setFloat(3, eP.getPrezzo());
                stmt.setString(4, eP.getImmagine1());
                stmt.setString(5, eP.getImmagine2());
                stmt.setString(6, eP.getImmagine3());
                stmt.setString(7, eP.getImmagine4());
                stmt.setInt(8, eP.getQuantita());
                // Forza il tipo a "dipinto" o "scultura" se l'oggetto è specializzato
                if (eP instanceof entity.EntityDipinto) {
                    stmt.setString(9, "dipinto");
                } else if (eP instanceof entity.EntityScultura) {
                    stmt.setString(9, "scultura");
                } else {
                    stmt.setString(9, eP.getTipo());
                }
                if (eP instanceof entity.EntityDipinto) {
                    stmt.setString(10, ((entity.EntityDipinto) eP).getTecnica());
                    stmt.setString(11, ((entity.EntityDipinto) eP).getDimensionetela());
                    stmt.setNull(12, java.sql.Types.FLOAT);
                    stmt.setNull(13, java.sql.Types.FLOAT);
                } else if (eP instanceof entity.EntityScultura) {
                    stmt.setNull(10, java.sql.Types.VARCHAR);
                    stmt.setNull(11, java.sql.Types.VARCHAR);
                    stmt.setFloat(12, ((entity.EntityScultura) eP).getPeso());
                    stmt.setFloat(13, ((entity.EntityScultura) eP).getAltezza());
                } else {
                    stmt.setNull(10, java.sql.Types.VARCHAR);
                    stmt.setNull(11, java.sql.Types.VARCHAR);
                    stmt.setNull(12, java.sql.Types.FLOAT);
                    stmt.setNull(13, java.sql.Types.FLOAT);
                }
                stmt.executeUpdate();
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    eP.setIdProdotto(rs.getInt(1));
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore inserimento prodotto: " + e.getMessage());
        }
    }

    // READ
    public static EntityProdotto getProdottoById(int id) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM Prodotto WHERE codice = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String tipo = rs.getString("tipo");
                    if ("dipinto".equalsIgnoreCase(tipo)) {
                        entity.EntityDipinto p = new entity.EntityDipinto(
                            rs.getInt("codice"),
                            rs.getString("nome"),
                            rs.getString("descrizione"),
                            rs.getFloat("prezzo"),
                            rs.getString("immagine1"),
                            rs.getString("immagine2"),
                            rs.getString("immagine3"),
                            rs.getString("immagine4"),
                            rs.getInt("quantitadisp"),
                            rs.getString("tecnica"),
                            rs.getString("dimensioni")
                        );
                        return p;
                    } else if ("scultura".equalsIgnoreCase(tipo)) {
                        entity.EntityScultura p = new entity.EntityScultura(
                            rs.getInt("codice"),
                            rs.getString("nome"),
                            rs.getString("descrizione"),
                            rs.getFloat("prezzo"),
                            rs.getString("immagine1"),
                            rs.getString("immagine2"),
                            rs.getString("immagine3"),
                            rs.getString("immagine4"),
                            rs.getInt("quantitadisp"),
                            rs.getFloat("peso"),
                            rs.getFloat("altezza")
                        );
                        return p;
                    } else {
                        EntityProdotto p = new EntityProdotto();
                        p.setIdProdotto(rs.getInt("codice"));
                        p.setNome(rs.getString("nome"));
                        p.setDescrizione(rs.getString("descrizione"));
                        p.setPrezzo(rs.getFloat("prezzo"));
                        p.setImmagine1(rs.getString("immagine1"));
                        p.setImmagine2(rs.getString("immagine2"));
                        p.setImmagine3(rs.getString("immagine3"));
                        p.setImmagine4(rs.getString("immagine4"));
                        p.setQuantita(rs.getInt("quantitadisp"));
                        p.setTipo(tipo);
                        return p;
                    }
                } else {
                    return null;
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura prodotto: " + e.getMessage());
        }
    }

    // UPDATE
    public static void updateProdotto(EntityProdotto p) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "UPDATE Prodotto SET nome=?, descrizione=?, prezzo=?, quantitadisp=?, tipo=?, tecnica=?, dimensioni=?, peso=?, altezza=? WHERE codice=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, p.getNome());
                stmt.setString(2, p.getDescrizione());
                stmt.setFloat(3, p.getPrezzo());
                stmt.setInt(4, p.getQuantita());
                // Forza il tipo a "dipinto" o "scultura" se l'oggetto è specializzato
                if (p instanceof entity.EntityDipinto) {
                    stmt.setString(5, "dipinto");
                } else if (p instanceof entity.EntityScultura) {
                    stmt.setString(5, "scultura");
                } else {
                    stmt.setString(5, p.getTipo());
                }
                if (p instanceof entity.EntityDipinto) {
                    stmt.setString(6, ((entity.EntityDipinto) p).getTecnica());
                    stmt.setString(7, ((entity.EntityDipinto) p).getDimensionetela());
                    stmt.setNull(8, java.sql.Types.FLOAT);
                    stmt.setNull(9, java.sql.Types.FLOAT);
                } else if (p instanceof entity.EntityScultura) {
                    stmt.setNull(6, java.sql.Types.VARCHAR);
                    stmt.setNull(7, java.sql.Types.VARCHAR);
                    stmt.setFloat(8, ((entity.EntityScultura) p).getPeso());
                    stmt.setFloat(9, ((entity.EntityScultura) p).getAltezza());
                } else {
                    stmt.setNull(6, java.sql.Types.VARCHAR);
                    stmt.setNull(7, java.sql.Types.VARCHAR);
                    stmt.setNull(8, java.sql.Types.FLOAT);
                    stmt.setNull(9, java.sql.Types.FLOAT);
                }
                stmt.setInt(10, p.getIdProdotto());
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento prodotto: " + e.getMessage());
        }
    }

    // DELETE
    public static void deleteProdotto(int id) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "DELETE FROM Prodotto WHERE codice=?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore eliminazione prodotto: " + e.getMessage());
        }
    }

    // LIST ALL
    public static List<EntityProdotto> getAllProdotti() throws DAOException, DBConnectionException {
        List<EntityProdotto> prodotti = new ArrayList<>();
        try {
            Connection conn = DBManager.getConnection();
            String query = "SELECT * FROM Prodotto";
            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String tipo = rs.getString("tipo");
                    if ("dipinto".equalsIgnoreCase(tipo)) {
                        entity.EntityDipinto p = new entity.EntityDipinto(
                            rs.getInt("codice"),
                            rs.getString("nome"),
                            rs.getString("descrizione"),
                            rs.getFloat("prezzo"),
                            rs.getString("immagine1"),
                            rs.getString("immagine2"),
                            rs.getString("immagine3"),
                            rs.getString("immagine4"),
                            rs.getInt("quantitadisp"),
                            rs.getString("tecnica"),
                            rs.getString("dimensioni")
                        );
                        prodotti.add(p);
                    } else if ("scultura".equalsIgnoreCase(tipo)) {
                        entity.EntityScultura p = new entity.EntityScultura(
                            rs.getInt("codice"),
                            rs.getString("nome"),
                            rs.getString("descrizione"),
                            rs.getFloat("prezzo"),
                            rs.getString("immagine1"),
                            rs.getString("immagine2"),
                            rs.getString("immagine3"),
                            rs.getString("immagine4"),
                            rs.getInt("quantitadisp"),
                            rs.getFloat("peso"),
                            rs.getFloat("altezza")
                        );
                        prodotti.add(p);
                    } else {
                        EntityProdotto p = new EntityProdotto();
                        p.setIdProdotto(rs.getInt("codice"));
                        p.setNome(rs.getString("nome"));
                        p.setDescrizione(rs.getString("descrizione"));
                        p.setPrezzo(rs.getFloat("prezzo"));
                        p.setImmagine1(rs.getString("immagine1"));
                        p.setImmagine2(rs.getString("immagine2"));
                        p.setImmagine3(rs.getString("immagine3"));
                        p.setImmagine4(rs.getString("immagine4"));
                        p.setQuantita(rs.getInt("quantitadisp"));
                        p.setTipo(tipo);
                        prodotti.add(p);
                    }
                }
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore lettura prodotti: " + e.getMessage());
        }
        return prodotti;
    }

	public static void aggiornaQuantitaProdotto(int idProdotto, int nuovaQuantita) throws DAOException, DBConnectionException {
        try {
            Connection conn = DBManager.getConnection();
            String query = "UPDATE Prodotto SET quantitadisp = ? WHERE codice = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, nuovaQuantita);
                stmt.setInt(2, idProdotto);
                stmt.executeUpdate();
            } finally {
                DBManager.closeConnection();
            }
        } catch (SQLException e) {
            throw new DAOException("Errore aggiornamento quantità prodotto: " + e.getMessage());
        }
		
	}
}
