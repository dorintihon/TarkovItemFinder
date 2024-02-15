import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Scanner; // Import the Scanner class

public class ColorExtractor {

    public static void main(String[] args) {
        String url = "https://escapefromtarkov.fandom.com/wiki/Loot"; // The URL of the page to check

        Scanner scanner = new Scanner(System.in); // Create a Scanner object

        String itemName = scanner.nextLine(); // Initial item name to search for
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
                processRow(selectedRow);

            } else if (rows.size() == 1) {
                // Only one row found, process it
                processRow(rows.first());
            } else {
                // No rows found
                System.out.println("Item " + itemName + " not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processRow(Element row) {
        // Find all `th` elements in this row
//         Elements rows = doc.select("tr:has(th:contains(" + itemName + "))");
        Elements ths = row.select("th");
        Element notes = row.selectFirst("td");

        String auxiliaryCraftingColor = ths.get(2).attr("style");
        String craftingColor = ths.get(3).attr("style");
        String barterColor = ths.get(4).attr("style");

        System.out.println("For " + ths.getFirst().select("a").attr("title") + ":");
        System.out.println("Is Auxiliary Crafting: " + extractColor(auxiliaryCraftingColor));
        System.out.println("Is Crafting: " + extractColor(craftingColor));
        System.out.println("Is Barter: " + extractColor(barterColor));

        printInfoFromTd(notes);

    }

    public static void printInfoFromTd(Element td) {
        // Select all <b> elements directly under the <td> (not inside <p>)
        Element b = td.selectFirst(" b");

        if(b != null){
            // Get the corresponding <ul> for each <b>
            Element ul = b.nextElementSibling();
            if (ul != null && ul.tagName().equals("ul")) {
                printListItems(b.text(), ul);
            }
        }


        // Select all <b> elements inside <p> under the <td>
        Elements boldsInsideP = td.select("p > b");

        for (Element bInP : boldsInsideP) {
            if(bInP.parent() != null){
                // The <ul> for these would be right after the <p>
                Element ul2 = bInP.parent().nextElementSibling();
                if (ul2 != null && ul2.tagName().equals("ul")) {
                    printListItems(bInP.text(), ul2);
                }
            }
        }
    }

    public static void printListItems(String label, Element ul) {
        Elements listItems = ul.select("li");
        System.out.println("\n" + label + ":");
        for (Element listItem : listItems) {
            String text = listItem.text();
            System.out.println(" - " + text);
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
