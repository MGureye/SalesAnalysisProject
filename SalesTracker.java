import java.io.*;
import java.util.*;

public class SalesTracker {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter input CSV filename: ");
        String inputFile = scanner.nextLine();

        System.out.print("Enter output CSV filename (cleaned data): ");
        String outputFile = scanner.nextLine();

        List<Double> salesList = new ArrayList<>();
        Map<String, Double> productSales = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String header = br.readLine(); // read header line
            if (header != null) {
                bw.write(header);
                bw.newLine();
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");

                if (parts.length < 3) continue; // not enough columns
                String product = parts[1].trim();
                String salesStr = parts[2].trim();

                try {
                    // FIX: explicitly skip "NaN" text
                    if (salesStr.equalsIgnoreCase("NaN") || salesStr.isEmpty()) {
                        continue;
                    }

                    double sales = Double.parseDouble(salesStr);
                    salesList.add(sales);

                    productSales.put(product, productSales.getOrDefault(product, 0.0) + sales);

                    bw.write(parts[0] + "," + product + "," + sales);
                    bw.newLine();
                } catch (NumberFormatException e) {
                    // skip bad values
                }
            }

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (salesList.isEmpty()) {
            System.out.println("No valid sales data found.");
            return;
        }

        // Stats
        double total = salesList.stream().mapToDouble(Double::doubleValue).sum();
        double average = total / salesList.size();
        String topProduct = productSales.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        // Output
        System.out.println("\n--- Sales Summary ---");
        System.out.println("Total Sales: $" + String.format("%.2f", total));
        System.out.println("Average Sale: $" + String.format("%.2f", average));
        System.out.println("Top-Selling Product: " + topProduct);
        System.out.println("\nCleaned data saved to: " + outputFile);
    }
}

