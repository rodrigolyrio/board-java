package br.com.dio.board.ui;

import br.com.dio.board.exception.BusinessException;
import br.com.dio.board.exception.DatabaseException;
import br.com.dio.board.persistence.entity.BoardColumnEntity;
import br.com.dio.board.persistence.entity.BoardColumnKindEnum;
import br.com.dio.board.persistence.entity.BoardEntity;
import br.com.dio.board.service.BoardQueryService;
import br.com.dio.board.service.BoardService;

import java.util.ArrayList;
import java.util.List;

/** Menu principal: criar, selecionar e excluir boards. */
public class MainMenu {

    private final BoardService boardService = new BoardService();
    private final BoardQueryService queryService = new BoardQueryService();

    public void execute() {
        System.out.println("Bem-vindo ao gerenciador de boards!");
        while (true) {
            System.out.println();
            System.out.println("========== MENU PRINCIPAL ==========");
            System.out.println("1 - Criar um novo board");
            System.out.println("2 - Selecionar um board existente");
            System.out.println("3 - Excluir um board");
            System.out.println("4 - Listar boards");
            System.out.println("5 - Sair");
            final int option = Input.integer("Escolha uma opção: ", 1, 5);
            try {
                switch (option) {
                    case 1 -> createBoard();
                    case 2 -> selectBoard();
                    case 3 -> deleteBoard();
                    case 4 -> listBoards();
                    case 5 -> {
                        System.out.println("Até a próxima!");
                        return;
                    }
                    default -> System.out.println("Opção inválida.");
                }
            } catch (BusinessException e) {
                System.out.println("Atenção: " + e.getMessage());
            } catch (DatabaseException e) {
                System.out.println("Erro no banco de dados: " + e.getMessage());
            }
        }
    }

    private void createBoard() {
        final BoardEntity board = new BoardEntity();
        board.setName(Input.requiredText("Informe o nome do board: "));

        final int pendingAmount = Input.integer(
                "Seu board terá colunas \"pendentes\" além das 3 padrão (inicial, final e cancelamento)? "
                        + "Informe quantas (0 a 10): ", 0, 10);

        final List<BoardColumnEntity> columns = new ArrayList<>();
        columns.add(column(Input.requiredText("Nome da coluna INICIAL: "), 0, BoardColumnKindEnum.INITIAL));
        for (int i = 1; i <= pendingAmount; i++) {
            columns.add(column(Input.requiredText("Nome da coluna PENDENTE " + i + ": "), i,
                    BoardColumnKindEnum.PENDING));
        }
        columns.add(column(Input.requiredText("Nome da coluna FINAL: "), pendingAmount + 1,
                BoardColumnKindEnum.FINAL));
        columns.add(column(Input.requiredText("Nome da coluna de CANCELAMENTO: "), pendingAmount + 2,
                BoardColumnKindEnum.CANCEL));
        board.setColumns(columns);

        boardService.insert(board);
        System.out.printf("Board \"%s\" criado com sucesso (id %d).%n", board.getName(), board.getId());
    }

    private void selectBoard() {
        final long id = Input.id("Informe o id do board que deseja selecionar: ");
        final BoardEntity board = queryService.findById(id).orElse(null);
        if (board == null) {
            System.out.println("Não foi encontrado um board com o id " + id + ".");
            return;
        }
        new BoardMenu(board).execute();
    }

    private void deleteBoard() {
        final long id = Input.id("Informe o id do board que será excluído: ");
        final BoardEntity board = queryService.findById(id).orElse(null);
        if (board == null) {
            System.out.println("Não foi encontrado um board com o id " + id + ".");
            return;
        }
        final String answer = Input.text("Excluir \"" + board.getName()
                + "\" apagará também todas as colunas e cards. Confirma? (sim/não): ").toLowerCase();
        if (answer.equals("sim") || answer.equals("s")) {
            boardService.delete(id);
            System.out.println("O board " + id + " foi excluído.");
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    private void listBoards() {
        final List<BoardEntity> boards = queryService.findAll();
        if (boards.isEmpty()) {
            System.out.println("Nenhum board cadastrado ainda.");
            return;
        }
        boards.forEach(b -> System.out.printf("[%d] %s%n", b.getId(), b.getName()));
    }

    private BoardColumnEntity column(final String name, final int order, final BoardColumnKindEnum kind) {
        final BoardColumnEntity column = new BoardColumnEntity();
        column.setName(name);
        column.setOrder(order);
        column.setKind(kind);
        return column;
    }
}
