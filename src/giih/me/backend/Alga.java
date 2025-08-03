package giih.me.backend;

public class Alga extends Item implements IPerecivel{
	private int validade;
	
    public Alga() {
        super(0, 0, "", 0, "Alga", false);
        this.validade = 0;
    }
    
	public Alga(int id, double valorCompra, int quantidade, String icone, int validade, boolean desbloqueadoInicialmente) {
	    super(id, valorCompra, icone, quantidade, "Alga", desbloqueadoInicialmente);
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
    public String getDetalhes() {
        return " | Validade: " + this.validade + " dia(s)";
    }
    
    @Override
    public Item criarCopiaParaCompra(int quantidade) {
        return new Alga(this.getId(), this.getValorCompra(), quantidade, this.getIcone(), this.getValidade(), this.isDesbloqueado());
    }

    public int getValidade() {
        return validade;
    }
}
