package server;

import domain.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EstoqueDatabase {
    private final Map<String, Produto> produtos = new ConcurrentHashMap<>();
    private final Map<String, Funcionario> funcionarios = new ConcurrentHashMap<>();

    public EstoqueDatabase() {
        // Dados Iniciais de Demonstração
        Produto p1 = new Produto("PRD-001", "Caixa de Ferramentas Industriais", "Equipamentos", 350.00, 10);
        p1.adicionarEstoque(45);
        Produto p2 = new Produto("PRD-002", "Palete de Madeira Tratada (120x80)", "Logística", 125.50, 20);
        p2.adicionarEstoque(120);
        Produto p3 = new Produto("PRD-003", "Empilhadeira Elétrica Compacta", "Maquinário", 28500.00, 2);
        p3.adicionarEstoque(3);
        Produto p4 = new Produto("PRD-004", "Fita de Arquear Polipropileno", "Insumos", 45.00, 15);
        p4.adicionarEstoque(8); // Estoque Baixo!

        produtos.put(p1.getId(), p1);
        produtos.put(p2.getId(), p2);
        produtos.put(p3.getId(), p3);
        produtos.put(p4.getId(), p4);

        FuncProducao f1 = new FuncProducao("FUNC-001", "Carlos Eduardo Silva", 3400.00, "Manhã", "Recebimento");
        FuncProducao f2 = new FuncProducao("FUNC-002", "Felipe Pinete", 3400.00, "Tarde", "Expedição");
        Gestor g1 = new Gestor("MGR-001", "Ana Beatriz Souza", 8200.00, "Logística & Inventário");

        funcionarios.put(f1.getId(), f1);
        funcionarios.put(f2.getId(), f2);
        funcionarios.put(g1.getId(), g1);
    }

    public Collection<Produto> getProdutos() { return produtos.values(); }
    public Produto getProduto(String id) { return produtos.get(id); }
    public void addProduto(Produto p) { produtos.put(p.getId(), p); }

    public Collection<Funcionario> getFuncionarios() { return funcionarios.values(); }

    public String getDashboardStatsJson() {
        double valorTotalEstoque = 0;
        int totalProdutos = produtos.size();
        int totalItensFisicos = 0;
        int alertasEstoqueBaixo = 0;

        for (Produto p : produtos.values()) {
            valorTotalEstoque += p.getValorTotalEmEstoque();
            totalItensFisicos += p.getQuantidadeEmEstoque();
            if (p.isEstoqueBaixo()) alertasEstoqueBaixo++;
        }

        return String.format(
            "{\"valorTotalEstoque\":%.2f,\"totalProdutos\":%d,\"totalItensFisicos\":%d,\"alertasEstoqueBaixo\":%d,\"totalFuncionarios\":%d}",
            valorTotalEstoque, totalProdutos, totalItensFisicos, alertasEstoqueBaixo, funcionarios.size()
        );
    }
}