package UI;

import java.awt.Color;
import java.awt.Font;
import javax.swing.Timer;
import util.Constants;

/**
 * Pantalla de carga que se muestra mientras el cliente intenta
 * conectarse al servidor remoto.
 */
public class LoadingUI extends javax.swing.JFrame {

    private Timer spinnerTimer;
    private int spinnerFrame = 0;
    private static final String[] SPINNER_FRAMES = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};

    private static final String[] STATUS_MSGS = {
        "Estableciendo conexión con el servidor...",
        "Verificando credenciales del módulo...",
        "Sincronizando configuración de discos...",
        "Registrando sesión en bitácora..."
    };
    private int msgIndex = 0;
    private Timer msgTimer;

    public LoadingUI() {
        initComponents();
        initConfig();
        startAnimations();
    }

    private void initConfig() {
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        // Borde superior blanco
        pnl_border.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 1, 1, 1,
                new Color(0xffffff)));
    }

    /** Arranca el spinner de caracteres y el rotador de mensajes */
    private void startAnimations() {
        // Spinner de caracteres braille (~100ms por frame)
        spinnerTimer = new Timer(100, e -> {
            lbl_spinner.setText(SPINNER_FRAMES[spinnerFrame % SPINNER_FRAMES.length]);
            spinnerFrame++;
        });
        spinnerTimer.start();

        // Rotar mensajes de estado cada 2.5 s
        msgTimer = new Timer(2500, e -> {
            msgIndex = (msgIndex + 1) % STATUS_MSGS.length;
            txt_inf.setText(STATUS_MSGS[msgIndex]);
        });
        msgTimer.start();
    }

    /** Detiene las animaciones (llamar antes de cerrar o al conectar) */
    public void stopAnimations() {
        if (spinnerTimer != null) spinnerTimer.stop();
        if (msgTimer    != null) msgTimer.stop();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_border       = new javax.swing.JPanel();
        pnl_header       = new javax.swing.JPanel();
        lbl_header_dots  = new javax.swing.JLabel();
        lbl_header_title = new javax.swing.JLabel();
        lbl_spinner      = new javax.swing.JLabel();
        lbl_titulo       = new javax.swing.JLabel();
        prg_barra        = new javax.swing.JProgressBar();
        txt_inf          = new javax.swing.JLabel();
        btn_cancelar     = new javax.swing.JButton();

        // ── Ventana ────────────────────────────────────────────────
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        setType(java.awt.Window.Type.UTILITY);
        getContentPane().setBackground(new Color(0x880201));

        // ── Panel principal ────────────────────────────────────────
        pnl_border.setBackground(new Color(0x880201));
        pnl_border.setPreferredSize(new java.awt.Dimension(520, 280));

        // ── Header bar ─────────────────────────────────────────────
        pnl_header.setBackground(new Color(0x6f0100));
        pnl_header.setBorder(javax.swing.BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(0xffffff)));

        lbl_header_dots.setFont(new Font("Consolas", Font.PLAIN, 18));
        lbl_header_dots.setForeground(new Color(0xffffff));
        lbl_header_dots.setText("● ● ●");

        lbl_header_title.setFont(new Font("Consolas", Font.PLAIN, 11));
        lbl_header_title.setForeground(new Color(0xffffff));
        lbl_header_title.setText("TERMINAL DONNY — INIT");

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(pnl_header);
        pnl_header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_header_dots)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 0, Short.MAX_VALUE)
                .addComponent(lbl_header_title)
                .addContainerGap())
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lbl_header_dots)
            .addComponent(lbl_header_title)
        );

        // ── Spinner ────────────────────────────────────────────────
        lbl_spinner.setFont(new Font("Consolas", Font.PLAIN, 28));
        lbl_spinner.setForeground(new Color(0xffffff));
        lbl_spinner.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_spinner.setText(SPINNER_FRAMES[0]);

        // ── Título ─────────────────────────────────────────────────
        lbl_titulo.setFont(new Font("Consolas", Font.BOLD, 26));
        lbl_titulo.setForeground(new Color(0xffffff));
        lbl_titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_titulo.setText("CONECTANDO...");

        // ── Barra de progreso ──────────────────────────────────────
        prg_barra.setIndeterminate(true);
        prg_barra.setBackground(new Color(0x6f0100));
        prg_barra.setForeground(new Color(0xffffff));
        prg_barra.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));
        prg_barra.setBorderPainted(true);
        prg_barra.setStringPainted(false);
        prg_barra.setPreferredSize(new java.awt.Dimension(480, 6));

        // ── Mensaje de estado ──────────────────────────────────────
        txt_inf.setFont(new Font("Consolas", Font.PLAIN, 12));
        txt_inf.setForeground(new Color(0xffffff));
        txt_inf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txt_inf.setText(STATUS_MSGS[0]);

        // ── Botón cancelar ─────────────────────────────────────────
        btn_cancelar.setBackground(new Color(0x6f0100));
        btn_cancelar.setFont(new Font("Consolas", Font.BOLD, 12));
        btn_cancelar.setForeground(new Color(0xffffff));
        btn_cancelar.setText("✕  CANCELAR");
        btn_cancelar.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));
        btn_cancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_cancelar.setContentAreaFilled(true);
        btn_cancelar.setFocusPainted(false);
        btn_cancelar.setPreferredSize(new java.awt.Dimension(140, 32));
        btn_cancelar.addActionListener(evt -> btn_cancelarActionPerformed(evt));

        // ── Layout del panel principal ─────────────────────────────
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(pnl_border);
        pnl_border.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_header, javax.swing.GroupLayout.DEFAULT_SIZE,
                    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_spinner,  javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE)
                    .addComponent(lbl_titulo,   javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE)
                    .addComponent(prg_barra,    javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE)
                    .addComponent(txt_inf,      javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE, Short.MAX_VALUE)
                    .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE,
                            javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, 20))
        );
        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(pnl_header, javax.swing.GroupLayout.PREFERRED_SIZE, 36,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24)
                .addComponent(lbl_spinner,  javax.swing.GroupLayout.PREFERRED_SIZE, 32,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14)
                .addComponent(lbl_titulo,   javax.swing.GroupLayout.PREFERRED_SIZE, 40,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16)
                .addComponent(prg_barra,    javax.swing.GroupLayout.PREFERRED_SIZE, 6,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12)
                .addComponent(txt_inf,      javax.swing.GroupLayout.PREFERRED_SIZE, 20,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18)
                .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 32,
                        javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20)
        );

        // ── Layout raíz ────────────────────────────────────────────
        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(rootLayout);
        rootLayout.setHorizontalGroup(
            rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_border)
        );
        rootLayout.setVerticalGroup(
            rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_border)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {
        stopAnimations();
        System.exit(0);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton     btn_cancelar;
    private javax.swing.JLabel      lbl_header_dots;
    private javax.swing.JLabel      lbl_header_title;
    private javax.swing.JLabel      lbl_spinner;
    private javax.swing.JLabel      lbl_titulo;
    private javax.swing.JPanel      pnl_border;
    private javax.swing.JPanel      pnl_header;
    private javax.swing.JProgressBar prg_barra;
    public  static javax.swing.JLabel txt_inf;
    // End of variables declaration//GEN-END:variables
}
