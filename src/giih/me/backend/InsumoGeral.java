package giih.me.backend;

public class InsumoGeral extends Item {
	
	public InsumoGeral() {
	    super(0, 0.0, "", 0, "Insumo Geral", false);
	}

    public InsumoGeral(int id, double valorCompra, String icone, int quantidade, String tipo, boolean desbloqueadoInicialmente) {
        super(id, valorCompra, icone, quantidade, tipo, desbloqueadoInicialmente);
    }

    @Override
    public void avancarDia() {
        // Não faz nada, pois este item não estraga com o tempo.
    }

    @Override
    public boolean deveSerRemovido() {
        // Nunca é removido por validade.
        return false;
    }
    
    @Override
    public Item criarCopiaParaCompra(int quantidade) {
        return new InsumoGeral(this.getId(), this.getValorCompra(), this.getIcone(), quantidade, this.getTipo(), this.isDesbloqueado());
    }
}