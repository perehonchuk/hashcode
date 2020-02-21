package com.company;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class Main {

    public static final String FILE_NAME = "c_incunabula";
    public static int[] bookScores;

    public static void main(String[] args) {
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(FILE_NAME + ".txt");
        Scanner sc = new Scanner(inputStream);
        int books = sc.nextInt();
        int libCount = sc.nextInt();
        int days = sc.nextInt();

        bookScores = new int[books];
        for (int i = 0; i < books; i++) {
            bookScores[i] = sc.nextInt();
        }

        SetLibrary[] libraries = new SetLibrary[libCount];
        for (int i = 0; i < libCount; i++) {
            libraries[i] = readLibraryToSet(sc, bookScores, i);
        }

        Input input = new Input(libraries, days, bookScores);
        solveCDynProg(input);
    }

    public static void solveCDynProg(Input input) {
        StringBuilder stringBuilder = new StringBuilder();
        int totalLibs = 0;

        int[][] dp = new int[input.setLibraries.length + 1][input.days + 1];
        for (int i = 0; i < input.days; i++) {
            dp[0][i] = 0;
        }

        for (int i = 1; i <= input.setLibraries.length; i++) {
            for (int j = 0; j <= input.days; j++) {
                SetLibrary setLibrary = input.setLibraries[i - 1];
                if (setLibrary.signUpDays + 1 < j) {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i - 1][j - setLibrary.signUpDays] + setLibrary.libraryScore);
                } else {
                    dp[i][j] = dp[i - 1][j];
                }
            }
        }
        int res = dp[input.setLibraries.length][input.days];

        int elem = input.setLibraries.length;
        int days = input.days;
        int total = 0;
        while (elem > 0 && res > 0) {
            if (dp[elem - 1][days] == res) {
                elem--;
            } else {
                totalLibs++;
                SetLibrary lib = input.setLibraries[elem - 1];
                stringBuilder.append(lib.id)
                        .append(" ")
                        .append(lib.booksCount)
                        .append("\n");
                stringBuilder.append(
                        lib.books.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(" ")
                                )
                );
                stringBuilder.append("\n");
                elem--;
                res -= lib.libraryScore;
                days -= lib.signUpDays;
                total += lib.libraryScore;
            }
        }

        stringBuilder = new StringBuilder().append(totalLibs).append("\n").append(stringBuilder);

        try {
            Files.write(Paths.get(FILE_NAME + ".out"), stringBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(Arrays.toString(dp[input.setLibraries.length]));
        System.out.println(total);
    }

    public static void solveC(Input input) {
        StringBuilder stringBuilder = new StringBuilder();
        int totalLibs = 0;
        int daysRemaining = input.days;
        Arrays.sort(input.setLibraries, (o1, o2) -> {
            if (o1.signUpDays < o2.signUpDays) {
                return -1;
            } else if (o1.signUpDays > o2.signUpDays) {
                return 1;
            } else {
                return Integer.compare(o2.calcScore(), o1.calcScore());
            }
        });
        for (int i = 0; i < input.setLibraries.length; i++) {
            SetLibrary lib = input.setLibraries[i];
            if (lib.signUpDays + 1 <= daysRemaining) {
                totalLibs++;
                stringBuilder.append(lib.id)
                        .append(" ")
                        .append(lib.booksCount)
                        .append("\n");
                stringBuilder.append(
                        lib.books.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining(" ")
                                )
                );
                stringBuilder.append("\n");
                for (int j = i + 1; j < input.setLibraries.length; j++) {
                    SetLibrary libInternal = input.setLibraries[j];
                    libInternal.deleteBooks(lib.books);
                }
                daysRemaining -= lib.signUpDays;
                Arrays.sort(input.setLibraries, i, input.setLibraries.length, (o1, o2) -> {
                    if (o1.signUpDays < o2.signUpDays) {
                        return -1;
                    } else if (o1.signUpDays > o2.signUpDays) {
                        return 1;
                    } else {
                        return Integer.compare(o2.calcScore(), o1.calcScore());
                    }
                });
            }

        }

        stringBuilder = new StringBuilder().append(totalLibs).append("\n").append(stringBuilder);

        try {
            Files.write(Paths.get(FILE_NAME + ".out"), stringBuilder.toString().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public static SetLibrary readLibraryToSet(Scanner sc, int[] bookScores, int id) {
        int booksCount = sc.nextInt();
        int signUpProcess = sc.nextInt();
        int booksPerDay = sc.nextInt();
        int libraryScore = 0;

        SetLibrary library = new SetLibrary(id, booksCount, signUpProcess, booksPerDay);
        for (int i = 0; i < booksCount; i++) {
            int bookId = sc.nextInt();
            library.books.add(bookId);
            libraryScore += bookScores[bookId];
        }
        library.libraryScore = libraryScore;

        return library;
    }

    public static class Input {
        public Library[] libraries;
        public SetLibrary[] setLibraries;
        public int days;
        public int[] bookScores;

        public Input(Library[] libraries, int days, int[] bookScores) {
            this.libraries = libraries;
            this.days = days;
            this.bookScores = bookScores;
        }

        public Input(SetLibrary[] libraries, int days, int[] bookScores) {
            this.setLibraries = libraries;
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

    public static class SetLibrary {
        public int id;
        public int booksCount;
        public int signUpDays;
        public int booksPerDay;
        public Set<Integer> books;
        public int libraryScore = 0;

        public SetLibrary(int id, int booksCount, int signUpDays, int booksPerDay) {
            this.id = id;
            this.booksCount = booksCount;
            this.signUpDays = signUpDays;
            this.booksPerDay = booksPerDay;
            books = new HashSet<>();
        }

        public void deleteBooks(Set<Integer> bookIds) {
            books.removeAll(bookIds);
            this.booksCount = books.size();
        }

        public int calcScore() {
            libraryScore = 0;
            books.forEach(i -> libraryScore += bookScores[i]);
            return libraryScore;
        }
    }

    public static class Output {
        int signedLibCount;
    }
}
