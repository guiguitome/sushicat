package br.com.trabalhofinalpoo.sushicat.backend;

public class Peixe extends Item implements IPerecivel {
    private int validade;
    
    public Peixe() {
        super(0, 0, "", 0, "Peixe", false);
        this.validade = 0;
    }

    public Peixe(int id, double valorCompra, int quantidade, String icone, int validade, boolean desbloqueadoInicialmente) {
        super(id, valorCompra, icone, quantidade, "Peixe", desbloqueadoInicialmente);
        this.validade = validade;
    }

    @Override
    public void avancarDia() {
        this.validade--;
    }

    @Override
    public boolean deveSerRemovido() {
        return this.validade <= 0;
    }

    @Override
    public Item criarCopiaParaCompra(int quantidade) {
        return new Peixe(this.getId(), this.getValorCompra(), quantidade, this.getIcone(), this.getValidade(), this.isDesbloqueado());
    }

    public int getValidade() {
        return validade;
    }
}