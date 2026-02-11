package database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import entity.EntityClienteRegistrato;
import entity.EntityPropostaVendita;
import exception.DAOException;
import exception.DBConnectionException;

/**
 * PropostaVenditaDAO gestisce tutte le operazioni di accesso e manipolazione
 * delle proposte di vendita nel database del negozio di antiquariato.
 * Fornisce metodi per inserire, recuperare, aggiornare ed eliminare proposte di vendita.
 */
public class PropostaVenditaDAO {
    /**
     * Inserisce una nuova proposta di vendita nel database.
     */

    public static void insertProposta(EntityPropostaVendita proposta) throws SQLException {
        Connection conn = DBManager.getConnection();
        String sql = "INSERT INTO proposta_vendita (venditore, nome_prodotto, descrizione, tipo, prezzo_proposto, prezzo_gestore, quantita, stato, immagine1, immagine2, immagine3, immagine4, id_prodotto, prezzo, tecnica, dimensioni, peso, altezza) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, proposta.venditore.getNomeUtente());
            ps.setString(2, proposta.getNome());
            ps.setString(3, proposta.getDescrizione());
            ps.setString(4, proposta.tipo);
            ps.setFloat(5, proposta.prezzoProposto);
            ps.setFloat(6, proposta.prezzoGestore);
            ps.setInt(7, proposta.getQuantita());
            ps.setString(8, proposta.stato.name());
            ps.setString(9, proposta.getImmagine1());
            ps.setString(10, proposta.getImmagine2());
            ps.setString(11, proposta.getImmagine3());
            ps.setString(12, proposta.getImmagine4());
            if (proposta.getIdProdotto() > 0) {
                ps.setInt(13, proposta.getIdProdotto());
            } else {
                ps.setNull(13, java.sql.Types.INTEGER);
            }
            ps.setFloat(14, proposta.getPrezzo());
            ps.setString(15, proposta.getTecnica());
            ps.setString(16, proposta.getDimensioni());
            ps.setFloat(17, proposta.getPeso());
            ps.setFloat(18, proposta.getAltezza());
            ps.executeUpdate();
        }
    }

    /**
     * Helper DTO per contenere temporaneamente i dati di una proposta di vendita
     * durante la lettura dal database.
     */

    private static class PropostaVenditaDTO {
        String venditore;
        String nomeProdotto;
        String descrizione;
        String tipo;
        float prezzoProposto;
        float prezzoGestore;
        int quantita;
        String stato;
        String immagine1, immagine2, immagine3, immagine4;
        int idProdotto;
        float prezzo;
        String tecnica;
        String dimensioni;
        float peso;
        float altezza;
    }

    /**
     * Restituisce la lista delle proposte di vendita in attesa di valutazione (non accettate, rifiutate o annullate).
     */
    public static List<EntityPropostaVendita> getProposteInAttesa() throws SQLException, DAOException, DBConnectionException {
        List<EntityPropostaVendita> proposte = new ArrayList<>();
        List<PropostaVenditaDTO> dtos = new ArrayList<>();
        Connection conn = DBManager.getConnection();
        String sql = "SELECT * FROM proposta_vendita WHERE stato NOT IN ('ACCETTATA', 'RIFIUTATA', 'ANNULLATA')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PropostaVenditaDTO dto = new PropostaVenditaDTO();
                dto.venditore = rs.getString("venditore");
                dto.nomeProdotto = rs.getString("nome_prodotto");
                dto.descrizione = rs.getString("descrizione");
                dto.tipo = rs.getString("tipo");
                dto.prezzoProposto = rs.getFloat("prezzo_proposto");
                dto.prezzoGestore = rs.getFloat("prezzo_gestore");
                dto.quantita = rs.getInt("quantita");
                dto.stato = rs.getString("stato");
                dto.immagine1 = rs.getString("immagine1");
                dto.immagine2 = rs.getString("immagine2");
                dto.immagine3 = rs.getString("immagine3");
                dto.immagine4 = rs.getString("immagine4");
                dto.idProdotto = rs.getInt("id_prodotto");
                dto.prezzo = rs.getFloat("prezzo");
                dto.tecnica = rs.getString("tecnica");
                dto.dimensioni = rs.getString("dimensioni");
                dto.peso = rs.getFloat("peso");
                dto.altezza = rs.getFloat("altezza");
                dtos.add(dto);
            }
        }
        // Now build EntityPropostaVendita objects (may call DAO methods)
        for (PropostaVenditaDTO dto : dtos) {
            EntityClienteRegistrato venditore = database.ClienteRegistratoDAO.getClienteByUsername(dto.venditore);
            EntityPropostaVendita p = new EntityPropostaVendita(
                venditore,
                dto.nomeProdotto,
                dto.descrizione,
                dto.tipo,
                dto.prezzoProposto,
                dto.quantita,
                dto.immagine1,
                dto.immagine2,
                dto.immagine3,
                dto.immagine4,
                dto.tecnica,
                dto.dimensioni,
                dto.peso,
                dto.altezza
            );
            p.prezzoGestore = dto.prezzoGestore;
            p.stato = EntityPropostaVendita.Stato.valueOf(dto.stato);
            p.idProdotto = dto.idProdotto;
            p.prezzo = dto.prezzo;
            proposte.add(p);
        }
        return proposte;
    }

    /**
     * Restituisce la lista delle proposte di vendita fatte da uno specifico venditore.
     */
    public static List<EntityPropostaVendita> getProposteByVenditore(String venditoreUsername) throws SQLException, DAOException, DBConnectionException {
        List<EntityPropostaVendita> proposte = new ArrayList<>();
        List<PropostaVenditaDTO> dtos = new ArrayList<>();
        Connection conn = DBManager.getConnection();
        String sql = "SELECT * FROM proposta_vendita WHERE venditore = ? ORDER BY id DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, venditoreUsername);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PropostaVenditaDTO dto = new PropostaVenditaDTO();
                dto.venditore = rs.getString("venditore");
                dto.nomeProdotto = rs.getString("nome_prodotto");
                dto.descrizione = rs.getString("descrizione");
                dto.tipo = rs.getString("tipo");
                dto.prezzoProposto = rs.getFloat("prezzo_proposto");
                dto.prezzoGestore = rs.getFloat("prezzo_gestore");
                dto.quantita = rs.getInt("quantita");
                dto.stato = rs.getString("stato");
                dto.immagine1 = rs.getString("immagine1");
                dto.immagine2 = rs.getString("immagine2");
                dto.immagine3 = rs.getString("immagine3");
                dto.immagine4 = rs.getString("immagine4");
                dto.idProdotto = rs.getInt("id_prodotto");
                dto.prezzo = rs.getFloat("prezzo");
                dto.tecnica = rs.getString("tecnica");
                dto.dimensioni = rs.getString("dimensioni");
                dto.peso = rs.getFloat("peso");
                dto.altezza = rs.getFloat("altezza");
                dtos.add(dto);
            }
        }
        for (PropostaVenditaDTO dto : dtos) {
            EntityClienteRegistrato venditore = database.ClienteRegistratoDAO.getClienteByUsername(dto.venditore);
            EntityPropostaVendita p = new EntityPropostaVendita(
                venditore,
                dto.nomeProdotto,
                dto.descrizione,
                dto.tipo,
                dto.prezzoProposto,
                dto.quantita,
                dto.immagine1,
                dto.immagine2,
                dto.immagine3,
                dto.immagine4,
                dto.tecnica,
                dto.dimensioni,
                dto.peso,
                dto.altezza
            );
            p.prezzoGestore = dto.prezzoGestore;
            p.stato = EntityPropostaVendita.Stato.valueOf(dto.stato);
            p.idProdotto = dto.idProdotto;
            p.prezzo = dto.prezzo;
            proposte.add(p);
        }
        return proposte;
    }

    /**
     * Aggiorna i dati di una proposta di vendita esistente nel database.
     */
    public static void updateProposta(EntityPropostaVendita proposta) throws SQLException {
        Connection conn = DBManager.getConnection();
        String sql = "UPDATE proposta_vendita SET prezzo_gestore=?, stato=?, id_prodotto=?, prezzo=?, tecnica=?, dimensioni=?, peso=?, altezza=? WHERE venditore=? AND nome_prodotto=? AND stato NOT IN ('ACCETTATA', 'RIFIUTATA', 'ANNULLATA')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setFloat(1, proposta.prezzoGestore);
            ps.setString(2, proposta.stato.name());
            if (proposta.getIdProdotto() > 0) {
                ps.setInt(3, proposta.getIdProdotto());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setFloat(4, proposta.getPrezzo());
            ps.setString(5, proposta.getTecnica());
            ps.setString(6, proposta.getDimensioni());
            ps.setFloat(7, proposta.getPeso());
            ps.setFloat(8, proposta.getAltezza());
            ps.setString(9, proposta.venditore.getNomeUtente());
            ps.setString(10, proposta.getNome());
            ps.executeUpdate();
        }
    }

    /**
     * Elimina una proposta di vendita dal database in base a venditore e nome prodotto.
     */
    public static void deleteProposta(String venditore, String nomeProdotto) throws SQLException {
        Connection conn = DBManager.getConnection();
        String sql = "DELETE FROM proposta_vendita WHERE venditore=? AND nome_prodotto=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, venditore);
            ps.setString(2, nomeProdotto);
            ps.executeUpdate();
        }
    }
}
