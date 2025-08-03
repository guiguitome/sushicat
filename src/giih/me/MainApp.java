package giih.me;

import giih.me.backend.*;
import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class MainApp extends JFrame {
    // --- Atributos da Interface ---
    private Clip audioClip;
    private JPanel telaInicial;
    private JPanel telaJogo;
    private JPanel telaNiveis;

    // --- Labels que precisam ser atualizados ---
    private JLabel lblDia;
    private JLabel lblDinheiro;
    private JLabel lblResumoReceita;
    private JLabel lblResumoGastos;
    private JLabel lblResumoAluguel;
    private JLabel lblResumoLucro;

    // --- Conexão com o Backend ---
    private Jogo jogo;

    public MainApp() {
        jogo = new Jogo();
        Receita.inicializarReceitasPadrao();
        setTitle("Sushi Bar");
        setSize(1000, 800);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        playBackgroundMusic("src/resources/song.wav");
        criarTelas();
        setContentPane(telaInicial);
        setVisible(true);
    }

    private void atualizarStatusUI() {
        lblDia.setText("DIA: " + jogo.getDiaAtual());
        lblDinheiro.setText(String.format("R$ %.2f", jogo.getDinheiro()));
        telaJogo.revalidate();
        telaJogo.repaint();
    }

    private void preencherTelaResumo() {
        // Agora o Java sabe onde encontrar a classe Financeiro por causa do import
        Financeiro financeiro = jogo.getFinanceiro();
        int diaQueAcabou = jogo.getDiaAtual();
        double aluguelDoDia = 0.0;
        if (diaQueAcabou > 0 && diaQueAcabou % 3 == 0) {
            aluguelDoDia = financeiro.getValorAluguel();
        }
        double lucro = financeiro.calcularLucro();
        lblResumoReceita.setText(String.format("Receita Bruta: R$ %.2f", financeiro.getReceitasDia()));
        lblResumoGastos.setText(String.format("Gastos com Insumos: R$ %.2f", financeiro.getDespesasDia() - aluguelDoDia));
        lblResumoAluguel.setText(String.format("Custo do Aluguel: R$ %.2f", aluguelDoDia));
        if (lucro < 0) {
            lblResumoLucro.setForeground(Color.RED);
            lblResumoLucro.setText(String.format("Prejuízo do Dia: R$ %.2f", lucro));
        } else {
            lblResumoLucro.setForeground(Color.GREEN);
            lblResumoLucro.setText(String.format("Lucro do Dia: R$ %.2f", lucro));
        }
    }

    private void playBackgroundMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            FloatControl volume = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            volume.setValue(-20.0f);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            audioClip.start();
        } catch (Exception e) {
            System.err.println("Erro ao carregar o áudio: " + e.getMessage());
        }
    }

    private void playSoundEffect(String filePath) {
        try {
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Erro ao carregar o efeito sonoro: " + e.getMessage());
        }
    }

    /**
     * MÉTODO MODIFICADO: Agora cria apenas o número da quantidade e os botões.
     */
    private void criarLinhaDeItem(JPanel painel, giih.me.backend.Item item, int x, int y,
                                  java.util.Map<giih.me.backend.Item, Integer> carrinho, Runnable atualizarTotal) {

        carrinho.put(item, 0);

        // O código que criava o ícone e o nome foi removido.

        // Label da Quantidade
        JLabel lblQuantidade = new JLabel("0");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 20));
        lblQuantidade.setHorizontalAlignment(SwingConstants.CENTER);
        // Posição ajustada para ficar mais centralizada na linha
        lblQuantidade.setBounds(x + 240, y + 8, 30, 25);
        painel.add(lblQuantidade);

        // Botão de Diminuir (-)
        JButton btnMenos = new JButton();
        btnMenos.setBounds(x + 210, y + 8, 30, 25);
        btnMenos.setOpaque(false); btnMenos.setContentAreaFilled(false); btnMenos.setBorderPainted(false);
        painel.add(btnMenos);

        // Botão de Aumentar (+)
        JButton btnMais = new JButton();
        btnMais.setBounds(x + 270, y + 8, 30, 25);
        btnMais.setOpaque(false); btnMais.setContentAreaFilled(false); btnMais.setBorderPainted(false);
        painel.add(btnMais);

        // O código que criava os textos "Qtd" e "Preço" foi removido.

        // Lógica dos botões
        btnMenos.addActionListener(e -> {
            int qtd = carrinho.get(item);
            if (qtd > 0) {
                carrinho.put(item, qtd - 1);
                lblQuantidade.setText(String.valueOf(qtd - 1));
                atualizarTotal.run();
            }
        });
        btnMais.addActionListener(e -> {
            int qtd = carrinho.get(item);
            carrinho.put(item, qtd + 1);
            lblQuantidade.setText(String.valueOf(qtd + 1));
            atualizarTotal.run();
        });
    }

    private void mostrarTelaCompraIngredientes() {
        JDialog popup = new JDialog(this, "Comprar Ingredientes", true);
        popup.setSize(560, 600);
        popup.setUndecorated(true);
        popup.setLocationRelativeTo(this);

        JPanel painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("src/resources/ingredientes.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        painel.setLayout(null);

        JLabel lblDinheiroAtual = new JLabel(String.format("R$ %.2f", jogo.getDinheiro()));
        lblDinheiroAtual.setFont(new Font("Arial", Font.BOLD, 24));
        lblDinheiroAtual.setForeground(new Color(90, 50, 30));
        lblDinheiroAtual.setBounds(290, 92, 200, 30);
        painel.add(lblDinheiroAtual);

        JLabel lblTotal = new JLabel("R$ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setForeground(new Color(90, 50, 30));
        lblTotal.setBounds(170, 420, 150, 30);
        painel.add(lblTotal);

        java.util.Map<giih.me.backend.Item, Integer> carrinho = new java.util.HashMap<>();
        Runnable atualizarTotal = () -> {
            double custoTotal = 0;
            for (java.util.Map.Entry<giih.me.backend.Item, Integer> entrada : carrinho.entrySet()) {
                custoTotal += entrada.getKey().getValorCompra() * entrada.getValue();
            }
            lblTotal.setText(String.format("R$ %.2f", custoTotal));
        };

        java.util.List<giih.me.backend.Item> todosOsItensDaLoja = jogo.getLoja().getListaItens();

        // Variáveis para controlar a posição de cada item
        int yArroz = 175;
        int yPeixe = 270; // yArroz + 80
        int yAlga  = 333; // yPeixe + 80
        int xPos   = 85;

        for (giih.me.backend.Item item : todosOsItensDaLoja) {
            if (item.isDesbloqueado()) {
                if (item.getTipo().equalsIgnoreCase("Arroz")) {
                    criarLinhaDeItem(painel, item, xPos, yArroz, carrinho, atualizarTotal);
                } else if (item.getTipo().equalsIgnoreCase("Peixe")) {
                    criarLinhaDeItem(painel, item, xPos, yPeixe, carrinho, atualizarTotal);
                } else if (item.getTipo().equalsIgnoreCase("Alga")) {
                    criarLinhaDeItem(painel, item, xPos, yAlga, carrinho, atualizarTotal);
                }
            }
        }

        JButton btnComprar = new JButton();
        btnComprar.setBounds(185, 465, 170, 40);
        btnComprar.setOpaque(false);
        btnComprar.setContentAreaFilled(false);
        btnComprar.setBorderPainted(false);
        btnComprar.addActionListener(e -> {
            double custoFinal = 0;
            for (java.util.Map.Entry<giih.me.backend.Item, Integer> entrada : carrinho.entrySet()) {
                custoFinal += entrada.getKey().getValorCompra() * entrada.getValue();
            }
            if(custoFinal <= 0) {
                JOptionPane.showMessageDialog(popup, "Você não selecionou nenhum item!");
                return;
            }
            if (jogo.getDinheiro() >= custoFinal) {
                for (java.util.Map.Entry<giih.me.backend.Item, Integer> entrada : carrinho.entrySet()) {
                    if (entrada.getValue() > 0) {
                        jogo.getLoja().comprarItem(entrada.getKey().getId(), entrada.getValue(), jogo.getInventario(), jogo);
                    }
                }
                JOptionPane.showMessageDialog(popup, "Compra realizada com sucesso!");
                atualizarStatusUI();
                popup.dispose();
            } else {
                JOptionPane.showMessageDialog(popup, "Dinheiro insuficiente!");
            }
        });
        painel.add(btnComprar);

        JButton btnFechar = new JButton("X");
        btnFechar.setBounds(515, 15, 30, 30);
        btnFechar.addActionListener(e -> popup.dispose());
        painel.add(btnFechar);

        popup.setContentPane(painel);
        popup.setVisible(true);
    }

    private void mostrarTelaPrecos() {
        JDialog popup = new JDialog(this, "Definir Preços e Produção", true);
        popup.setSize(560, 600);
        popup.setUndecorated(true);
        popup.setLocationRelativeTo(this);

        JPanel painel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("src/resources/precos.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        painel.setLayout(null);

        // --- Estruturas de Dados ---
        java.util.Map<giih.me.backend.Receita, Integer> quantidadesParaCozinhar = new java.util.HashMap<>();
        java.util.Map<giih.me.backend.Receita, Double> precosEditados = new java.util.HashMap<>();

        // --- Criação Dinâmica das Linhas de Receita ---
        java.util.List<giih.me.backend.Receita> receitasDisponiveis = giih.me.backend.Receita.getReceitasDesbloqueadas();
        int yPos = 130;
        int espacamento = 100;

        for (giih.me.backend.Receita receita : receitasDisponiveis) {
            precosEditados.put(receita, receita.getPrecoVenda());
            quantidadesParaCozinhar.put(receita, 0);

            // --- Controles de QUANTIDADE A FAZER ---
            int maxPossivel = jogo.getInventario().getVendasPossiveis(receita);

            JLabel lblQtdAtual = new JLabel("0");
            lblQtdAtual.setFont(new Font("Arial", Font.BOLD, 20));
            lblQtdAtual.setHorizontalAlignment(SwingConstants.CENTER);
            lblQtdAtual.setBounds(290, yPos + 20, 40, 30);
            painel.add(lblQtdAtual);

            JButton btnMenosQtd = new JButton("-"); // Texto adicionado para visualização
            btnMenosQtd.setBounds(260, yPos + 20, 30, 30);
            // Linhas de invisibilidade removidas
            painel.add(btnMenosQtd);

            JButton btnMaisQtd = new JButton("+"); // Texto adicionado para visualização
            btnMaisQtd.setBounds(330, yPos + 20, 30, 30);
            // Linhas de invisibilidade removidas
            painel.add(btnMaisQtd);

            // --- Controles de PREÇO DE VENDA ---
            JLabel lblPrecoAtual = new JLabel(String.format("%.0f", receita.getPrecoVenda()));
            lblPrecoAtual.setFont(new Font("Arial", Font.BOLD, 20));
            lblPrecoAtual.setHorizontalAlignment(SwingConstants.CENTER);
            lblPrecoAtual.setBounds(395, yPos + 20, 40, 30);
            painel.add(lblPrecoAtual);

            JButton btnMenosPreco = new JButton("-"); // Texto adicionado para visualização
            btnMenosPreco.setBounds(365, yPos + 20, 30, 30);
            // Linhas de invisibilidade removidas
            painel.add(btnMenosPreco);

            JButton btnMaisPreco = new JButton("+"); // Texto adicionado para visualização
            btnMaisPreco.setBounds(435, yPos + 20, 30, 30);
            // Linhas de invisibilidade removidas
            painel.add(btnMaisPreco);

            // --- Lógica dos Botões ---
            btnMenosQtd.addActionListener(e -> {
                int qtd = quantidadesParaCozinhar.get(receita);
                if (qtd > 0) {
                    qtd--;
                    quantidadesParaCozinhar.put(receita, qtd);
                    lblQtdAtual.setText(String.valueOf(qtd));
                }
            });
            btnMaisQtd.addActionListener(e -> {
                int qtd = quantidadesParaCozinhar.get(receita);
                if (qtd < maxPossivel) {
                    qtd++;
                    quantidadesParaCozinhar.put(receita, qtd);
                    lblQtdAtual.setText(String.valueOf(qtd));
                }
            });

            btnMenosPreco.addActionListener(e -> {
                double preco = precosEditados.get(receita);
                if (preco > 0) {
                    preco--;
                    precosEditados.put(receita, preco);
                    lblPrecoAtual.setText(String.format("%.0f", preco));
                }
            });
            btnMaisPreco.addActionListener(e -> {
                double preco = precosEditados.get(receita);
                preco++;
                precosEditados.put(receita, preco);
                lblPrecoAtual.setText(String.format("%.0f", preco));
            });

            yPos += espacamento;
        }

        // --- Botão Salvar (agora visível) ---
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setBounds(185, 465, 170, 40);
        // Linhas de invisibilidade removidas
        btnSalvar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            for(java.util.Map.Entry<giih.me.backend.Receita, Double> entrada : precosEditados.entrySet()){
                entrada.getKey().setPrecoVenda(entrada.getValue());
            }
            JOptionPane.showMessageDialog(popup, "Preços salvos com sucesso!");
            popup.dispose();
        });
        painel.add(btnSalvar);

        // --- Botão Fechar ---
        JButton btnFechar = new JButton("X");
        btnFechar.setBounds(520, 10, 30, 30);
        btnFechar.addActionListener(e -> popup.dispose());
        painel.add(btnFechar);

        popup.setContentPane(painel);
        popup.setVisible(true);
    }

    private void mostrarTelaReceitas() {
        JOptionPane.showMessageDialog(this, "Livro de Receitas em construção!");
    }

    private void criarTelas() {
        // ----------- Tela Inicial -----------
        telaInicial = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("src/resources/tela.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        telaInicial.setLayout(null);

        JLabel labelBotao = new JLabel(new ImageIcon("src/resources/btn.png"));
        labelBotao.setBounds(300, 500, 360, 144);

        JButton btnInvisivel = new JButton();
        btnInvisivel.setBounds(300, 500, 360, 144);
        btnInvisivel.setOpaque(false);
        btnInvisivel.setContentAreaFilled(false);
        btnInvisivel.setBorderPainted(false);
        btnInvisivel.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            setContentPane(telaJogo);
            atualizarStatusUI(); // Garante que o status está correto ao entrar no jogo
            revalidate();
            repaint();
        });
        telaInicial.add(labelBotao);
        telaInicial.add(btnInvisivel);

        // ----------- Tela do Jogo (Menu Principal) -----------
        telaJogo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("src/resources/menu.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        telaJogo.setLayout(null);

        lblDinheiro = new JLabel("R$ 100.00");
        lblDinheiro.setFont(new Font("Arial", Font.BOLD, 26));
        lblDinheiro.setForeground(Color.BLACK);
        lblDinheiro.setBounds(580, 102, 250, 30);
        telaJogo.add(lblDinheiro);

        lblDia = new JLabel("DIA: 1");
        lblDia.setFont(new Font("Arial", Font.BOLD, 20));
        lblDia.setForeground(Color.WHITE);
        lblDia.setBounds(850, 10, 120, 30);
        telaJogo.add(lblDia);

        // Botões do menu principal
        JButton btnComprar = new JButton();
        btnComprar.setBounds(250, 250, 380, 90);
        btnComprar.setOpaque(false);
        btnComprar.setContentAreaFilled(false);
        btnComprar.setBorderPainted(false);
        btnComprar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            mostrarTelaCompraIngredientes();
        });
        telaJogo.add(btnComprar);

        JButton btnPrecos = new JButton();
        btnPrecos.setBounds(250, 360, 380, 90);
        btnPrecos.setOpaque(false);
        btnPrecos.setContentAreaFilled(false);
        btnPrecos.setBorderPainted(false);
        btnPrecos.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            mostrarTelaPrecos();
        });
        telaJogo.add(btnPrecos);

        JButton btnMelhorias = new JButton();
        btnMelhorias.setBounds(250, 470, 380, 90);
        btnMelhorias.setOpaque(false);
        btnMelhorias.setContentAreaFilled(false);
        btnMelhorias.setBorderPainted(false);
        btnMelhorias.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            JOptionPane.showMessageDialog(this, "Tela de Melhorias.", "Melhorias", JOptionPane.INFORMATION_MESSAGE);
        });
        telaJogo.add(btnMelhorias);

        JButton btnIniciarDia = new JButton();
        btnIniciarDia.setBounds(250, 580, 380, 90);
        btnIniciarDia.setOpaque(false);
        btnIniciarDia.setContentAreaFilled(false);
        btnIniciarDia.setBorderPainted(false);
        btnIniciarDia.addActionListener(e -> {
            List<String> mensagens = jogo.iniciarDia();
            playSoundEffect("src/resources/click.wav");

            if (!mensagens.isEmpty()) {
                for (String msg : mensagens) {
                    JOptionPane.showMessageDialog(this, msg, "Novidades!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            preencherTelaResumo();
            setContentPane(telaNiveis);
            revalidate();
            repaint();
        });
        telaJogo.add(btnIniciarDia);

        JButton btnLivro = new JButton();
        btnLivro.setBounds(680, 240, 120, 120);
        btnLivro.setOpaque(false);
        btnLivro.setContentAreaFilled(false);
        btnLivro.setBorderPainted(false);
        btnLivro.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            mostrarTelaReceitas();
        });
        telaJogo.add(btnLivro);

        // ----------- Tela de Resumo do Dia -----------
        telaNiveis = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("src/resources/resumo.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        telaNiveis.setLayout(null);

        lblResumoReceita = new JLabel("Receita Bruta: R$ 0.00");
        lblResumoReceita.setFont(new Font("Arial", Font.BOLD, 22));
        lblResumoReceita.setForeground(Color.WHITE);
        lblResumoReceita.setBounds(100, 200, 400, 30);
        telaNiveis.add(lblResumoReceita);

        lblResumoGastos = new JLabel("Gastos: R$ 0.00");
        lblResumoGastos.setFont(new Font("Arial", Font.BOLD, 22));
        lblResumoGastos.setForeground(Color.WHITE);
        lblResumoGastos.setBounds(100, 250, 400, 30);
        telaNiveis.add(lblResumoGastos);

        lblResumoAluguel = new JLabel("Aluguel: R$ 0.00");
        lblResumoAluguel.setFont(new Font("Arial", Font.BOLD, 22));
        lblResumoAluguel.setForeground(Color.WHITE);
        lblResumoAluguel.setBounds(100, 300, 400, 30);
        telaNiveis.add(lblResumoAluguel);

        lblResumoLucro = new JLabel("Lucro/Prejuízo: R$ 0.00");
        lblResumoLucro.setFont(new Font("Arial", Font.BOLD, 24));
        lblResumoLucro.setForeground(Color.GREEN);
        lblResumoLucro.setBounds(100, 350, 400, 30);
        telaNiveis.add(lblResumoLucro);

        JButton btnVoltar = new JButton();
        btnVoltar.setBounds(385, 620, 230, 80);
        btnVoltar.setOpaque(false);
        btnVoltar.setContentAreaFilled(false);
        btnVoltar.setBorderPainted(false);
        btnVoltar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");

            // É AQUI que o jogo avança para o próximo dia!
            jogo.prepararProximoDia();

            setContentPane(telaJogo);
            atualizarStatusUI(); // Atualiza a tela principal para o NOVO dia
            revalidate();
            repaint();
        });
        telaNiveis.add(btnVoltar);
    }

        // ... (código da tela de resumo)

        // --- ActionListener do BOTÃO VOLTAR (da tela de resumo) ---

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}