package br.com.trabalhofinalpoo.sushicat.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jogo {
    private double dinheiro;
    private int diaAtual;
    private Inventario inventario;
    private LojaInsumos loja;
    private Financeiro financeiro;
    private Map<Receita, Integer> planoDeProducao;

    public Jogo() {
        this.dinheiro = 100.0;
        this.diaAtual = 1;
        this.inventario = new Inventario();
        this.loja = new LojaInsumos();
        this.financeiro = new Financeiro();
        this.planoDeProducao = new HashMap<>();
    }

    public Jogo(double dinheiroInicial) {
        this.dinheiro = dinheiroInicial;
        this.diaAtual = 1;
        this.inventario = new Inventario();
        this.loja = new LojaInsumos();
        this.financeiro = new Financeiro();
        this.planoDeProducao = new HashMap<>();
    }

    public void setPlanoDeProducao(Map<Receita, Integer> plano) {
        this.planoDeProducao = plano;
    }

    public List<String> iniciarDia() {
        List<String> mensagensDoDia = verificarDesbloqueiosDeReceitas();

        if (planoDeProducao != null && !planoDeProducao.isEmpty()) {
            for (Map.Entry<Receita, Integer> entradaDoPlano : planoDeProducao.entrySet()) {
                Receita receita = entradaDoPlano.getKey();
                int quantidadeAPreparar = entradaDoPlano.getValue();
                if (quantidadeAPreparar == 0) continue;

                inventario.consumirIngredientes(receita, quantidadeAPreparar);

                int vendasRealizadas = 0;
                for (int i = 0; i < quantidadeAPreparar; i++) {
                    if (receita.ehVendidaHoje()) {
                        vendasRealizadas++;
                    }
                }

                if (vendasRealizadas > 0) {
                    double receitaGerada = receita.getPrecoVenda() * vendasRealizadas;
                    financeiro.adicionarReceita(receitaGerada);
                    this.dinheiro += receitaGerada;
                }
            }
        }

        inventario.avancarDiaParaItens();

        if (diaAtual > 0 && diaAtual % 3 == 0) {
            double custoAluguelHoje = financeiro.getValorAluguel();
            financeiro.adicionarDespesa(custoAluguelHoje);
            gastarDinheiro(custoAluguelHoje);
            mensagensDoDia.add("Hoje foi dia de pagar o aluguel!");
        }

        return mensagensDoDia;
    }

    public void prepararProximoDia() {
        financeiro.iniciarNovoDia();
        diaAtual++;
        if (planoDeProducao != null) {
            planoDeProducao.clear();
        }
    }

    private List<String> verificarDesbloqueiosDeReceitas() {
        List<String> mensagens = new ArrayList<>();
        if (diaAtual == 3) {
            for (Receita r : Receita.getTodasReceitas()) {
                if (r.getNome().equals("Sushi") && !r.isDesbloqueada()) {
                    r.setDesbloqueada(true);
                    mensagens.add("PARABÉNS! Você desbloqueou a receita: Sushi!");
                    for (Item ingredienteDaReceita : r.getIngredientes()) {
                        for (Item itemDaLoja : loja.getListaItens()) {
                            if (itemDaLoja.getId() == ingredienteDaReceita.getId() && !itemDaLoja.isDesbloqueado()) {
                                itemDaLoja.setDesbloqueado(true);
                                mensagens.add("Novo ingrediente na loja: " + itemDaLoja.getTipo() + "!");
                            }
                        }
                    }
                }
            }
        }
        return mensagens;
    }

    public Inventario getInventario() { return inventario; }
    public LojaInsumos getLoja() { return loja; }
    public double getDinheiro() { return dinheiro; }
    public Financeiro getFinanceiro() { return financeiro; }
    public int getDiaAtual() { return diaAtual; }
    public void gastarDinheiro(double valor) { this.dinheiro -= valor; }
}