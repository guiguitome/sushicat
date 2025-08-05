package br.com.trabalhofinalpoo.sushicat.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Receita {
    private String nome;
    private List<Item> ingredientes;
    private double precoVenda;
    private double chanceVenda;
    private boolean desbloqueada;
    private String icone;

    private static List<Receita> todasReceitas = new ArrayList<>();

    public static final double CHANCE_BASE = 1.0;
    public static final double FATOR_DIFICULDADE = 0.5;
    public static final double CHANCE_MINIMA = 0.0;

    public Receita() {
        this.nome = "";
        this.ingredientes = new ArrayList<>();
        this.precoVenda = 0.0;
        this.chanceVenda = 0.0;
        this.desbloqueada = false;
        this.icone = "";
    }

    public Receita(String nome, List<Item> ingredientes, double precoVenda, double chanceVenda, boolean desbloqueadaInicialmente, String icone) {
        this.nome = nome;
        this.ingredientes = ingredientes;
        this.precoVenda = precoVenda;
        this.chanceVenda = chanceVenda;
        this.desbloqueada = desbloqueadaInicialmente;
        this.icone = icone;
        todasReceitas.add(this);
    }

    public void recalcularChanceVenda(LojaInsumos loja) {
        double custo = this.getCustoProducao(loja);
        if (custo <= 0) {
            this.setChanceVenda(CHANCE_BASE);
            return;
        }

        double markup = this.precoVenda / custo;
        if (markup <= 1.0) {
            this.setChanceVenda(CHANCE_BASE);
            return;
        }

        double penalidade = (markup - 1.0) * FATOR_DIFICULDADE;
        double novaChance = CHANCE_BASE - penalidade;

        this.setChanceVenda(Math.max(novaChance, CHANCE_MINIMA));
    }

    public static void inicializarReceitasPadrao() {
        if (!todasReceitas.isEmpty()) return;

        LojaInsumos lojaParaCustos = new LojaInsumos();
        final double MARGEM_LUCRO_INICIAL = 1.0;

        List<Item> ingredientesNiguiri = new ArrayList<>();
        ingredientesNiguiri.add(new Peixe(1, 0, 1, "fish_icon.png", 0, true));
        ingredientesNiguiri.add(new Arroz(2, 0, 1, "rice_icon.png", 0, true));

        Receita niguiri = new Receita("Niguiri", ingredientesNiguiri, 0, 0, true, "src/resources/niguiri_icon.png");
        double custoNiguiri = niguiri.getCustoProducao(lojaParaCustos);
        niguiri.setPrecoVenda(custoNiguiri * MARGEM_LUCRO_INICIAL);
        niguiri.recalcularChanceVenda(lojaParaCustos);

        List<Item> ingredientesSushi = new ArrayList<>();
        ingredientesSushi.add(new Peixe(1, 0, 1, "fish_icon.png", 0, true));
        ingredientesSushi.add(new Arroz(2, 0, 1, "rice_icon.png", 0, true));
        ingredientesSushi.add(new Alga(3, 0, 1, "seaweed_icon.png", 0, false));

        Receita sushi = new Receita("Sushi", ingredientesSushi, 0, 0, false, "src/resources/sushi_icon.png");
        double custoSushi = sushi.getCustoProducao(lojaParaCustos);
        sushi.setPrecoVenda(custoSushi * MARGEM_LUCRO_INICIAL);
        sushi.recalcularChanceVenda(lojaParaCustos);
    }

    public double getCustoProducao(LojaInsumos loja) {
        double custoTotal = 0;
        for (Item ingredienteReceita : this.ingredientes) {
            for (Item itemLoja : loja.getListaItens()) {
                if (ingredienteReceita.getId() == itemLoja.getId()) {
                    custoTotal += itemLoja.getValorCompra() * ingredienteReceita.getQuantidade();
                    break;
                }
            }
        }
        return custoTotal;
    }

    public boolean ehVendidaHoje() {

        double randomValue = Math.random();

        boolean vendeu = randomValue < chanceVenda;

        System.out.println(String.format("Tentando vender %s: Chance=%.2f, Sorteado=%.2f, Vendeu=%b",
                this.getNome(), chanceVenda, randomValue, vendeu));

        return vendeu;
    }

    public String getNome() { return nome; }
    public double getPrecoVenda() { return precoVenda; }
    public List<Item> getIngredientes() { return ingredientes; }
    public double getChanceVenda() { return chanceVenda; }
    public boolean isDesbloqueada() { return desbloqueada; }
    public String getIcone() { return icone; } // Getter que faltava
    public static List<Receita> getTodasReceitas() { return todasReceitas; }
    public static List<Receita> getReceitasDesbloqueadas() {
        return todasReceitas.stream()
                .filter(Receita::isDesbloqueada)
                .collect(Collectors.toList());
    }

    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }
    public void setChanceVenda(double chanceVenda) { this.chanceVenda = chanceVenda; }
    public void setDesbloqueada(boolean desbloqueada) { this.desbloqueada = desbloqueada; }
}