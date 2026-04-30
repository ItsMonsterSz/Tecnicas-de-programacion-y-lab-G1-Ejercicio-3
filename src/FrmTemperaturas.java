import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import controladores.TemperaturaControlador;
import datechooser.beans.DateChooserCombo;
import modelos.RegistroTemperatura;
import servicios.TemperaturaServicio;

public class FrmTemperaturas extends JFrame {

    private JComboBox cmbCiudad;
    private DateChooserCombo dccDesde, dccHasta;
    private JTabbedPane tpTemperaturas;
    private JPanel pnlGrafica;
    private JPanel pnlEstadisticas;
    private JPanel pnlExtremos;

    private List<RegistroTemperatura> datos;

    public FrmTemperaturas() {

        setTitle("Temperaturas por Ciudad");
        setSize(700, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JToolBar tb = new JToolBar();

        JButton btnGraficar = new JButton();
        btnGraficar.setIcon(new ImageIcon(getClass().getResource("/iconos/Grafica.png")));
        btnGraficar.setToolTipText("Gráfica Temperatura vs Fecha");
        btnGraficar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnGraficarClick();
            }
        });
        tb.add(btnGraficar);

        JButton btnCalcularEstadisticas = new JButton();
        btnCalcularEstadisticas.setIcon(new ImageIcon(getClass().getResource("/iconos/Datos.png")));
        btnCalcularEstadisticas.setToolTipText("Estadísticas de la ciudad seleccionada");
        btnCalcularEstadisticas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnCalcularEstadisticasClick();
            }
        });
        tb.add(btnCalcularEstadisticas);

        JButton btnExtremos = new JButton("Extremos");
        btnExtremos.setToolTipText("Ciudad más/menos calurosa para la fecha de inicio");
        btnExtremos.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                btnExtremosClick();
            }
        });
        tb.add(btnExtremos);

        // Contenedor principal con BoxLayout vertical (igual que FrmCambios)
        JPanel pnlTemperaturas = new JPanel();
        pnlTemperaturas.setLayout(new BoxLayout(pnlTemperaturas, BoxLayout.Y_AXIS));

        JPanel pnlDatosProceso = new JPanel();
        pnlDatosProceso.setPreferredSize(new Dimension(pnlDatosProceso.getWidth(), 50));
        pnlDatosProceso.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pnlDatosProceso.setLayout(null);

        JLabel lblCiudad = new JLabel("Ciudad");
        lblCiudad.setBounds(10, 10, 100, 25);
        pnlDatosProceso.add(lblCiudad);

        cmbCiudad = new JComboBox();
        cmbCiudad.setBounds(110, 10, 100, 25);
        pnlDatosProceso.add(cmbCiudad);

        dccDesde = new DateChooserCombo();
        dccDesde.setBounds(220, 10, 100, 25);
        pnlDatosProceso.add(dccDesde);

        dccHasta = new DateChooserCombo();
        dccHasta.setBounds(330, 10, 100, 25);
        pnlDatosProceso.add(dccHasta);

        pnlGrafica = new JPanel();
        JScrollPane spGrafica = new JScrollPane(pnlGrafica);

        pnlEstadisticas = new JPanel();
        pnlExtremos = new JPanel();

        tpTemperaturas = new JTabbedPane();
        tpTemperaturas.addTab("Gráfica", spGrafica);
        tpTemperaturas.addTab("Estadísticas", pnlEstadisticas);
        tpTemperaturas.addTab("Extremos", pnlExtremos);

        pnlTemperaturas.add(pnlDatosProceso);
        pnlTemperaturas.add(tpTemperaturas);

        getContentPane().add(tb, BorderLayout.NORTH);
        getContentPane().add(pnlTemperaturas, BorderLayout.CENTER);

        cargarDatos();
    }

    private void cargarDatos() {
        String rutaDatos = System.getProperty("user.dir") + "/src/datos/Temperaturas.csv";
        datos = TemperaturaServicio.getDatos(rutaDatos);
        var ciudades = TemperaturaServicio.getCiudades(datos);
        cmbCiudad.setModel(new DefaultComboBoxModel(ciudades.toArray()));
    }

    private void btnGraficarClick() {
        if (cmbCiudad.getSelectedIndex() >= 0) {
            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            TemperaturaControlador.graficar(pnlGrafica, datos, ciudad, desde, hasta);
            tpTemperaturas.setSelectedIndex(0);
        }
    }

    private void btnCalcularEstadisticasClick() {
        if (cmbCiudad.getSelectedIndex() >= 0) {
            String ciudad = (String) cmbCiudad.getSelectedItem();
            LocalDate desde = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate hasta = dccHasta.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            TemperaturaControlador.getEstadisticas(pnlEstadisticas, datos, ciudad, desde, hasta);
            tpTemperaturas.setSelectedIndex(1);
        }
    }

    private void btnExtremosClick() {
        // Usa la fecha "desde" como fecha específica a consultar
        LocalDate fecha = dccDesde.getSelectedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        TemperaturaControlador.getExtremos(pnlExtremos, datos, fecha);
        tpTemperaturas.setSelectedIndex(2);
    }

}
