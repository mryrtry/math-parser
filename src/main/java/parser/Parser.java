package parser;

import function.MathFunction;
import lexer.Token;
import parser.nodes.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static lexer.Token.TokenType.LEFT_PARENTHESIS;
import static lexer.Token.TokenType.UNARY_OPERATION;

public class Parser {
    private static final Map<String, Integer> OPERATOR_PRECEDENCE = Map.of(
            "+", 1,
            "-", 1,
            "*", 2,
            "/", 2,
            "%", 2,
            "^", 3
    );

    public static MathFunction parse(List<Token> tokens) {
        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException("Token list cannot be null or empty");
        }

        VariableTracker variableTracker = new VariableTracker();
        Stack<ASTNode> output = new Stack<>();
        Stack<Token> operators = new Stack<>();

        for (Token token : tokens) {
            switch (token.type()) {
                case NUMBER:
                    output.push(new NumberNode(new BigDecimal(token.value())));
                    break;

                case CONSTANT:
                    output.push(parseConstant(token.value()));
                    break;

                case VARIABLE:
                    int varIndex = variableTracker.getIndexForVariable(token.value());
                    output.push(new VariableNode(token.value(), varIndex));
                    break;

                case UNARY_OPERATION:
                case LEFT_PARENTHESIS:
                    operators.push(token);
                    break;

                case BINARY_OPERATION:
                    processBinaryOperator(output, operators, token, variableTracker);
                    operators.push(token);
                    break;

                case RIGHT_PARENTHESIS:
                    processRightParenthesis(output, operators, variableTracker);
                    break;
            }
        }

        processRemainingOperators(output, operators, variableTracker);
        validateFinalOutput(output);

        return output.pop().toMathFunction();
    }

    private static ASTNode parseConstant(String constant) {
        return switch (constant) {
            case "e" -> new NumberNode(BigDecimal.valueOf(Math.E));
            case "pi" -> new NumberNode(BigDecimal.valueOf(Math.PI));
            default -> throw new IllegalArgumentException("Unknown constant: " + constant);
        };
    }

    private static void processBinaryOperator(Stack<ASTNode> output, Stack<Token> operators, Token token,
                                              VariableTracker variableTracker) {
        while (!operators.isEmpty() &&
                operators.peek().type() != LEFT_PARENTHESIS &&
                (operators.peek().type() == UNARY_OPERATION ||
                        (OPERATOR_PRECEDENCE.get(token.value()) < OPERATOR_PRECEDENCE.get(operators.peek().value()) ||
                                (OPERATOR_PRECEDENCE.get(token.value()).equals(OPERATOR_PRECEDENCE.get(operators.peek().value()))
                                        && !token.value().equals("^"))))) {
            applyOperator(output, operators.pop(), variableTracker);
        }
    }

    private static void processRightParenthesis(Stack<ASTNode> output, Stack<Token> operators,
                                                VariableTracker variableTracker) {
        while (!operators.isEmpty() && operators.peek().type() != LEFT_PARENTHESIS) {
            applyOperator(output, operators.pop(), variableTracker);
        }

        if (operators.isEmpty()) {
            throw new IllegalArgumentException("Mismatched parentheses");
        }

        operators.pop();

        if (!operators.isEmpty() && operators.peek().type() == UNARY_OPERATION) {
            applyOperator(output, operators.pop(), variableTracker);
        }
    }

    private static void processRemainingOperators(Stack<ASTNode> output, Stack<Token> operators,
                                                  VariableTracker variableTracker) {
        while (!operators.isEmpty()) {
            if (operators.peek().type() == LEFT_PARENTHESIS) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            applyOperator(output, operators.pop(), variableTracker);
        }
    }

    private static void validateFinalOutput(Stack<ASTNode> output) {
        if (output.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }
    }

    private static void applyOperator(Stack<ASTNode> output, Token operator, VariableTracker variableTracker) {
        switch (operator.type()) {
            case UNARY_OPERATION:
                applyUnaryOperator(output, operator);
                break;
            case BINARY_OPERATION:
                applyBinaryOperator(output, operator, variableTracker);
                break;
            default:
                throw new IllegalArgumentException("Unexpected operator type: " + operator.type());
        }
    }

    private static void applyUnaryOperator(Stack<ASTNode> output, Token operator) {
        if (output.isEmpty()) {
            throw new IllegalArgumentException("Not enough operands for unary operator: " + operator.value());
        }
        output.push(new UnaryOperationNode(operator.value(), output.pop()));
    }

    private static void applyBinaryOperator(Stack<ASTNode> output, Token operator, VariableTracker variableTracker) {
        if (output.size() < 2) {
            throw new IllegalArgumentException("Not enough operands for binary operator: " + operator.value());
        }
        ASTNode right = output.pop();
        ASTNode left = output.pop();
        output.push(new BinaryOperationNode(operator.value(), left, right, variableTracker.getTotalVariables()));
    }
}