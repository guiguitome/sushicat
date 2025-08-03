package giih.me.backend;

/**
 * Representa um item genérico no jogo, como um ingrediente ou insumo.
 * É uma classe abstrata, pois define um conceito que precisa ser 
 * implementado por classes concretas (como Peixe, InsumoGeral, etc.).
 */
public abstract class Item {

    // Atributos comuns a todos os itens
    private int id;
    private double valorCompra;
    private String icone;
    private int quantidade;
    private String tipo;
    private boolean desbloqueado; // NOVO ATRIBUTO para o sistema de desbloqueio

    /**
     * Construtor principal e mais completo, que define o estado de desbloqueio.
     * Todas as classes filhas (Peixe, Arroz, etc.) deverão chamar este construtor.
     */
    public Item(int id, double valorCompra, String icone, int quantidade, String tipo, boolean desbloqueadoInicialmente) {
        this.id = id;
        this.valorCompra = valorCompra;
        this.icone = icone;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.desbloqueado = desbloqueadoInicialmente;
    }

    // --- MÉTODOS ABSTRATOS ---
    // Classes filhas (como Peixe) SÃO OBRIGADAS a implementar estes métodos.

    public abstract void avancarDia();
    public abstract boolean deveSerRemovido();
    public abstract Item criarCopiaParaCompra(int quantidade);


    // --- MÉTODOS CONCRETOS (com implementação padrão) ---

    public String getDetalhes() {
        return "";
    }

    public void adicionarQuantidade(int q) {
        this.quantidade += q;
    }

    public void removerQuantidade(int q) {
        this.quantidade -= q;
        if (this.quantidade < 0) {
            this.quantidade = 0;
        }
    }

    // --- GETTERS E SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getValorCompra() {
        return valorCompra;
    }

    public void setValorCompra(double valorCompra) {
        this.valorCompra = valorCompra;
    }

    public String getIcone() {
        return icone;
    }

    public void setIcone(String icone) {
        this.icone = icone;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }

    public void setDesbloqueado(boolean desbloqueado) {
        this.desbloqueado = desbloqueado;
    }
}