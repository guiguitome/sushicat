package br.com.trabalhofinalpoo.sushicat.backend;

public class InsumoGeral extends Item {
	
	public InsumoGeral() {
	    super(0, 0.0, "", 0, "Insumo Geral", false);
	}

    public InsumoGeral(int id, double valorCompra, String icone, int quantidade, String tipo, boolean desbloqueadoInicialmente) {
        super(id, valorCompra, icone, quantidade, tipo, desbloqueadoInicialmente);
    }

    @Override
    public void avancarDia() {

    }

    @Override
    public boolean deveSerRemovido() {
        return false;
    }
    
    @Override
    public Item criarCopiaParaCompra(int quantidade) {
        return new InsumoGeral(this.getId(), this.getValorCompra(), this.getIcone(), quantidade, this.getTipo(), this.isDesbloqueado());
    }
}