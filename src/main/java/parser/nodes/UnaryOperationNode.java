package parser.nodes;


import function.MathFunction;

import java.math.BigDecimal;

public class UnaryOperationNode implements ASTNode {
    private final String operator;
    private final ASTNode operand;

    public UnaryOperationNode(String operator, ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public MathFunction toMathFunction() {
        MathFunction operandFunc = operand.toMathFunction();

        return new MathFunction(operandFunc.getVarNumber()) {
            @Override
            public BigDecimal calculate(BigDecimal[] values) {
                BigDecimal val = operandFunc.calculate(values);

                return switch (operator) {
                    case "-" -> val.negate();
                    case "sin" -> BigDecimal.valueOf(Math.sin(val.doubleValue()));
                    case "cos" -> BigDecimal.valueOf(Math.cos(val.doubleValue()));
                    case "tan" -> BigDecimal.valueOf(Math.tan(val.doubleValue()));
                    case "catan" -> BigDecimal.valueOf(Math.atan(val.doubleValue()));
                    case "sqrt" -> {
                        if (val.compareTo(BigDecimal.ZERO) < 0) {
                            throw new ArithmeticException("Square root of negative number");
                        }
                        yield BigDecimal.valueOf(Math.sqrt(val.doubleValue()));
                    }
                    case "ln", "lg" -> {
                        if (val.compareTo(BigDecimal.ZERO) <= 0) {
                            throw new ArithmeticException("Logarithm of non-positive number");
                        }
                        yield operator.equals("ln")
                                ? BigDecimal.valueOf(Math.log(val.doubleValue()))
                                : BigDecimal.valueOf(Math.log10(val.doubleValue()));
                    }
                    case "abs" -> val.abs();
                    default -> throw new UnsupportedOperationException("Unknown operator: " + operator);
                };
            }
        };
    }
}