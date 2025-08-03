package giih.me.backend;

public class Financeiro {
    // Agora isso representa o VALOR do aluguel, não uma despesa diária.
	private double valorAluguel;
    
    private double receitasDia;
    private double despesasDia;
    
    public Financeiro() {
        this.valorAluguel = 30.0;
    }

    public Financeiro(double valorAluguel) {
        this.valorAluguel = valorAluguel;
    }

    public void iniciarNovoDia() {
        receitasDia = 0;
        despesasDia = 0;
    }

    public void adicionarReceita(double valor) {
        receitasDia += valor;
    }

    public void adicionarDespesa(double valor) {
        despesasDia += valor;
    }

    /**
     * O cálculo do lucro agora é mais simples. O aluguel será adicionado
     * como uma despesa no dia correto pela classe Jogo.
     */
    public double calcularLucro() {
        return receitasDia - despesasDia;
    }

    public double getReceitasDia() { return receitasDia; }
    public double getDespesasDia() { return despesasDia; }

    // Método para que a classe Jogo saiba qual o valor do aluguel a ser pago.
    public double getValorAluguel() {
        return this.valorAluguel;
    }
}