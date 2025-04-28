package Lexer;

import java.util.Objects;

public class Token {

    private final TokenType type;

    private final String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token(TokenType type) {
        this.type = type;
        this.value = null;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public enum TokenType {
        BINARY_OPERATION,
        UNARY_OPERATION,
        NUMBER,
        VARIABLE,
        CONSTANT,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS
    }

    @Override
    public String toString() {
        return "%s (%s)".formatted(value, type.toString());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Token token = (Token) object;
        return type == token.type && Objects.equals(value, token.value);
    }
}