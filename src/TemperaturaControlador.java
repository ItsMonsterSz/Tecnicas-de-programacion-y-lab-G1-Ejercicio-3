package controladores;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import modelos.RegistroTemperatura;
import servicios.TemperaturaServicio;

public class TemperaturaControlador {

    public static void graficar(JPanel pnlGrafica,
            List<RegistroTemperatura> registros,
            String ciudad, LocalDate desde, LocalDate hasta) {

        var datosFiltrados = TemperaturaServicio.filtrar(registros, ciudad, desde, hasta);
        var datosGrafica = TemperaturaServicio.getDatosGrafica(datosFiltrados);

        TimeSeries serie = new TimeSeries(ciudad);
        datosGrafica.forEach((fecha, valor) -> {
            var fechaSerie = new Day(fecha.getDayOfMonth(), fecha.getMonthValue(), fecha.getYear());
            serie.add(fechaSerie, valor);
        });

        TimeSeriesCollection series = new TimeSeriesCollection();
        series.addSeries(serie);

        JFreeChart graficador = ChartFactory.createTimeSeriesChart(
                "Temperatura (°C) de " + ciudad + " vs Fecha",
                "Fecha",
                "Temperatura (°C)",
                series);

        ChartPanel pnlGraficador = new ChartPanel(graficador);
        pnlGraficador.setPreferredSize(new Dimension(500, 300));

        pnlGrafica.removeAll();
        pnlGrafica.setLayout(new BorderLayout());
        pnlGrafica.add(pnlGraficador, BorderLayout.CENTER);
        pnlGrafica.revalidate();
    }

    public static void getEstadisticas(JPanel pnlEstadisticas,
            List<RegistroTemperatura> registros,
            String ciudad, LocalDate desde, LocalDate hasta) {

        var datosFiltrados = TemperaturaServicio.filtrar(registros, ciudad, desde, hasta);
        var valores = TemperaturaServicio.getValores(datosFiltrados);
        var estadisticas = TemperaturaServicio.getEstadisticas(valores);

        pnlEstadisticas.setLayout(new GridBagLayout());
        pnlEstadisticas.removeAll();

        var gbc = new GridBagConstraints();
        gbc.gridy = 0;
        estadisticas.forEach((clave, valor) -> {
            gbc.gridx = 0;
            pnlEstadisticas.add(new JLabel(clave), gbc);
            gbc.gridx = 1;
            pnlEstadisticas.add(new JLabel(String.format("%.2f °C", valor)), gbc);
            gbc.gridy++;
        });
        pnlEstadisticas.revalidate();
    }

    public static void getExtremos(JPanel pnlEstadisticas,
            List<RegistroTemperatura> registros,
            LocalDate fecha) {

        var masCalurosa = TemperaturaServicio.getCiudadMasCalurosa(registros, fecha);
        var menosCalurosa = TemperaturaServicio.getCiudadMenosCalurosa(registros, fecha);

        pnlEstadisticas.setLayout(new GridBagLayout());
        pnlEstadisticas.removeAll();

        var gbc = new GridBagConstraints();
        gbc.gridy = 0;

        gbc.gridx = 0;
        pnlEstadisticas.add(new JLabel("Fecha consultada:"), gbc);
        gbc.gridx = 1;
        pnlEstadisticas.add(new JLabel(fecha.toString()), gbc);
        gbc.gridy++;

        if (masCalurosa != null) {
            gbc.gridx = 0;
            pnlEstadisticas.add(new JLabel("Ciudad más calurosa:"), gbc);
            gbc.gridx = 1;
            pnlEstadisticas.add(new JLabel(
                    masCalurosa.getCiudad() + "  (" + String.format("%.1f", masCalurosa.getTemperatura()) + " °C)"), gbc);
            gbc.gridy++;
        }

        if (menosCalurosa != null) {
            gbc.gridx = 0;
            pnlEstadisticas.add(new JLabel("Ciudad menos calurosa:"), gbc);
            gbc.gridx = 1;
            pnlEstadisticas.add(new JLabel(
                    menosCalurosa.getCiudad() + "  (" + String.format("%.1f", menosCalurosa.getTemperatura()) + " °C)"), gbc);
            gbc.gridy++;
        }

        if (masCalurosa == null && menosCalurosa == null) {
            gbc.gridx = 0;
            pnlEstadisticas.add(new JLabel("Sin datos para esa fecha."), gbc);
        }

        pnlEstadisticas.revalidate();
    }

}
