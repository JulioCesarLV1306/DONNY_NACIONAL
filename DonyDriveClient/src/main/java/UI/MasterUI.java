package UI;

import service.DriveService;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import javax.swing.DefaultListModel;
import model.Bitacora;
import model.Modulo;
import model.Drive;
import model.Usuario;
import service.BitacoraService;
import service.UsuarioService;
import util.Constants;
import util.Util;

/**
 * @author Diego Baes - Modernized UI
 */
public class MasterUI extends javax.swing.JFrame {

    private final DriveService driveService;
    BitacoraService bitacoraService;
    private static int contadorPeticiones = 0;

    private final DefaultListModel listModel;
    private static Modulo modulo;

    public MasterUI(Modulo modulo) {
        this.driveService = new DriveService();
        this.bitacoraService = new BitacoraService();
        this.modulo = modulo;
        
        initComponents();
        
        // --- FIX VISUAL FORZADO PARA EL BOTÓN DESCONECTAR ---
        // Esto le quita el renderizado nativo de Windows y lo hace completamente plano
        btn_desconectar.setBackground(new java.awt.Color(255, 255, 255)); // Fondo blanco absoluto
        btn_desconectar.setForeground(new java.awt.Color(111, 1, 0));     // Texto rojo oscuro
        btn_desconectar.setContentAreaFilled(false);                      // Apaga el botón 3D de Windows
        btn_desconectar.setOpaque(true);                                  // Obliga a pintar el fondo blanco
        btn_desconectar.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1)); // Quita el borde gris por defecto
        // ----------------------------------------------------

        this.listModel = new DefaultListModel();
        initConfig();
        cargarDiscos();
        cargarHoraInicioSesion();
    }

    private void initConfig() {
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("TERMINAL DONNY " + Constants.VERSION_CLIENT);
        pnl_root.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff), 2));
        txt_ipclient.setText(modulo.getCPcIp());
        txt_nombreMaquina.setText(modulo.getCPcUsuario());
        lbl_version.setText(Constants.VERSION_CLIENT);
    }

    private void cargarDiscos() {
        List<Drive> lista = driveService.getListaDiscos();
        for (Drive drive : lista) {
            String label = drive.getNombre();
            listModel.addElement(label);
        }
        lst_discos.setModel(listModel);
    }

    private void cargarHoraInicioSesion() {
        txt_datesesion.setText(Util.getCurrentDate());
    }

    public static void aumentarContadorPeticiones() {
        contadorPeticiones++;
        txt_npeticiones.setText(String.valueOf(contadorPeticiones));
    }

    public static Modulo conectarModo() {
        txt_conexion.setText("\u25CF  CONECTADO");
        txt_conexion.setForeground(new Color(0xffffff));
        txt_conexion.setBackground(new Color(0x6f0100));
        txt_conexion.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));
        return modulo;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnl_root = new javax.swing.JPanel();
        pnl_header = new javax.swing.JPanel();
        lbl_header_dots = new javax.swing.JLabel();
        lbl_header_title = new javax.swing.JLabel();
        lbl_version = new javax.swing.JLabel();
        pnl_identity = new javax.swing.JPanel();
        lbl_maq_label = new javax.swing.JLabel();
        txt_nombreMaquina = new javax.swing.JLabel();
        lbl_ip_label = new javax.swing.JLabel();
        txt_ipclient = new javax.swing.JLabel();
        pnl_status = new javax.swing.JPanel();
        txt_conexion = new javax.swing.JLabel();
        pnl_metrics = new javax.swing.JPanel();
        pnl_card_sesion = new javax.swing.JPanel();
        lbl_sesion_label = new javax.swing.JLabel();
        txt_datesesion = new javax.swing.JLabel();
        lbl_sesion_sub = new javax.swing.JLabel();
        pnl_card_usb = new javax.swing.JPanel();
        lbl_usb_label = new javax.swing.JLabel();
        txt_npeticiones = new javax.swing.JLabel();
        lbl_usb_sub = new javax.swing.JLabel();
        pnl_drives = new javax.swing.JPanel();
        pnl_drives_header = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lst_discos = new javax.swing.JList<>();
        pnl_footer = new javax.swing.JPanel();
        lbl_sesion_info = new javax.swing.JLabel();
        btn_desconectar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setPreferredSize(new java.awt.Dimension(720, 460));
        getContentPane().setBackground(new Color(0x880201));

        pnl_root.setBackground(new Color(0x880201));
        pnl_root.setPreferredSize(new java.awt.Dimension(720, 460));

        pnl_header.setBackground(new Color(0x6f0100));
        pnl_header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xffffff)));

        lbl_header_dots.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        lbl_header_dots.setForeground(new Color(0xffffff));
        lbl_header_dots.setText("● ● ●");

        lbl_header_title.setFont(new java.awt.Font("Consolas", 1, 13)); // NOI18N
        lbl_header_title.setForeground(new Color(0xffffff));
        lbl_header_title.setText("TERMINAL DONNY");

        lbl_version.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        lbl_version.setForeground(new Color(0xffffff));
        lbl_version.setText("v5.0");
        lbl_version.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));

        javax.swing.GroupLayout pnl_headerLayout = new javax.swing.GroupLayout(pnl_header);
        pnl_header.setLayout(pnl_headerLayout);
        pnl_headerLayout.setHorizontalGroup(
            pnl_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_headerLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lbl_header_dots)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_header_title)
                .addGap(8, 8, 8)
                .addComponent(lbl_version)
                .addGap(14, 14, 14))
        );
        pnl_headerLayout.setVerticalGroup(
            pnl_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lbl_header_dots)
                .addComponent(lbl_header_title)
                .addComponent(lbl_version))
        );

        pnl_identity.setBackground(new Color(0x6f0100));
        pnl_identity.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));

        lbl_maq_label.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        lbl_maq_label.setForeground(new Color(0xffffff));
        lbl_maq_label.setText("MODULO / MAQUINA");

        txt_nombreMaquina.setFont(new java.awt.Font("Consolas", 1, 18)); // NOI18N
        txt_nombreMaquina.setForeground(new Color(0xffffff));
        txt_nombreMaquina.setText("PC-NOMBRE");

        lbl_ip_label.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        lbl_ip_label.setForeground(new Color(0xffffff));
        lbl_ip_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_ip_label.setText("DIRECCION IP");

        txt_ipclient.setFont(new java.awt.Font("Consolas", 1, 16)); // NOI18N
        txt_ipclient.setForeground(new Color(0xffffff));
        txt_ipclient.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        txt_ipclient.setText("0.0.0.0");

        javax.swing.GroupLayout pnl_identityLayout = new javax.swing.GroupLayout(pnl_identity);
        pnl_identity.setLayout(pnl_identityLayout);
        pnl_identityLayout.setHorizontalGroup(
            pnl_identityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_identityLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnl_identityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_maq_label)
                    .addComponent(txt_nombreMaquina))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnl_identityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_ip_label)
                    .addComponent(txt_ipclient))
                .addGap(12, 12, 12))
        );
        pnl_identityLayout.setVerticalGroup(
            pnl_identityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_identityLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnl_identityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_maq_label)
                    .addComponent(lbl_ip_label))
                .addGap(3, 3, 3)
                .addGroup(pnl_identityLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_nombreMaquina)
                    .addComponent(txt_ipclient))
                .addGap(6, 6, 6))
        );

        pnl_status.setBackground(new Color(0x880201));

        txt_conexion.setBackground(new Color(0x6f0100));
        txt_conexion.setFont(new java.awt.Font("Consolas", 1, 14)); // NOI18N
        txt_conexion.setForeground(new Color(0xffffff));
        txt_conexion.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        txt_conexion.setText("●  DESCONECTADO");
        txt_conexion.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));
        txt_conexion.setOpaque(true);

        javax.swing.GroupLayout pnl_statusLayout = new javax.swing.GroupLayout(pnl_status);
        pnl_status.setLayout(pnl_statusLayout);
        pnl_statusLayout.setHorizontalGroup(
            pnl_statusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_statusLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txt_conexion, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_statusLayout.setVerticalGroup(
            pnl_statusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_statusLayout.createSequentialGroup()
                .addGap(4, 4, 4)
                .addComponent(txt_conexion, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        pnl_metrics.setBackground(new Color(0x880201));

        pnl_card_sesion.setBackground(new Color(0x6f0100));
        pnl_card_sesion.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));

        lbl_sesion_label.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        lbl_sesion_label.setForeground(new Color(0xffffff));
        lbl_sesion_label.setText("INICIO DE SESION");

        txt_datesesion.setFont(new java.awt.Font("Consolas", 1, 22)); // NOI18N
        txt_datesesion.setForeground(new Color(0xffffff));
        txt_datesesion.setText("00:00");

        lbl_sesion_sub.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        lbl_sesion_sub.setForeground(new Color(0xffffff));
        lbl_sesion_sub.setText("Hoy");

        javax.swing.GroupLayout pnl_card_sesionLayout = new javax.swing.GroupLayout(pnl_card_sesion);
        pnl_card_sesion.setLayout(pnl_card_sesionLayout);
        pnl_card_sesionLayout.setHorizontalGroup(
            pnl_card_sesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_card_sesionLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnl_card_sesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_sesion_label)
                    .addComponent(txt_datesesion)
                    .addComponent(lbl_sesion_sub))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_card_sesionLayout.setVerticalGroup(
            pnl_card_sesionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_card_sesionLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbl_sesion_label)
                .addGap(3, 3, 3)
                .addComponent(txt_datesesion)
                .addGap(2, 2, 2)
                .addComponent(lbl_sesion_sub)
                .addGap(6, 6, 6))
        );

        pnl_card_usb.setBackground(new Color(0x6f0100));
        pnl_card_usb.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));

        lbl_usb_label.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        lbl_usb_label.setForeground(new Color(0xffffff));
        lbl_usb_label.setText("PETICIONES USB");

        txt_npeticiones.setFont(new java.awt.Font("Consolas", 1, 22)); // NOI18N
        txt_npeticiones.setForeground(new Color(0xffffff));
        txt_npeticiones.setText("0");

        lbl_usb_sub.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        lbl_usb_sub.setForeground(new Color(0xffffff));
        lbl_usb_sub.setText("En esta sesion");

        javax.swing.GroupLayout pnl_card_usbLayout = new javax.swing.GroupLayout(pnl_card_usb);
        pnl_card_usb.setLayout(pnl_card_usbLayout);
        pnl_card_usbLayout.setHorizontalGroup(
            pnl_card_usbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_card_usbLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnl_card_usbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_usb_label)
                    .addComponent(txt_npeticiones)
                    .addComponent(lbl_usb_sub))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_card_usbLayout.setVerticalGroup(
            pnl_card_usbLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_card_usbLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(lbl_usb_label)
                .addGap(3, 3, 3)
                .addComponent(txt_npeticiones)
                .addGap(2, 2, 2)
                .addComponent(lbl_usb_sub)
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout pnl_metricsLayout = new javax.swing.GroupLayout(pnl_metrics);
        pnl_metrics.setLayout(pnl_metricsLayout);
        pnl_metricsLayout.setHorizontalGroup(
            pnl_metricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_metricsLayout.createSequentialGroup()
                .addComponent(pnl_card_sesion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(pnl_card_usb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_metricsLayout.setVerticalGroup(
            pnl_metricsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_card_sesion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnl_card_usb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pnl_drives.setBackground(new Color(0x6f0100));
        pnl_drives.setBorder(javax.swing.BorderFactory.createLineBorder(new Color(0xffffff)));

        pnl_drives_header.setBackground(new Color(0x5f0000));
        pnl_drives_header.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0xffffff)));

        jLabel3.setFont(new java.awt.Font("Consolas", 0, 11)); // NOI18N
        jLabel3.setForeground(new Color(0xffffff));
        jLabel3.setText("DISCOS DUROS DETECTADOS");

        javax.swing.GroupLayout pnl_drives_headerLayout = new javax.swing.GroupLayout(pnl_drives_header);
        pnl_drives_header.setLayout(pnl_drives_headerLayout);
        pnl_drives_headerLayout.setHorizontalGroup(
            pnl_drives_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_drives_headerLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnl_drives_headerLayout.setVerticalGroup(
            pnl_drives_headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3)
        );

        jScrollPane1.setBackground(new Color(0x880201));
        jScrollPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        lst_discos.setBackground(new Color(0x880201));
        lst_discos.setFont(new java.awt.Font("Consolas", 0, 13)); // NOI18N
        lst_discos.setForeground(new Color(0xffffff));
        lst_discos.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 8, 4, 8));
        lst_discos.setSelectionBackground(new Color(0x6f0100));
        lst_discos.setSelectionForeground(new Color(0xffffff));
        lst_discos.setFixedCellHeight(32);
        lst_discos.setFocusable(false);
        jScrollPane1.setViewportView(lst_discos);

        javax.swing.GroupLayout pnl_drivesLayout = new javax.swing.GroupLayout(pnl_drives);
        pnl_drives.setLayout(pnl_drivesLayout);
        pnl_drivesLayout.setHorizontalGroup(
            pnl_drivesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_drives_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnl_drivesLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1)
                .addGap(6, 6, 6))
        );
        pnl_drivesLayout.setVerticalGroup(
            pnl_drivesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_drivesLayout.createSequentialGroup()
                .addComponent(pnl_drives_header, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4))
        );

        pnl_footer.setBackground(new Color(0x880201));

        lbl_sesion_info.setFont(new java.awt.Font("Consolas", 0, 11)); // NOI18N
        lbl_sesion_info.setForeground(new Color(0xffffff));
        lbl_sesion_info.setText("Sesion activa");

        btn_desconectar.setBackground(new Color(0xffffff));
        btn_desconectar.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btn_desconectar.setForeground(new Color(0x6f0100));
        btn_desconectar.setText("DESCONECTAR");
        btn_desconectar.setContentAreaFilled(false);
        btn_desconectar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btn_desconectar.setFocusPainted(false);
        btn_desconectar.setOpaque(true);
        btn_desconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_desconectarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnl_footerLayout = new javax.swing.GroupLayout(pnl_footer);
        pnl_footer.setLayout(pnl_footerLayout);
        pnl_footerLayout.setHorizontalGroup(
            pnl_footerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_footerLayout.createSequentialGroup()
                .addComponent(lbl_sesion_info)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_desconectar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        pnl_footerLayout.setVerticalGroup(
            pnl_footerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_footerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(lbl_sesion_info)
                .addComponent(btn_desconectar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout pnl_rootLayout = new javax.swing.GroupLayout(pnl_root);
        pnl_root.setLayout(pnl_rootLayout);
        pnl_rootLayout.setHorizontalGroup(
            pnl_rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnl_rootLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnl_rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnl_identity, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_metrics, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_drives, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnl_footer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        pnl_rootLayout.setVerticalGroup(
            pnl_rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_rootLayout.createSequentialGroup()
                .addComponent(pnl_header, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnl_identity, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnl_status, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnl_metrics, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnl_drives, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(pnl_footer, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_root, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_root, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_desconectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_desconectarActionPerformed
        btn_desconectar.setEnabled(false);
        
        // Crear/obtener usuario del módulo
        UsuarioService usuarioService = new UsuarioService();
        String dniModulo = "MODULO_" + modulo.getCPcUsuario();
        String nombreModulo = "Módulo " + modulo.getXDescripcion();
        Usuario usuario = usuarioService.createIfNotExists(dniModulo, nombreModulo);
        
        Bitacora bitacora = new Bitacora();
        bitacora.setCIpModulo(modulo.getCPcIp());
        bitacora.setNIdUsuario(usuario.getNIdUsuario());
        bitacora.setTDescripcionAccion("EL MODULO " + modulo.getCPcUsuario() + " (" + modulo.getCPcIp() + ") FINALIZO CON EXITO EL CLIENTE");
        bitacora.setCCodigoAccion("LOGOUT_MODULO");
        bitacora.setCAudUid(dniModulo);
        bitacoraService.create(bitacora);
        System.exit(0);
    }//GEN-LAST:event_btn_desconectarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_desconectar;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lbl_header_dots;
    private javax.swing.JLabel lbl_header_title;
    private javax.swing.JLabel lbl_ip_label;
    private javax.swing.JLabel lbl_maq_label;
    private javax.swing.JLabel lbl_sesion_info;
    private javax.swing.JLabel lbl_sesion_label;
    private javax.swing.JLabel lbl_sesion_sub;
    private javax.swing.JLabel lbl_usb_label;
    private javax.swing.JLabel lbl_usb_sub;
    private javax.swing.JLabel lbl_version;
    private javax.swing.JList<String> lst_discos;
    private javax.swing.JPanel pnl_card_sesion;
    private javax.swing.JPanel pnl_card_usb;
    private javax.swing.JPanel pnl_drives;
    private javax.swing.JPanel pnl_drives_header;
    private javax.swing.JPanel pnl_footer;
    private javax.swing.JPanel pnl_header;
    private javax.swing.JPanel pnl_identity;
    private javax.swing.JPanel pnl_metrics;
    private javax.swing.JPanel pnl_root;
    private javax.swing.JPanel pnl_status;
    private static javax.swing.JLabel txt_conexion;
    private javax.swing.JLabel txt_datesesion;
    private javax.swing.JLabel txt_ipclient;
    private javax.swing.JLabel txt_nombreMaquina;
    private static javax.swing.JLabel txt_npeticiones;
    // End of variables declaration//GEN-END:variables
}