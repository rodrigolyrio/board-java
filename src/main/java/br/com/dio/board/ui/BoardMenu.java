package br.com.dio.board.ui;

import br.com.dio.board.dto.BlockInfoDTO;
import br.com.dio.board.dto.BoardColumnInfoDTO;
import br.com.dio.board.dto.CardDetailsDTO;
import br.com.dio.board.dto.CardReportDTO;
import br.com.dio.board.dto.ColumnPassageDTO;
import br.com.dio.board.dto.ColumnWithCardsDTO;
import br.com.dio.board.exception.BusinessException;
import br.com.dio.board.exception.DatabaseException;
import br.com.dio.board.persistence.entity.BoardEntity;
import br.com.dio.board.persistence.entity.CardEntity;
import br.com.dio.board.service.BoardQueryService;
import br.com.dio.board.service.CardService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** Menu de um board selecionado: operações sobre cards e colunas. */
public class BoardMenu {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final BoardEntity board;
    private final CardService cardService = new CardService();
    private final BoardQueryService queryService = new BoardQueryService();

    public BoardMenu(final BoardEntity board) {
        this.board = board;
    }

    public void execute() {
        System.out.printf("%nBem-vindo ao board \"%s\" (id %d). Selecione a operação desejada.%n",
                board.getName(), board.getId());
        while (true) {
            System.out.println();
            System.out.println("===== BOARD: " + board.getName() + " =====");
            System.out.println("1 - Criar um card");
            System.out.println("2 - Mover um card para a próxima coluna");
            System.out.println("3 - Bloquear um card");
            System.out.println("4 - Desbloquear um card");
            System.out.println("5 - Cancelar um card");
            System.out.println("6 - Ver board");
            System.out.println("7 - Ver coluna com cards");
            System.out.println("8 - Ver card");
            System.out.println("9 - Relatório do card (tempo por coluna e bloqueios)");
            System.out.println("10 - Voltar ao menu anterior");
            System.out.println("11 - Sair");
            final int option = Input.integer("Escolha uma opção: ", 1, 11);
            try {
                switch (option) {
                    case 1 -> createCard();
                    case 2 -> moveCard();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> showReport();
                    case 10 -> {
                        return;
                    }
                    case 11 -> {
                        System.out.println("Até a próxima!");
                        System.exit(0);
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

    private void createCard() {
        final CardEntity card = new CardEntity();
        card.setTitle(Input.requiredText("Informe o título do card: "));
        card.setDescription(Input.requiredText("Informe a descrição do card: "));
        cardService.create(board.getId(), card);
        System.out.println("Card criado com sucesso (id " + card.getId() + ") na coluna inicial.");
    }

    private void moveCard() {
        final long cardId = Input.id("Informe o id do card que deseja mover para a próxima coluna: ");
        cardService.moveToNextColumn(board.getId(), cardId);
        final CardDetailsDTO card = queryService.findCard(board.getId(), cardId);
        System.out.printf("O card %d foi movido para a coluna \"%s\".%n", cardId, card.columnName());
    }

    private void blockCard() {
        final long cardId = Input.id("Informe o id do card que será bloqueado: ");
        final String reason = Input.text("Informe o motivo do bloqueio do card: ");
        cardService.block(board.getId(), cardId, reason);
        System.out.println("O card " + cardId + " foi bloqueado.");
    }

    private void unblockCard() {
        final long cardId = Input.id("Informe o id do card que será desbloqueado: ");
        final String reason = Input.text("Informe o motivo do desbloqueio do card: ");
        cardService.unblock(board.getId(), cardId, reason);
        System.out.println("O card " + cardId + " foi desbloqueado.");
    }

    private void cancelCard() {
        final long cardId = Input.id("Informe o id do card que deseja cancelar: ");
        cardService.cancel(board.getId(), cardId);
        System.out.println("O card " + cardId + " foi cancelado.");
    }

    private void showBoard() {
        final List<BoardColumnInfoDTO> columns = queryService.findColumns(board.getId());
        System.out.printf("Board [%d] %s%n", board.getId(), board.getName());
        columns.forEach(c -> System.out.printf("  Coluna [%d] %s - tipo: %s - %d card(s)%n",
                c.id(), c.name(), c.kind().getLabel(), c.cardsAmount()));
    }

    private void showColumn() {
        final List<BoardColumnInfoDTO> columns = queryService.findColumns(board.getId());
        System.out.println("Escolha uma coluna do board:");
        columns.forEach(c -> System.out.printf("  [%d] %s%n", c.id(), c.name()));
        final long columnId = Input.id("Informe o id da coluna: ");
        final ColumnWithCardsDTO column = queryService.findColumnWithCards(board.getId(), columnId);
        System.out.printf("Coluna \"%s\":%n", column.name());
        if (column.cards().isEmpty()) {
            System.out.println("  (sem cards)");
        }
        column.cards().forEach(card -> System.out.printf("  Card %d - %s%s%n    %s%n",
                card.id(), card.title(), card.blocked() ? " [BLOQUEADO]" : "", card.description()));
    }

    private void showCard() {
        final long cardId = Input.id("Informe o id do card: ");
        final CardDetailsDTO card = queryService.findCard(board.getId(), cardId);
        printCard(card);
    }

    private void showReport() {
        final long cardId = Input.id("Informe o id do card: ");
        final CardReportDTO report = queryService.buildCardReport(board.getId(), cardId);
        printCard(report.card());

        System.out.println("  --- Tempo em cada coluna ---");
        for (ColumnPassageDTO p : report.passages()) {
            final LocalDateTime end = p.leftAt() == null ? LocalDateTime.now() : p.leftAt();
            System.out.printf("  %s: entrou em %s, %s (permanência: %s)%n",
                    p.columnName(), DATE_FORMAT.format(p.enteredAt()),
                    p.leftAt() == null ? "ainda está nela" : "saiu em " + DATE_FORMAT.format(p.leftAt()),
                    formatDuration(Duration.between(p.enteredAt(), end)));
        }

        System.out.println("  --- Bloqueios ---");
        if (report.blocks().isEmpty()) {
            System.out.println("  Nenhum bloqueio registrado.");
        }
        for (BlockInfoDTO b : report.blocks()) {
            System.out.printf("  Bloqueado em %s. Motivo: %s%n", DATE_FORMAT.format(b.blockedAt()), b.blockReason());
            if (b.unblockedAt() == null) {
                System.out.println("    Ainda bloqueado.");
            } else {
                System.out.printf("    Desbloqueado em %s. Motivo: %s (ficou bloqueado por %s)%n",
                        DATE_FORMAT.format(b.unblockedAt()), b.unblockReason(),
                        formatDuration(Duration.between(b.blockedAt(), b.unblockedAt())));
            }
        }
    }

    private void printCard(final CardDetailsDTO card) {
        System.out.printf("Card %d - %s%n", card.id(), card.title());
        System.out.printf("  Descrição: %s%n", card.description());
        System.out.printf("  Coluna atual: %s (%s)%n", card.columnName(), card.columnKind().getLabel());
        System.out.printf("  Situação: %s%n", card.blocked() ? "BLOQUEADO" : "livre");
        System.out.printf("  Vezes que foi bloqueado: %d%n", card.blocksAmount());
    }

    private String formatDuration(final Duration duration) {
        final long hours = duration.toHours();
        final long minutes = duration.toMinutesPart();
        final long seconds = duration.toSecondsPart();
        return String.format("%dh %02dmin %02ds", hours, minutes, seconds);
    }
}
