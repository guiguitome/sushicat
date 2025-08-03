package giih.me.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Receita {
    private String nome;
    private List<Item> ingredientes;
    private double precoVenda;
    private double chanceVenda;
    private boolean desbloqueada; // <-- NOVO ATRIBUTO!

    private static List<Receita> todasReceitas = new ArrayList<>();
    
    public Receita() {
        this.nome = "";
        this.ingredientes = new java.util.ArrayList<>();
        this.precoVenda = 0.0;
        this.chanceVenda = 0.0;
        this.desbloqueada = false;
        // Importante: O construtor vazio não adiciona a receita à lista estática
        // para não criar receitas "fantasmas" no jogo.
    }

    /**
     * NOVO MÉTODO ESTÁTICO: Ponto central para criar todas as receitas padrão do jogo.
     * Por ser 'public static', pode ser chamado de qualquer lugar, como da MainApp.
     */
    public static void inicializarReceitasPadrao() {
        // Garante que as receitas só sejam criadas uma vez
        if (!todasReceitas.isEmpty()) {
            return;
        }

        // --- DEFINIÇÕES DE BALANCEAMENTO INICIAL ---
        final double MARGEM_LUCRO_INICIAL = 1.0;
        final double CHANCE_BASE = 1.0;
        final double FATOR_DIFICULDADE = 0.5;
        final double CHANCE_MINIMA = 0.0;

        // --- Receita 1: Niguiri (começa desbloqueada) ---
        List<Item> ingredientesNiguiri = new ArrayList<>();
        ingredientesNiguiri.add(new Peixe(1, 0, 1, null, 0, true));
        ingredientesNiguiri.add(new Arroz(2, 0, 1, null, 0, true));

        Receita niguiri = new Receita("Niguiri", ingredientesNiguiri, 0, 0, true);
        double custoNiguiri = niguiri.getCustoProducao();
        double precoNiguiri = custoNiguiri * MARGEM_LUCRO_INICIAL;
        double markupNiguiri = custoNiguiri > 0 ? precoNiguiri / custoNiguiri : 1.0;
        double chanceNiguiri = CHANCE_BASE - ((markupNiguiri - 1.0) * FATOR_DIFICULDADE);
        if (chanceNiguiri < CHANCE_MINIMA) chanceNiguiri = CHANCE_MINIMA;
        niguiri.setPrecoVenda(precoNiguiri);
        niguiri.setChanceVenda(chanceNiguiri);

        // --- Receita 2: Sushi (começa bloqueada) ---
        List<Item> ingredientesSushi = new ArrayList<>();
        ingredientesSushi.add(new Peixe(1, 0, 1, null, 0, true));
        // CORRIGIDO AQUI: Adicionado o '0' da validade
        ingredientesSushi.add(new Arroz(2, 0, 1, null, 0, true));
        // CORRIGIDO AQUI: Adicionado o '0' da validade
        ingredientesSushi.add(new Alga(3, 0, 1, null, 0, false));

        Receita sushi = new Receita("Sushi", ingredientesSushi, 0, 0, false);
        double custoSushi = sushi.getCustoProducao();
        double precoSushi = custoSushi * MARGEM_LUCRO_INICIAL;
        double markupSushi = custoSushi > 0 ? precoSushi / custoSushi : 1.0;
        double chanceSushi = CHANCE_BASE - ((markupSushi - 1.0) * FATOR_DIFICULDADE);
        if (chanceSushi < CHANCE_MINIMA) chanceSushi = CHANCE_MINIMA;
        sushi.setPrecoVenda(precoSushi);
        sushi.setChanceVenda(chanceSushi);
    }

    public Receita(String nome, List<Item> ingredientes, double precoVenda, double chanceVenda, boolean desbloqueadaInicialmente) {
        this.nome = nome;
        this.ingredientes = ingredientes;
        this.precoVenda = precoVenda;
        this.chanceVenda = chanceVenda;
        this.desbloqueada = desbloqueadaInicialmente; // <-- ATRIBUÍDO NO CONSTRUTOR
        todasReceitas.add(this);
    }
    
    // ... (método getCustoProducao e ehVendidaHoje continuam iguais)
    public double getCustoProducao() {
        double custoTotal = 0;
        LojaInsumos loja = new LojaInsumos();
        
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
        return Math.random() < chanceVenda;
    }

    // --- GETTERS ---
    public String getNome() { return nome; }
    public double getPrecoVenda() { return precoVenda; }
    public List<Item> getIngredientes() { return ingredientes; }
    public double getChanceVenda() { return chanceVenda; }
    public boolean isDesbloqueada() { return desbloqueada; } // <-- NOVO GETTER!
    public static List<Receita> getTodasReceitas() { return todasReceitas; }

    /**
     * NOVO MÉTODO: Retorna uma lista apenas com as receitas desbloqueadas.
     * Isso será muito útil para o resto do jogo.
     */
    public static List<Receita> getReceitasDesbloqueadas() {
        return todasReceitas.stream()
                .filter(Receita::isDesbloqueada)
                .collect(Collectors.toList());
    }

    // --- SETTERS ---
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }
    public void setChanceVenda(double chanceVenda) { this.chanceVenda = chanceVenda; }
    public void setDesbloqueada(boolean desbloqueada) { this.desbloqueada = desbloqueada; } // <-- NOVO SETTER!
}