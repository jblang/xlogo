/**
 * Title :        XLogo
 * Description :  XLogo is an interpreter for the Logo programming language
 */
package xlogo.kernel;

import xlogo.Logo;
import xlogo.utils.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * List manipulation primitives: first, last, butfirst, butlast, item, etc.
 */
public class ListPrimitives extends PrimitiveGroup {

    public ListPrimitives(Interpreter interpreter) {
        super(interpreter);
    }

    @Override
    public List<Primitive> getPrimitives() {
        return List.of(
            new Primitive("list.additem", 3, false, this::addItem),
            new Primitive("list.butfirst", 1, false, this::butFirst),
            new Primitive("list.butlast", 1, false, this::butLast),
            new Primitive("list.count", 1, false, this::count),
            new Primitive("list.first", 1, false, this::first),
            new Primitive("list.fput", 2, false, this::fput),
            new Primitive("list.item", 2, false, this::item),
            new Primitive("list.last", 1, false, this::last),
            new Primitive("list.list", 2, true, this::list),
            new Primitive("list.list?", 1, false, this::isListPrimitive),
            new Primitive("list.lput", 2, false, this::lput),
            new Primitive("list.member", 2, false, this::isMember71),
            new Primitive("list.member?", 2, false, this::isMember69),
            new Primitive("list.pick", 1, false, this::pick),
            new Primitive("list.remove", 2, false, this::remove),
            new Primitive("list.replace", 3, false, this::replaceItem),
            new Primitive("list.reverse", 1, false, this::reverse)
        );
    }

    void addItem(Stack<String> param) {
        try {
            String list = getFinalList(param.get(0));
            int index = kernel.getCalculator().getInteger(param.get(1));
            String word = getWord(param.get(2));
            if (null != word && word.isEmpty()) word = "\\v";
            if (null == word) word = "[ " + getFinalList(param.get(2)) + "]";
            char element;
            int counter = 1;
            boolean space = true;
            boolean bracket = false;
            boolean error = true;
            for (int j = 0; j < list.length(); j++) {
                if (counter == index) {
                    error = false;
                    counter = j;
                    break;
                }
                element = list.charAt(j);
                if (element == '[') {
                    if (space) bracket = true;
                    space = false;
                }
                if (element == ' ') {
                    space = true;
                    if (bracket) {
                        bracket = false;
                        j = extractList(list, j);
                    }
                    counter++;
                    if (j == list.length() - 1 && counter == index) {
                        error = false;
                        counter = list.length();
                    }
                }
            }
            if (error && index != counter)
                throw new LogoException(app, Logo.getString("interpreter.error.list.missingElement1") + " " + index + " " + Logo.getString("interpreter.error.list.missingElement2") + list + "]");
            String result;
            if (!list.trim().isEmpty())
                result = "[ " + list.substring(0, counter) + word + " " + list.substring(counter) + "] ";
            else result = "[ " + word + " ] ";
            setReturnValue(true);
            pushResult(result);
        } catch (LogoException ignored) {
        }
    }

    void replaceItem(Stack<String> param) {
        try {
            String list = getFinalList(param.get(0));
            int index = kernel.getCalculator().getInteger(param.get(1));
            String word = getWord(param.get(2));
            if (null != word && word.isEmpty()) word = "\\v";
            if (null == word) word = "[ " + getFinalList(param.get(2)) + "]";
            char element;
            int counter = 1;
            boolean space = true;
            boolean bracket = false;
            boolean error = true;
            for (int j = 0; j < list.length(); j++) {
                if (counter == index) {
                    error = false;
                    counter = j;
                    break;
                }
                element = list.charAt(j);
                if (element == '[') {
                    if (space) bracket = true;
                    space = false;
                }
                if (element == ' ') {
                    space = true;
                    if (bracket) {
                        bracket = false;
                        j = extractList(list, j);
                    }
                    counter++;
                    if (j == list.length() - 1 && counter == index) {
                        error = false;
                        counter = list.length();
                    }
                }
            }
            if (error)
                throw new LogoException(app, Logo.getString("interpreter.error.list.missingElement1") + " " + index + " " + Logo.getString("interpreter.error.list.missingElement2") + list + "]");
            // Getting the element to replace
            String oldElement = item(list, index);
            int length = oldElement.length();
            if (oldElement.startsWith("[") || oldElement.startsWith("\"")) length--;
            String result = "[ " + list.substring(0, counter) + word + list.substring(counter + length) + "] ";
            setReturnValue(true);
            pushResult(result);
        } catch (LogoException ignored) {
        }
    }

    void isMember69(Stack<String> param) {
        try {
            isMember(param, 69);
        } catch (LogoException ignored) {
        }
    }

    void isMember71(Stack<String> param) {
        try {
            isMember(param, 71);
        } catch (LogoException ignored) {
        }
    }

    void isListPrimitive(Stack<String> param) {
        String list = param.get(0).trim();
        if (isList(list)) pushResult(Logo.getString("interpreter.keyword.true"));
        else pushResult(Logo.getString("interpreter.keyword.false"));
        setReturnValue(true);
    }

    void count(Stack<String> param) {
        setReturnValue(true);
        String word = getWord(param.get(0));
        if (null == word) {
            try {
                String list = getFinalList(param.get(0));
                pushResult(String.valueOf(numberOfElements(list)));
            } catch (LogoException ignored) {
            }
        } else pushResult(String.valueOf(getWordLength(word)));
    }

    void first(Stack<String> param) {
        setReturnValue(true);
        String word = getWord(param.get(0));
        if (null == word) { // If this is a list
            try {
                String list = getFinalList(param.get(0));
                pushResult(item(list, 1));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(word) == 1) pushResult(getWordPrefix() + word);
        else {
            String st = "";
            try {
                st = itemWord(1, word);
                Double.parseDouble(st);
                pushResult(st);
            } catch (LogoException ignored) {
            } catch (NumberFormatException e2) {
                pushResult("\"" + st);
            }
        }
    }

    void last(Stack<String> param) {
        setReturnValue(true);
        String word = getWord(param.get(0));
        if (null == word) { // If it's a list
            try {
                String list = getFinalList(param.get(0));
                pushResult(item(list, numberOfElements(list)));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(word) == 1) pushResult(getWordPrefix() + word);
        else {
            String st = "";
            try {
                st = itemWord(getWordLength(word), word);
                Double.parseDouble(st);
                pushResult(st);
            } catch (NumberFormatException e1) {
                pushResult("\"" + st);
            } catch (LogoException ignored) {
            }
        }
    }

    void butFirst(Stack<String> param) {
        setReturnValue(true);
        String word = getWord(param.get(0));
        if (null == word) {
            try {
                String list = getFinalList(param.get(0)).trim();
                String element = item(list, 1);
                int length = element.length();
                if (element.startsWith("\"") || element.startsWith("[")) length--;
                pushResult("[" + list.substring(length) + " ] ");
            } catch (LogoException ignored) {
            }
        } else if (word.isEmpty()) {
            try {
                throw new LogoException(app, Logo.getString("interpreter.error.wordEmpty"));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(word) == 1) pushResult("\"");
        else {
            if (!word.startsWith("\\")) word = word.substring(1);
            else word = word.substring(2);
            try {
                Double.parseDouble(word);
                pushResult(word);
            } catch (NumberFormatException e) {
                pushResult(getWordPrefix() + word);
            }
        }
    }

    void butLast(Stack<String> param) {
        setReturnValue(true);
        String word = getWord(param.get(0));
        if (null == word) {
            try {
                String list = getFinalList(param.get(0)).trim();
                String element = item(list, numberOfElements(list));
                int length = element.length();
                if (element.startsWith("\"") || element.startsWith("[")) length--;
                pushResult("[ " + list.substring(0, list.length() - length) + "] ");
            } catch (LogoException ignored) {
            }
        } else if (word.isEmpty()) {
            try {
                throw new LogoException(app, Logo.getString("interpreter.error.wordEmpty"));
            } catch (LogoException ignored) {
            }
        } else if (getWordLength(word) == 1) pushResult("\"");
        else {
            String tmp = word.substring(0, word.length() - 1);
            if (tmp.endsWith("\\")) tmp = tmp.substring(0, tmp.length() - 1);
            try {
                Double.parseDouble(tmp);
                pushResult(tmp);
            } catch (NumberFormatException e) {
                pushResult(getWordPrefix() + tmp);
            }
        }
    }

    void item(Stack<String> param) {
        setReturnValue(true);
        try {
            String word = getWord(param.get(1));
            if (null == word)
                pushResult(item(getFinalList(param.get(1)), kernel.getCalculator().getInteger(param.get(0))));
            else {
                int i = kernel.getCalculator().getInteger(param.get(0));
                if (i < 1 || i > getWordLength(word))
                    throw new LogoException(app, Utils.primitiveName("list.item") + " " + Logo.getString("interpreter.error.arguments.openBracket") + i + " " + Logo.getString("interpreter.error.arguments.closeBracket") + ".");
                else {
                    String st = itemWord(i, word);
                    try {
                        Double.parseDouble(st);
                        pushResult(st);
                    } catch (NumberFormatException e1) {
                        pushResult("\"" + st);
                    }
                }
            }
        } catch (LogoException ignored) {
        }
    }

    void remove(Stack<String> param) {
        setReturnValue(true);
        try {
            StringBuilder list = new StringBuilder(getFinalList(param.get(1)));
            StringTokenizer st = new StringTokenizer(list.toString());
            list = new StringBuilder("[ ");
            String word = getWord(param.get(0));
            String str;
            if (null != word && word.isEmpty()) word = "\\v";
            if (null == word) word = param.get(0).trim();

            while (st.hasMoreTokens()) {
                str = st.nextToken();
                if (str.equals("[")) str = extractList(st);
                if (!str.equals(word)) list.append(str).append(" ");
            }
            pushResult(list.toString().trim() + " ] ");
        } catch (LogoException ignored) {
        }
    }

    void pick(Stack<String> param) {
        setReturnValue(true);
        String word = getWord(param.get(0));
        if (null == word) {
            try {
                String list = getFinalList(param.get(0));
                int number = (int) Math.floor(numberOfElements(list) * Math.random()) + 1;
                String tmp = item(list, number);
                if (tmp.equals("\"\\v")) tmp = "\"";
                pushResult(tmp);
            } catch (LogoException ignored) {
            }
        } else {
            int number = (int) Math.floor(Math.random() * getWordLength(word)) + 1;
            String st = "";
            try {
                st = itemWord(number, word);
                Double.parseDouble(st);
                pushResult(st);
            } catch (NumberFormatException e1) {
                pushResult("\"" + st);
            } catch (LogoException ignored) {
            }
        }
    }

    void reverse(Stack<String> param) {
        try {
            StringBuilder list = new StringBuilder(getFinalList(param.get(0)).trim());
            setReturnValue(true);
            StringTokenizer st = new StringTokenizer(list.toString());
            list = new StringBuilder(" ] ");
            String element;
            while (st.hasMoreTokens()) {
                element = st.nextToken();
                if (element.equals("[")) element = extractList(st);
                list.insert(0, " " + element);
            }
            pushResult("[" + list);
        } catch (LogoException ignored) {
        }
    }

    void lput(Stack<String> param) {
        try {
            String list = getFinalList(param.get(1)).trim();
            setReturnValue(true);
            String word = getWord(param.get(0));
            if (null != word && word.isEmpty()) word = "\\v";
            // If it's a list
            pushResult(("[ " + list).trim() + " " + Objects.requireNonNullElseGet(word, () -> param.get(0).trim()) + " ] ");
        } catch (LogoException ignored) {
        }
    }

    void fput(Stack<String> param) {
        try {
            String list = getFinalList(param.get(1));
            setReturnValue(true);
            String word = getWord(param.get(0));
            if (null != word && word.isEmpty()) word = "\\v";
            if (null == word) {
                if (!list.isEmpty())
                    pushResult("[ " + param.get(0).trim() + " " + list.trim() + " ] ");
                else pushResult("[ " + param.get(0).trim() + " ] ");
            } else {
                if (!list.isEmpty()) pushResult("[ " + word + " " + list.trim() + " ] ");
                else pushResult("[ " + word + " ] ");
            }
        } catch (LogoException ignored) {
        }
    }

    void list(Stack<String> param) {
        StringBuilder list = new StringBuilder("[ ");
        setReturnValue(true);
        for (String item : param) {
            String word = getWord(item);
            if (null == word) {
                list.append(item);
            } else {
                if (word.isEmpty()) word = "\\v";
                list.append(word).append(" ");
            }
        }
        pushResult(list + "] ");
    }
}
