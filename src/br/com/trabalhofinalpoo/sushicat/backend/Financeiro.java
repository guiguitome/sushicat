package br.com.trabalhofinalpoo.sushicat.backend;

public class Financeiro {
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

    public double calcularLucro() {
        return receitasDia - despesasDia;
    }

    public double getReceitasDia() { return receitasDia; }
    public double getDespesasDia() { return despesasDia; }

    public double getValorAluguel() {
        return this.valorAluguel;
    }
}