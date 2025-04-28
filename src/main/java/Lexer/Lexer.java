package Lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static Lexer.Token.TokenType.*;

public class Lexer {

    private static final String BINARY_OPS = "+-*/%^";
    private static final Set<String> UNARY_OPS = Set.of("sin", "cos", "tan", "catan", "atan", "acatan", "ln", "lg", "sqrt", "abs");
    private static final Set<String> CONSTANTS = Set.of("e", "pi");

    public static List<Token> tokenize(String expression) {
        String refactoredExpression = expression
                .replace(",", ".")
                .replaceAll("\\s+", "");

        List<Token> tokens = new ArrayList<>();
        int length = refactoredExpression.length();
        int position = 0;

        while (position < length) {
            char currentChar = refactoredExpression.charAt(position);

            if ("(".indexOf(currentChar) != -1) {
                tokens.add(new Token(LEFT_PARENTHESIS, "("));
                position++;
                continue;
            }

            if (")".indexOf(currentChar) != -1) {
                tokens.add(new Token(RIGHT_PARENTHESIS, ")"));
                position++;
                continue;
            }

            if ("-".indexOf(currentChar) != -1
                    && (position == 0
                    || "(".indexOf(refactoredExpression.charAt(position - 1)) != -1
                    || BINARY_OPS.indexOf(refactoredExpression.codePointAt(position - 1)) != -1)) {
                tokens.add(new Token(UNARY_OPERATION, "-"));
                position++;
                continue;
            }

            if (BINARY_OPS.indexOf(currentChar) != -1) {
                tokens.add(new Token(BINARY_OPERATION, String.valueOf(currentChar)));
                position++;
                continue;
            }

            if (Character.isDigit(currentChar)
                    || ".".indexOf(currentChar) != -1) {
                StringBuilder number = new StringBuilder();
                while (Character.isDigit(currentChar)
                        || ".".indexOf(currentChar) != -1) {
                    number.append(currentChar);
                    if (++position >= length) break;
                    currentChar = refactoredExpression.charAt(position);
                }
                tokens.add(new Token(NUMBER, number.toString()));
                continue;
            }

            if (Character.isLetterOrDigit(currentChar)) {
                StringBuilder var = new StringBuilder();
                while (Character.isLetterOrDigit(currentChar)) {
                    var.append(currentChar);
                    if (++position >= length) break;
                    currentChar = refactoredExpression.charAt(position);
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
                tokens.add(new Token(VARIABLE, ident));
                continue;
            }

            throw new IllegalArgumentException("Unknown character: " + currentChar);
        }

        return tokens;
    }

}