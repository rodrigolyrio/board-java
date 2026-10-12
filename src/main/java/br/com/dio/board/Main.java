package br.com.dio.board;

import br.com.dio.board.config.MigrationRunner;
import br.com.dio.board.ui.MainMenu;

public class Main {

    public static void main(final String[] args) {
        MigrationRunner.run();
        new MainMenu().execute();
    }
}
