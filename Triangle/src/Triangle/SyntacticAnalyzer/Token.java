package Triangle.SyntacticAnalyzer;

final class Token extends Object {

    protected int kind;
    protected String spelling;
    protected SourcePosition position;

    public Token(int kind, String spelling, SourcePosition position) {

        if (kind == Token.IDENTIFIER) {
            int currentKind = firstReservedWord;
            boolean searching = true;

            while (searching) {
                int comparison = tokenTable[currentKind].compareTo(spelling);
                if (comparison == 0) {
                    this.kind = currentKind;
                    searching = false;
                } else if (comparison > 0 || currentKind == lastReservedWord) {
                    this.kind = Token.IDENTIFIER;
                    searching = false;
                } else {
                    currentKind++;
                }
            }
        } else {
            this.kind = kind;
        }

        this.spelling = spelling;
        this.position = position;
    }

    public static String spell(int kind) {
        return tokenTable[kind];
    }

    public String toString() {
        return "Kind=" + kind + ", spelling=" + spelling
                + ", position=" + position;
    }

    // Token classes...
    public static final int // literals, identifiers, operators...
            INTLITERAL = 0,
            CHARLITERAL = 1,
            IDENTIFIER = 2,
            OPERATOR = 3,
            // reserved words - must be in alphabetical order...
            ARRAY = 4,
            BEGIN = 5,
            CASE = 6,
            CONST = 7,
            DO = 8,
            DOWNTO = 9,
            ELSE = 10,
            END = 11,
            FALSE = 12,
            FOR = 13,
            FUNC = 14,
            IF = 15,
            IN = 16,
            LET = 17,
            MATCH = 18,
            OF = 19,
            OTHERWISE = 20,
            PROC = 21,
            RECORD = 22,
            REPEAT = 23,
            THEN = 24,
            TO = 25,
            TRUE = 26,
            TYPE = 27,
            UNTIL = 28,
            VAR = 29,
            WHILE = 30,
            // punctuation...
            DOT = 31,
            COLON = 32,
            SEMICOLON = 33,
            COMMA = 34,
            BECOMES = 35,
            IS = 36,
            // brackets...
            LPAREN = 37,
            RPAREN = 38,
            LBRACKET = 39,
            RBRACKET = 40,
            LCURLY = 41,
            RCURLY = 42,
            // special tokens...
            EOT = 43,
            ERROR = 44;

    private static String[] tokenTable = new String[]{
        "<int>",        // 0
        "<char>",       // 1
        "<identifier>", // 2
        "<operator>",   // 3
        "array",        // 4
        "begin",        // 5
        "case",         // 6
        "const",        // 7
        "do",           // 8
        "downto",       // 9
        "else",         // 10
        "end",          // 11
        "false",        // 12
        "for",          // 13
        "func",         // 14
        "if",           // 15
        "in",           // 16
        "let",          // 17
        "match",        // 18
        "of",           // 19
        "otherwise",    // 20
        "proc",         // 21
        "record",       // 22
        "repeat",       // 23
        "then",         // 24
        "to",           // 25
        "true",         // 26
        "type",         // 27
        "until",        // 28 ✅
        "var",          // 29
        "while",        // 30
        ".",            // 31
        ":",            // 32
        ";",            // 33
        ",",            // 34
        ":=",           // 35
        "~",            // 36
        "(",            // 37
        ")",            // 38
        "[",            // 39
        "]",            // 40
        "{",            // 41
        "}",            // 42
        "",             // 43 (EOT)
        "<error>",      // 44
    };

    private final static int firstReservedWord = Token.ARRAY;
    private final static int lastReservedWord = Token.WHILE;

}
