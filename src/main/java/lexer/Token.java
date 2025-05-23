package lexer;

import java.util.Objects;

public record Token(TokenType type, String value) {

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

    public enum TokenType {
        BINARY_OPERATION,
        UNARY_OPERATION,
        NUMBER,
        VARIABLE,
        CONSTANT,
        LEFT_PARENTHESIS,
        RIGHT_PARENTHESIS
    }
}