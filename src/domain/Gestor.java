package domain;

public class Gestor extends Funcionario {
    private String areaGestao;

    public Gestor(String id, String nome, double salario, String areaGestao) {
        super(id, nome, salario);
        this.areaGestao = areaGestao;
    }

    public String getAreaGestao() { return areaGestao; }

    @Override
    public String getCargo() {
        return "Gestor de Operações";
    }

    @Override
    public String getAtividadeAtual() {
        return "Supervisionando inventário e coordenando equipe de " + areaGestao;
    }

    @Override
    public String toJson() {
        return String.format(
            "{\"id\":\"%s\",\"nome\":\"%s\",\"salario\":%.2f,\"cargo\":\"%s\",\"area\":\"%s\",\"atividade\":\"%s\"}",
            id, nome, salario, getCargo(), areaGestao, getAtividadeAtual()
        );
    }
}