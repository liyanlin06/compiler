package com.demo;

import com.sun.org.apache.bcel.internal.classfile.Code;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Lexer {
    // 这里定义全局的数据定位作为lineno和column
    private int lineno = 1;
    private int column = 1;
    private int pos = 0;
    // 当前指向的位置数值
    // private char current_char = ' ';

    // 获取数据，还是全局的数据，需要进行编译的数据序列
    private char[] sequence;

    // 构造器
    public Lexer() {
    }

    // 有参数的构造器
    public Lexer(char[] sequence) {
        this.sequence = sequence;
    }

    // 返回tokens的列表数据，相当于一张含有token的列表数据
    public List<Token> gather_all_tokens() throws IllegalAccessException {
        // 这里使用list<token>实现即可

        List<Token> tokens = new ArrayList<>();
        Token token = get_next_token();
        tokens.add(token);
        // ----------------------------------------------------------------------------------------------
        // 打印输出结果
        System.out.println("Lexer分析结果：");
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("%20s %20s %10s %10s %10s", "type", "value", "lineno", "column", "width");
        System.out.println();
        System.out.println("-----------------------------------------------------------------------------");
        System.out.format("%20s %20s %10s %10s %10s", token.getType(), token.getValue(), token.getLineno(), token.getColumn(), token.getWidth());
        System.out.println();
        // -----------------------------------------------------------------------------------------------
        while (!token.getType().equals(TokenType.TK_EOF)) {
            token = get_next_token();
            tokens.add(token);
            // 由于最后一个返回的是Token.EOF，其对应的值为null，所以这里的最后一个会打印出来null，不要奇怪！
            System.out.format("%20s %20s %10s %10s %10s", token.getType(), token.getValue(), token.getLineno(), token.getColumn(), token.getWidth());
            System.out.println();
        }

        System.out.println("-----------------------------------------------------------------------------");
        return tokens;
    }

    // 辅助函数，我们接着寻找下一个如果遇到换行符的情况下：
    // 修改全局变量的玩意儿
    public void advance(char[] sequence) {
        // 辅助函数，判断是否为换行符
        if (sequence[pos] == '\n') {
            lineno += 1;
            column = 0;
        }
        pos += 1;
        if (pos > sequence.length - 1) {
            // current_char = '\0';
        } else {
            // sequence[pos] = sequence[pos];
            column += 1;
        }
    }

    // 辅助函数，我们接着寻找下一个如果遇到空白符号的情况下：
    /*    public void skipwhitespace(char[] sequence) {
            while (sequence[pos] != '\0' && Character.isWhitespace(sequence[pos])) {
                advance(sequence);}}
     */
    // identifier标识符的正则表达式判断，不能以数字开头
    public boolean isidentifier(char ch) {
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ch == '_';
    }

    // 第二部分的identifier的判断
    public boolean isidentifier2(char ch) {
        return isidentifier(ch) || ('0' <= ch && ch <= '9');
    }

    // 判断数字类型的token序列
    public Token number(char ch) throws IllegalAccessException {
        // 判断当前是否遇到的是数值类型的语法，这是是数字判断分析器
        // 输出为一个token，该token是数字或浮点数类型的
        Token token = new Token();
        int old_column = column;
        StringBuilder result = new StringBuilder();
        while (sequence[pos] != '\0' && Character.isDigit(sequence[pos])) {
            result.append(sequence[pos]);
            advance(sequence);
        }
        // 这里只能判断整数类型的数据
        token.setType(isIn(TokenType.TK_INTEGER_CONST));
        token.setValue(result.toString());
        token.setLineno(lineno);
        token.setColumn(old_column);
        token.setWidth(column - old_column);
        return token;
    }

    // Read a punctuator token from p and returns
    // 含操作运算符的判断
    public int punctuator(String str) throws IllegalAccessException {
        // 这里是标点符号的判断，这里是标点符号的判断
        // 输出为一个token，该token是标点符号类型的
        // System.out.println("pun:"+str);
        if (str.equals("==") || str.equals("!=") || str.equals("<=") || str.equals(">=")) {
            return 2;
        } else {
            if (str.length() == 1) {
                if (isIn(str) != null) {
                    return 1;
                }
            }
        }
        return -1;
    }

    // 循环遍历接口
    public static String isIn(String str) throws IllegalAccessException {
        Class aClass = TokenType.class;
        Field[] fields = aClass.getDeclaredFields();
        for (Field field : fields) {
            if (field.get(aClass).equals(str)) {
                return field.getName();
            }
        }
        return null;
    }

    public Token get_next_token() throws IllegalAccessException {
        // 语法分析的主函数，这里我们认为是状态转换图
        // System.out.println("seq:"+sequence[pos]);
        while (pos < sequence.length && sequence[pos] != '\0') {
            // 首先判断是不是换行符号
            if (sequence[pos] == '\n') {
                advance(sequence);
                continue;
            }
            // 再判断是否为空格符号
            char ch = sequence[pos];
            if (Character.isSpaceChar(ch)) {
                advance(sequence);
                continue;
            }
            // 判断是否为数字
            if (Character.isDigit(ch)) {
                return number(ch);
            }
            // identifier的检测
            if (isidentifier(ch)) {
                Token token = new Token();
                // int old_column = column;
                StringBuilder result = new StringBuilder("" + ch);
                advance(sequence);
                while (sequence[pos] != '\0' && isidentifier2(sequence[pos])) {
                    result.append(sequence[pos]);
                    advance(sequence);
                }
                // 循环遍历
                if (isIn(result.toString()) != null) {
                    token.setType(isIn(result.toString()));
                    token.setValue(result.toString());
                    token.setColumn(column);
                    token.setLineno(lineno);
                    token.setWidth(result.length());
                    return token;
                } else {
                    token.setType(isIn(TokenType.TK_IDENT));
                    token.setValue(result.toString());
                    token.setColumn(column);
                    token.setLineno(lineno);
                    token.setWidth(result.length());
                    return token;
                }
            }
            // Punctuators
            //  two-characters punctuator
            // 符号的判断
            String str = "";
            if (pos < sequence.length - 1) {
                str += "" + sequence[pos] + sequence[pos + 1];
                // System.out.println("strtest:"+str);
            }
            if (punctuator(str) == 2) {
                Token token = new Token();
                token.setType(isIn(str));
                token.setValue(str);
                token.setLineno(lineno);
                token.setColumn(column);
                token.setWidth(2);
                advance(sequence);
                advance(sequence);
                return token;
            } else if (isIn("" + sequence[pos]) != null) {
                // System.out.println("test"+sequence[pos]);
                Token token = new Token();
                token.setType(isIn(sequence[pos] + ""));
                token.setValue(sequence[pos] + "");
                token.setLineno(lineno);
                token.setColumn(column);
                token.setWidth(1);
                advance(sequence);
                return token;
            } else {
                System.out.printf("lineno:" + lineno + ",column:" + column + ";invalid token!");
            }
        }
        return new Token(TokenType.TK_EOF, null);
    }
}


class Parser {
    // 引入前文的Lexer
    private Lexer lexer;
    // 全局的tokens序列的索引值
    private int index = -1;

    public Parser(Lexer lexer, List<Token> tokens) throws IllegalAccessException {
        this.lexer = lexer;
        this.tokens = tokens;
    }

    //引入前文的tokens序列
    private List<Token> tokens;
    private Token current_token;

    // 当前的函数名称
    private String current_function_name = "";

    // 获取lexer中的get_next_token方法函数
    public Token get_next_token(List<Token> tokens) {
        assert tokens != null;
        if (index < tokens.size() - 1) {
            index++;
            return tokens.get(index);
        } else {
            System.out.println("Array out of bounds");
            return null;
        }
    }

    // 出现错误那么就报告错误，error函数
    public void error(Token token) {
        System.out.println("Error parsing input in lineno=" + token.getLineno() + "; and column = " + token.getColumn() + "; and width= " + token.getWidth());
    }

    // 匹配当前token的类型是否合法
    public void match(String str) {
        if (Objects.equals(current_token.getType(), str)) {
            // 如果匹配，则获取下一个token序列
            System.out.println("当前匹配的token" + current_token.getType() + "匹配的字符串" + str);
            // System.out.println("当前匹配的token"+current_token.getType()+"匹配的字符串"+str);
            current_token = get_next_token(tokens);
        } else {
            // 错误匹配，我们则获取当前错误匹配的token
            error(current_token);
        }
    }

    // primary = "(" expr ")" | identifier args? | num
    // args = "(" (assign ("," assign)*)? ")"
    public AST_Node primary() {
        Token token = current_token;
        // "(" expr ")"
        if (Objects.equals(token.getType(), "TK_LPAREN")) {
            match("TK_LPAREN");
            AST_Node node = expression();
            match("TK_RPAREN");
            return node;
        }
        // identifier的匹配，对应于上述的或运算
        if (token.getType().equals("TK_IDENT")) {
            token = current_token;
            match("TK_IDENT");
            if (current_token.getType().equals("TK_LPAREN")) {
                String function_name = token.getValue();
                this.current_function_name = function_name;
                match("TK_LPAREN");
                List<AST_Node> actual_parameter_nodes = new ArrayList<>();
                if (!Objects.equals(current_token.getType(), "TK_RPAREN")) {
                    AST_Node node = expression();
                    actual_parameter_nodes.add(node);
                }
                while (current_token.getType().equals("TK_COMMA")) {
                    match("TK_COMMA");
                    AST_Node node = expression();
                    actual_parameter_nodes.add(node);
                }
                match("TK_RPAREN");
                return new FunctionCall_Node(function_name, actual_parameter_nodes, token);
            }

            // array_item的相关实现
            if (current_token.getType().equals("TK_LBRACK")) {
                Token array_name_token = token;
                match("TK_LBRACK");
                AST_Node index = expression();
                if (current_token.getType().equals("TK_RBRACK")) {
                    match("TK_RBRACK");
                    return new Var_array_item_Node(array_name_token, index);
                }
            }
            return new Var_Node(token);
        }
        // num (including bool type constants: true and false)
        if (Objects.equals(token.getType(), "TK_INTEGER_CONST")) {
            match("TK_INTEGER_CONST");
            return new Num_Node(token);
        } else if (Objects.equals(token.getType(), "TK_BOOL_CONST_TRUE")) {
            match("TK_BOOL_CONST_TRUE");
            return new Num_Node(token);
        } else if (Objects.equals(token.getType(), "TK_BOOL_CONST_FALSE")) {
            match("TK_BOOL_CONST_FALSE");
            return new Num_Node(token);
        }

        return null;
    }

    // unary = ("+" | "-" | "!") unary
    //        | primary
    public AST_Node unary() {
        Token token = current_token;
        if (Objects.equals(token.getType(), "TK_PLUS")) {
            match("TK_PLUS");
            return new UnaryOp_Node(token, unary());
        } else if (Objects.equals(token.getType(), "TK_MINUS")) {
            match("TK_MINUS");
            return new UnaryOp_Node(token, unary());
        } else if (Objects.equals(token.getType(), "TK_NOT")) {
            match("TK_NOT");
            return new UnaryOp_Node(token, unary());
        } else {
            return primary();
        }
    }

    // mul_div = unary ("*" unary | "/" unary)*
    public AST_Node mul_div() {
        AST_Node node = unary();
        while (true) {
            Token token = current_token;
            if (Objects.equals(current_token.getType(), "TK_MUL")) {
                match("TK_MUL");
                node = new BinaryOp_Node(node, token, unary());
                continue;
            } else if (Objects.equals(current_token.getType(), "TK_DIV")) {
                match("TK_DIV");
                node = new BinaryOp_Node(node, token, unary());
                continue;
            }
            return node;
        }
    }

    // add-sub = mul_div ("+" mul_div | "-" mul_div)*
    public AST_Node add_sub() {
        AST_Node node = mul_div();
        while (true) {
            Token token = current_token;
            if (Objects.equals(current_token.getType(), "TK_PLUS")) {
                match("TK_PLUS");
                node = new BinaryOp_Node(node, token, mul_div());
                continue;
            } else if (Objects.equals(current_token.getType(), "TK_MINUS")) {
                match("TK_MINUS");
                node = new BinaryOp_Node(node, token, mul_div());
                continue;
            }
            return node;
        }
    }


    // relational = add_sub ("<" add_sub | "<=" add_sub | ">" add_sub | ">=" add_sub)*
    public AST_Node relational() {
        AST_Node node = add_sub();
        while (true) {
            Token token = current_token;
            if (Objects.equals(current_token.getType(), "TK_LT")) {
                match("TK_LT");
                node = new BinaryOp_Node(node, token, add_sub());
                continue;
            } else if (Objects.equals(current_token.getType(), "TK_LE")) {
                match("TK_LE");
                node = new BinaryOp_Node(node, token, add_sub());
                continue;
            } else if (Objects.equals(current_token.getType(), "TK_GT")) {
                match("TK_GT");
                node = new BinaryOp_Node(node, token, add_sub());
                continue;
            } else if (Objects.equals(current_token.getType(), "TK_GE")) {
                match("TK_GE");
                node = new BinaryOp_Node(node, token, add_sub());
                continue;
            }
            return node;
        }
    }

    // equality = relational ("==" relational | "! =" relational)*
    public AST_Node equality() {
        AST_Node node = relational();
        while (true) {
            Token token = current_token;
            if (Objects.equals(current_token.getType(), "TK_EQ")) {
                match("TK_EQ");
                node = new BinaryOp_Node(node, token, relational());
                continue;
            } else if (Objects.equals(current_token.getType(), "TK_NE")) {
                match("TK_NE");
                node = new BinaryOp_Node(node, token, relational());
                continue;
            }
            return node;
        }
    }

    // logic = equality ("&&" equality | "||" equality)*
    public AST_Node logic() {
        AST_Node node = equality();
        while (true) {
            Token token = current_token;
            if (Objects.equals(current_token.getType(), "TK_AND")) {
                match("TK_AND");
                node = new BinaryOp_Node(node, token, equality());
                continue;
            } else if (Objects.equals(current_token.getType(), "TK_OR")) {
                match("TK_OR");
                node = new BinaryOp_Node(node, token, equality());
                continue;
            }
            return node;
        }
    }

    // expression := logic ("=" expression)?
    public AST_Node expression() {
        AST_Node node = logic();
        Token token = current_token;
        if (Objects.equals(token.getType(), "TK_ASSIGN")) {
            match("TK_ASSIGN");
            node = new Assign_Node(node, token, expression());
        }
        return node;
    }

    // expression-statement := expression? ";"
    public AST_Node expression_statement() {
        Token token = current_token;
        AST_Node node = null;
        if (Objects.equals(token.getType(), "TK_SEMICOLON")) {
            match("TK_SEMICOLON");
        } else {
            node = expression();
            if (Objects.equals(current_token.getType(), "TK_SEMICOLON")) {
                match("TK_SEMICOLON");
            } else {
                error(token);
            }
        }
        return node;
    }

    //    statement = expression-statement
    //               | "return" expression-statement
    //               | block
    //               | "if" "(" expression ")" statement ("else" statement)?
    //               | "while" "(" expression ")" statement

    public AST_Node statement() {
        Token token = current_token;
        AST_Node node = null;
        if (Objects.equals(token.getType(), "TK_RETURN")) {
            match("TK_RETURN");
            node = new Return_Node(token, expression_statement(), current_function_name);
            // System.out.println();
            // System.out.println(current_function_name);
            return node;
        } else if (Objects.equals(token.getType(), "TK_LBRACE")) {
            return block();
        } else if (Objects.equals(token.getType(), "TK_IF")) {
            AST_Node condition = null;
            AST_Node then_statement = null;
            AST_Node else_statement = null;
            match("TK_IF");
            if (Objects.equals(current_token.getType(), "TK_LPAREN")) {
                match("TK_LPAREN");
                condition = expression();
                match("TK_RPAREN");
                if (Objects.equals(current_token.getType(), "TK_THEN")) {
                    match("TK_THEN");
                    then_statement = statement();
                    if (Objects.equals(current_token.getType(), "TK_ELSE")) {
                        match("TK_ELSE");
                        else_statement = statement();
                    }
                }
            }
            return new If_Node(condition, then_statement, else_statement);
        } else if (Objects.equals(token.getType(), "TK_WHILE")) {
            AST_Node condition = null;
            AST_Node statement = null;
            match("TK_WHILE");
            if (Objects.equals(current_token.getType(), "TK_LPAREN")) {
                match("TK_LPAREN");
                condition = expression();
                match("TK_RPAREN");
                statement = statement();
                return new While_Node(condition, statement);
            }
        } else {
            return expression_statement();
        }
        return null;
    }

    // variable_declaration := type_specification identifier ("," indentifier)* ";"
    //                       | type_specification identifier "[" num "]" ("=" "{" (num)? ("," num)* "}")? ";"
    private List<AST_Node> variable_declaration() {
        BasicType_Node basictype_node = (BasicType_Node) type_specification();
        List<AST_Node> variable_nodes = new ArrayList<>();
        Token token = null;
        while (!Objects.equals(current_token.getType(), "TK_SEMICOLON")) {
            if (Objects.equals(current_token.getType(), "TK_IDENT")) {
                //  System.out.println(current_token);
                token = current_token;
                match("TK_IDENT");
                if (current_token.getType().equals("TK_LBRACK")) {
                    List<String> array_items = new ArrayList<>();
                    match("TK_LBRACK");
                    String array_size = new String();
                    if (current_token.getType().equals("TK_INTEGER_CONST")) {
                        array_size = current_token.getValue();
                        match("TK_INTEGER_CONST");
                        if (current_token.getType().equals("TK_RBRACK"))
                            match("TK_RBRACK");
                    }
                    if (current_token.getType().equals("TK_ASSIGN")) {
                        match("TK_ASSIGN");
                        if (current_token.getType().equals("TK_LBRACE")) {
                            match("TK_LBRACE");
                            while (!current_token.getType().equals("TK_RBRACE")) {
                                if (current_token.getType().equals("TK_INTEGER_CONST")) {
                                    array_items.add(current_token.getValue());
                                    match("TK_INTEGER_CONST");
                                }
                                if (current_token.getType().equals("TK_COMMA")) {
                                    match("TK_COMMA");
                                    if (current_token.getType().equals("TK_INTEGER_CONST")) {
                                        array_items.add(current_token.getValue());
                                        match("TK_INTEGER_CONST");
                                    } else {
                                        System.out.println("array item error at lineno= " + current_token.getLineno() + "; column = " + current_token.getColumn());
                                        System.exit(1);
                                    }
                                }
                            }
                            match("TK_RBRACE");
                            Var_Node var_node = new Var_Node(token, new Var_Node_In_Array_Value_J(array_size, array_items));
                            AST_Node node = new VarDecl_Node(basictype_node, var_node);
                            variable_nodes.add(node);
                        }
                    }
                } else {
                    Var_Node var_node = new Var_Node(token);
                    AST_Node node = new VarDecl_Node(basictype_node, var_node);
                    variable_nodes.add(node);
                    if (current_token.getType().equals("TK_COMMA")) {
                        match("TK_COMMA");
                    }
                    while (!current_token.getType().equals("TK_SEMICOLON")) {
                        if (current_token.getType().equals("TK_IDENT")) {
                            var_node = new Var_Node(current_token);
                            node = new VarDecl_Node(basictype_node, var_node);
                            match("TK_IDENT");
                            variable_nodes.add(node);
                            if (current_token.getType().equals("TK_COMMA")) {
                                match("TK_COMMA");
                            }
                        }
                    }
                }
            }
        }
        match("TK_SEMICOLON");
        return variable_nodes;
    }

    // compound_statement = (variable_declaration | statement)*
    private List<AST_Node> compound_statement() {
        List<AST_Node> statement_nodes = new ArrayList<>();
        while (!Objects.equals(current_token.getType(), "TK_RBRACE") && !Objects.equals(current_token.getType(), TokenType.TK_EOF)) {
            if (Objects.equals(current_token.getType(), "TK_INT") || Objects.equals(current_token.getType(), "TK_BOOL")) {
                List<AST_Node> variable_nodes = variable_declaration();
                statement_nodes.addAll(variable_nodes);
            } else {
                AST_Node node = statement();
                if (node != null) {
                    statement_nodes.add(node);
                }
            }
        }
        return statement_nodes;
    }

    //  block = "{" compound_statement "}"
    public AST_Node block() {
        if (Objects.equals(current_token.getType(), "TK_LBRACE")) {
            Token ltok = current_token;
            match("TK_LBRACE");
            List<AST_Node> statement_nodes = compound_statement();
            Token rtok = current_token;
            match("TK_RBRACE");
            return new Block_Node(ltok, rtok, statement_nodes);
        }
        return null;
    }


    // formal_parameter = type_specification identifier
    private AST_Node formal_parameter() {
        BasicType_Node basicType_node = (BasicType_Node) type_specification();
        Var_Node parameter_node = new Var_Node(current_token);
        match("TK_IDENT");
        return new FormalParam_Node(basicType_node, parameter_node);
    }

    // formal_parameters = formal_parameter (, formal_parameter)*
    public List<AST_Node> formal_parameters() {
        List<AST_Node> formal_params = new ArrayList<>();
        formal_params.add(formal_parameter());
        while (!Objects.equals(current_token.getType(), "TK_RPAREN")) {
            if (Objects.equals(current_token.getType(), "TK_COMMA")) {
                match("TK_COMMA");
                formal_params.add(formal_parameter());
            } else {
                System.out.println("parameter list error");
                System.exit(1);
            }
        }
        return formal_params;
    }


    // type_specification = int | bool
    public AST_Node type_specification() {
        Token token = current_token;
        if (Objects.equals(current_token.getType(), "TK_INT")) {
            match("TK_INT");
        } else if (Objects.equals(current_token.getType(), "TK_BOOL")) {
            match("TK_BOOL");
        }
        // System.out.println(token);
        AST_Node node = new BasicType_Node(token);
        return node;
    }

    // function_definition= type_specification identifier "(" formal_parameters? ")" block
    public AST_Node function_definition() {
        BasicType_Node basictype_node = (BasicType_Node) type_specification();
        String function_name = current_token.getValue();
        match("TK_IDENT");
        List<AST_Node> formal_params = new ArrayList<>();
        AST_Node block_node = null;
        if (Objects.equals(current_token.getType(), "TK_LPAREN")) {
            match("TK_LPAREN");
            if (!Objects.equals(current_token.getType(), "TK_RPAREN")) {
                formal_params = formal_parameters();
            }
            match("TK_RPAREN");
        }
        this.current_function_name = function_name;
        if (Objects.equals(current_token.getType(), "TK_LBRACE")) {
            block_node = block();
        } else {
            error(current_token);
        }
        // 这里缺少函数名
        return new FunctionDef_Node(basictype_node, current_function_name, formal_params, block_node);
    }

    /*
        program := function_definition*
        function_definition := type_specification identifier "(" formal_parameters? ")" block
        formal_parameters := formal_parameter ("," formal_parameter)*
        formal_parameter := type_specification identifier
        type_specification := "int" | "bool"
        block := "{" compound_statement "}"
        compound_statement := (variable_declaration | statement)*
        statement := expression-statement
                    | "return" expression-statement
                    | block
                    | "if" "(" expression ")" statement ("else" statement)?
                    | "while" "(" expression ")" statement
        variable_declaration := type_specification identifier ("," identifier)* ";"
                              | type_specification identifier "[" num "]" ("=" "{" (num)? ("," num)* "}")?
        expression-statement := expression? ";"
        expression := logic ("=" expression)?
        logic := equality ("&&" equality | "||" equality)*
        equality := relational ("==" relational | "! =" relational)*
        relational := add_sub ("<" add_sub | "<=" add_sub | ">" add_sub | ">=" add_sub)*
        add_sub := mul_div ("+" mul_div | "-" mul_div)*
        mul_div := unary ("*" unary | "/" unary)*
        unary := unary := ("+" | "-" | "!") unary | primary
        primary := "(" expression ")" | identifier args?| num | identifier "[" expression "]"
        args := "(" (expression ("," expression)*)? ")"
     */
    // program = function_definition*
    public List<AST_Node> parser(List<Token> tokens) {

        System.out.println("--------------------------------Parser Tree------------------------------");
        this.tokens = tokens;
        this.current_token = get_next_token(tokens);
        List<AST_Node> function_definition_nodes = new ArrayList<>();
        while (!Objects.equals(current_token.getType(), TokenType.TK_EOF)) {
            AST_Node node = function_definition();
            function_definition_nodes.add(node);
        }
        if (!Objects.equals(current_token.getType(), TokenType.TK_EOF)) {
            System.out.println("出错啦~");
            error(current_token);
            System.out.println("ErrorCode" + current_token.getType());
        }
        System.out.println("----------------------------构造结束----------------------------------------");
        return function_definition_nodes;
    }
}


public class Compiler {
    public static void main(String[] args) throws IOException, IllegalAccessException {

        // 1. 读取文件
        char sequences[] = new FileReader().file_text("src/com/demo/tmpc");
        System.out.println(sequences);
        // 2. 词法分析
        Lexer lexer = new Lexer(sequences);
        List<Token> tokens = lexer.gather_all_tokens();
        System.out.println("---------------------词法分析结束---------------------");

        // 3. 语法分析
        Parser parser = new Parser(lexer, tokens);
        List<AST_Node> node = parser.parser(tokens);
        System.out.println("---------------------语法分析结束---------------------");

        // 4. 语义分析
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.semantic_analyze(node);
        System.out.println("---------------------词义分析结束---------------------");

        // 5.代码生成
        CodeGenerator codeGenerator = new CodeGenerator();
        codeGenerator.code_generate(node);
        System.out.println("---------------------代码生成结束---------------------");


    }
}

