package domain;

public abstract class Funcionario {
    protected String id;
    protected String nome;
    protected double salario;

    public Funcionario(String id, String nome, double salario) {
        this.id = id;
        this.nome = nome;
        this.salario = salario;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getSalario() { return salario; }
    public void setSalario(double salario) { this.salario = salario; }

    public abstract String getCargo();
    public abstract String getAtividadeAtual();
    public abstract String toJson();
}