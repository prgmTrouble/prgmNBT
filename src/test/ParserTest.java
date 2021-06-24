package test;

import java.util.ArrayList;
import java.util.Stack;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import nbt.exception.NBTConversionException;
import nbt.exception.NBTParsingException;
import nbt.value.NBTString;
import nbt.value.NBTValue;
import nbt.value.collection.NBTArray;
import nbt.value.collection.NBTObject;
import nbt.value.number.NBTByte;
import nbt.value.number.NBTDouble;
import nbt.value.number.NBTFloat;
import nbt.value.number.NBTInt;
import nbt.value.number.NBTLong;
import nbt.value.number.NBTShort;
import settings.Settings;
import settings.Version;

public class ParserTest {
    private static final Pattern INT_ARRAY_MATCHER = Pattern.compile("\\[[-+\\d|,\\s]+\\]");
    
    private static class Trace {
        int level = 0;
        Stack<String> trace = new Stack<>();
        private void tab() {
            if(level != 0)
                System.out.print("- ".repeat(level - 1)+"  ");
        }
        private static String join(final Object...in) {
            final StringJoiner j = new StringJoiner(">,<","<",">");
            if(in != null) for(final Object o : in) j.add(o.toString());
            return j.toString();
        }
        Trace invoke(final String name,final Object...args) {
            System.out.println();
            tab();
            System.out.println(trace.push("%s(%s)".formatted(name,join(args))));
            ++level;
            return this;
        }
        Trace print(final String txt) {
            tab();
            System.out.println(txt);
            return this;
        }
        <T> T ret(final T t) {
            --level;
            tab();
            System.out.println("~"+trace.pop()+" -> <"+t.toString()+">");
            return t;
        }
    }
    public static final Trace t = new Trace();
    public static void main(final String...args) throws NBTParsingException {
        Settings.version(Version.v13w36a);
        t.invoke("main",args);
        final String in = "{display:{Lore:[:3d,c:\\\",d:e]}}";
        final NBTValue v = getTagFromJson(in);
        t.ret(v.toString());
    }

    public static NBTValue getTagFromJson(String jsonString) throws NBTParsingException
    {
        t.invoke("getTagFromJson",jsonString);
        
        jsonString = jsonString.trim();

        if (!jsonString.startsWith("{"))
        {
            throw new NBTParsingException("Invalid tag encountered, expected \'{\' as first char.");
        }
        else if (topTagsCount(jsonString) != 1)
        {
            throw new NBTParsingException("Encountered multiple top tags, only one expected");
        }
        else
        {
            return t.ret(nameValueToNBT("tag", jsonString).parse());
        }
    }

    static int topTagsCount(String str) throws NBTParsingException
    {
        t.invoke("topTagsCount",str);
        int numCharsOnEmptyStack = 0;
        boolean inQuotes = false;
        Stack<Character> stack = new Stack<>();

        for (int j = 0; j < str.length(); ++j)
        {
            char charAtJ = str.charAt(j);

            if (charAtJ == '"')
            {
                if (isCharEscaped(str, j)) {if(!inQuotes) throw new NBTParsingException("Illegal use of \\\": " + str);}
                else inQuotes = !inQuotes;
            }
            else if (!inQuotes)
            {
                if (charAtJ != '{' && charAtJ != '[')
                {
                    if (charAtJ == '}' && (stack.isEmpty() || stack.pop() != '{'))
                        throw new NBTParsingException("Unbalanced curly brackets {}: " + str);

                    if (charAtJ == ']' && (stack.isEmpty() || stack.pop() != '['))
                        throw new NBTParsingException("Unbalanced square brackets []: " + str);
                }
                else
                {
                    t.print("found char '%c'%s".formatted(charAtJ,stack.isEmpty()? " on empty stack.":"."));
                    if (stack.isEmpty()) ++numCharsOnEmptyStack;
                    stack.push(charAtJ);
                }
            }
        }

        if (inQuotes) throw new NBTParsingException("Unbalanced quotation: " + str);
        else if (!stack.isEmpty()) throw new NBTParsingException("Unbalanced brackets: " + str);
        else {
            if (numCharsOnEmptyStack == 0 && !str.isEmpty()) {
                numCharsOnEmptyStack = 1;
                t.print("no structure chars");
            }
            return t.ret(numCharsOnEmptyStack);
        }
    }

    static Any joinStrToNBT(String... args) throws NBTParsingException
    {
        return t.invoke("joinStrToNBT",args).ret(nameValueToNBT(args[0], args[1]));
    }

    static Any nameValueToNBT(String key, String value) throws NBTParsingException
    {
        t.invoke("nameValueToNBT",key,value);
        value = value.trim();
        
        final boolean isObj = value.startsWith("{"),
                      isArr = value.startsWith("[") && !INT_ARRAY_MATCHER.matcher(value).matches();
        if(isObj || isArr) {
            t.print((isObj? "object":"array")+" found");
            value = value.substring(1, value.length() - 1);
            Coll coll = isObj? new Compound(key) : new List(key);
            String s;
            for(;value.length() > 0;value = value.substring(s.length() + 1)) {
                s = nextNameValuePair(value,isObj);
                if(s.length() > 0) coll.tagList.add(getTagFromNameValue(s,isArr));
                if(value.length() < s.length() + 1) break;
                final char c = value.charAt(s.length());
                if(c != ',' && c != '{' && c != '}' && c != '[' && c != ']')
                    throw new NBTParsingException("Unexpected token \'" + c + "\' at: " + value.substring(s.length()));
            }
            return t.ret(coll);
        } else
            return t.print("primitive found").ret(new Primitive(key, value));
    }

    private static Any getTagFromNameValue(String str, boolean isArray) throws NBTParsingException
    {
        return t.invoke("getTagFromNameValue",str,isArray).ret(joinStrToNBT(locateName(str,isArray),locateValue(str,isArray)));
    }

    private static String nextNameValuePair(String str, boolean isCompound) throws NBTParsingException
    {
        t.invoke("nextNameValuePair",str,isCompound);
        int i = getNextCharIndex(str, ':');
        int j = getNextCharIndex(str, ',');
        t.print("i=%d j=%d".formatted(i,j));
        
        if (isCompound) {
            if (i == -1) throw new NBTParsingException("Unable to locate name/value separator for string: " + str);
            if (j != -1 && j < i) throw new NBTParsingException("Name error at: " + str);
        } else if (i == -1 || i > j) i = -1;
        
        return t.ret(locateValueAt(str, i));
    }

    private static String locateValueAt(String str, int index) throws NBTParsingException
    {
        t.invoke("locateValueAt",str,index);
        Stack<Character> stack = new Stack<>();
        int i = index + 1;
        boolean inQuotes = false;
        boolean quotedWS = false;
        boolean wasntWS = false;

        for (int exitQuote = 0; i < str.length(); ++i) {
            char current = str.charAt(i);

            if (current == '"') {
                if (isCharEscaped(str, i)) {
                    if (!inQuotes)
                        throw new NBTParsingException("Illegal use of \\\": " + str);
                } else {
                    inQuotes = !inQuotes;
                    quotedWS |= inQuotes && !wasntWS;

                    if (!inQuotes) exitQuote = i;
                }
            }
            else if (!inQuotes) {
                if (current != '{' && current != '[') {
                    if(current == '}' || current == ']') {
                        final Character p = stack.isEmpty()? null : stack.pop();
                        if (current == '}' && (p == null || p != '{'))
                            throw new NBTParsingException("Unbalanced curly brackets {}: " + str);
                        if (current == ']' && (p == null || p != '['))
                            throw new NBTParsingException("Unbalanced square brackets []: " + str);
                        t.print("found character '%c'".formatted(current));
                    } else if (current == ',' && stack.isEmpty())
                        return t.print("found comma").ret(str.substring(0, i));
                } else {
                    t.print("found character '%c'".formatted(current));
                    stack.push(current);
                }
            }

            if (!Character.isWhitespace(current)) {
                if (!inQuotes && quotedWS && exitQuote != i)
                    return t.print("non-whitespace character was not in quotes," +
                                   " there was quoted whitespce, and isn't an exit quote.")
                            .ret(str.substring(0, exitQuote + 1));
                wasntWS = true;
            }
        }
        return t.print("reached end of string").ret(str.substring(0, i));
    }

    private static String locateName(String str, boolean isArray) throws NBTParsingException
    {
        t.invoke("locateName",str,isArray);
        if (isArray) {
            str = str.trim();
            if (str.startsWith("{") || str.startsWith("["))
                return t.print("array value of container type without key").ret("");
        }
        int i = getNextCharIndex(str, ':');
        t.print("colon @ %d".formatted(i));
        if (i == -1) {
            if (isArray) return t.print("array value without key").ret("");
            else throw new NBTParsingException("Unable to locate name/value separator for string: " + str);
        } else return t.ret(str.substring(0, i).trim());
    }

    private static String locateValue(String str, boolean isArray) throws NBTParsingException
    {
        t.invoke("locateValue",str,isArray);
        if (isArray) {
            str = str.trim();
            if (str.startsWith("{") || str.startsWith("["))
                return t.print("array value of container type").ret(str);
        }

        int i = getNextCharIndex(str, ':');
        t.print("colon @ %d".formatted(i));
        if (i == -1) {
            if (isArray) return t.print("array value without key").ret(str);
            else throw new NBTParsingException("Unable to locate name/value separator for string: " + str);
        } else return t.ret(str.substring(i + 1).trim());
    }

    private static int getNextCharIndex(String str, char targetChar)
    {
        t.invoke("getNextCharIndex",str,targetChar);
        int i = 0;

        for (boolean notInQuote = true; i < str.length(); ++i)
        {
            char c0 = str.charAt(i);

            if (c0 == '"') {if (!isCharEscaped(str, i)) notInQuote = !notInQuote;}
            else if (notInQuote) {
                if (c0 == targetChar) return t.print("found target").ret(i);
                if (c0 == '{' || c0 == '[') return t.print("found structure").ret(-1);
            }
        }
        return t.print("target not found").ret(-1);
    }

    private static boolean isCharEscaped(String str, int index)
    {
        return t.invoke("isCharEscaped",str,index)
                .ret(
                    index > 0 &&
                    str.charAt(index - 1) == '\\' &&
                    !isCharEscaped(str, index - 1)
                );
    }
    
    
    
    abstract static class Any {protected String json; public abstract NBTValue parse() throws NBTParsingException;}
    abstract static class Coll extends Any {final java.util.List<Any> tagList = new ArrayList<>();}
    static class Compound extends Coll
    {
        public Compound(String jsonIn) {
            t.invoke("new Compound",jsonIn);
            this.json = jsonIn;
            t.ret("Compound<%s>".formatted(jsonIn));
        }
        
        @Override
        public String toString() {
            return "Compound<%s>".formatted(json);
        }
        public NBTValue parse() throws NBTParsingException
        {
            t.invoke("Compound.parse");
            NBTObject nbttagcompound = new NBTObject();
            for (Any jsontonbt$any : this.tagList) {
                t.print("parsing %s".formatted(jsontonbt$any.json));
                nbttagcompound.set(jsontonbt$any.json, jsontonbt$any.parse());
            }
            return t.ret(nbttagcompound);
        }
    }

    static class List extends Coll
    {
        public List(String json) {
            t.invoke("new List",json);
            this.json = json;
            t.ret("List<%s>".formatted(json));
        }
        @Override
        public String toString() {
            return "List<%s>".formatted(json);
        }

        public NBTValue parse() throws NBTParsingException
        {
            t.invoke("List.parse");
            NBTArray nbttaglist = new NBTArray();
            for (Any jsontonbt$any : this.tagList) {
                t.print("parsing %s".formatted(jsontonbt$any.json));
                try {
                    nbttaglist.add(jsontonbt$any.parse());
                } catch(NBTConversionException e) {
                    throw new NBTParsingException("Conversion error",e);
                }
            }
            return t.ret(nbttaglist);
        }
    }

    static class Primitive extends Any
    {
        private static final Pattern DOUBLE = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[d|D]");
        private static final Pattern FLOAT = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+[f|F]");
        private static final Pattern BYTE = Pattern.compile("[-+]?[0-9]+[b|B]");
        private static final Pattern LONG = Pattern.compile("[-+]?[0-9]+[l|L]");
        private static final Pattern SHORT = Pattern.compile("[-+]?[0-9]+[s|S]");
        private static final Pattern INTEGER = Pattern.compile("[-+]?[0-9]+");
        private static final Pattern DOUBLE_UNTYPED = Pattern.compile("[-+]?[0-9]*\\.?[0-9]+");
        protected String jsonValue;

        public Primitive(String jsonIn, String valueIn)
        {
            t.invoke("new Primitive",jsonIn,valueIn);
            this.json = jsonIn;
            this.jsonValue = valueIn;
            t.ret("Primitive<%s,%s>".formatted(jsonIn,valueIn));
        }
        @Override
        public String toString() {
            return "Primitive<%s,%s>".formatted(json,jsonValue);
        }

        public NBTValue parse() throws NBTParsingException
        {
            t.invoke("Primitive.parse");
            try {
                final String substr = jsonValue.substring(0,Math.max(jsonValue.length()-1,0));
                if (DOUBLE.matcher(jsonValue).matches())
                    return t.print("matched double")
                            .ret(new NBTDouble(Double.parseDouble(substr)));
                if (FLOAT.matcher(jsonValue).matches())
                    return t.print("matched float")
                            .ret(new NBTFloat(Float.parseFloat(substr)));
                if (BYTE.matcher(jsonValue).matches())
                    return t.print("matched byte")
                            .ret(new NBTByte(Byte.parseByte(substr)));
                if (LONG.matcher(jsonValue).matches())
                    return t.print("matched long")
                            .ret(new NBTLong(Long.parseLong(substr)));
                if (SHORT.matcher(jsonValue).matches())
                    return t.print("matched short")
                            .ret(new NBTShort(Short.parseShort(substr)));
                if (INTEGER.matcher(jsonValue).matches())
                    return t.print("matched int")
                            .ret(new NBTInt(Integer.parseInt(jsonValue)));
                if (DOUBLE_UNTYPED.matcher(jsonValue).matches())
                    return t.print("matched double untyped")
                            .ret(new NBTDouble(Double.parseDouble(jsonValue)));
                if ("true".equalsIgnoreCase(jsonValue) || "false".equalsIgnoreCase(jsonValue))
                    return t.print("matched bool")
                            .ret(new NBTByte((byte)(Boolean.parseBoolean(jsonValue) ? 1 : 0)));
            } catch (NumberFormatException var6) {
                jsonValue = jsonValue.replaceAll("\\\\\"", "\"");
                return t.print("numeric failed, parsing <%s> as string instead".formatted(jsonValue))
                        .ret(new NBTString(jsonValue));
            }

            if (this.jsonValue.startsWith("[") && this.jsonValue.endsWith("]"))
            {
                String s = jsonValue.substring(1, jsonValue.length() - 1);
                t.print("parsing int array data <%s>".formatted(s));
                try {
                    final NBTArray arr = new NBTArray();
                    for(final String x : s.split(","))
                        if(!s.isEmpty()) arr.add(new NBTInt(Integer.parseInt(x.trim())));
                    return t.print("successfully parsed int array").ret(arr);
                }
                catch (NumberFormatException var5) {
                    return t.print("parsing int array failed, parsing <%s> as string instead".formatted(jsonValue))
                            .ret(new NBTString(jsonValue));
                } catch(final NBTConversionException e) {
                    throw new NBTParsingException("Conversion error",e);
                }
            }
            else
            {
                t.print("parsing <%s> as string".formatted(jsonValue));
                if (jsonValue.startsWith("\"") && jsonValue.endsWith("\"")) {
                    jsonValue = jsonValue.substring(1, jsonValue.length() - 1);
                    t.print("unwrapped quotes <%s>".formatted(jsonValue));
                }
                
                jsonValue = jsonValue.replaceAll("\\\\\"", "\"");
                t.print("replacing %s with %s -> <%s>".formatted("\\\\\"", "\"",jsonValue));
                StringBuilder stringbuilder = new StringBuilder();
                for (int i = 0; i < jsonValue.length(); ++i)
                {
                    if (i < jsonValue.length() - 1 && jsonValue.charAt(i) == '\\' && jsonValue.charAt(i + 1) == '\\')
                    {stringbuilder.append('\\');++i;}
                    else stringbuilder.append(jsonValue.charAt(i));
                }
                final String result = stringbuilder.toString();
                return t.print("un-escaped <%s>".formatted(result))
                        .ret(new NBTString(result));
            }
        }
    }
    
}






































