import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.Gson;

public class ConversorMonedas {
    private static String API_KEY;
    private static String BASE_URL;

    public static void main(String[] args) throws Exception {
        API_KEY = new String(Files.readAllBytes(Paths.get("apikey.txt"))).trim();
        BASE_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

        Scanner sc = new Scanner(System.in);
        String continuar = "";
        String monedaBase = null;
        String monedaDestino = null;
        while (!continuar.equalsIgnoreCase("salir")) {
            monedaBase = null;
            monedaDestino = null;
            // Mostrar opciones y pedir moneda base
            while (monedaBase == null) {
                System.out.println("Seleccione la moneda que desea convertir:");
                System.out.println("1. USD\n2. ARS\n3. EUR\n4. JPY\n5. BRL");
                System.out.print("Opción: ");
                monedaBase = opcionAMoneda(sc.nextLine());
                if (monedaBase == null) System.out.println("Opción inválida.");
            }

            // Pedir moneda destino
            while (monedaDestino == null) {
                System.out.println("Seleccione la moneda destino:");
                System.out.println("1. USD\n2. ARS\n3. EUR\n4. JPY\n5. BRL");
                System.out.print("Opción: ");
                monedaDestino = opcionAMoneda(sc.nextLine());
                if (monedaDestino == null) System.out.println("Opción inválida.");
            }

            // Pedir cantidad
            System.out.print("Ingrese cantidad a convertir: ");
            double cantidad = sc.nextDouble();
            sc.nextLine(); // limpiar el salto de línea pendiente

            // ... Acá va el código de conversión con API ...


        System.out.printf("Convertir %.2f %s a %s\n", cantidad, monedaBase, monedaDestino);

        // Consultar API y hacer la conversión
        String url = BASE_URL + monedaBase;

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Gson gson = new Gson();
            ApiResponse apiResponse = gson.fromJson(response.body(), ApiResponse.class);

            if ("success".equals(apiResponse.result)) {
                if (apiResponse.conversion_rates.containsKey(monedaDestino)) {
                    double tasa = apiResponse.conversion_rates.get(monedaDestino);
                    double resultado = cantidad * tasa;

                    System.out.printf("%.2f %s equivalen a %.2f %s\n", cantidad, monedaBase, resultado, monedaDestino);
                } else {
                    System.out.println("Moneda destino no encontrada en la API.");
                }
            } else {
                System.out.println("Error en la respuesta de la API.");
            }
        } else {
            System.out.println("Error al llamar a la API. Código: " + response.statusCode());
        }

            // Preguntar si quiere seguir
            System.out.println("\n¿Deseás hacer otra conversión? Escribí 'salir' para terminar o Enter para continuar:");
            continuar = sc.nextLine();
        }
        sc.close();
        System.out.println( "Gracias por utilizar el sistema de conversion de monedas");
    }

    private static String opcionAMoneda(String opcion) {
        return switch (opcion) {
            case "1" -> "USD";
            case "2" -> "ARS";
            case "3" -> "EUR";
            case "4" -> "JPY";
            case "5" -> "BRL";
            default -> null;
        };
    }


    private static class ApiResponse {
        String result;
        Map<String, Double> conversion_rates;
    }
}
