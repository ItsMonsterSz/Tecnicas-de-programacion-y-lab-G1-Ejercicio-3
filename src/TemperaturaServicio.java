package servicios;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import modelos.RegistroTemperatura;

public class TemperaturaServicio {

    public static List<RegistroTemperatura> getDatos(String nombreArchivo) {
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try {
            var lineas = Files.lines(Paths.get(nombreArchivo));

            return lineas.skip(1)
                    .map(linea -> linea.split(","))
                    .map(textos -> new RegistroTemperatura(textos[0],
                            LocalDate.parse(textos[1].trim(), formatoFecha),
                            Double.parseDouble(textos[2].trim())))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static List<String> getCiudades(List<RegistroTemperatura> registros) {
        return registros.stream()
                .map(RegistroTemperatura::getCiudad)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public static List<RegistroTemperatura> filtrar(List<RegistroTemperatura> registros,
            String ciudad, LocalDate desde, LocalDate hasta) {
        return registros.stream()
                .filter(r -> r.getCiudad().equals(ciudad) &&
                        !r.getFecha().isBefore(desde) && !r.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }

    public static Map<LocalDate, Double> getDatosGrafica(List<RegistroTemperatura> registros) {
        return registros.stream()
                .collect(Collectors.toMap(RegistroTemperatura::getFecha, RegistroTemperatura::getTemperatura));
    }

    public static List<Double> getValores(List<RegistroTemperatura> registros) {
        return registros.stream()
                .map(RegistroTemperatura::getTemperatura)
                .collect(Collectors.toList());
    }

    public static double getPromedio(List<Double> valores) {
        return valores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    public static double getDesviacionEstandar(List<Double> valores) {
        var promedio = getPromedio(valores);
        return valores.stream()
                .mapToDouble(valor -> Math.abs(promedio - valor))
                .average()
                .orElse(0.0);
    }

    public static double getMaximo(List<Double> valores) {
        return valores.stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);
    }

    public static double getMinimo(List<Double> valores) {
        return valores.stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);
    }

    public static double getMediana(List<Double> valores) {
        if (valores.isEmpty())
            return 0;

        var valoresOrdenados = valores.stream()
                .sorted()
                .collect(Collectors.toList());

        var n = valoresOrdenados.size();
        return n % 2 == 1 ? valoresOrdenados.get(n / 2)
                : (valoresOrdenados.get(n / 2 - 1) + valoresOrdenados.get(n / 2)) / 2;
    }

    public static double getModa(List<Double> valores) {
        if (valores.isEmpty())
            return 0;

        return valores.stream()
                .collect(Collectors.groupingBy(valor -> valor, Collectors.counting()))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(0.0);
    }

    public static Map<String, Double> getEstadisticas(List<Double> valores) {
        return Map.of(
                "Promedio", getPromedio(valores),
                "Desviación estándar", getDesviacionEstandar(valores),
                "Máximo", getMaximo(valores),
                "Mínimo", getMinimo(valores),
                "Mediana", getMediana(valores),
                "Moda", getModa(valores)
        );
    }

    // Funcionalidad extra del examen: ciudad más y menos calurosa en una fecha
    public static RegistroTemperatura getCiudadMasCalurosa(List<RegistroTemperatura> registros, LocalDate fecha) {
        return registros.stream()
                .filter(r -> r.getFecha().equals(fecha))
                .max(java.util.Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .orElse(null);
    }

    public static RegistroTemperatura getCiudadMenosCalurosa(List<RegistroTemperatura> registros, LocalDate fecha) {
        return registros.stream()
                .filter(r -> r.getFecha().equals(fecha))
                .min(java.util.Comparator.comparingDouble(RegistroTemperatura::getTemperatura))
                .orElse(null);
    }

}
