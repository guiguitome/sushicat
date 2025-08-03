package giih.me.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Gerencia os itens disponíveis para compra e o processo de venda ao jogador.
 * Esta versão utiliza polimorfismo e um sistema de desbloqueio para itens.
 */
public class LojaInsumos {
    
    // A lista de "protótipos" de todos os itens que a loja pode vender.
    private List<Item> listaItens; 

    public LojaInsumos() {
        this.listaItens = new ArrayList<>();
        
        // --- Populando a loja ---
        // Agora, o último parâmetro do construtor define se o item começa DESBLOQUEADO (true) ou BLOQUEADO (false).

        // Peixe e Arroz são necessários para Niguiri (receita inicial), então começam desbloqueados.
        listaItens.add(new Peixe(1, 10.0, 1, "🐟", 1, true));
        listaItens.add(new Arroz(2, 6.0, 1, "🍚", 3, true));
        
        // Alga é necessária para Sushi (receita bloqueada), então começa bloqueada.
        listaItens.add(new Alga(3, 8.0, 1, "🌿", 2, false));
    }
    
    public LojaInsumos(java.util.List<Item> itensParaVender) {
        this.listaItens = itensParaVender;
    }

    /**
     * Retorna a lista de TODOS os itens, incluindo os bloqueados.
     * Útil para a lógica interna do jogo, como o desbloqueio.
     * @return A lista completa de itens.
     */
    public List<Item> getListaItens() {
        return listaItens;
    }

    /**
     * NOVO MÉTODO: Retorna uma lista apenas com os itens desbloqueados.
     * Este método deve ser usado para mostrar a loja ao jogador.
     */
    public List<Item> getItensDisponiveisParaCompra() {
        return this.listaItens.stream()
                .filter(Item::isDesbloqueado)
                .collect(Collectors.toList());
    }

    /**
     * Realiza a compra de um item da loja.
     * O método agora é muito mais simples e escalável graças ao polimorfismo.
     */
    public void comprarItem(int id, int qtd, Inventario inventario, Jogo jogo) {
        // Procura o item na lista de produtos da loja pelo ID
        for (Item itemModelo : listaItens) {
            if (itemModelo.getId() == id) {
                // Adicionada uma verificação para garantir que o jogador não compre um item bloqueado
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
                return; // Encerra o método após encontrar e processar o item
            }
        }

        // Se o loop terminar sem encontrar o item
        System.out.println("\n>> Item com ID " + id + " não encontrado na loja.");
    }
}