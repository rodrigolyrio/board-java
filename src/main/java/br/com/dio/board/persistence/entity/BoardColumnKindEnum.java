package br.com.dio.board.persistence.entity;

public enum BoardColumnKindEnum {

    INITIAL("Inicial"),
    PENDING("Pendente"),
    FINAL("Final"),
    CANCEL("Cancelamento");

    private final String label;

    BoardColumnKindEnum(final String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
