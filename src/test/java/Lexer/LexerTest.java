package Lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static Lexer.Token.TokenType.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LexerTest {

    @Test
    void testBasicBinaryOperations() {
        assertTokens("1 + 2", List.of(
                token(NUMBER, "1"), token(BINARY_OPERATION, "+"), token(NUMBER, "2")));

        assertTokens("3-4", List.of(
                token(NUMBER, "3"), token(BINARY_OPERATION, "-"), token(NUMBER, "4")));

        assertTokens("5*6", List.of(
                token(NUMBER, "5"), token(BINARY_OPERATION, "*"), token(NUMBER, "6")));

        assertTokens("7/8", List.of(
                token(NUMBER, "7"), token(BINARY_OPERATION, "/"), token(NUMBER, "8")));

        assertTokens("9^10", List.of(
                token(NUMBER, "9"), token(BINARY_OPERATION, "^"), token(NUMBER, "10")));
    }

    @Test
    void testUnaryMinus() {
        assertTokens("-5", List.of(
                token(UNARY_OPERATION, "-"), token(NUMBER, "5")));

        assertTokens("+3", List.of( // + обычно считается бинарным, но зависит от вашей логики
                token(BINARY_OPERATION, "+"), token(NUMBER, "3")));

        assertTokens("-(2+3)", List.of(
                token(UNARY_OPERATION, "-"), token(LEFT_PARENTHESIS, "("),
                token(NUMBER, "2"), token(BINARY_OPERATION, "+"),
                token(NUMBER, "3"), token(RIGHT_PARENTHESIS, ")")));
    }

    @Test
    void testFunctions() {
        assertTokens("sin(x)", List.of(
                token(UNARY_OPERATION, "sin"), token(LEFT_PARENTHESIS, "("),
                token(VARIABLE, "x"), token(RIGHT_PARENTHESIS, ")")));

        assertTokens("cos(2*pi)", List.of(
                token(UNARY_OPERATION, "cos"), token(LEFT_PARENTHESIS, "("),
                token(NUMBER, "2"), token(BINARY_OPERATION, "*"),
                token(CONSTANT, "pi"), token(RIGHT_PARENTHESIS, ")")));

        assertTokens("tan(-0.5)", List.of(
                token(UNARY_OPERATION, "tan"), token(LEFT_PARENTHESIS, "("),
                token(UNARY_OPERATION, "-"), token(NUMBER, "0.5"),
                token(RIGHT_PARENTHESIS, ")")));
    }

    @Test
    void testNumbers() {
        assertTokens("3.14", List.of(token(NUMBER, "3.14")));
        assertTokens(".5", List.of(token(NUMBER, ".5")));
        assertTokens("0.123", List.of(token(NUMBER, "0.123")));
        assertTokens("42", List.of(token(NUMBER, "42")));
    }

    @Test
    void testConstants() {
        assertTokens("pi", List.of(token(CONSTANT, "pi")));
        assertTokens("e", List.of(token(CONSTANT, "e")));
        assertTokens("Pi", List.of(token(CONSTANT, "pi")));
        assertTokens("E", List.of(token(CONSTANT, "e")));
    }

    @Test
    void testVariables() {
        assertTokens("x", List.of(token(VARIABLE, "x")));
        assertTokens("xyz", List.of(token(VARIABLE, "xyz")));
        assertTokens("x1", List.of(token(VARIABLE, "x1")));
    }

    @Test
    void testParentheses() {
        assertTokens("(1+2)*3", List.of(
                token(LEFT_PARENTHESIS, "("), token(NUMBER, "1"),
                token(BINARY_OPERATION, "+"), token(NUMBER, "2"),
                token(RIGHT_PARENTHESIS, ")"), token(BINARY_OPERATION, "*"),
                token(NUMBER, "3")));

        assertTokens("((1))", List.of(
                token(LEFT_PARENTHESIS, "("), token(LEFT_PARENTHESIS, "("),
                token(NUMBER, "1"), token(RIGHT_PARENTHESIS, ")"),
                token(RIGHT_PARENTHESIS, ")")));
    }

    @Test
    void testWhitespaceHandling() {
        assertTokens("  1  +   2  ", List.of(
                token(NUMBER, "1"), token(BINARY_OPERATION, "+"), token(NUMBER, "2")));

        assertTokens("x\t+\ny", List.of(
                token(VARIABLE, "x"), token(BINARY_OPERATION, "+"), token(VARIABLE, "y")));
    }

    @Test
    void testInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenize("1 @ 2"));
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenize("x#"));
        assertThrows(IllegalArgumentException.class, () -> Lexer.tokenize("$"));
    }

    @Test
    void testEmptyInput() {
        assertTokens("", List.of());
    }

    @Test
    void testOnlyWhitespace() {
        assertTokens("   \t\n  ", List.of());
    }

    @Test
    void testSingleToken() {
        assertTokens("42", List.of(token(NUMBER, "42")));
        assertTokens("x", List.of(token(VARIABLE, "x")));
        assertTokens("(", List.of(token(LEFT_PARENTHESIS, "(")));
    }

    @Test
    void testComplexExpression1() {
        assertTokens("sin(2*pi*x) + cos(y) / 2.5 - e^3", List.of(
                token(UNARY_OPERATION, "sin"), token(LEFT_PARENTHESIS, "("),
                token(NUMBER, "2"), token(BINARY_OPERATION, "*"),
                token(CONSTANT, "pi"), token(BINARY_OPERATION, "*"),
                token(VARIABLE, "x"), token(RIGHT_PARENTHESIS, ")"),
                token(BINARY_OPERATION, "+"), token(UNARY_OPERATION, "cos"),
                token(LEFT_PARENTHESIS, "("), token(VARIABLE, "y"),
                token(RIGHT_PARENTHESIS, ")"), token(BINARY_OPERATION, "/"),
                token(NUMBER, "2.5"), token(BINARY_OPERATION, "-"),
                token(CONSTANT, "e"), token(BINARY_OPERATION, "^"),
                token(NUMBER, "3")));
    }

    @Test
    void testComplexExpression2() {
        assertTokens("-sqrt(x^2 + y^2) * (1 - abs(sin(2*pi*t)))", List.of(
                token(UNARY_OPERATION, "-"), token(UNARY_OPERATION, "sqrt"),
                token(LEFT_PARENTHESIS, "("), token(VARIABLE, "x"),
                token(BINARY_OPERATION, "^"), token(NUMBER, "2"),
                token(BINARY_OPERATION, "+"), token(VARIABLE, "y"),
                token(BINARY_OPERATION, "^"), token(NUMBER, "2"),
                token(RIGHT_PARENTHESIS, ")"), token(BINARY_OPERATION, "*"),
                token(LEFT_PARENTHESIS, "("), token(NUMBER, "1"),
                token(BINARY_OPERATION, "-"), token(UNARY_OPERATION, "abs"),
                token(LEFT_PARENTHESIS, "("), token(UNARY_OPERATION, "sin"),
                token(LEFT_PARENTHESIS, "("), token(NUMBER, "2"),
                token(BINARY_OPERATION, "*"), token(CONSTANT, "pi"),
                token(BINARY_OPERATION, "*"), token(VARIABLE, "t"),
                token(RIGHT_PARENTHESIS, ")"), token(RIGHT_PARENTHESIS, ")"),
                token(RIGHT_PARENTHESIS, ")")));
    }

    @Test
    public void testNoImplicitMult_NumberThenVariable() {
        assertTokens("2x", List.of(
                token(NUMBER, "2"),
                token(VARIABLE, "x")
        ));
    }

    @Test
    public void testNoImplicitMult_VariableThenParen() {
        assertTokens("x(2)", List.of(
                token(VARIABLE, "x"),
                token(LEFT_PARENTHESIS, "("),
                token(NUMBER, "2"),
                token(RIGHT_PARENTHESIS, ")")
        ));
    }

    @Test
    public void testNoImplicitMult_ParenThenVariable() {
        assertTokens("(x)y", List.of(
                token(LEFT_PARENTHESIS, "("),
                token(VARIABLE, "x"),
                token(RIGHT_PARENTHESIS, ")"),
                token(VARIABLE, "y")
        ));
    }

    @Test
    public void testNoImplicitMult_NumberThenFunction() {
        assertTokens("2sin(x)", List.of(
                token(NUMBER, "2"),
                token(UNARY_OPERATION, "sin"),
                token(LEFT_PARENTHESIS, "("),
                token(VARIABLE, "x"),
                token(RIGHT_PARENTHESIS, ")")
        ));
    }

    @Test
    public void testExplicitMult_StillWorks() {
        assertTokens("2*x", List.of(
                token(NUMBER, "2"),
                token(BINARY_OPERATION, "*"),
                token(VARIABLE, "x")
        ));
    }

    @Test
    public void testWhitespace_DoesNotImplyMult() {
        assertTokens("x y", List.of(
                token(VARIABLE, "xy")
        ));
    }

    private void assertTokens(String input, List<Token> expected) {
        List<Token> tokens = Lexer.tokenize(input);
        System.out.println(expected);
        System.out.println(tokens);
        assertEquals(expected, tokens);
    }

    private Token token(Token.TokenType type, String value) {
        return new Token(type, value);
    }

}