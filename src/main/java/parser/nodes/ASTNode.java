package parser.nodes;

import function.MathFunction;

public interface ASTNode {
    MathFunction toMathFunction();
}