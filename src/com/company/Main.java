package com.company;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner("");
        int books = sc.nextInt();
        int libCount = sc.nextInt();
        int days = sc.nextInt();

        int[] bookScores = new int[books];
        for (int i = 0; i < books; i++) {
            bookScores[i] = sc.nextInt();
        }

        Library[] libraries = new Library[libCount];
        for (int i = 0; i < libCount; i++) {
            libraries[i] = readLibrary(sc);
        }

        Input input = new Input(libraries, days, bookScores);
    }

    public static Library readLibrary(Scanner sc) {
        int booksCount = sc.nextInt();
        int signUpProcess = sc.nextInt();
        int booksPerDay = sc.nextInt();

        Library library = new Library(booksCount, signUpProcess, booksPerDay);
        for (int i = 0; i < booksCount; i++) {
            library.books[i] = sc.nextInt();
        }
        return library;
    }

    public static class Input {
        Library[] libraries;
        int days;
        int[] bookScores;

        public Input(Library[] libraries, int days, int[] bookScores) {
            this.libraries = libraries;
            this.days = days;
            this.bookScores = bookScores;
        }
    }

    public static class Library {
        public int booksCount;
        public int signUpDays;
        public int booksPerDay;
        public int[] books;

        public Library(int booksCount, int signUpDays, int booksPerDay) {
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
