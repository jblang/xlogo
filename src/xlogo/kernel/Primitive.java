package xlogo.kernel;

import java.util.Stack;

class Primitive implements AbstractProcedure {
    public final String key;
    public final int arity;
    public final boolean general;
    public final PrimFunc function;

    Primitive(String key, int arity, boolean general, PrimFunc function) {
        this.key = key;
        this.arity = arity;
        this.general = general;
        this.function = function;
    }

    @Override
    public boolean isGeneral() {
        return general;
    }

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public void execute(Stack<String> params) {
        function.execute(params);
    }

    interface PrimFunc {
        void execute(Stack<String> params);
    }
}
