package com.demo;
import java.util.List;

abstract public class AST_Node {
    public abstract void accept(NodeVisitor nodeVisitor);
}

class UnaryOp_Node extends AST_Node {

    Token token;

    Token op;
    AST_Node right;

    public UnaryOp_Node(Token token, AST_Node right) {
        this.token = token;
        this.op = op = token;
        this.right = right;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_UnaryOp_Node(this);
    }
}

class If_Node extends AST_Node {
    AST_Node condition;
    AST_Node then_statement;
    AST_Node else_statement;

    public If_Node(AST_Node condition, AST_Node then_statement, AST_Node else_statement) {
        this.condition = condition;
        this.then_statement = then_statement;
        this.else_statement = else_statement;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_If_Node(this);
    }
}

class While_Node extends AST_Node {
    AST_Node condition, statement;

    public While_Node(AST_Node condition, AST_Node statement) {
        this.condition = condition;
        this.statement = statement;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_While_Node(this);
    }
}

class Return_Node extends AST_Node {
    Token token;
    AST_Node right;
    String function_name;

    public Return_Node(Token token, AST_Node right, String function_name) {
        this.token = token;
        this.right = right;
        this.function_name = function_name;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_Return_Node(this);
    }
}

class Block_Node extends AST_Node {
    Token ltoken, rtoken;
    List<AST_Node> statement_nodes;

    public Block_Node(Token ltoken, Token rtoken, List<AST_Node> statement_nodes) {
        this.ltoken = ltoken;
        this.rtoken = rtoken;
        this.statement_nodes = statement_nodes;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_Block_Node(this);
    }
}

class BinaryOp_Node extends AST_Node {
    Token token, op;
    AST_Node left, right;

    public BinaryOp_Node(AST_Node left,Token op, AST_Node right) {
        this.token = op;
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_BinaryOp_Node(this);
    }
}

class Assign_Node extends AST_Node {
    Token token, op;
    AST_Node left, right;

    public Assign_Node(AST_Node left, Token op,  AST_Node right) {
        this.token = op;
        this.op = op;
        this.left = left;
        this.right = right;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_Assign_Node(this);
    }
}

class FunctionCall_Node extends AST_Node {
    String function_name;
    List<AST_Node> actual_parameter_nodes;
    Token token;

    public FunctionCall_Node(String function_name, List<AST_Node> actual_parameter_nodes, Token token) {
        this.function_name = function_name;
        this.actual_parameter_nodes = actual_parameter_nodes;
        this.token = token;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_FunctionCall_Node(this);
    }
}

class Num_Node extends AST_Node {
    Token token;
    String value;

    public Num_Node(Token token) {
        this.token = token;
        this.value = token.getValue();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_Num_Node(this);
    }
}

class Var_Node_In_Array_Value_J {
    String size;
    List<String> value;

    public Var_Node_In_Array_Value_J(String size, List<String> value) {
        this.size = size;
        this.value = value;
    }
}

class Var_Node extends AST_Node {
    Token token;
    String name;
    //List<String> values = new ArrayList<>();
    Var_Node_In_Array_Value_J value_array = null;
    String array = null;
    Symbol symbol = null; ////////// -----------------------------------------------注意symbol类的重写

    public Var_Node(Token token) {
        this.token = token;
        this.name = token.getValue();
    }

    public Var_Node(Token token,Var_Node_In_Array_Value_J j) {
        this.token = token;
        this.name = token.getValue();
        this.value_array = j;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_Var_Node(this);
    }
}

class Var_array_item_Node extends AST_Node {
    Token token;
    AST_Node index;
    String array = "Yes";
    Symbol symbol = null;

    public Var_array_item_Node(Token token, AST_Node index) {
        this.token = token;
        this.index = index;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_Var_array_item_Node(this);
    }
}

class BasicType_Node extends AST_Node {
    Token token;
    String value;

    public BasicType_Node(Token token) {
        this.token = token;
        this.value = token.getValue();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_BasicType_Node(this);
    }
}

class VarDecl_Node extends AST_Node {
    BasicType_Node basicType_node;
    Var_Node var_node;

    public VarDecl_Node(BasicType_Node basicType_node, Var_Node var_node) {
        this.basicType_node = basicType_node;
        this.var_node = var_node;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_VarDecl_Node(this);
    }
}

class FormalParam_Node extends AST_Node {
    BasicType_Node basicType_node;
    Var_Node parameter_node;
    Symbol parameter_symbol = null;

    public FormalParam_Node(BasicType_Node basicType_node, Var_Node parameter_node) {
        this.basicType_node = basicType_node;
        this.parameter_node = parameter_node;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_FormalParam_Node(this);
    }
}

class FunctionDef_Node extends AST_Node {
    AST_Node basictype_node;
    String function_name;
    List<AST_Node> formal_parameters;
    AST_Node block_node;
    int offset = 0;

    public FunctionDef_Node(AST_Node type_node, String function_name, List<AST_Node> formal_parameters, AST_Node block_node) {
        this.basictype_node = type_node;
        this.function_name = function_name;
        this.formal_parameters = formal_parameters;
        this.block_node = block_node;
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit_FunctionDef_Node(this);
    }
}



