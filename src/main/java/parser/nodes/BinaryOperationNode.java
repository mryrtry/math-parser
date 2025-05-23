package parser.nodes;


import function.MathFunction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BinaryOperationNode implements ASTNode {
    private final String operator;
    private final ASTNode left;
    private final ASTNode right;
    private final int varCount;

    public BinaryOperationNode(String operator, ASTNode left, ASTNode right, int varCount) {
        this.operator = operator;
        this.left = left;
        this.right = right;
        this.varCount = varCount;
    }

    @Override
    public MathFunction toMathFunction() {
        MathFunction leftFunc = left.toMathFunction();
        MathFunction rightFunc = right.toMathFunction();

        return new MathFunction(varCount) {
            @Override
            public BigDecimal calculate(BigDecimal[] values) {
                BigDecimal leftVal = leftFunc.calculate(values);
                BigDecimal rightVal = rightFunc.calculate(values);

                return switch (operator) {
                    case "+" -> leftVal.add(rightVal);
                    case "-" -> leftVal.subtract(rightVal);
                    case "*" -> leftVal.multiply(rightVal);
                    case "/" -> {
                        if (rightVal.compareTo(BigDecimal.ZERO) == 0) {
                            throw new ArithmeticException("Division by zero");
                        }
                        yield leftVal.divide(rightVal, 20, RoundingMode.HALF_UP);
                    }
                    case "%" -> leftVal.remainder(rightVal);
                    case "^" -> {
                        double leftDouble = leftVal.doubleValue();
                        double rightDouble = rightVal.doubleValue();
                        if (leftDouble < 0 && rightDouble % 1 != 0) {
                            throw new ArithmeticException("Negative base with fractional exponent is not real");
                        }
                        yield BigDecimal.valueOf(Math.pow(leftDouble, rightDouble));
                    }
                    default -> throw new UnsupportedOperationException("Unknown operator: " + operator);
                };
            }
        };
    }
}