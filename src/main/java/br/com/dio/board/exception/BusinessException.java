package br.com.dio.board.exception;

/** Violação de uma regra de negócio (mensagem pode ser exibida ao usuário). */
public class BusinessException extends RuntimeException {

    public BusinessException(final String message) {
        super(message);
    }
}
