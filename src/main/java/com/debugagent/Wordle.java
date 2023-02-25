package com.debugagent;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.util.*;

public class Wordle {
    private static final String WORD = "LYMPH";
    private static List<String> DICTIONARY;
    private static List<String> words = new ArrayList<>();
    private static JsonAdapter<List<State>> listJsonAdapter;
    private static List<State> status;
    public static void main(String[] args) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type type = Types.newParameterizedType(List.class, State.class);
        listJsonAdapter = moshi.adapter(type);
        File statusFile = new File("state.json");
        if(statusFile.exists()) {
            status = listJsonAdapter.fromJson(Files.readString(statusFile.toPath()));
        } else {
            status = new ArrayList<>();
        }
        DICTIONARY = Files.readAllLines(new File("words.txt").toPath());
        DICTIONARY = DICTIONARY.stream().map(String::toUpperCase).toList();

        System.out.println("Write a guess:");
        Scanner scanner = new Scanner(System.in);
        int attempts = 0;
        for(String line = scanner.nextLine() ; line != null ; line = scanner.nextLine()) {
            if(line.length() != 5) {
                System.out.println("5 characters only... Please try again:");
                continue;
            }
            line = line.toUpperCase();
            if(line.equals(WORD)) {
                System.out.println("Success!!!");
                words.add(line);
                finishGame();
            }
            if(!DICTIONARY.contains(line)) {
                System.out.println("Word not in dictionary... Try again:");
            } else {
                words.add(line);
                attempts++;
                printWordResult(line);
                if(attempts > 7) {
                    System.out.println("Game over!");
                    finishGame();
                }
            }
        }
    }

    private static void finishGame() throws IOException {
        status.add(new State(WORD, System.currentTimeMillis(), words));
        String json = listJsonAdapter.toJson(status);
        Files.writeString(new File("state.json").toPath(), json);
        System.exit(0);
    }


    private static void printWordResult(String word) {
        for(int iter = 0 ; iter < word.length() ; iter++) {
            char currentChar = word.charAt(iter);
            if(currentChar == WORD.charAt(iter)) {
                System.out.print("G"); // Green
                continue;
            }
            if(WORD.indexOf(currentChar) > -1) {
                System.out.print("Y"); // Yellow
                continue;
            }
            System.out.print("B"); // Black
        }
        System.out.println();
    }
}