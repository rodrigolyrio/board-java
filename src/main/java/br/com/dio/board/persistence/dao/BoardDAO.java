package br.com.dio.board.persistence.dao;

import br.com.dio.board.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BoardDAO {

    private final Connection connection;

    public BoardDAO(final Connection connection) {
        this.connection = connection;
    }

    public void insert(final BoardEntity entity) throws SQLException {
        final String sql = "INSERT INTO boards (name) VALUES (?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                }
            }
        }
    }

    public boolean delete(final Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("DELETE FROM boards WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM boards WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(map(rs)) : Optional.empty();
            }
        }
    }

    public boolean existsByName(final String name) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("SELECT 1 FROM boards WHERE name = ?")) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<BoardEntity> findAll() throws SQLException {
        final List<BoardEntity> boards = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("SELECT id, name FROM boards ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                boards.add(map(rs));
            }
        }
        return boards;
    }

    private BoardEntity map(final ResultSet rs) throws SQLException {
        final BoardEntity board = new BoardEntity();
        board.setId(rs.getLong("id"));
        board.setName(rs.getString("name"));
        return board;
    }
}
