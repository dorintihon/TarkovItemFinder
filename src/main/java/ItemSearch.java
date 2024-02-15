import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner; // Import the Scanner class

public class ItemSearch {

    public static void main(String[] args) {
        String url = "https://escapefromtarkov.fandom.com/wiki/Loot"; // The URL of the page to check

        Scanner scanner = new Scanner(System.in); // Create a Scanner object

        String itemName = scanner.nextLine();; // Initial item name to search for
        try {
            Document doc = Jsoup.connect(url).get();
            Elements rows = doc.select("tr:has(th:contains(" + itemName + "))");

            if (rows.size() > 1) {
                // More than one row found, ask the user to specify which one they want
                System.out.println("Multiple items found for " + itemName + ". Please select one:");
                int index = 1;
                for (Element row : rows) {
                    System.out.println(index++ + ": " + row.select("th").select("a").attr("title"));
                }

                int choice = scanner.nextInt(); // Read user input

                // Validate user input
                if (choice < 1 || choice > rows.size()) {
                    System.out.println("Invalid selection.");
                    return; // Exit if the selection is invalid
                }

                // Process the selected row
                Element selectedRow = rows.get(choice - 1);
                processRow(selectedRow, itemName);

            } else if (rows.size() == 1) {
                // Only one row found, process it
                processRow(rows.first(), itemName);
            } else {
                // No rows found
                System.out.println("Item " + itemName + " not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processRow(Element row, String itemName) {
        // Find all `th` elements in this row
        Elements ths = row.select("th");
        Elements notes = row.select("td");

        String auxiliaryCraftingColor = ths.get(2).attr("style");
        String craftingColor = ths.get(3).attr("style");
        String barterColor = ths.get(4).attr("style");
        String name = notes.select("b").text();

        System.out.println("For " + ths.getFirst().select("a").attr("title") + ":");
        System.out.println("Is Auxiliary Crafting: " + extractColor(auxiliaryCraftingColor));
        System.out.println("Is Crafting: " + extractColor(craftingColor));
        System.out.println("Is Barter: " + extractColor(barterColor));

        if(!name.isEmpty()){
            System.out.println("Notes:\n" + name);
        }

    }

    private static String extractColor(String style) {
        if (style.contains("color: red")) {
            return "NO";
        } else if (style.contains("color: green")) {
            return "YES";
        }
        return "none"; // Default return if no color is found
    }
}
