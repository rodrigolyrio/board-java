package br.com.dio.board.exception;

public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(final String message) {
        super(message);
    }
}
