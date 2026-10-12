package br.com.dio.board.persistence.entity;

public class CardEntity {

    private Long id;
    private String title;
    private String description;
    private Long boardColumnId;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Long getBoardColumnId() {
        return boardColumnId;
    }

    public void setBoardColumnId(final Long boardColumnId) {
        this.boardColumnId = boardColumnId;
    }
}
