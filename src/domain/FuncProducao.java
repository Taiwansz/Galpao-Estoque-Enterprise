package domain;

public class FuncProducao extends Funcionario {
    private String turno;
    private String setor;

    public FuncProducao(String id, String nome, double salario, String turno, String setor) {
        super(id, nome, salario);
        this.turno = turno;
        this.setor = setor;
    }

    public String getTurno() { return turno; }
    public String getSetor() { return setor; }

    @Override
    public String getCargo() {
        return "Operador de Produção";
    }

    @Override
    public String getAtividadeAtual() {
        return "Operando equipamentos de movimentação no setor " + setor + " (Turno: " + turno + ")";
    }

    @Override
    public String toJson() {
        return String.format(
            "{\"id\":\"%s\",\"nome\":\"%s\",\"salario\":%.2f,\"cargo\":\"%s\",\"turno\":\"%s\",\"setor\":\"%s\",\"atividade\":\"%s\"}",
            id, nome, salario, getCargo(), turno, setor, getAtividadeAtual()
        );
    }
}