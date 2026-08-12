package domain;

public class Produto {
    private String id;
    private String nome;
    private String categoria;
    private double preco;
    private int quantidadeEmEstoque;
    private int estoqueMinimo;

    public Produto(String id, String nome, String categoria, double preco, int estoqueMinimo) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.preco = preco;
        this.quantidadeEmEstoque = 0; // Inicialização obrigatória zerada
        this.estoqueMinimo = estoqueMinimo;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    public int getEstoqueMinimo() { return estoqueMinimo; }

    public void adicionarEstoque(int quant) {
        if (quant > 0) {
            this.quantidadeEmEstoque += quant;
        }
    }

    public boolean removerEstoque(int quant) {
        if (quant > 0 && quant <= this.quantidadeEmEstoque) {
            this.quantidadeEmEstoque -= quant;
            return true;
        }
        return false;
    }

    public boolean isEstoqueBaixo() {
        return this.quantidadeEmEstoque <= this.estoqueMinimo;
    }

    public double getValorTotalEmEstoque() {
        return this.preco * this.quantidadeEmEstoque;
    }

    public String toJson() {
        return String.format(
            "{\"id\":\"%s\",\"nome\":\"%s\",\"categoria\":\"%s\",\"preco\":%.2f,\"quantidadeEmEstoque\":%d,\"estoqueMinimo\":%d,\"estoqueBaixo\":%b,\"valorTotal\":%.2f}",
            id, nome.replace("\"", "\\\""), categoria, preco, quantidadeEmEstoque, estoqueMinimo, isEstoqueBaixo(), getValorTotalEmEstoque()
        );
    }
}