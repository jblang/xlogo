package xlogo.kernel.grammar;

import java.util.Vector;

public class LogoTree {
    private final Vector<LogoTree> children;
    private LogoTree parent;
    private LogoType value;
    private final boolean isRoot = false;
    private final boolean isProcedure = false;
    private final boolean isPrimitive = false;
    private final boolean isLeaf = false;

    LogoTree() {
        children = new Vector<LogoTree>();
    }

    protected LogoTree getParent() {
        return parent;
    }

    protected void setParent(LogoTree lt) {
        this.parent = lt;
    }

    protected boolean isRoot() {
        return isRoot;
    }

    protected void addChild(LogoTree child) {
        children.add(child);
    }

    protected LogoType getValue() {
        return value;
    }

    protected void setValue(LogoType value) {
        this.value = value;
    }

    protected boolean isLeaf() {
        return isLeaf;
    }

    LogoType evaluate() {
        Vector<LogoType> args = new Vector<LogoType>();
        for (int i = 0; i < children.size(); i++) {
            LogoTree child = children.get(i);
            if (child.isLeaf()) args.add(child.getValue());
            else args.add(child.evaluate());
        }
        return null;
    }
}
