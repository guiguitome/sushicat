package giih.me.backend;

import java.util.List;
import java.util.Scanner;

public class LivroDeReceitas {

    public void mostrar(Scanner scanner) {
        System.out.println("\n--- 📖 LIVRO DE RECEITAS DESBLOQUEADAS ---");
        List<Receita> receitas = Receita.getReceitasDesbloqueadas();

        if (receitas.isEmpty()) {
            System.out.println("Você ainda não desbloqueou nenhuma receita.");
        } else {
            for (Receita r : receitas) {
                System.out.println("\n========================================");
                System.out.printf("Prato: %s\n", r.getNome());
                System.out.println("Ingredientes Necessários:");
                for (Item ingrediente : r.getIngredientes()) {
                    System.out.printf("- 1x %s\n", ingrediente.getTipo());
                }
                System.out.println("========================================");
            }
        }
        System.out.print("\nPressione ENTER para voltar ao menu...");
        scanner.nextLine(); // Limpa o buffer
        scanner.nextLine(); // Espera o Enter
    }
}