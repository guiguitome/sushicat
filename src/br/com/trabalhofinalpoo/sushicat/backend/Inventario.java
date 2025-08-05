package br.com.trabalhofinalpoo.sushicat.backend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Inventario {
    private List<Item> itens;

    public Inventario() {
        this.itens = new ArrayList<>();
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

    public void consumirIngredientes(Receita receita, int quantidade) {
        if (quantidade <= 0) return;

        for (Item ingredienteNecessario : receita.getIngredientes()) {
            for (Item itemNoInventario : this.itens) {
                if (itemNoInventario.getId() == ingredienteNecessario.getId()) {
                    int qtdARemover = ingredienteNecessario.getQuantidade() * quantidade;
                    itemNoInventario.removerQuantidade(qtdARemover);
                    break;
                }
            }
        }
        this.itens.removeIf(item -> item.getQuantidade() <= 0);
    }

    public void avancarDiaParaItens() {
        Iterator<Item> iterator = itens.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            item.avancarDia();

            if (item.deveSerRemovido()) {
                System.out.println("Um item (" + item.getTipo() + " " + item.getIcone() + ") estragou e foi jogado fora!");
                iterator.remove();
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

}