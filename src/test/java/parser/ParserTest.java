package parser;

import function.MathFunction;
import lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void testBasicOperations() {
        testExpression("2+3", "5");
        testExpression("5-2", "3");
        testExpression("3*4", "12");
        testExpression("8/2", "4");
        testExpression("7%3", "1");
        testExpression("2^3", "8");
    }

    @Test
    void testOperatorPrecedence() {
        testExpression("2+3*4", "14");
        testExpression("(2+3)*4", "20");
        testExpression("2^3^2", "512");
        testExpression("2^(3^2)", "512");
        testExpression("(2^3)^2", "64");
    }

    @Test
    void testUnaryOperations() {
        testExpression("-5", "-5");
        testExpression("sin(0)", "0.0");
        testExpression("cos(0)", "1.0");
        testExpression("tan(0)", "0.0");
        testExpression("abs(-3)", "3");
        testExpression("sqrt(4)", "2.0");
        testExpression("ln(1)", "0.0");
        testExpression("lg(100)", "2.0");
    }

    @Test
    void testVariables() {
        testExpressionWithVars("x", new BigDecimal[]{new BigDecimal("5")}, "5");
        testExpressionWithVars("x+y",
                new BigDecimal[]{new BigDecimal("2"), new BigDecimal("3")},
                "5");
        testExpressionWithVars("x*y+z",
                new BigDecimal[]{new BigDecimal("2"), new BigDecimal("3"), new BigDecimal("4")},
                "10");
    }

    @Test
    void testConstants() {
        testExpression("e", String.valueOf(Math.E));
        testExpression("pi", String.valueOf(Math.PI));
    }

    @Test
    void testErrors() {
        assertThrowsWithMessage("2+", "Not enough operands");
        assertThrowsWithMessage("sin()", "Not enough operands");
        assertThrowsWithMessage("(2+3", "Mismatched parentheses");
        assertThrowsWithMessage("2+3)", "Mismatched parentheses");
        assertCalculateThrowsWithMessage("1/0", "Division by zero");
        assertCalculateThrowsWithMessage("sqrt(-1)", "Square root of negative");
        assertCalculateThrowsWithMessage("ln(0)", "Logarithm of non-positive");
    }

    @Test
    void testComplexExpressions() {
        testExpression("(2+3)*(4-1)", "15");
        testExpression("sin(pi/2)", "1.0");
        testExpressionWithVars("sqrt(x^2+y^2)", new BigDecimal[]{new BigDecimal(4), new BigDecimal(3)}, "5.0");
        testExpression("abs(-5)*2+3", "13");
    }

    @Test
    void testEdgeCases() {
        testExpression("9999999999999999999999999999999999999999 + 1",
                "10000000000000000000000000000000000000000");
        testExpression("0.1 + 0.2", "0.3");
    }

    @Test
    void testHardExpressions() {
        testExpression("sqrt(16)-21+3^2^1-21*3*(6+2)+ln(e)", "-511.0");
        testExpression("(1 + 2) * (3 + (4 * (5 + 6))) / 2^3", "17.625");
        testExpressionWithVars("a*b + c*d - m/f + g^h",
                new BigDecimal[]{new BigDecimal("2"), new BigDecimal("2"),
                        new BigDecimal("3"), new BigDecimal("3"),
                        new BigDecimal("6"), new BigDecimal("2"),
                        new BigDecimal("7"), new BigDecimal("1")},
                "17");
        testExpression("1+2*3-4/5+6%7^8*9-10+11*12-13/14+15%16^17*18-19+20*21",
                "852.27142857142857142857");
    }

    private void testExpression(String expr, String expected) {
        MathFunction func = Parser.parse(Lexer.tokenize(expr));
        BigDecimal result = func.calculate(new BigDecimal[0]);
        assertEquals(new BigDecimal(expected).setScale(20, RoundingMode.HALF_UP), result.setScale(20, RoundingMode.HALF_UP));
    }

    private void testExpressionWithVars(String expr, BigDecimal[] vars, String expected) {
        MathFunction func = Parser.parse(Lexer.tokenize(expr));
        BigDecimal result = func.calculate(vars);
        assertEquals(new BigDecimal(expected).setScale(20, RoundingMode.HALF_UP), result.setScale(20, RoundingMode.HALF_UP));
    }

    private void assertThrowsWithMessage(String expr, String messagePart) {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                Parser.parse(Lexer.tokenize(expr))
        );
        assertTrue(exception.getMessage().contains(messagePart));
    }

    private void assertCalculateThrowsWithMessage(String expr, String messagePart) {
        Exception exception = assertThrows(ArithmeticException.class, () ->
                Parser.parse(Lexer.tokenize(expr)).calculate(new BigDecimal[1])
        );
        assertTrue(exception.getMessage().contains(messagePart));
    }
}