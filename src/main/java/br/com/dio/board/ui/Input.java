package br.com.dio.board.ui;

import java.util.NoSuchElementException;
import java.util.Scanner;

/** Leitura segura do teclado: repete a pergunta até receber uma entrada válida. */
final class Input {

    private static final Scanner SCANNER = new Scanner(System.in);

    private Input() {
    }

    static String text(final String prompt) {
        System.out.print(prompt);
        try {
            return SCANNER.nextLine().trim();
        } catch (NoSuchElementException e) {
            // Entrada encerrada (Ctrl+D / fim do arquivo): sai sem stack trace.
            System.out.println();
            System.exit(0);
            return "";
        }
    }

    static String requiredText(final String prompt) {
        while (true) {
            final String value = text(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Este campo é obrigatório.");
        }
    }

    static int integer(final String prompt, final int min, final int max) {
        while (true) {
            final String line = text(prompt);
            try {
                final int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // cai na mensagem abaixo
            }
            System.out.printf("Entrada inválida. Digite um número entre %d e %d.%n", min, max);
        }
    }

    static long id(final String prompt) {
        while (true) {
            final String line = text(prompt);
            try {
                return Long.parseLong(line);
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite o número do id.");
            }
        }
    }
}
