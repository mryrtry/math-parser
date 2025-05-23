package lexer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LexerTest {
    @Test
    void testBasicOperations() {
        List<Token> tokens = Lexer.tokenize("2+3*4");
        assertEquals(5, tokens.size());
        assertEquals("2", tokens.get(0).value());
        assertEquals("+", tokens.get(1).value());
        assertEquals("3", tokens.get(2).value());
        assertEquals("*", tokens.get(3).value());
        assertEquals("4", tokens.get(4).value());
    }

    @Test
    void testUnaryMinus() {
        List<Token> tokens = Lexer.tokenize("-x + (-2)");
        assertEquals(7, tokens.size());
        assertEquals("-", tokens.get(0).value());
        assertEquals("x", tokens.get(1).value());
        assertEquals("+", tokens.get(2).value());
        assertEquals("(", tokens.get(3).value());
        assertEquals("-", tokens.get(4).value());
        assertEquals("2", tokens.get(5).value());
        assertEquals(")", tokens.get(6).value());
    }

    @Test
    void testFunctions() {
        List<Token> tokens = Lexer.tokenize("sin(x)+cos(y)");
        assertEquals(9, tokens.size());
        assertEquals("sin", tokens.get(0).value());
        assertEquals("(", tokens.get(1).value());
        assertEquals("x", tokens.get(2).value());
        assertEquals(")", tokens.get(3).value());
        assertEquals("+", tokens.get(4).value());
        assertEquals("cos", tokens.get(5).value());
    }

    @Test
    void testVariables() {
        List<Token> tokens = Lexer.tokenize("a + b - c");
        assertEquals(5, tokens.size());
        assertEquals("a", tokens.get(0).value());
        assertEquals("+", tokens.get(1).value());
        assertEquals("b", tokens.get(2).value());
        assertEquals("-", tokens.get(3).value());
        assertEquals("c", tokens.get(4).value());
    }

    @Test
    void testInvalidMultiLetterVariable() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Lexer.tokenize("abc + 2"));
        assertTrue(exception.getMessage().contains("Variables must be single-letter"));
    }

    @Test
    void testInvalidNumberFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Lexer.tokenize("2.3.4 + 5"));
        assertTrue(exception.getMessage().contains("multiple dots"));
    }

    @Test
    void testUnknownCharacter() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Lexer.tokenize("2 # 3"));
        assertTrue(exception.getMessage().contains("Unknown character"));
    }

    @Test
    void testConstants() {
        List<Token> tokens = Lexer.tokenize("e + pi");
        assertEquals(3, tokens.size());
        assertEquals("e", tokens.get(0).value());
        assertEquals("+", tokens.get(1).value());
        assertEquals("pi", tokens.get(2).value());
    }

    @Test
    void testEmptyExpression() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> Lexer.tokenize(""));
        assertTrue(exception.getMessage().contains("cannot be null or empty"));
    }
}