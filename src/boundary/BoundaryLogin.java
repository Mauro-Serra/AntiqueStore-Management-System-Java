package boundary;

import control.GestioneNegozio;
import entity.EntityClienteRegistrato;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BoundaryLogin extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public BoundaryLogin() {
        setTitle("Login Negozio Antiquariato");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 480);
        setLocationRelativeTo(null);
        setResizable(false);

        // Background sfumato
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(120, 100, 180);
                Color color2 = new Color(200, 160, 200);
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Card centrale con bordo arrotondato
        JPanel cardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 210));
                g2.fillRoundRect(0, 20, getWidth(), getHeight()-20, 32, 32);
            }
        };
        cardPanel.setOpaque(false);
        cardPanel.setPreferredSize(new Dimension(340, 420)); // aumenta l'altezza del cardPanel
        cardPanel.setLayout(null);

        // Icona utente stilizzata in alto (ora logo)
        JLabel iconLabel = new JLabel() {
            private ImageIcon logoIcon = null;
            private boolean triedLoad = false;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!triedLoad) {
                    triedLoad = true;
                    java.net.URL imgURL = getClass().getResource("/boundary/graphics/logo_antiquary.png");
                    if (imgURL != null) {
                        logoIcon = new ImageIcon(imgURL);
                    } else {
                        System.err.println("[DEBUG] Logo non trovato: /boundary/graphics/logo_antiquary.png");
                    }
                }
                if (logoIcon != null) {
                    int boxW = 200, boxH = 200;
                    int imgW = logoIcon.getIconWidth();
                    int imgH = logoIcon.getIconHeight();
                    float scale = Math.min((float)boxW/imgW, (float)boxH/imgH);
                    int drawW = Math.round(imgW * scale);
                    int drawH = Math.round(imgH * scale);
                    int x = (getWidth() - drawW) / 2;
                    int y = 20 + (getHeight() - drawH) / 2 - 12; // alza di 12px rispetto a prima
                    g.drawImage(logoIcon.getImage(), x, y, drawW, drawH, this);
                } else {
                    g.setColor(Color.DARK_GRAY);
                    g.setFont(new Font("SansSerif", Font.BOLD, 14));
                    g.drawString("LOGO", (getWidth()-40)/2, getHeight()/2);
                }
            }
        };
        iconLabel.setBounds(60, 10, 220, 120); // alza di 20px rispetto a prima
        cardPanel.add(iconLabel);

        // Username field con icona
        JLabel userIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 80, 120));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(2, 4, 16, 16); // testa
                g2.drawArc(2, 12, 16, 10, 0, -180); // spalle
            }
        };
        userIcon.setBounds(45, 185, 20, 24); // sposta più in basso di 30px
        cardPanel.add(userIcon);
        usernameField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Trasparente, solo linea sotto
                g.setColor(new Color(60, 80, 120));
                g.fillRect(0, getHeight()-2, getWidth(), 2);
            }
        };
        usernameField.setBounds(70, 180, 220, 32); // sposta più in basso di 30px
        usernameField.setOpaque(false);
        usernameField.setBackground(new Color(0,0,0,0));
        usernameField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        usernameField.setForeground(new Color(60, 80, 120));
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        usernameField.setCaretColor(new Color(60, 80, 120));
        cardPanel.add(usernameField);

        // Password field con icona
        JLabel passIcon = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(60, 80, 120));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRect(2, 8, 16, 10); // corpo lucchetto
                g2.drawArc(2, 4, 16, 8, 0, 180); // arco
            }
        };
        passIcon.setBounds(45, 235, 20, 24); // sposta più in basso di 30px
        cardPanel.add(passIcon);
        passwordField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Trasparente, solo linea sotto
                g.setColor(new Color(60, 80, 120));
                g.fillRect(0, getHeight()-2, getWidth(), 2);
            }
        };
        passwordField.setBounds(70, 230, 220, 32); // sposta più in basso di 30px
        passwordField.setOpaque(false);
        passwordField.setBackground(new Color(0,0,0,0));
        passwordField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        passwordField.setForeground(new Color(60, 80, 120));
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        passwordField.setCaretColor(new Color(60, 80, 120));
        cardPanel.add(passwordField);
        
        // Login button
        JButton loginButton = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(50, 60, 100) : new Color(60, 80, 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setBounds(40, 320, 250, 38); // sposta più in basso di 30px
        loginButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        loginButton.setContentAreaFilled(false);
        loginButton.setOpaque(false);
        loginButton.setHorizontalTextPosition(SwingConstants.CENTER);
        loginButton.setVerticalTextPosition(SwingConstants.CENTER);
        cardPanel.add(loginButton);

        // Register button arrotondato
        JButton registerButton = new JButton("REGISTER") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(180, 180, 220) : new Color(210, 210, 235)); // più scuro
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        registerButton.setForeground(new Color(60, 80, 120));
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 15));
        registerButton.setBounds(40, 370, 250, 36); // sposta più in basso di 30px
        registerButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        registerButton.setContentAreaFilled(false);
        registerButton.setOpaque(false);
        registerButton.setHorizontalTextPosition(SwingConstants.CENTER);
        registerButton.setVerticalTextPosition(SwingConstants.CENTER);
        cardPanel.add(registerButton);

        // Rimuovi gli sfondi arrotondati dei campi
        // ...rimuovi usernameBg e passwordBg...

        backgroundPanel.add(cardPanel);
        setContentPane(backgroundPanel);

        // Azioni
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                openRegisterDialog();
            }
        });
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if ("admin".equals(username) && "admin".equals(password)) {
            JOptionPane.showMessageDialog(this, "Accesso gestore effettuato!");
            dispose();
            BoundaryGestore.startTerminal();
        } else {
            try {
                GestioneNegozio gestione = GestioneNegozio.getInstance();
                String result = gestione.loginClienteBCED(username, password);
                // Check for 'successo' (Italian) instead of 'success' for better compatibility
                if (result != null && result.toLowerCase().contains("successo")) {
                    EntityClienteRegistrato cliente = gestione.getClienteByUsernameBCED(username);
                    JOptionPane.showMessageDialog(this, "Accesso cliente effettuato!");
                    dispose();
                    BoundaryCliente.startTerminal(cliente);
                } else {
                    JOptionPane.showMessageDialog(this, result != null ? result : "Credenziali errate!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Errore: " + ex.getMessage());
            }
        }
    }

    // Apre una finestra di registrazione utente con tutti i campi richiesti
    private void openRegisterDialog() {
        JDialog dialog = new JDialog(this, "Registrazione", true);
        dialog.setSize(370, 340);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);

        // Username label arrotondata e centrata
        JLabel userLabel = new JLabel("Username:", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        userLabel.setBounds(30, 30, 100, 25);
        userLabel.setOpaque(false);
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        userLabel.setForeground(new Color(60, 80, 120));
        userLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(userLabel);
        JTextField userField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        userField.setBounds(140, 30, 180, 25);
        userField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        userField.setOpaque(false);
        dialog.add(userField);

        JLabel passLabel = new JLabel("Password:", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        passLabel.setBounds(30, 70, 100, 25);
        passLabel.setOpaque(false);
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        passLabel.setForeground(new Color(60, 80, 120));
        passLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(passLabel);
        JPasswordField passField = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        passField.setBounds(140, 70, 180, 25);
        passField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        passField.setOpaque(false);
        dialog.add(passField);

        JLabel telLabel = new JLabel("Telefono:", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        telLabel.setBounds(30, 110, 100, 25);
        telLabel.setOpaque(false);
        telLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        telLabel.setForeground(new Color(60, 80, 120));
        telLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(telLabel);
        JTextField telField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        telField.setBounds(140, 110, 180, 25);
        telField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        telField.setOpaque(false);
        dialog.add(telField);

        JLabel cardLabel = new JLabel("Carta di Credito:", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(230, 230, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cardLabel.setBounds(30, 150, 100, 25);
        cardLabel.setOpaque(false);
        cardLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        cardLabel.setForeground(new Color(60, 80, 120));
        cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dialog.add(cardLabel);
        JTextField cardField = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        cardField.setBounds(140, 150, 180, 25);
        cardField.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        cardField.setOpaque(false);
        dialog.add(cardField);

        JButton regBtn = new JButton("Registrati") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? new Color(50, 60, 100) : new Color(60, 80, 120));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        regBtn.setForeground(Color.WHITE);
        regBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        regBtn.setBounds(110, 210, 130, 36);
        regBtn.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        regBtn.setContentAreaFilled(false);
        regBtn.setOpaque(false);
        regBtn.setHorizontalTextPosition(SwingConstants.CENTER);
        regBtn.setVerticalTextPosition(SwingConstants.CENTER);
        dialog.add(regBtn);

        regBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                String telefono = telField.getText();
                String carta = cardField.getText();
                try {
                    GestioneNegozio gestione = GestioneNegozio.getInstance();
                    String result = gestione.registraClienteBCED(username, password, telefono, carta);
                    JOptionPane.showMessageDialog(dialog, result);
                    if (result != null && result.toLowerCase().contains("successo")) {
                        dialog.dispose();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Errore: " + ex.getMessage());
                }
            }
        });
        dialog.setVisible(true);
    }
}
