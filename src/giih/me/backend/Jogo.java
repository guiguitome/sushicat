package giih.me.backend;

import java.util.ArrayList;
import java.util.List;

public class Jogo {
    private double dinheiro;
    private int diaAtual;
    private Inventario inventario;
    private LojaInsumos loja;
    private Financeiro financeiro;

    public Jogo() {
        this.dinheiro = 100.0;
        this.diaAtual = 1;
        this.inventario = new Inventario();
        this.loja = new LojaInsumos();
        this.financeiro = new Financeiro();
    }

    public Jogo(double dinheiroInicial) {
        this.dinheiro = dinheiroInicial;
        this.diaAtual = 1;
        this.inventario = new Inventario();
        this.loja = new LojaInsumos();
        this.financeiro = new Financeiro();
    }

    public List<String> iniciarDia() {
        List<String> mensagensDoDia = verificarDesbloqueiosDeReceitas();

        System.out.println("\n--- VENDAS DO DIA " + diaAtual + " ---");

        for (Receita receita : Receita.getReceitasDesbloqueadas()) {
            int maxVendas = inventario.getVendasPossiveis(receita);
            if (maxVendas == 0) continue;

            int vendasRealizadas = 0;
            for (int i = 0; i < maxVendas; i++) {
                if (receita.ehVendidaHoje()) {
                    vendasRealizadas++;
                }
            }

            if (vendasRealizadas > 0) {
                System.out.println("Vendido: " + vendasRealizadas + "x " + receita.getNome());
                inventario.consumirIngredientes(receita, vendasRealizadas);

                double receitaGerada = receita.getPrecoVenda() * vendasRealizadas;
                financeiro.adicionarReceita(receitaGerada);
                this.dinheiro += receitaGerada; // CORREÇÃO: Dinheiro da venda é somado diretamente.
            }
        }

        System.out.println("\nProcessando passagem do dia para o estoque...");
        inventario.avancarDiaParaItens();

        mostrarResumo();

        return mensagensDoDia;
    }

    public void prepararProximoDia() {
        financeiro.iniciarNovoDia();
        diaAtual++;
    }

    public void mostrarResumo() {
        double custoAluguelHoje = 0.0;

        // Verifica se é dia de pagar o aluguel
        if (diaAtual > 0 && diaAtual % 3 == 0) {
            custoAluguelHoje = financeiro.getValorAluguel();

            // Adiciona o aluguel como despesa no financeiro (para o cálculo do lucro)
            financeiro.adicionarDespesa(custoAluguelHoje);

            // Subtrai o aluguel do dinheiro total (transação direta)
            gastarDinheiro(custoAluguelHoje);

            System.out.println("\n>> HOJE É DIA DE PAGAR O ALUGUEL! <<");
        }

        System.out.println("\n--- Resumo do Dia " + diaAtual + " ---");
        System.out.println("Receita Bruta: R$" + String.format("%.2f", financeiro.getReceitasDia()));

        // --- LÓGICA CORRIGIDA AQUI ---
        // 1. Pega o total de despesas do dia (que pode incluir o aluguel).
        double despesasTotaisDoDia = financeiro.getDespesasDia();
        // 2. Subtrai o aluguel (que é 30 ou 0) para isolar apenas os gastos com insumos.
        double gastosApenasComInsumos = despesasTotaisDoDia - custoAluguelHoje;

        // 3. Exibe os valores separados.
        System.out.println("Gastos com Insumos: R$" + String.format("%.2f", gastosApenasComInsumos));
        System.out.println("Custo do Aluguel: R$" + String.format("%.2f", custoAluguelHoje));

        // O lucro é calculado sobre o total de despesas e serve apenas para exibição.
        double lucro = financeiro.calcularLucro();

        if (lucro >= 0) {
            System.out.println("Lucro do Dia: R$" + String.format("%.2f", lucro));
        } else {
            System.out.println("Prejuízo do Dia: R$" + String.format("%.2f", lucro));
        }
        System.out.println("-------------------------");
        System.out.println("Dinheiro atual: R$" + String.format("%.2f", dinheiro));
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

    // --- GETTERS E SETTERS ---
    public Inventario getInventario() { return inventario; }
    public LojaInsumos getLoja() { return loja; }
    public double getDinheiro() { return dinheiro; }
    public void gastarDinheiro(double valor) { this.dinheiro -= valor; }
    public Financeiro getFinanceiro() { return financeiro; }
    public int getDiaAtual() { return diaAtual; }
}