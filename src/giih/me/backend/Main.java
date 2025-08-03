package giih.me.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {

	public static void main(String[] args) {
	    Scanner scanner = new Scanner(System.in);
	    Jogo jogo = new Jogo();
	    
	    inicializarReceitas();

	    System.out.println("Bem-vindo ao Sushi Cat!");
	    System.out.println("Seu objetivo é gerenciar seu restaurante de sushi e obter lucro.");

	    mostrarStatus(jogo);

	    while (true) {
	        System.out.println("\nO que você deseja fazer?");
	        System.out.println("1. Comprar Insumos na Loja");
	        System.out.println("2. Ver meu Inventário");
	        System.out.println("3. Ver Livro de Receitas");
	        System.out.println("4. Gerenciar Cardápio e Preços");
	        System.out.println("5. Iniciar Vendas do Dia");
	        System.out.println("6. Sair do Jogo");
	        
	        int escolha = -1;
	        try {
	            System.out.print("Escolha uma opção: ");
	            escolha = scanner.nextInt();
	        } catch (InputMismatchException e) {
	            System.out.println("\n>> Erro: Por favor, digite apenas um número.");
	            scanner.next(); 
	            continue;     
	        }
            scanner.nextLine(); 

	        switch (escolha) {
	            case 1:
	                comprarInsumos(jogo, scanner);
	                break;
	            case 2:
	                jogo.getInventario().mostrarItens();
	                break;
	            case 3:
	                new LivroDeReceitas().mostrar(scanner);
	                break;
	            case 4:
	                new GerenciadorPrecos().iniciar(scanner);
	                break;
	            case 5:
	                jogo.iniciarDia();
                    mostrarStatus(jogo);
	                break;
	            case 6:
	                System.out.println("Obrigado por jogar Sushi Cat! Até a próxima!");
	                scanner.close();
	                return;
	            default:
	                System.out.println("Opção inválida! Por favor, tente novamente.");
	        }

	        if (jogo.getDinheiro() < 0) {
	            System.out.println("\nGame Over! Você faliu.");
	            break;
	        }
	    }

	    scanner.close();
	}
	
	private static void mostrarStatus(Jogo jogo) {
	    System.out.println("\n-------------------------------------------");
	    System.out.printf("Dia: %d | Dinheiro: R$%.2f\n", jogo.getDiaAtual(), jogo.getDinheiro());
	    System.out.println("-------------------------------------------");
	}

	private static void inicializarReceitas() {
	    final double MARGEM_LUCRO_INICIAL = 1.0; 
	    final double CHANCE_BASE = 1.0;
	    final double FATOR_DIFICULDADE = 0.5;
	    final double CHANCE_MINIMA = 0.0;

	    List<Item> ingredientesNiguiri = new ArrayList<>();
	    ingredientesNiguiri.add(new Peixe(1, 0, 1, null, 0, false));
	    ingredientesNiguiri.add(new Arroz(2, 0, 1, null, 0, false));

	    Receita niguiri = new Receita("Niguiri", ingredientesNiguiri, 0, 0, true);
	    double custoNiguiri = niguiri.getCustoProducao();
	    double precoNiguiri = custoNiguiri * MARGEM_LUCRO_INICIAL;
	    double markupNiguiri = precoNiguiri / custoNiguiri;
	    double chanceNiguiri = CHANCE_BASE - ((markupNiguiri - 1.0) * FATOR_DIFICULDADE);
	    if (chanceNiguiri < CHANCE_MINIMA) chanceNiguiri = CHANCE_MINIMA;
	    niguiri.setPrecoVenda(precoNiguiri);
	    niguiri.setChanceVenda(chanceNiguiri);
	    
	    List<Item> ingredientesSushi = new ArrayList<>();
	    ingredientesSushi.add(new Peixe(1, 0, 1, null, 0, false));
	    ingredientesSushi.add(new Arroz(2, 0, 1, null, 0, false));
	    ingredientesSushi.add(new Alga(3, 0, 1, null, 0, false));

	    Receita sushi = new Receita("Sushi", ingredientesSushi, 0, 0, false);
	    double custoSushi = sushi.getCustoProducao();
	    double precoSushi = custoSushi * MARGEM_LUCRO_INICIAL;
	    double markupSushi = precoSushi / custoSushi;
	    double chanceSushi = CHANCE_BASE - ((markupSushi - 1.0) * FATOR_DIFICULDADE);
	    if (chanceSushi < CHANCE_MINIMA) chanceSushi = CHANCE_MINIMA;
	    sushi.setPrecoVenda(precoSushi);
	    sushi.setChanceVenda(chanceSushi);
	}

	private static void comprarInsumos(Jogo jogo, Scanner scanner) {
	    System.out.println("\n--- Loja de Insumos ---");

	    List<Item> itensDisponiveis = jogo.getLoja().getItensDisponiveisParaCompra();

	    for (Item item : itensDisponiveis) {
	        System.out.println("ID: " + item.getId() + " | Item: " + item.getTipo() + " | Preço: R$" + String.format("%.2f", item.getValorCompra()));
	    }
	    System.out.println("-----------------------");

        // --- Adicionado tratamento de exceção para o ID ---
        int idCompra;
	    try {
	        System.out.print("Digite o ID do item que deseja comprar (ou 0 para voltar): ");
	        idCompra = scanner.nextInt();
	    } catch (InputMismatchException e) {
	        System.out.println("\n>> Erro: ID inválido. Por favor, digite apenas um número.");
	        scanner.next(); // Limpa o buffer
	        return; // Volta ao menu
	    }
	    if (idCompra == 0) return;

        // --- Adicionado tratamento de exceção para a Quantidade ---
        int qtdCompra;
	    try {
	        System.out.print("Digite a quantidade: ");
	        qtdCompra = scanner.nextInt();
	    } catch (InputMismatchException e) {
	        System.out.println("\n>> Erro: Quantidade inválida. Por favor, digite apenas um número.");
	        scanner.next(); // Limpa o buffer
	        return; // Volta ao menu
	    }
        scanner.nextLine();

	    if (qtdCompra > 0) {
	        jogo.getLoja().comprarItem(idCompra, qtdCompra, jogo.getInventario(), jogo);
	        mostrarStatus(jogo);
	    } else {
	        System.out.println("Quantidade inválida.");
	    }
        System.out.print("\nPressione ENTER para voltar ao menu...");
        scanner.nextLine();
	}
}