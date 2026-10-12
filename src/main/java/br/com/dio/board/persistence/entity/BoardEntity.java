package br.com.dio.board.persistence.entity;

import java.util.ArrayList;
import java.util.List;

public class BoardEntity {

    private Long id;
    private String name;
    private List<BoardColumnEntity> columns = new ArrayList<>();

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

    public List<BoardColumnEntity> getColumns() {
        return columns;
    }

    public void setColumns(final List<BoardColumnEntity> columns) {
        this.columns = columns;
    }
}
