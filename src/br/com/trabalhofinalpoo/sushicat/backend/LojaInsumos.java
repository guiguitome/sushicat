package br.com.trabalhofinalpoo.sushicat.backend;

import java.util.ArrayList;
import java.util.List;

public class LojaInsumos {

    private List<Item> listaItens; 

    public LojaInsumos() {
        this.listaItens = new ArrayList<>();

        listaItens.add(new Peixe(1, 10.0, 1, "🐟", 1, true));
        listaItens.add(new Arroz(2, 6.0, 1, "🍚", 3, true));
        listaItens.add(new Alga(3, 8.0, 1, "🌿", 2, false));
    }
    
    public LojaInsumos(java.util.List<Item> itensParaVender) {
        this.listaItens = itensParaVender;
    }

    public List<Item> getListaItens() {
        return listaItens;
    }

    public void comprarItem(int id, int qtd, Inventario inventario, Jogo jogo) {
        for (Item itemModelo : listaItens) {
            if (itemModelo.getId() == id) {
                if (!itemModelo.isDesbloqueado()) {
                    System.out.println("\n>> Item com ID " + id + " não encontrado na loja.");
                    return;
                }
                
                double custoTotal = itemModelo.getValorCompra() * qtd;

                if (jogo.getDinheiro() >= custoTotal) {
                    
                    jogo.gastarDinheiro(custoTotal);
                    jogo.getFinanceiro().adicionarDespesa(custoTotal);

                    Item itemComprado = itemModelo.criarCopiaParaCompra(qtd);
                    
                    inventario.adicionarItem(itemComprado);
                    System.out.println("\n>> Compra realizada: " + qtd + "x " + itemComprado.getTipo() + " por R$" + String.format("%.2f", custoTotal));

                } else {
                    System.out.println("\n>> Dinheiro insuficiente para comprar este item!");
                }
                return;
            }
        }
        System.out.println("\n>> Item com ID " + id + " não encontrado na loja.");
    }
}