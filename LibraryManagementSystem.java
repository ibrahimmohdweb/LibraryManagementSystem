import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.util.*;

class Book {
    private String id;
    private String title;
    private String author;
    private int quantity;

    public Book(String id, String title, String author, int quantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getQuantity() {
        return quantity;
    }

    public String toFileString() {
        return id + "|" + title + "|" + author + "|" + quantity;
    }

    public void display() {
        System.out.println("Book ID  : " + id);
        System.out.println("Title    : " + title);
        System.out.println("Author   : " + author);
        System.out.println("Quantity : " + quantity);
        System.out.println("---------------------------");
    }
}

public class LibraryManagementSystem {

    private static final Scanner sc = new Scanner(System.in);
    private static final ArrayList<Book> books = new ArrayList<>();
    private static final String FILE_NAME = "books.txt";

    public static void main(String[] args) {
        loadBooks();

        while (true) {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Search Book");
            System.out.println("4. Display All Books");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    addBook();
                    break;
                case "2":
                    removeBook();
                    break;
                case "3":
                    searchBook();
                    break;
                case "4":
                    displayBooks();
                    break;
                case "5":
                    saveBooks();
                    System.out.println("Thank you for using the system!");
                    sc.close();
                    return;
                default:
                    System.out.println(
                        "Invalid choice! Enter a number from 1 to 5."
                    );
            }
        }
    }

    private static void addBook() {
        System.out.print("Enter Book ID: ");
        String id = sc.nextLine().trim();

        if (id.isEmpty() || id.contains("|")) {
            System.out.println("Invalid or empty Book ID.");
            return;
        }

        for (Book book : books) {
            if (book.getId().equalsIgnoreCase(id)) {
                System.out.println("Book ID already exists!");
                return;
            }
        }

        System.out.print("Enter Book Title: ");
        String title = sc.nextLine().trim();

        System.out.print("Enter Author Name: ");
        String author = sc.nextLine().trim();

        if (title.isEmpty() || author.isEmpty()
                || title.contains("|") || author.contains("|")) {
            System.out.println("Title and author must be valid.");
            return;
        }

        System.out.print("Enter Quantity: ");
        String input = sc.nextLine().trim();

        try {
            int quantity = Integer.parseInt(input);

            if (quantity <= 0) {
                System.out.println("Quantity must be greater than zero.");
                return;
            }

            books.add(new Book(id, title, author, quantity));

            if (saveBooks()) {
                System.out.println("Book added successfully!");
            } else {
                books.remove(books.size() - 1);
                System.out.println("Could not save book. Operation cancelled.");
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid quantity! Enter a whole number.");
        }
    }

    private static void removeBook() {
        System.out.print("Enter Book ID to remove: ");
        String id = sc.nextLine().trim();

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);

            if (book.getId().equalsIgnoreCase(id)) {
                books.remove(i);

                if (saveBooks()) {
                    System.out.println("Book removed successfully!");
                } else {
                    books.add(i, book);
                    System.out.println("Could not save changes. Removal cancelled.");
                }
                return;
            }
        }

        System.out.println("Book not found!");
    }

    private static void searchBook() {
        System.out.print("Enter Book ID, Title, or Author: ");
        String query = sc.nextLine().trim().toLowerCase(Locale.ROOT);

        if (query.isEmpty()) {
            System.out.println("Search input cannot be empty.");
            return;
        }

        boolean found = false;

        for (Book book : books) {
            if (book.getId().toLowerCase(Locale.ROOT).contains(query)
                    || book.getTitle().toLowerCase(Locale.ROOT).contains(query)
                    || book.getAuthor().toLowerCase(Locale.ROOT).contains(query)) {
                book.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    private static void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("The library is empty.");
            return;
        }

        System.out.println("\n===== All Books =====");

        for (Book book : books) {
            book.display();
        }
    }

    private static boolean saveBooks() {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(FILE_NAME))) {

            for (Book book : books) {
                writer.println(book.toFileString());
            }

            return true;

        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
            return false;
        }
    }

    private static void loadBooks() {
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader =
                new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split("\\|", -1);

                    if (data.length != 4
                            || data[0].trim().isEmpty()
                            || data[1].trim().isEmpty()
                            || data[2].trim().isEmpty()) {
                        System.out.println("Skipping invalid book record.");
                        continue;
                    }

                    int quantity = Integer.parseInt(data[3]);

                    if (quantity <= 0) {
                        System.out.println("Skipping invalid book quantity.");
                        continue;
                    }

                    boolean duplicate = false;

                    for (Book book : books) {
                        if (book.getId().equalsIgnoreCase(data[0])) {
                            duplicate = true;
                            break;
                        }
                    }

                    if (!duplicate) {
                        books.add(new Book(
                            data[0], data[1], data[2], quantity
                        ));
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Skipping record with invalid quantity.");
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }
}