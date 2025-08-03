package giih.me.backend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Inventario {
    private List<Item> itens;

    public Inventario() {
        this.itens = new ArrayList<>();
    }
    
    public Inventario(java.util.List<Item> itensIniciais) {
        this.itens = itensIniciais;
    }

    public void adicionarItem(Item itemComprado) {
        for (Item itemNoInventario : itens) {
            if (itemNoInventario.getId() == itemComprado.getId()) {
                itemNoInventario.adicionarQuantidade(itemComprado.getQuantidade());
                return;
            }
        }
        this.itens.add(itemComprado);
    }

    public boolean temIngredientes(Receita receita) {
        for (Item ingredienteNecessario : receita.getIngredientes()) {
            boolean encontrado = false;
            for (Item itemNoInventario : this.itens) {
                if (itemNoInventario.getId() == ingredienteNecessario.getId()) {
                    if (itemNoInventario.getQuantidade() >= ingredienteNecessario.getQuantidade()) {
                        encontrado = true;
                        break;
                    }
                }
            }
            if (!encontrado) {
                return false;
            }
        }
        return true;
    }

 // Em public class Inventario

 // Este método consome apenas 1 receita (não estava no seu código, mas é útil)
	 public void consumirIngredientes(Receita receita) {
	     consumirIngredientes(receita, 1); // Chama a versão mais completa com quantidade 1
	 }

 // Este é o método principal que precisa ser corrigido
	 public void consumirIngredientes(Receita receita, int quantidade) {
	     if (quantidade <= 0) return;
	
	     // 1. Loop para diminuir a quantidade (como antes)
	     for (Item ingredienteNecessario : receita.getIngredientes()) {
	         for (Item itemNoInventario : this.itens) {
	             if (itemNoInventario.getId() == ingredienteNecessario.getId()) {
	                 int qtdARemover = ingredienteNecessario.getQuantidade() * quantidade;
	                 itemNoInventario.removerQuantidade(qtdARemover);
	                 break; // Sai do loop interno após encontrar e atualizar o item
	             }
	         }
	     }
	
	     // 2. --- PASSO DE LIMPEZA (A CORREÇÃO) ---
	     // Depois de consumir, remove da lista todos os itens que ficaram com quantidade zero ou menos.
	     this.itens.removeIf(item -> item.getQuantidade() <= 0);
	 }


 // Substitua o método verificarValidade() por este:
    public void avancarDiaParaItens() {
        Iterator<Item> iterator = itens.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            item.avancarDia(); // Cada item sabe como "envelhecer"

            if (item.deveSerRemovido()) {
                System.out.println("Um item (" + item.getTipo() + " " + item.getIcone() + ") estragou e foi jogado fora!");
                iterator.remove(); // Remove da lista de forma segura
            }
        }
    }
    
    public int getVendasPossiveis(Receita receita) {
        int maxPossivel = Integer.MAX_VALUE;

        for (Item ingredienteNecessario : receita.getIngredientes()) {
            boolean achouIngrediente = false;
            for (Item itemNoInventario : this.itens) {
                if (itemNoInventario.getId() == ingredienteNecessario.getId()) {
                    int qtdDisponivel = itemNoInventario.getQuantidade();
                    int qtdNecessaria = ingredienteNecessario.getQuantidade();
                    
                    if (qtdNecessaria == 0) continue;

                    int podeFazer = qtdDisponivel / qtdNecessaria;
                    
                    if (podeFazer < maxPossivel) {
                        maxPossivel = podeFazer;
                    }
                    achouIngrediente = true;
                    break;
                }
            }
            if (!achouIngrediente) {
                return 0;
            }
        }
        
        return (maxPossivel == Integer.MAX_VALUE) ? 0 : maxPossivel;
    }


 // Seu método mostrarItens() também fica mais limpo:
    public void mostrarItens() {
        System.out.println("\n--- Inventário Atual ---");
        if (itens.isEmpty()) {
            System.out.println("O inventário está vazio.");
        } else {
            for (Item item : itens) {
                // Não precisamos mais de 'instanceof' aqui!
                System.out.println("- Item: " + item.getTipo() + " (ID: " + item.getId() + ") | Qtd: " + item.getQuantidade() + item.getDetalhes());
            }
        }
        System.out.println("------------------------");
    }

    public int getQuantidadeDeItem(int itemId) {
        for (Item item : this.itens) {
            if (item.getId() == itemId) {
                return item.getQuantidade(); // Encontrou o item, retorna a quantidade
            }
        }
        return 0; // Não encontrou o item no inventário
    }

}