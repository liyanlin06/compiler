package com.demo;

import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;

import java.util.List;
import java.util.Objects;

public class CodeGenerator extends NodeVisitor {

    public String[] parameter_registers = {"rdi", "rsi", "rdx", "rcx", "r8", "r9"};

    public int align_to(int n, int align) {
        return ((n + align - 1) / align) * align;
    }

    @Override
    public void visit_UnaryOp_Node(UnaryOp_Node node) {
        if (node.op.getType().equals("TK_MINUS")) {
            node.right.accept(this);
            System.out.println("    neg %rax");
        } else if (node.op.getType().equals("TK_NOT")) {
            node.right.accept(this);
            System.out.println("    not %rax");
        }
    }

    @Override
    public void visit_Return_Node(Return_Node node) {
        node.right.accept(this);
        if (node.token.getType().equals("TK_RETURN")) {
            System.out.println("    jmp ." + node.function_name + ".return");
        }
    }

    @Override
    public void visit_BinaryOp_Node(BinaryOp_Node node) {
        node.right.accept(this);
        System.out.println("    push %rax");
        node.left.accept(this);
        System.out.println("    pop %rdi");
        if (node.op.getType().equals("TK_PLUS")) {
            System.out.println("    add %rdi, %rax");
        } else if (node.op.getType().equals("TK_MINUS")) {
            System.out.println("    sub %rdi, %rax");
        } else if (node.op.getType().equals("TK_MUL")) {
            System.out.println("    imul %rdi, %rax");
        } else if (node.op.getType().equals("TK_DIV")) {
            System.out.println("    cqo");
            System.out.println("    idiv %rdi");
        } else if (node.op.getType().equals("TK_EQ")) {
            System.out.println("    cmp %rdi, %rax");
            System.out.println("    sete %al");
            System.out.println("    movzb %al, %rax");
        } else if (node.op.getType().equals("TK_NE")) {
            System.out.println("    cmp %rdi, %rax");
            System.out.println("    setne %al");
            System.out.println("    movzb %al, %rax");
        } else if (node.op.getType().equals("TK_LT")) {
            System.out.println("    cmp %rdi, %rax");
            System.out.println("    setl %al");
            System.out.println("    movzb %al, %rax");
        } else if (node.op.getType().equals("TK_GT")) {
            System.out.println("    cmp %rdi, %rax");
            System.out.println("    setg %al");
            System.out.println("    movzb %al, %rax");
        } else if (node.op.getType().equals("TK_LE")) {
            System.out.println("    cmp %rdi, %rax");
            System.out.println("    setle %al");
            System.out.println("    movzb %al, %rax");
        } else if (node.op.getType().equals("TK_GE")) {
            System.out.println("    cmp %rdi, %rax");
            System.out.println("    setge %al");
            System.out.println("    movzb %al, %rax");
        } else if (node.op.getType().equals("TK_AND")) {
            System.out.println("    and %rdi, %rax");
        } else if (node.op.getType().equals("TK_OR")) {
            System.out.println("    or %rdi, %rax");
        }
    }

    public void generate_array_item_address(Var_array_item_Node node) {
        int offset = ((Var_Symbol) node.symbol).getVar_offset();
        if (node.index instanceof Var_Node) {
            node.index.accept(this);
            System.out.println("    sub $1, %rax");
            System.out.println("    imul $8, %rax");
            System.out.println("    push %rax");
            System.out.println("    lea " + offset + "(%rbp), %rax");
            System.out.println("    pop %rdi");
            System.out.println("    add %rdi, %rax");
        } else {
            int array_item_offset = (Integer.parseInt(((Num_Node) node.index).value) - 1) * 8;
            System.out.println("    mov $" + array_item_offset + ", %rax");
            System.out.println("    push %rax");
            System.out.println("    lea " + offset + "(%rbp), %rax");
            System.out.println("    pop %rdi");
            System.out.println("    add %rdi, %rax");
        }
    }

    @Override
    public void visit_Assign_Node(Assign_Node node) {
        if (node.left instanceof Var_Node) {
            if (((Var_Node) node.left).token.getType().equals("TK_IDENT")) {
                int var_offset = ((Var_Symbol) ((Var_Node) node.left).symbol).getVar_offset();
                System.out.println("    lea " + var_offset + "(%rbp), %rax");
                System.out.println("    push %rax");
                if (((Var_Node) node.left).array != null) {
                    generate_array_item_address((Var_array_item_Node) node.left);
                    System.out.println("    push %rax");
                }
                node.right.accept(this);
                System.out.println("    pop %rdi");
                System.out.println("    mov %rax, (%rdi)");
            } else {
                System.out.println("not an lvalue");
            }
        } else {
            if (((Var_array_item_Node) node.left).token.getType().equals("TK_IDENT")) {
                int var_offset = ((Var_Symbol) ((Var_array_item_Node) node.left).symbol).getVar_offset();
                System.out.println("    lea " + var_offset + "(%rbp), %rax");
                System.out.println("    push %rax");
                if (((Var_array_item_Node) node.left).array != null) {
                    generate_array_item_address((Var_array_item_Node) node.left);
                    System.out.println("    push %rax");
                }
                node.right.accept(this);
                System.out.println("    pop %rdi");
                System.out.println("    mov %rax, (%rdi)");
            } else {
                System.out.println("not an lvalue");
            }
        }

    }

    @Override
    public void visit_If_Node(If_Node node) {
        Count.i += 1;
        int localLabel = Count.i;
        node.condition.accept(this);
        System.out.println("    cmp $0, %rax");
        System.out.println("    je  .L.else." + localLabel);
        if (node.then_statement != null) {
            node.then_statement.accept(this);
        }
        System.out.println("    jmp .L.endd." + localLabel);
        System.out.println(".L.else." + localLabel + ":");
        if (node.else_statement != null) {
            node.else_statement.accept(this);
        }
        System.out.println(".L.endd." + localLabel + ":");
    }

    @Override
    public void visit_While_Node(While_Node node) {
        Count.i += 1;
        int localLabel = Count.i;
        System.out.println(".L.condition." + localLabel + ":");
        node.condition.accept(this);
        System.out.println("    cmp $0, %rax");
        System.out.println("    je  .L.end." + localLabel);
        if (node.statement != null) {
            node.statement.accept(this);
        }
        System.out.println("    jmp .L.condition." + localLabel);
        System.out.println(".L.end." + localLabel + ":");
    }

    @Override
    public void visit_Block_Node(Block_Node node) {
        for (AST_Node node1 : node.statement_nodes) {
            node1.accept(this);
        }
    }

    @Override
    public void visit_Num_Node(Num_Node node) {
        if (Objects.equals(node.value, "true")) {
            System.out.println("    mov $1, %rax");
        } else if (Objects.equals(node.value, "false")) {
            System.out.println("    mov $0, %rax");
        } else {
            System.out.println("    mov $" + node.value + ", %rax");
        }
    }

    @Override
    public void visit_Var_Node(Var_Node node) {
        int var_offset = ((Var_Symbol) node.symbol).getVar_offset();
        System.out.println("    lea " + var_offset + "(%rbp), %rax");
        System.out.println("    mov (%rax), %rax");
    }

    @Override
    public void visit_VarDecl_Node(VarDecl_Node node) {
        if (node.var_node.value_array != null) {
            int array_offset = ((Var_Symbol) node.var_node.symbol).getVar_offset();
            int array_size = Integer.parseInt(node.var_node.value_array.size);
            int i = 0;
            while (i < array_size) {
                int array_item_offset = i * 8;
                System.out.println("    mov $" + array_item_offset + ", %rax");
                System.out.println("    push %rax");
                System.out.println("    lea " + array_offset + "(%rbp), %rax");
                System.out.println("    pop %rdi");
                System.out.println("    add %rdi, %rax");

                int item_value = Integer.parseInt(node.var_node.value_array.value.get(i));
                System.out.println("    mov $" + item_value + ", %rdi");
                System.out.println("    mov %rdi, (%rax)");
                i += 1;
            }
        }
    }

    @Override
    public void visit_FormalParam_Node(FormalParam_Node node) {
        return;
    }

    @Override
    public void visit_FunctionDef_Node(FunctionDef_Node node) {
        Offset.sum = 0;
        System.out.println("    .text");
        System.out.println("    .global " + node.function_name);
        System.out.println(node.function_name + ":");
        System.out.println("    push %rbp");
        System.out.println("    mov %rsp, %rbp");
        int stack_size = align_to(node.offset, 16);
        System.out.println("    sub $" + stack_size + ", %rsp");

        int i = 0;
        for (AST_Node node1 : node.formal_parameters) {
            int parameter_offset = ((Parameter_Symbol) ((FormalParam_Node) node1).parameter_symbol).getOffset();
            System.out.println("    mov %" + parameter_registers[i] + ", " + parameter_offset + "(%rbp)");
            i += 1;
        }
        node.block_node.accept(this);
        System.out.println("." + node.function_name + ".return:");
        System.out.println("    mov %rbp, %rsp");
        System.out.println("    pop %rbp");
        System.out.println("    ret");
    }

    @Override
    public void visit_FunctionCall_Node(FunctionCall_Node node) {
        int nparams = 0;
        for (AST_Node node1 : node.actual_parameter_nodes) {
            node1.accept(this);
            System.out.println("    push %rax");
            nparams += 1;
        }
        for (int i = nparams; i > 0; i--) {
            System.out.println("    pop %" + parameter_registers[i - 1]);
        }
        System.out.println("    mov $0, %rax");
        System.out.println("    call " + node.function_name);
    }

    @Override
    public void visit_Var_array_item_Node(Var_array_item_Node node) {
        generate_array_item_address(node);
        System.out.println("    mov (%rax), %rax");
    }

    @Override
    public void visit_BasicType_Node(BasicType_Node node) {

    }

    public void code_generate(List<AST_Node> tree) {
        for (AST_Node node : tree) {
            if (node != null) {
                node.accept(this);
            }
        }
    }
}
