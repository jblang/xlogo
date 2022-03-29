package xlogo.kernel;

import java.util.Stack;

interface AbstractProcedure {
    boolean isGeneral();
    int getArity();
    void execute(Stack<String> param);
}
