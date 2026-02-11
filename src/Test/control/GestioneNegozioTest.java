package Test.control;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import control.GestioneNegozio;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import database.*;
import entity.*;
import exception.DAOException;
import java.util.*;

public class GestioneNegozioTest {
    private GestioneNegozio gestoreSpy;
    private EntityCarrello carrello;
    private EntityClienteRegistrato cliente;
    private EntityProdotto prodotto;

    @AfterEach
    public void tearDown() {
        clearAllCaches();
    }

    @BeforeEach
    public void setUp() {
        carrello = mock(EntityCarrello.class);
        cliente = mock(EntityClienteRegistrato.class);
        prodotto = mock(EntityProdotto.class);
        gestoreSpy = spy(GestioneNegozio.getInstance());
    }

    // --- ACQUISTO ---
    @Test
    public void testAcquistoPagamentoFallito() throws Exception {
        when(cliente.getTelefono()).thenReturn("1234567890");
        when(carrello.getIdCarrello()).thenReturn(1);
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 2));
        when(prodotto.getIdProdotto()).thenReturn(101);
        when(prodotto.getNome()).thenReturn("Mouse");
        when(prodotto.getQuantita()).thenReturn(5);
        try (
            MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class);
            MockedConstruction<CarrelloDAO> carrelloDAOMock = mockConstruction(CarrelloDAO.class, (mock, ctx) -> {
                when(mock.getProdottiByCarrelloId(1)).thenReturn(List.of(prodotto));
                when(mock.getQuantitaProdottoInCarrello(1, 101)).thenReturn(2);
            })
        ) {
            prodottoDAOMock.when(() -> ProdottoDAO.getProdottoById(101)).thenReturn(prodotto);
            doReturn(false).when(gestoreSpy).effettuaPagamento(any(), any(), anyFloat());
            String result = gestoreSpy.acquistaProdottoBCED(carrello, cliente, 29.99f);
            assertEquals("Pagamento non riuscito. Riprova.", result);
        }
    }

    @Test
    public void testAcquistoCarrelloConProdottiDiversi() throws Exception {
        EntityProdotto prodotto2 = mock(EntityProdotto.class);
        when(cliente.getTelefono()).thenReturn("1234567890");
        when(carrello.getIdCarrello()).thenReturn(1);
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 1, prodotto2, 2));
        when(prodotto.getIdProdotto()).thenReturn(101);
        when(prodotto.getNome()).thenReturn("Mouse");
        when(prodotto.getQuantita()).thenReturn(5);
        when(prodotto2.getIdProdotto()).thenReturn(102);
        when(prodotto2.getNome()).thenReturn("Tastiera");
        when(prodotto2.getQuantita()).thenReturn(2);
        try (
            MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class);
            MockedStatic<OrdineDAO> ordineDAOMock = mockStatic(OrdineDAO.class);
            MockedConstruction<CarrelloDAO> carrelloDAOMock = mockConstruction(CarrelloDAO.class, (mock, ctx) -> {
                when(mock.getProdottiByCarrelloId(1)).thenReturn(List.of(prodotto, prodotto2));
                when(mock.getQuantitaProdottoInCarrello(1, 101)).thenReturn(1);
                when(mock.getQuantitaProdottoInCarrello(1, 102)).thenReturn(2);
            })
        ) {
            prodottoDAOMock.when(() -> ProdottoDAO.getProdottoById(101)).thenReturn(prodotto);
            prodottoDAOMock.when(() -> ProdottoDAO.getProdottoById(102)).thenReturn(prodotto2);
            ordineDAOMock.when(() -> OrdineDAO.createOrdine(any())).thenAnswer(inv -> null);
            doReturn(true).when(gestoreSpy).effettuaPagamento(any(), any(), anyFloat());
            doNothing().when(gestoreSpy).inviaNotifica(anyString(), anyString());
            String result = gestoreSpy.acquistaProdottoBCED(carrello, cliente, 39.99f);
            assertEquals("Acquisto completato! Riceverai una conferma via SMS.", result);
        }
    }

    @Test
    public void testAcquistoQuantitaUgualeDisponibile() throws Exception {
        when(cliente.getTelefono()).thenReturn("1234567890");
        when(carrello.getIdCarrello()).thenReturn(1);
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 2));
        when(prodotto.getIdProdotto()).thenReturn(101);
        when(prodotto.getNome()).thenReturn("Mouse");
        when(prodotto.getQuantita()).thenReturn(2);
        try (
            MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class);
            MockedStatic<OrdineDAO> ordineDAOMock = mockStatic(OrdineDAO.class);
            MockedConstruction<CarrelloDAO> carrelloDAOMock = mockConstruction(CarrelloDAO.class, (mock, ctx) -> {
                when(mock.getProdottiByCarrelloId(1)).thenReturn(List.of(prodotto));
                when(mock.getQuantitaProdottoInCarrello(1, 101)).thenReturn(2);
            })
        ) {
            prodottoDAOMock.when(() -> ProdottoDAO.getProdottoById(101)).thenReturn(prodotto);
            prodottoDAOMock.when(() -> ProdottoDAO.deleteProdotto(101)).thenAnswer(inv -> null);
            ordineDAOMock.when(() -> OrdineDAO.createOrdine(any())).thenAnswer(inv -> null);
            doReturn(true).when(gestoreSpy).effettuaPagamento(any(), any(), anyFloat());
            doNothing().when(gestoreSpy).inviaNotifica(anyString(), anyString());
            String result = gestoreSpy.acquistaProdottoBCED(carrello, cliente, 29.99f);
            assertEquals("Acquisto completato! Riceverai una conferma via SMS.", result);
        }
    }

    @Test
    public void testAcquistoQuantitaNegativa() throws Exception {
        when(cliente.getTelefono()).thenReturn("1234567890");
        when(carrello.getIdCarrello()).thenReturn(1);
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 3));
        when(prodotto.getIdProdotto()).thenReturn(101);
        when(prodotto.getNome()).thenReturn("Mouse");
        when(prodotto.getQuantita()).thenReturn(2);
        try (
            MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class);
            MockedConstruction<CarrelloDAO> carrelloDAOMock = mockConstruction(CarrelloDAO.class, (mock, ctx) -> {
                when(mock.getProdottiByCarrelloId(1)).thenReturn(List.of(prodotto));
                when(mock.getQuantitaProdottoInCarrello(1, 101)).thenReturn(3);
            })
        ) {
            prodottoDAOMock.when(() -> ProdottoDAO.getProdottoById(101)).thenReturn(prodotto);
            String result = gestoreSpy.acquistaProdottoBCED(carrello, cliente, 29.99f);
            assertEquals("Errore di sincronizzazione: quantità negativa.", result);
        }
    }

    // --- VALUTAZIONE PROPOSTE ---
    @Test
    public void testValutaProposta_AzioneNonValida() {
        EntityClienteRegistrato venditoreMock = mock(EntityClienteRegistrato.class);
        when(venditoreMock.getTelefono()).thenReturn("1234567890");
        when(venditoreMock.getNomeUtente()).thenReturn("utente1");
        String nomeVenditore = "utente1";
        String nomeProdotto = "Quadro";
        String azione = "invalid";
        Float nuovoPrezzo = 100f;
        EntityPropostaVendita proposta = new EntityPropostaVendita(
            venditoreMock, nomeProdotto, "desc", "dipinto", 80f, 1, "img1", "img2", "img3", "img4", "olio", "50x70", 0f, 0f
        );
        proposta.stato = EntityPropostaVendita.Stato.IN_ATTESA;
        List<EntityPropostaVendita> proposte = List.of(proposta);
        try (MockedStatic<PropostaVenditaDAO> propostaDAOMock = mockStatic(PropostaVenditaDAO.class);
             MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class)) {
            propostaDAOMock.when(PropostaVenditaDAO::getProposteInAttesa).thenReturn(proposte);
            propostaDAOMock.when(() -> PropostaVenditaDAO.updateProposta(any())).thenAnswer(inv -> null);
            prodottoDAOMock.when(() -> ProdottoDAO.createProdotto(any())).thenAnswer(inv -> null);
            String result = gestoreSpy.valutaPropostaVenditaBCED(nomeVenditore, nomeProdotto, azione, nuovoPrezzo);
            assertEquals("Azione non valida. Usa: accetta, rifiuta, controproponi.", result);
        }
    }

    @Test
    public void testValutaProposta_PropostaGiaGestita() {
        EntityClienteRegistrato venditoreMock = mock(EntityClienteRegistrato.class);
        when(venditoreMock.getTelefono()).thenReturn("1234567890");
        when(venditoreMock.getNomeUtente()).thenReturn("utente1");
        String nomeVenditore = "utente1";
        String nomeProdotto = "Quadro";
        String azione = "accetta";
        Float nuovoPrezzo = 100f;
        EntityPropostaVendita proposta = new EntityPropostaVendita(
            venditoreMock, nomeProdotto, "desc", "dipinto", 80f, 1, "img1", "img2", "img3", "img4", "olio", "50x70", 0f, 0f
        );
        proposta.stato = EntityPropostaVendita.Stato.ACCETTATA;
        List<EntityPropostaVendita> proposte = List.of(proposta);
        try (MockedStatic<PropostaVenditaDAO> propostaDAOMock = mockStatic(PropostaVenditaDAO.class);
             MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class)) {
            propostaDAOMock.when(PropostaVenditaDAO::getProposteInAttesa).thenReturn(proposte);
            propostaDAOMock.when(() -> PropostaVenditaDAO.updateProposta(any())).thenAnswer(inv -> null);
            prodottoDAOMock.when(() -> ProdottoDAO.createProdotto(any())).thenAnswer(inv -> null);
            String result = gestoreSpy.valutaPropostaVenditaBCED(nomeVenditore, nomeProdotto, azione, nuovoPrezzo);
            assertEquals("Proposta non trovata o già gestita.", result);
        }
    }

    @Test
    public void testValutaProposta_PropostaMancante() {
        String nomeVenditore = "utente1";
        String nomeProdotto = "Quadro";
        String azione = "accetta";
        Float nuovoPrezzo = 100f;
        try (MockedStatic<PropostaVenditaDAO> propostaDAOMock = mockStatic(PropostaVenditaDAO.class)) {
            propostaDAOMock.when(PropostaVenditaDAO::getProposteInAttesa).thenReturn(List.of());
            String result = gestoreSpy.valutaPropostaVenditaBCED(nomeVenditore, nomeProdotto, azione, nuovoPrezzo);
            assertEquals("Proposta non trovata o già gestita.", result);
        }
    }

    @Test
    public void testValutaProposta_VenditoreNullo() {
        String nomeVenditore = "utente1";
        String nomeProdotto = "Quadro";
        String azione = "accetta";
        Float nuovoPrezzo = 100f;
        EntityPropostaVendita proposta = new EntityPropostaVendita(
            null, nomeProdotto, "desc", "dipinto", 80f, 1, "img1", "img2", "img3", "img4", "olio", "50x70", 0f, 0f
        );
        proposta.stato = EntityPropostaVendita.Stato.IN_ATTESA;
        List<EntityPropostaVendita> proposte = List.of(proposta);
        try (MockedStatic<PropostaVenditaDAO> propostaDAOMock = mockStatic(PropostaVenditaDAO.class);
             MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class)) {
            propostaDAOMock.when(PropostaVenditaDAO::getProposteInAttesa).thenReturn(proposte);
            propostaDAOMock.when(() -> PropostaVenditaDAO.updateProposta(any())).thenAnswer(inv -> null);
            prodottoDAOMock.when(() -> ProdottoDAO.createProdotto(any())).thenAnswer(inv -> null);
            String result = gestoreSpy.valutaPropostaVenditaBCED(nomeVenditore, nomeProdotto, azione, nuovoPrezzo);
            assertEquals("Proposta non trovata o già gestita.", result);
        }
    }

    // --- GESTIONE DEL CARRELLO ---
    @Test
    public void testAggiungiProdottoGiaPresenteNelCarrello() {
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 1));
        when(prodotto.getIdProdotto()).thenReturn(101);
        when(prodotto.getNome()).thenReturn("Mouse");
        EntityProdotto nuovoProdotto = mock(EntityProdotto.class);
        when(nuovoProdotto.getIdProdotto()).thenReturn(101);
        when(nuovoProdotto.getNome()).thenReturn("Mouse");
        assertDoesNotThrow(() -> carrello.aggiungiProdotto(nuovoProdotto, 2));
    }

    @Test
    public void testRimuoviProdottoParzialeDalCarrello() {
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 3));
        when(prodotto.getIdProdotto()).thenReturn(101);
        assertDoesNotThrow(() -> carrello.rimuoviProdotto(prodotto, 1));
    }

    @Test
    public void testRimuoviProdottoTotaleDalCarrello() {
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 1));
        when(prodotto.getIdProdotto()).thenReturn(101);
        assertDoesNotThrow(() -> carrello.rimuoviProdotto(prodotto, 1));
    }

    @Test
    public void testRimuoviProdottoNonPresenteNelCarrello() {
        when(carrello.getProdotti()).thenReturn(Map.of());
        EntityProdotto altroProdotto = mock(EntityProdotto.class);
        when(altroProdotto.getIdProdotto()).thenReturn(999);
        assertDoesNotThrow(() -> carrello.rimuoviProdotto(altroProdotto, 1));
    }

    @Test
    public void testSvuotaCarrello() {
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 2));
        try (MockedStatic<CarrelloDAO> carrelloDAOMock = mockStatic(CarrelloDAO.class)) {
            carrelloDAOMock.when(() -> CarrelloDAO.svuotaCarrello(anyInt())).thenAnswer(inv -> null);
            assertDoesNotThrow(() -> gestoreSpy.svuotaCarrelloBCED(carrello));
        }
    }

    // --- AGGIUNTA PRODOTTO ---
    @Test
    public void testAggiungiProdotto_QuantitaNegativa() {
        String nome = "Vaso";
        String descrizione = "desc";
        float prezzo = 10.0f;
        String tipo = "scultura";
        int quantita = -1;
        String result = gestoreSpy.aggiungiProdottoBCED(nome, descrizione, prezzo, null, null, null, null, quantita, tipo, null, null, 1f, 1f);
        assertEquals("Quantità non valida.", result);
    }

    @Test
    public void testAggiungiProdotto_TipoNonValido() {
        String nome = "Vaso";
        String descrizione = "desc";
        float prezzo = 10.0f;
        String tipo = "invalid";
        try (MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class)) {
            String result = gestoreSpy.aggiungiProdottoBCED(nome, descrizione, prezzo, null, null, null, null, 1, tipo, null, null, 1f, 1f);
            assertEquals("Prodotto aggiunto con successo.", result);
        }
    }

    @Test
    public void testAggiungiProdotto_ImmaginiNulle() {
        String nome = "Vaso";
        String descrizione = "desc";
        float prezzo = 10.0f;
        String tipo = "scultura";
        try (MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class)) {
            String result = gestoreSpy.aggiungiProdottoBCED(nome, descrizione, prezzo, null, null, null, null, 1, tipo, null, null, 1f, 1f);
            assertEquals("Prodotto aggiunto con successo.", result);
        }
    }

    @Test
    public void testAggiungiProdotto_NomeGiaEsistente() {
        String nome = "Vaso";
        String descrizione = "desc";
        float prezzo = 10.0f;
        String tipo = "scultura";
        try (MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class)) {
            prodottoDAOMock.when(() -> ProdottoDAO.createProdotto(any())).thenThrow(new DAOException("Nome già esistente"));
            String result = gestoreSpy.aggiungiProdottoBCED(nome, descrizione, prezzo, null, null, null, null, 1, tipo, null, null, 1f, 1f);
            assertTrue(result.contains("Errore di database"));
        }
    }

    // --- TEST INSERIMENTO CORRETTO ---
    @Test
    public void testAcquistoProdotto_TuttoCorretto() throws Exception {
        when(cliente.getTelefono()).thenReturn("1234567890");
        when(carrello.getIdCarrello()).thenReturn(1);
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 1));
        when(prodotto.getIdProdotto()).thenReturn(101);
        when(prodotto.getNome()).thenReturn("Mouse");
        when(prodotto.getQuantita()).thenReturn(10);
        try (
            MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class);
            MockedStatic<OrdineDAO> ordineDAOMock = mockStatic(OrdineDAO.class);
            MockedConstruction<CarrelloDAO> carrelloDAOMock = mockConstruction(CarrelloDAO.class, (mock, ctx) -> {
                when(mock.getProdottiByCarrelloId(1)).thenReturn(List.of(prodotto));
                when(mock.getQuantitaProdottoInCarrello(1, 101)).thenReturn(1);
            })
        ) {
            prodottoDAOMock.when(() -> ProdottoDAO.getProdottoById(101)).thenReturn(prodotto);
            ordineDAOMock.when(() -> OrdineDAO.createOrdine(any())).thenAnswer(inv -> null);
            doReturn(true).when(gestoreSpy).effettuaPagamento(any(), any(), anyFloat());
            doNothing().when(gestoreSpy).inviaNotifica(anyString(), anyString());
            String result = gestoreSpy.acquistaProdottoBCED(carrello, cliente, 29.99f);
            assertEquals("Acquisto completato! Riceverai una conferma via SMS.", result);
        }
    }

    @Test
    public void testValutaProposta_TuttoCorretto() throws Exception {
        EntityClienteRegistrato venditoreMock = mock(EntityClienteRegistrato.class);
        when(venditoreMock.getTelefono()).thenReturn("1234567890");
        when(venditoreMock.getNomeUtente()).thenReturn("utente1");
        String nomeVenditore = "utente1";
        String nomeProdotto = "Quadro";
        String azione = "accetta";
        Float nuovoPrezzo = 100f;
        EntityPropostaVendita proposta = new EntityPropostaVendita(
            venditoreMock, nomeProdotto, "desc", "dipinto", 80f, 1, "img1", "img2", "img3", "img4", "olio", "50x70", 0f, 0f
        );
        proposta.stato = EntityPropostaVendita.Stato.IN_ATTESA;
        List<EntityPropostaVendita> proposte = List.of(proposta);
        try (
            MockedStatic<PropostaVenditaDAO> propostaDAOMock = mockStatic(PropostaVenditaDAO.class);
            MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class);
            MockedStatic<GestioneNegozio> gestioneNegozioMock = mockStatic(GestioneNegozio.class, CALLS_REAL_METHODS)
        ) {
            propostaDAOMock.when(PropostaVenditaDAO::getProposteInAttesa).thenReturn(proposte);
            propostaDAOMock.when(() -> PropostaVenditaDAO.updateProposta(any())).thenAnswer(inv -> null);
            prodottoDAOMock.when(() -> ProdottoDAO.createProdotto(any())).thenAnswer(inv -> null);
            // Mock inviaNotifica per evitare interazione DB/notifiche
            doNothing().when(gestoreSpy).inviaNotifica(anyString(), anyString());
            String result = gestoreSpy.valutaPropostaVenditaBCED(nomeVenditore, nomeProdotto, azione, nuovoPrezzo);
            assertTrue(result.contains("Proposta accettata"));
        }
    }

    @Test
    public void testAggiungiProdotto_TuttoCorretto() {
        String nome = "Vaso";
        String descrizione = "desc";
        float prezzo = 10.0f;
        String tipo = "scultura";
        try (MockedStatic<ProdottoDAO> prodottoDAOMock = mockStatic(ProdottoDAO.class)) {
            prodottoDAOMock.when(() -> ProdottoDAO.createProdotto(any())).thenAnswer(inv -> null);
            String result = gestoreSpy.aggiungiProdottoBCED(nome, descrizione, prezzo, null, null, null, null, 1, tipo, null, null, 1f, 1f);
            assertEquals("Prodotto aggiunto con successo.", result);
        }
    }

    @Test
    public void testGestioneCarrello_TuttoCorretto() {
        when(carrello.getProdotti()).thenReturn(Map.of(prodotto, 2));
        when(prodotto.getIdProdotto()).thenReturn(101);
        when(prodotto.getNome()).thenReturn("Mouse");
        EntityProdotto nuovoProdotto = mock(EntityProdotto.class);
        when(nuovoProdotto.getIdProdotto()).thenReturn(102);
        when(nuovoProdotto.getNome()).thenReturn("Tastiera");
        // Aggiunta prodotto diverso
        assertDoesNotThrow(() -> carrello.aggiungiProdotto(nuovoProdotto, 1));
        // Rimozione prodotto esistente
        assertDoesNotThrow(() -> carrello.rimuoviProdotto(prodotto, 1));
    }

    // --- REPORTISTICA ---
    @Test
    public void testGeneraReportAcquistiBCED_TuttoMockato() {
        try (MockedStatic<database.ClienteRegistratoDAO> clienteDAOMock = mockStatic(database.ClienteRegistratoDAO.class)) {
            clienteDAOMock.when(() -> database.ClienteRegistratoDAO.generaReportAcquisti(anyInt(), anyString())).thenAnswer(inv -> null);
            String result = gestoreSpy.generaReportAcquistiBCED(2, "report_acquisti.csv");
            assertTrue(result.contains("Report acquisti generato con successo"));
        }
    }

    @Test
    public void testGeneraReportVenditeBCED_TuttoMockato() {
        try (MockedStatic<database.ClienteRegistratoDAO> clienteDAOMock = mockStatic(database.ClienteRegistratoDAO.class)) {
            clienteDAOMock.when(() -> database.ClienteRegistratoDAO.generaReportVendite(anyInt(), anyString())).thenAnswer(inv -> null);
            String result = gestoreSpy.generaReportVenditeBCED(2, "report_vendite.csv");
            assertTrue(result.contains("Report vendite generato con successo"));
        }
    }

    // --- REGISTRAZIONE E ACCESSO ---
    @Test
    public void testRegistraClienteBCED_TuttoCorretto() {
        String nomeUtente = "mariorossi";
        String password = "password123";
        String telefono = "3331234567";
        String carta = "1234567890123456";
        try (MockedStatic<ClienteRegistratoDAO> clienteDAOMock = mockStatic(ClienteRegistratoDAO.class)) {
            // Simula successo inserimento
            clienteDAOMock.when(() -> ClienteRegistratoDAO.createCliente(any())).thenAnswer(inv -> null);
            String result = gestoreSpy.registraClienteBCED(nomeUtente, password, telefono, carta);
            assertTrue(result.contains("Registrazione completata"));
        }
    }

    @Test
    public void testRegistraClienteBCED_DatiMancantiONonValidi() {
        // Dati mancanti
        String result1 = gestoreSpy.registraClienteBCED(null, "pass", "333", "1234567890123456");
        assertTrue(result1.contains("Dati mancanti"));
        // Telefono non valido
        String result2 = gestoreSpy.registraClienteBCED("user", "pass", "abcde", "1234567890123456");
        assertTrue(result2.contains("Telefono non valido"));
        // Carta non valida
        String result3 = gestoreSpy.registraClienteBCED("user", "pass", "3331234567", "1234");
        assertTrue(result3.contains("Carta non valida"));
    }

    @Test
    public void testRegistraClienteBCED_DAOException() {
        String nomeUtente = "mariorossi";
        String password = "password123";
        String telefono = "3331234567";
        String carta = "1234567890123456";
        try (MockedStatic<ClienteRegistratoDAO> clienteDAOMock = mockStatic(ClienteRegistratoDAO.class)) {
            clienteDAOMock.when(() -> ClienteRegistratoDAO.createCliente(any())).thenThrow(new DAOException("errore DB"));
            String result = gestoreSpy.registraClienteBCED(nomeUtente, password, telefono, carta);
            assertTrue(result.contains("Errore di database"));
        }
    }

    @Test
    public void testLoginClienteBCED_CredenzialiCorrette() throws Exception {
        String username = "mariorossi";
        String password = "password123";
        EntityClienteRegistrato clienteMock = mock(EntityClienteRegistrato.class);
        when(clienteMock.getPassword()).thenReturn(password);
        try (MockedStatic<ClienteRegistratoDAO> clienteDAOMock = mockStatic(ClienteRegistratoDAO.class)) {
            clienteDAOMock.when(() -> ClienteRegistratoDAO.getClienteByUsername(username)).thenReturn(clienteMock);
            String result = gestoreSpy.loginClienteBCED(username, password);
            assertTrue(result.contains("Accesso effettuato"));
        }
    }

    @Test
    public void testLoginClienteBCED_CredenzialiErrate() throws Exception {
        String username = "mariorossi";
        String password = "wrongpass";
        EntityClienteRegistrato clienteMock = mock(EntityClienteRegistrato.class);
        when(clienteMock.getPassword()).thenReturn("password123");
        try (MockedStatic<ClienteRegistratoDAO> clienteDAOMock = mockStatic(ClienteRegistratoDAO.class)) {
            clienteDAOMock.when(() -> ClienteRegistratoDAO.getClienteByUsername(username)).thenReturn(clienteMock);
            String result = gestoreSpy.loginClienteBCED(username, password);
            assertTrue(result.contains("Credenziali errate"));
        }
    }

    @Test
    public void testLoginClienteBCED_UtenteNonTrovato() throws Exception {
        String username = "notfound";
        String password = "pass";
        try (MockedStatic<ClienteRegistratoDAO> clienteDAOMock = mockStatic(ClienteRegistratoDAO.class)) {
            clienteDAOMock.when(() -> ClienteRegistratoDAO.getClienteByUsername(username)).thenReturn(null);
            String result = gestoreSpy.loginClienteBCED(username, password);
            assertTrue(result.contains("Credenziali errate"));
        }
    }

    @Test
    public void testLoginClienteBCED_DAOException() throws Exception {
        String username = "mariorossi";
        String password = "password123";
        try (MockedStatic<ClienteRegistratoDAO> clienteDAOMock = mockStatic(ClienteRegistratoDAO.class)) {
            clienteDAOMock.when(() -> ClienteRegistratoDAO.getClienteByUsername(username)).thenThrow(new DAOException("errore DB"));
            String result = gestoreSpy.loginClienteBCED(username, password);
            assertTrue(result.contains("Errore durante l'accesso"));
        }
    }
}


