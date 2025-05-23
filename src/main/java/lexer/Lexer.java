package lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static lexer.Token.TokenType.*;

public class Lexer {
    private static final String BINARY_OPS = "+-*/%^";
    private static final Set<String> UNARY_OPS = Set.of("sin", "cos", "tan", "catan", "ln", "lg", "sqrt", "abs");
    private static final Set<String> CONSTANTS = Set.of("e", "pi");

    public static List<Token> tokenize(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        String refactoredExpression = expression
                .replace(",", ".")
                .replaceAll("\\s+", "");

        List<Token> tokens = new ArrayList<>();
        int length = refactoredExpression.length();
        int position = 0;

        while (position < length) {
            char currentChar = refactoredExpression.charAt(position);
            int currentPosition = position + 1;

            if (currentChar == '(') {
                tokens.add(new Token(LEFT_PARENTHESIS, "("));
                position++;
                continue;
            }

            if (currentChar == ')') {
                tokens.add(new Token(RIGHT_PARENTHESIS, ")"));
                position++;
                continue;
            }

            if (currentChar == '-' && (position == 0 ||
                    tokens.get(tokens.size() - 1).type() == LEFT_PARENTHESIS ||
                    tokens.get(tokens.size() - 1).type() == BINARY_OPERATION)) {
                tokens.add(new Token(UNARY_OPERATION, "-"));
                position++;
                continue;
            }

            if (BINARY_OPS.indexOf(currentChar) != -1) {
                tokens.add(new Token(BINARY_OPERATION, String.valueOf(currentChar)));
                position++;
                continue;
            }

            if (Character.isDigit(currentChar) || currentChar == '.') {
                StringBuilder number = new StringBuilder();
                boolean hasDot = false;
                while (position < length &&
                        (Character.isDigit(refactoredExpression.charAt(position)) ||
                                refactoredExpression.charAt(position) == '.')) {

                    if (refactoredExpression.charAt(position) == '.') {
                        if (hasDot) {
                            throw new IllegalArgumentException(
                                    String.format("Invalid number format: multiple dots at position %d", currentPosition)
                            );
                        }
                        hasDot = true;
                    }

                    number.append(refactoredExpression.charAt(position));
                    position++;
                    currentPosition = position + 1;
                }
                tokens.add(new Token(NUMBER, number.toString()));
                continue;
            }

            if (Character.isLetter(currentChar)) {
                StringBuilder var = new StringBuilder();
                while (position < length && Character.isLetter(refactoredExpression.charAt(position))) {
                    var.append(refactoredExpression.charAt(position));
                    position++;
                    currentPosition = position + 1;
                }
                String ident = var.toString().toLowerCase();

                if (UNARY_OPS.contains(ident)) {
                    tokens.add(new Token(UNARY_OPERATION, ident));
                    continue;
                }

                if (CONSTANTS.contains(ident)) {
                    tokens.add(new Token(CONSTANT, ident));
                    continue;
                }

                if (ident.length() == 1) {
                    tokens.add(new Token(VARIABLE, ident));
                } else {
                    throw new IllegalArgumentException(
                            String.format("Variables must be single-letter: '%s' at position %d", ident, currentPosition - ident.length())
                    );
                }
                continue;
            }

            throw new IllegalArgumentException(
                    String.format("Unknown character '%c' at position %d", currentChar, currentPosition)
            );
        }

        return tokens;
    }
}