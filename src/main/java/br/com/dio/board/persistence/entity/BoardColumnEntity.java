package br.com.dio.board.persistence.entity;

public class BoardColumnEntity {

    private Long id;
    private String name;
    private int order;
    private BoardColumnKindEnum kind;
    private Long boardId;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public BoardColumnKindEnum getKind() {
        return kind;
    }

    public void setKind(final BoardColumnKindEnum kind) {
        this.kind = kind;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(final Long boardId) {
        this.boardId = boardId;
    }
}
