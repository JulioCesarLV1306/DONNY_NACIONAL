package UI;

import java.awt.Color;
import java.awt.Font;
import javax.swing.Timer;

/**
 * Pantalla de carga renovada: Fondo blanco con acentos en rojo institucional.
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

    // Paleta de colores DONNY Modern
    private final Color RED_DONNY = new Color(0x820000); // Rojo institucional
    private final Color WHITE_BG = new Color(0xFFFFFF);  // Blanco puro
    private final Color GRAY_TEXT = new Color(0x666666); // Gris para info secundaria

    public LoadingUI() {
        initComponents();
        initConfig();
        startAnimations();
    }

    private void initConfig() {
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        // Borde exterior sutil en rojo
        pnl_border.setBorder(javax.swing.BorderFactory.createLineBorder(RED_DONNY, 1));
    }

    private void startAnimations() {
        spinnerTimer = new Timer(100, e -> {
            lbl_spinner.setText(SPINNER_FRAMES[spinnerFrame % SPINNER_FRAMES.length]);
            spinnerFrame++;
        });
        spinnerTimer.start();

        msgTimer = new Timer(2500, e -> {
            msgIndex = (msgIndex + 1) % STATUS_MSGS.length;
            txt_inf.setText(STATUS_MSGS[msgIndex]);
        });
        msgTimer.start();
    }

    public void stopAnimations() {
        if (spinnerTimer != null) spinnerTimer.stop();
        if (msgTimer != null) msgTimer.stop();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        pnl_border = new javax.swing.JPanel();
        pnl_header = new javax.swing.JPanel();
        lbl_header_dots = new javax.swing.JLabel();
        lbl_header_title = new javax.swing.JLabel();
        lbl_spinner = new javax.swing.JLabel();
        lbl_titulo = new javax.swing.JLabel();
        prg_barra = new javax.swing.JProgressBar();
        txt_inf = new javax.swing.JLabel();
        btn_cancelar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setType(java.awt.Window.Type.UTILITY);
        getContentPane().setBackground(WHITE_BG);

        // -- Panel Principal --
        pnl_border.setBackground(WHITE_BG);
        pnl_border.setPreferredSize(new java.awt.Dimension(520, 280));

        // -- Header (Rojo sólido) --
        pnl_header.setBackground(RED_DONNY);
        
        lbl_header_dots.setFont(new Font("Consolas", Font.PLAIN, 18));
        lbl_header_dots.setForeground(WHITE_BG);
        lbl_header_dots.setText("● ● ●");

        lbl_header_title.setFont(new Font("Consolas", Font.BOLD, 11));
        lbl_header_title.setForeground(WHITE_BG);
        lbl_header_title.setText("TERMINAL DONNY — INIT");

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(pnl_header);
        pnl_header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headerLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lbl_header_dots)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 250, Short.MAX_VALUE)
                .addComponent(lbl_header_title)
                .addGap(14, 14, 14))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
            .addComponent(lbl_header_dots, 36, 36, 36)
            .addComponent(lbl_header_title, 36, 36, 36)
        );

        // -- Spinner (Rojo) --
        lbl_spinner.setFont(new Font("Consolas", Font.PLAIN, 32));
        lbl_spinner.setForeground(RED_DONNY);
        lbl_spinner.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // -- Título (Rojo) --
        lbl_titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lbl_titulo.setForeground(RED_DONNY);
        lbl_titulo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_titulo.setText("CONECTANDO...");

        // -- Barra de Progreso (Fondo blanco, frente rojo) --
        prg_barra.setIndeterminate(true);
        prg_barra.setBackground(new Color(0xF2F2F2));
        prg_barra.setForeground(RED_DONNY);
        prg_barra.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xDDDDDD)));

        // -- Texto de Información (Gris oscuro) --
        txt_inf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txt_inf.setForeground(GRAY_TEXT);
        txt_inf.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        // -- Botón Cancelar (Outline Rojo) --
        btn_cancelar.setBackground(WHITE_BG);
        btn_cancelar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn_cancelar.setForeground(RED_DONNY);
        btn_cancelar.setText("CANCELAR");
        btn_cancelar.setBorder(javax.swing.BorderFactory.createLineBorder(RED_DONNY));
        btn_cancelar.setFocusPainted(false);
        btn_cancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // -- Layout General --
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(pnl_border);
        pnl_border.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_spinner, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_titulo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(prg_barra, javax.swing.GroupLayout.DEFAULT_SIZE, 440, Short.MAX_VALUE)
                    .addComponent(txt_inf, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40))
            .addGroup(javax.swing.GroupLayout.Alignment.CENTER, layout.createSequentialGroup()
                .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(pnl_header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(lbl_spinner)
                .addGap(10, 10, 10)
                .addComponent(lbl_titulo)
                .addGap(20, 20, 20)
                .addComponent(prg_barra, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(txt_inf)
                .addGap(25, 25, 25)
                .addComponent(btn_cancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(rootLayout);
        rootLayout.setHorizontalGroup(rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnl_border, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        rootLayout.setVerticalGroup(rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnl_border, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }

    private void btn_cancelarActionPerformed(java.awt.event.ActionEvent evt) {
        stopAnimations();
        System.exit(0);
    }

    private javax.swing.JButton btn_cancelar;
    private javax.swing.JLabel lbl_header_dots;
    private javax.swing.JLabel lbl_header_title;
    private javax.swing.JLabel lbl_spinner;
    private javax.swing.JLabel lbl_titulo;
    private javax.swing.JPanel pnl_border;
    private javax.swing.JPanel pnl_header;
    private javax.swing.JProgressBar prg_barra;
    public static javax.swing.JLabel txt_inf;
}