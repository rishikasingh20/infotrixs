import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class CurrencyConverter {
    private static Map<String, String> currencyCodes = new HashMap<>();
    private static List<String> favoriteCurrencies = new ArrayList<>();
    private static final String API_KEY = "bd4cbcd507339c4d6ccead6e3dd8969f"; // Replace with your API key

    public static void main(String[] args) throws IOException {
        fetchCurrencyCodes();

        Scanner sc = new Scanner(System.in);

        System.out.println("Welcome to the real-time currency converter");
        while (true) {
            System.out.println("Options:");
            System.out.println("1. Show all currencies");
            System.out.println("2. Convert currency");
            System.out.println("3. Add favorite currency");
            System.out.println("4. Update favorite currency");
            System.out.println("5. Show favorite currencies");
            System.out.println("6. Exit");
            System.out.print("Select an option (1-6): ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    showAllCurrencies();
                    break;
                case 2:
                    convertCurrency(sc);
                    break;
                case 3:
                    addFavoriteCurrency(sc);
                    break;
                case 4:
                    updateFavoriteCurrency(sc);
                    break;
                case 5:
                    showFavoriteCurrencies();
                    break;
                case 6:
                    System.out.println("Thank you for using the currency converter");
                    System.exit(0);
                default:
                    System.out.println("Invalid option. Please select a valid option (1-6).");
            }
        }
    }

    private static void fetchCurrencyCodes() throws IOException {
        String API_URL = "http://api.exchangerate.host/list?access_key=bd4cbcd507339c4d6ccead6e3dd8969f";

        try {
            URL url = new URL(API_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    JSONObject obj = new JSONObject(response.toString());

                    if (obj.has("currencies")) {
                        JSONObject currencies = obj.getJSONObject("currencies");

                        for (String code : currencies.keySet()) {
                            String currencyName = currencies.getString(code);
                            currencyCodes.put(code, currencyName);
                        }
                    }
                }
            } else {
                System.out.println("Failed to fetch currency codes. Check your API key and URL.");
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error while fetching currency codes: " + e.getMessage());
        }
    }


    private static void showAllCurrencies() {
        System.out.println("All available currencies:");
        for (Map.Entry<String, String> entry : currencyCodes.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    private static void convertCurrency(Scanner sc) {
        System.out.println("Currency converting FROM?");
        String fromCode = sc.nextLine();

        System.out.println("Currency converting TO?");
        String toCode = sc.nextLine();

        if (currencyCodes.containsKey(fromCode) && currencyCodes.containsKey(toCode)) {
            System.out.println("Amount you wish to convert?");
            double amount = sc.nextDouble();
            sc.nextLine(); // Consume newline

            double exchangeRate = 1.0; // Default to 1 in case of API failure

            // Make the API call to retrieve the exchange rate
            try {
                String API_URL = "http://api.exchangerate.host/convert?access_key=" + API_KEY +
                        "&from=" + fromCode + "&to=" + toCode + "&amount=" + amount;

                URL url = new URL(API_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");

                int responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader in = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()))) {
                        StringBuilder response = new StringBuilder();
                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }

                        JSONObject obj = new JSONObject(response.toString());

                        if (obj.has("result")) {
                            exchangeRate = obj.getDouble("result");
                        }
                    }
                } else {
                    System.out.println("Failed to fetch exchange rate. Using default rate of 1.");
                }
            } catch (IOException | JSONException e) {
                System.out.println("Error while fetching exchange rate: " + e.getMessage());
            }

            double convertedAmount = amount * exchangeRate;
            System.out.println(amount + " " + fromCode + " = " + convertedAmount + " " + toCode);
        } else {
            System.out.println("Invalid currency codes.");
        }
    }

    private static void addFavoriteCurrency(Scanner sc) {
        System.out.println("Enter the currency code you want to add to favorites:");
        String code = sc.nextLine();
        if (currencyCodes.containsKey(code)) {
            favoriteCurrencies.add(code);
            System.out.println(code + " added to favorites.");
        } else {
            System.out.println("Invalid currency code.");
        }
    }

    private static void updateFavoriteCurrency(Scanner sc) {
        System.out.println("Enter the currency code you want to update in favorites:");
        String code = sc.nextLine();
        if (currencyCodes.containsKey(code)) {
            if (favoriteCurrencies.contains(code)) {
                favoriteCurrencies.remove(code);
                System.out.println(code + " removed from favorites.");
            } else {
                favoriteCurrencies.add(code);
                System.out.println(code + " added to favorites.");
            }
        } else {
            System.out.println("Invalid currency code.");
        }
    }

    private static void showFavoriteCurrencies() {
        System.out.println("Favorite currencies:");
        for (String code : favoriteCurrencies) {
            String name = currencyCodes.get(code);
            if (name != null) {
                System.out.println(code + ": " + name);
            }
        }
    }
}
