package com.demo;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

class Symbol {
    private String name;
    private String type = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public Symbol(String name) {
        this.name = name;
    }
}

class Var_Symbol extends Symbol {
    private int var_offset;
//    private AST_Node block_ast = null;

    Symbol symbol = null;

    public int getVar_offset() {
        return var_offset;
    }

    public void setVar_offset(int var_offset) {
        this.var_offset = var_offset;
    }

//    public AST_Node getBlock_ast() {
//        return block_ast;
//    }

//    public void setBlock_ast(AST_Node block_ast) {
//        this.block_ast = block_ast;
//    }

    public Var_Symbol(String name, String type, int var_offset) {
        super(name, type);
        this.var_offset = var_offset;
    }
}

class Function_Symbol extends Symbol {

    private List<Var_Symbol> formal_params = new ArrayList<>();

    AST_Node block_ast = null;

    public List<Var_Symbol> getFormal_params() {
        return formal_params;
    }

    public void setFormal_params(List<Var_Symbol> formal_params) {
        this.formal_params = formal_params;
    }

    public Function_Symbol(String name, List<Var_Symbol> formal_params) {
        super(name);
        this.formal_params = formal_params;
    }

    public Function_Symbol(String name) {
        super(name);
    }
}

class Parameter_Symbol extends Symbol {

    private int offset;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public Parameter_Symbol(String name, String type, int offset) {
        super(name, type);
    }

    public Parameter_Symbol(String name) {
        super(name);
    }
}

class ScopedSymbolTable {
    // 哈希表
    Dictionary<String, Symbol> _symbols = new Hashtable<>();
    String scope_name;
    int scope_level;
    ScopedSymbolTable enclosing_scope = null;

    public ScopedSymbolTable(String scope_name, int scope_level, ScopedSymbolTable enclosing_scope) {
        this.scope_name = scope_name;
        this.scope_level = scope_level;
        this.enclosing_scope = enclosing_scope;
    }

    public ScopedSymbolTable(String scope_name, int scope_level) {
        this.scope_name = scope_name;
        this.scope_level = scope_level;
    }

    public void insert(Symbol symbol) {
        this._symbols.put(symbol.getName(), symbol);
    }


    public Symbol lookup(String name) {
        boolean current_scope_only = false;
        Symbol symbol = this._symbols.get(name);
        if (symbol != null)
            return symbol;
        if (current_scope_only)
            return null;
        if (enclosing_scope != null)
            return this.enclosing_scope.lookup(name);
        return null;
    }

    public Symbol lookup(String name, boolean current_scope_only) {
        Symbol symbol = _symbols.get(name);

        if (symbol != null) {
            return symbol;
        }
        if (current_scope_only) {
            return null;
        }

        if (enclosing_scope != null) {
            return enclosing_scope.lookup(name);
        }
        return null;
    }
}

class Offset {
    public static int sum = -999;
}
class Count{
    public static int i = 0;
}

public class SemanticAnalyzer extends NodeVisitor {

    public static boolean _SHOULD_LOG_SCOPE = false;
    // 符号表
    ScopedSymbolTable current_scope;

    public SemanticAnalyzer() {
        current_scope = null;
        ScopedSymbolTable global_scope = new ScopedSymbolTable("global", 0, current_scope);
        current_scope = global_scope;
    }

    public void log(String msg) {
        if (_SHOULD_LOG_SCOPE)
            System.out.println(msg);
    }

    @Override
    public void visit_UnaryOp_Node(UnaryOp_Node node) {
        node.right.accept(this);
        System.out.println("visit UnaryOp_Node");

    }

    @Override
    public void visit_Return_Node(Return_Node node) {
        node.right.accept(this);
        System.out.println("visit Return_Node");
    }

    @Override
    public void visit_BinaryOp_Node(BinaryOp_Node node) {
        node.left.accept(this);
        node.right.accept(this);
        System.out.println("visit BinaryOp_Node");
    }

    @Override
    public void visit_Assign_Node(Assign_Node node) {
        node.left.accept(this);
        node.right.accept(this);
        System.out.println("visit Assign_Node");
    }

    @Override
    public void visit_If_Node(If_Node node) {
        node.condition.accept(this);
        if (node.then_statement != null) {
            node.then_statement.accept(this);
        }
        if (node.else_statement != null) {
            node.else_statement.accept(this);
        }
        System.out.println("visit If_Node");
    }

    @Override
    public void visit_While_Node(While_Node node) {
        node.condition.accept(this);
        if (node.statement != null) {
            node.statement.accept(this);
        }
        System.out.println("visit While_Node");
    }

    @Override
    public void visit_Block_Node(Block_Node node) {
        String block_name = current_scope.scope_name + " block" + (current_scope.scope_level + 1);
        log(block_name);
        ScopedSymbolTable block_scope = new ScopedSymbolTable(block_name,
                current_scope.scope_level + 1, current_scope);
        this.current_scope = block_scope;
        for (AST_Node node1 : node.statement_nodes) {
            node1.accept(this);
        }
        this.current_scope = current_scope.enclosing_scope;
        log("LEAVE scope" + block_scope);
        System.out.println("visit Block_Node");
    }

    @Override
    public void visit_Num_Node(Num_Node node) {
        System.out.println("visit Num_Node");
    }

    @Override
    public void visit_Var_Node(Var_Node node) {
        String var_name = node.name;
        Symbol var_symbol = current_scope.lookup(var_name);
        if (var_symbol == null) {
            System.out.println("semantic error, var\"" + var_name + "\" not declared");
            System.exit(1);
        } else {
            node.symbol = var_symbol;
        }
        System.out.println("visit Var_Node");
    }

    @Override
    public void visit_Var_array_item_Node(Var_array_item_Node node) {
        String array_name = node.token.getValue();
        Symbol array_symbol = current_scope.lookup(array_name);
        if (array_symbol == null) {
            System.out.println("semantic error, array variable \"" + array_name + "\" not declared");
            System.exit(1);
        } else {
            node.symbol = array_symbol;
            node.index.accept(this);
        }
        System.out.println("visit Var_array_item_Node");
    }

    @Override
    public void visit_BasicType_Node(BasicType_Node node) {
        System.out.println("visit BasicType_Node");

    }

    @Override
    public void visit_VarDecl_Node(VarDecl_Node node) {
        String var_name = node.var_node.name;
        String var_basictype = node.basicType_node.value;
        if (node.var_node.value_array != null) {
            Offset.sum += 8 * Integer.parseInt(node.var_node.value_array.size);
            int var_offset = -Offset.sum;
            Symbol var_symbol = new Var_Symbol(var_name, var_basictype, var_offset);
            node.var_node.symbol = var_symbol;
            this.current_scope.insert(var_symbol);
        } else {
            Offset.sum += 8;
            int var_offset = -Offset.sum;
            Symbol var_symbol = new Var_Symbol(var_name, var_basictype, var_offset);
            this.current_scope.insert(var_symbol);
        }
        System.out.println("visit VarDecl_Node");

    }

    @Override
    public void visit_FormalParam_Node(FormalParam_Node node) {
        String parameter_name = node.parameter_node.name;
        String parameter_type = node.basicType_node.value;
        Offset.sum += 8;
        int parameter_offset = -Offset.sum;
        Symbol parameter_symbol = new Parameter_Symbol(parameter_name, parameter_type, parameter_offset);
        current_scope.insert(parameter_symbol);
        node.parameter_symbol = parameter_symbol;
        System.out.println("visit FormalParam_Node");
    }

    @Override
    public void visit_FunctionDef_Node(FunctionDef_Node node) {
        Offset.sum = 0;
        String function_name = node.function_name;
        Function_Symbol function_symbol = new Function_Symbol(function_name);
        current_scope.insert(function_symbol);

        ScopedSymbolTable function_scope = new ScopedSymbolTable(function_name, current_scope.scope_level + 1, current_scope);
        current_scope = function_scope;

        for (AST_Node node1 : node.formal_parameters) {
            node1.accept(this);
        }
        node.block_node.accept(this);
        node.offset = Offset.sum;
        current_scope = current_scope.enclosing_scope;
        function_symbol.block_ast = node.block_node;
        System.out.println("visit FunctionDef_Node");
    }

    @Override
    public void visit_FunctionCall_Node(FunctionCall_Node node) {
        System.out.println("visit FunctionCall_Node");

    }


    public void semantic_analyze(List<AST_Node> tree) {
        System.out.println("语义分析");
        for (AST_Node node : tree) {
            if (node != null) {
                node.accept(this);
            }
        }
    }
}
