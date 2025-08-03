package giih.me.backend;

import java.util.List;
import java.util.Scanner;

public class GerenciadorPrecos {

    // --- CONSTANTES DE BALANCEAMENTO ATUALIZADAS ---
    // Chance de venda quando o preço é igual ao custo.
    private static final double CHANCE_BASE = 1.0; // 1.0 = 100%

    // Fator que define a dificuldade. 0.5 significa que a cada 100% de lucro sobre o custo,
    // a chance de venda cai 50%. (Ex: Preço = 2x Custo -> Chance = 100% - 50% = 50%)
    private static final double FATOR_DIFICULDADE = 0.5;

    // Chance de venda nunca será menor que isso.
    private static final double CHANCE_MINIMA = 0.0; // 0%
    
    public GerenciadorPrecos() {
    }

    public GerenciadorPrecos(Scanner scanner) {
    }

    public void iniciar(Scanner scanner) {
        while (true) {
            System.out.println("\n--- GERENCIAMENTO DE CARDÁPIO E PREÇOS ---");

            // --- MUDANÇA PRINCIPAL AQUI ---
            // Agora, pegamos apenas a lista de receitas que já foram desbloqueadas.
            List<Receita> receitas = Receita.getReceitasDesbloqueadas();
            
            if (receitas.isEmpty()) {
                System.out.println("Nenhuma receita desbloqueada para gerenciar ainda.");
                break;
            }

            for (int i = 0; i < receitas.size(); i++) {
                Receita r = receitas.get(i);
                System.out.printf("%d. %s | Custo: R$%.2f | Preço Atual: R$%.2f | Chance Venda: %.0f%%\n",
                        (i + 1), r.getNome(), r.getCustoProducao(), r.getPrecoVenda(), r.getChanceVenda() * 100);
            }
            System.out.println("-------------------------------------------");
            System.out.println("0. Voltar ao menu principal");
            System.out.print("Escolha uma receita para modificar o preço (ou 0 para sair): ");

            int escolha = scanner.nextInt();
            if (escolha == 0) {
                break;
            }

            if (escolha < 1 || escolha > receitas.size()) {
                System.out.println("Opção inválida! Tente novamente.");
                continue;
            }

            Receita receitaSelecionada = receitas.get(escolha - 1);
            
            System.out.printf("Defina o novo preço de venda para '%s' (custo: R$%.2f): R$ ", 
                    receitaSelecionada.getNome(), receitaSelecionada.getCustoProducao());
            
            double novoPreco = scanner.nextDouble();
            
            if (novoPreco < receitaSelecionada.getCustoProducao()) {
                System.out.println("Atenção: O preço de venda está abaixo do custo! Isso gerará prejuízo.");
            }

            receitaSelecionada.setPrecoVenda(novoPreco);
            double novaChance = calcularNovaChance(receitaSelecionada);
            receitaSelecionada.setChanceVenda(novaChance);

            System.out.printf("\n>> Preço de '%s' atualizado para R$%.2f!\n", receitaSelecionada.getNome(), novoPreco);
            System.out.printf(">> Nova chance de venda calculada: %.0f%%\n", novaChance * 100);
        }
    }

    private double calcularNovaChance(Receita receita) {
        double custo = receita.getCustoProducao();
        double preco = receita.getPrecoVenda();

        if (custo <= 0) return CHANCE_BASE;
        
        double markup = preco / custo;

        if (markup <= 1.0) return CHANCE_BASE;

        double penalidade = (markup - 1.0) * FATOR_DIFICULDADE;
        double novaChance = CHANCE_BASE - penalidade;

        if (novaChance < CHANCE_MINIMA) {
            return CHANCE_MINIMA;
        }

        return novaChance;
    }
}