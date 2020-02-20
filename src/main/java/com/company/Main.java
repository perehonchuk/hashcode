package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class Main {

    public static final String FILE_NAME = "b_read_on";

    public static void main(String[] args) {

        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(FILE_NAME + ".txt");
        Scanner sc = new Scanner(inputStream);
        int books = sc.nextInt();
        int libCount = sc.nextInt();
        int days = sc.nextInt();

        int[] bookScores = new int[books];
        for (int i = 0; i < books; i++) {
            bookScores[i] = sc.nextInt();
        }

        Library[] libraries = new Library[libCount];
        for (int i = 0; i < libCount; i++) {
            libraries[i] = readLibrary(sc, bookScores, i);
        }

        Input input = new Input(libraries, days, bookScores);
        solveB(input);
    }

    public static void solveC(Input input) {

    }

    public static void solveB(Input input) {
        Arrays.sort(input.libraries, Comparator.comparingInt(l -> l.signUpDays));

        int daysLeft = input.days;
        int currentLib = 0;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(input.libraries.length)
                .append("\n");
        for (Library lib: input.libraries) {
            stringBuilder.append(lib.id)
                    .append(" ")
                    .append(lib.booksCount)
                    .append("\n");
            stringBuilder.append(
                    stream(lib.books)
                            .mapToObj(String::valueOf)
                            .collect(Collectors.joining(" ")
                            )
            );
            stringBuilder
                    .append("\n");
        }

        try {
            Files.write(Paths.get(FILE_NAME + ".out"), stringBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void solve(Input input) {
        Arrays.sort(input.libraries, Comparator.comparingInt(l -> l.libraryScore));
        int daysLeft = input.days;
        int librariesCount = 0;
        Map<Integer, List<Integer>> librariesToSignup = new LinkedHashMap<>();
        while((daysLeft > 0) && (librariesCount < input.libraries.length)) {
            Library currentLibrary = input.libraries[librariesCount];
            daysLeft -= currentLibrary.signUpDays;
            int countBooksToScan = (daysLeft * currentLibrary.booksPerDay);
            if (daysLeft > 0) {
                List<Integer> booksToScan = stream(currentLibrary.books)
                        .boxed()
                        .sorted(Comparator.comparingInt(b -> input.bookScores[b]))
                        .limit(countBooksToScan)
                        .collect(Collectors.toList());

                librariesToSignup.put(currentLibrary.id, booksToScan);

                librariesCount++;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(librariesToSignup.size())
                .append("\n");
        for (Map.Entry<Integer, List<Integer>> libraryEntry : librariesToSignup.entrySet()) {
            List<Integer> books = libraryEntry.getValue();
            stringBuilder.append(libraryEntry.getKey())
                    .append(" ")
                    .append(books.size())
                    .append("\n");
            for (Integer book : books) {
                stringBuilder.append(book)
                        .append(" ");
            }
            stringBuilder.append("\n");

        }

        try {
            Files.write(Paths.get(FILE_NAME + ".out"), stringBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static Library readLibrary(Scanner sc, int[] bookScores, int id) {
        int booksCount = sc.nextInt();
        int signUpProcess = sc.nextInt();
        int booksPerDay = sc.nextInt();
        int libraryScore = 0;

        Library library = new Library(id, booksCount, signUpProcess, booksPerDay);
        for (int i = 0; i < booksCount; i++) {
            library.books[i] = sc.nextInt();
            libraryScore += bookScores[library.books[i]];
        }
        library.libraryScore = libraryScore;

        return library;
    }

    public static class Input {
        public Library[] libraries;
        public int days;
        public int[] bookScores;

        public Input(Library[] libraries, int days, int[] bookScores) {
            this.libraries = libraries;
            this.days = days;
            this.bookScores = bookScores;
        }
    }

    public static class Library {
        public int id;
        public int booksCount;
        public int signUpDays;
        public int booksPerDay;
        public int[] books;
        public int libraryScore = 0;

        public Library(int id, int booksCount, int signUpDays, int booksPerDay) {
            this.id = id;
            this.booksCount = booksCount;
            this.signUpDays = signUpDays;
            this.booksPerDay = booksPerDay;
            books = new int[booksCount];
        }
    }

    public static class Output {
        int signedLibCount;
    }
}
