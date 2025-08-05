package br.com.trabalhofinalpoo.sushicat;

import br.com.trabalhofinalpoo.sushicat.backend.Financeiro;
import br.com.trabalhofinalpoo.sushicat.backend.Item;
import br.com.trabalhofinalpoo.sushicat.backend.Jogo;
import br.com.trabalhofinalpoo.sushicat.backend.Receita;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Random;
import javax.swing.border.Border;

public class MainApp extends JFrame {
    private Clip audioClip;
    private JPanel telaInicial;
    private JPanel telaJogo;
    private JPanel telaNiveis;

    private JLabel lblDia;
    private JLabel lblDinheiro;
    private JLabel lblResumoReceita;
    private JLabel lblResumoGastos;
    private JLabel lblResumoAluguel;
    private JLabel lblResumoLucro;

    private Jogo jogo;

    private static final List<String> MENSAGENS_TRANSICAO = Arrays.asList(
            "Você deve pagar aluguel a cada 3 dias. Tenha dinheiro pra isso!",
            "Saldo negativo = falência = fim de jogo!",
            "Produto caro vende menos!",
            "Clientes gostam de produto bom e barato!",
            "Verificando o frescor dos peixes...",
            "Use o dinheiro com cuidado!",
            "Será que hoje teremos muitos clientes?"
    );
    private final Random random = new Random();

    public MainApp() {
        jogo = new Jogo();
        Receita.inicializarReceitasPadrao();
        setTitle("Sushi Bar");
        setSize(1000, 800);
        try {
            ImageIcon icone = new ImageIcon("src/resources/sushicat_logo.png");
            setIconImage(icone.getImage());
        } catch (Exception e) {
            System.out.println("Erro ao carregar o ícone da janela: " + e.getMessage());
        }
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        playBackgroundMusic("src/resources/song.wav");
        criarTelas();
        setContentPane(telaInicial);
        setVisible(true);
    }

    private void atualizarStatusUI() {
        Color corTextoMarrom = new Color(0x57, 0x12, 0x07);
        lblDia.setText("DIA: " + jogo.getDiaAtual());
        lblDinheiro.setText(String.format("%.2f", jogo.getDinheiro()));
        lblDinheiro.setForeground(corTextoMarrom);
        telaJogo.revalidate();
        telaJogo.repaint();
    }

    private boolean verificarGameOver() {
        if (jogo.getDinheiro() < 0) {
            JOptionPane.showMessageDialog(this,
                    "Você faliu! Seu saldo ficou negativo e o restaurante fechou.",
                    "Game Over",
                    JOptionPane.ERROR_MESSAGE);

            this.jogo = new Jogo();
            br.com.trabalhofinalpoo.sushicat.backend.Receita.inicializarReceitasPadrao();

            setContentPane(telaInicial);
            revalidate();
            repaint();
            atualizarStatusUI();
            return true;
        }
        return false;
    }

    private void preencherTelaResumo() {
        Financeiro financeiro = jogo.getFinanceiro();
        int diaQueAcabou = jogo.getDiaAtual();

        double aluguelDoDia = 0.0;
        if (diaQueAcabou > 0 && diaQueAcabou % 3 == 0) {
            aluguelDoDia = financeiro.getValorAluguel();
        }

        double gastosComInsumos = financeiro.getDespesasDia() - aluguelDoDia;
        double lucro = financeiro.calcularLucro();

        lblResumoReceita.setText(String.format("R$ %.2f", financeiro.getReceitasDia()));
        lblResumoGastos.setText(String.format("R$ %.2f", gastosComInsumos));
        lblResumoAluguel.setText(String.format("R$ %.2f", aluguelDoDia));

        if (lucro < 0) {
            lblResumoLucro.setText(String.format("- R$ %.2f", Math.abs(lucro)));
            lblResumoLucro.setForeground(new Color(0xD32F2F));
        } else {
            lblResumoLucro.setText(String.format("R$ %.2f", lucro));
            lblResumoLucro.setForeground(new Color(0x388E3C));
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

    private void criarLinhaDeItem(JPanel painel, Item item, int x, int y,
                                  Map<Item, Integer> carrinho, Runnable atualizarTotal) {
        carrinho.put(item, 0);
        JLabel lblQuantidade = new JLabel("0");
        lblQuantidade.setFont(new Font("Arial", Font.BOLD, 20));
        lblQuantidade.setForeground(new Color(0x57, 0x12, 0x07));
        lblQuantidade.setHorizontalAlignment(SwingConstants.CENTER);
        lblQuantidade.setBounds(x + 251, y + 8, 30, 25);
        painel.add(lblQuantidade);
        JButton btnMenos = new JButton();
        btnMenos.setBounds(x + 210, y + 8, 30, 25);
        btnMenos.setOpaque(false); btnMenos.setContentAreaFilled(false); btnMenos.setBorderPainted(false);
        painel.add(btnMenos);
        JButton btnMais = new JButton();
        btnMais.setBounds(x + 270, y + 8, 30, 25);
        btnMais.setOpaque(false); btnMais.setContentAreaFilled(false); btnMais.setBorderPainted(false);
        painel.add(btnMais);
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
                Image bg = new ImageIcon("src/resources/ingredientes2.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        painel.setLayout(null);
        JLabel lblDinheiroAtual = new JLabel(String.format("R$ %.2f", jogo.getDinheiro()));
        lblDinheiroAtual.setFont(new Font("Arial", Font.BOLD, 22));
        lblDinheiroAtual.setForeground(new Color(0x57, 0x12, 0x07));
        lblDinheiroAtual.setBounds(300, 50, 200, 30);
        painel.add(lblDinheiroAtual);
        JLabel lblTotal = new JLabel("R$ 0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setForeground(new Color(0x57, 0x12, 0x07));
        lblTotal.setBounds(160, 455, 150, 30);
        painel.add(lblTotal);
        Map<Item, Integer> carrinho = new HashMap<>();
        Runnable atualizarTotal = () -> {
            double custoTotal = 0;
            for (Map.Entry<Item, Integer> entrada : carrinho.entrySet()) {
                custoTotal += entrada.getKey().getValorCompra() * entrada.getValue();
            }
            lblTotal.setText(String.format("R$ %.2f", custoTotal));
        };
        List<Item> todosOsItensDaLoja = jogo.getLoja().getListaItens();
        int yArroz = 155, yPeixe = 270, yAlga = 380, xPos = 85;
        for (Item item : todosOsItensDaLoja) {
            if (item.isDesbloqueado()) {
                if (item.getTipo().equalsIgnoreCase("Arroz")) {
                    criarLinhaDeItem(painel, item, xPos, yArroz, carrinho, atualizarTotal);
                } else if (item.getTipo().equalsIgnoreCase("Peixe")) {
                    criarLinhaDeItem(painel, item, xPos, yPeixe, carrinho, atualizarTotal);
                } else if (item.getTipo().equalsIgnoreCase("Alga")) {
                    criarLinhaDeItem(painel, item, xPos, yAlga, carrinho, atualizarTotal);
                }
            } else {
                if (item.getTipo().equalsIgnoreCase("Alga")) {
                    JLabel bloqueadoOverlay = new JLabel("Bloqueado") {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                            g2.setColor(new Color(0, 0, 0, 180));
                            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                            super.paintComponent(g);
                            g2.dispose();
                        }
                    };
                    bloqueadoOverlay.setFont(new Font("Arial", Font.BOLD, 18));
                    bloqueadoOverlay.setHorizontalAlignment(SwingConstants.CENTER);
                    bloqueadoOverlay.setForeground(Color.WHITE);
                    bloqueadoOverlay.setOpaque(false);
                    bloqueadoOverlay.setBounds(66, 338, 429, 85);
                    painel.add(bloqueadoOverlay);
                }
            }
        }
        JButton btnComprar = new JButton();
        btnComprar.setBounds(185, 515, 170, 40);
        btnComprar.setOpaque(false); btnComprar.setContentAreaFilled(false); btnComprar.setBorderPainted(false);
        btnComprar.addActionListener(e -> {
            double custoFinal = 0;
            for (Map.Entry<Item, Integer> entrada : carrinho.entrySet()) {
                custoFinal += entrada.getKey().getValorCompra() * entrada.getValue();
            }
            if(custoFinal <= 0) {
                JOptionPane.showMessageDialog(popup, "Você não selecionou nenhum item!");
                return;
            }
            if (jogo.getDinheiro() >= custoFinal) {
                for (Map.Entry<Item, Integer> entrada : carrinho.entrySet()) {
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
        final Color corNormal = new Color(0x57, 0x12, 0x07);
        final Color corHover = Color.RED;
        btnFechar.setBounds(510, 15, 50, 50);
        btnFechar.setFont(new Font("Arial", Font.BOLD, 24));
        btnFechar.setForeground(corNormal);
        btnFechar.setOpaque(false); btnFechar.setContentAreaFilled(false); btnFechar.setBorderPainted(false);
        btnFechar.setFocusPainted(false);
        btnFechar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFechar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnFechar.setForeground(corHover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnFechar.setForeground(corNormal); }
        });
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
        Map<Receita, Integer> quantidadesParaCozinhar = new HashMap<>();
        Map<Receita, Double> precosEditados = new HashMap<>();
        List<Receita> todasAsReceitas = Receita.getTodasReceitas();
        int yPos = 160;
        int espacamento = 175;
        for (Receita receita : todasAsReceitas) {
            if (receita.isDesbloqueada()) {
                precosEditados.put(receita, receita.getPrecoVenda());
                quantidadesParaCozinhar.put(receita, 0);
                int maxPossivel = jogo.getInventario().getVendasPossiveis(receita);
                JLabel lblQtdAtual = new JLabel("0");
                lblQtdAtual.setFont(new Font("Arial", Font.BOLD, 20));
                lblQtdAtual.setForeground(new Color(0x57, 0x12, 0x07));
                lblQtdAtual.setHorizontalAlignment(SwingConstants.CENTER);
                lblQtdAtual.setBounds(333, yPos + 20, 40, 30);
                painel.add(lblQtdAtual);
                JButton btnMenosQtd = new JButton();
                btnMenosQtd.setBounds(305, yPos + 20, 30, 30);
                btnMenosQtd.setOpaque(false); btnMenosQtd.setContentAreaFilled(false); btnMenosQtd.setBorderPainted(false);
                painel.add(btnMenosQtd);
                JButton btnMaisQtd = new JButton();
                btnMaisQtd.setBounds(365, yPos + 20, 30, 30);
                btnMaisQtd.setOpaque(false); btnMaisQtd.setContentAreaFilled(false); btnMaisQtd.setBorderPainted(false);
                painel.add(btnMaisQtd);
                JLabel lblPrecoAtual = new JLabel(String.format("%.0f", receita.getPrecoVenda()));
                lblPrecoAtual.setFont(new Font("Arial", Font.BOLD, 20));
                lblPrecoAtual.setForeground(new Color(0x57, 0x12, 0x07));
                lblPrecoAtual.setHorizontalAlignment(SwingConstants.CENTER);
                lblPrecoAtual.setBounds(425, yPos + 20, 40, 30);
                painel.add(lblPrecoAtual);
                JButton btnMenosPreco = new JButton();
                btnMenosPreco.setBounds(390, yPos + 20, 30, 30);
                btnMenosPreco.setOpaque(false); btnMenosPreco.setContentAreaFilled(false); btnMenosPreco.setBorderPainted(false);
                painel.add(btnMenosPreco);
                JButton btnMaisPreco = new JButton();
                btnMaisPreco.setBounds(465, yPos + 20, 30, 30);
                btnMaisPreco.setOpaque(false); btnMaisPreco.setContentAreaFilled(false); btnMaisPreco.setBorderPainted(false);
                painel.add(btnMaisPreco);
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
            } else {
                JLabel bloqueadoOverlay = new JLabel("Bloqueado") {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(0, 0, 0, 180));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
                        super.paintComponent(g);
                        g2.dispose();
                    }
                };
                bloqueadoOverlay.setFont(new Font("Arial", Font.BOLD, 22));
                bloqueadoOverlay.setHorizontalAlignment(SwingConstants.CENTER);
                bloqueadoOverlay.setForeground(Color.WHITE);
                bloqueadoOverlay.setOpaque(false);
                bloqueadoOverlay.setBounds(65, yPos - 43, 430, 113);
                painel.add(bloqueadoOverlay);
            }
            yPos += espacamento;
        }
        JButton btnSalvar = new JButton();
        btnSalvar.setBounds(185, 495, 190, 60);
        btnSalvar.setOpaque(false); btnSalvar.setContentAreaFilled(false); btnSalvar.setBorderPainted(false);
        btnSalvar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalvar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            for(java.util.Map.Entry<br.com.trabalhofinalpoo.sushicat.backend.Receita, Double> entrada : precosEditados.entrySet()){
                Receita receita = entrada.getKey();
                double novoPreco = entrada.getValue();
                receita.setPrecoVenda(novoPreco);
                receita.recalcularChanceVenda(jogo.getLoja());
            }

            jogo.setPlanoDeProducao(quantidadesParaCozinhar);

            JOptionPane.showMessageDialog(popup, "Plano de produção e preços salvos!");
            popup.dispose();
        });
        painel.add(btnSalvar);
        JButton btnFechar = new JButton("X");
        final Color corNormal = new Color(0x57, 0x12, 0x07);
        final Color corHover = Color.RED;
        btnFechar.setBounds(510, 15, 50, 50);
        btnFechar.setFont(new Font("Arial", Font.BOLD, 24));
        btnFechar.setForeground(corNormal);
        btnFechar.setOpaque(false); btnFechar.setContentAreaFilled(false); btnFechar.setBorderPainted(false);
        btnFechar.setFocusPainted(false);
        btnFechar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnFechar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) { btnFechar.setForeground(corHover); }
            public void mouseExited(java.awt.event.MouseEvent evt) { btnFechar.setForeground(corNormal); }
        });
        btnFechar.addActionListener(e -> popup.dispose());
        painel.add(btnFechar);
        popup.setContentPane(painel);
        popup.setVisible(true);
    }

    private void mostrarTransicaoDeDia() {
        JPanel painelAnimacao = new JPanel(new BorderLayout());
        painelAnimacao.setBackground(Color.BLACK);
        ImageIcon gif = new ImageIcon("src/resources/transicao.gif");
        JLabel labelAnimacao = new JLabel(gif);
        painelAnimacao.add(labelAnimacao, BorderLayout.CENTER);
        String mensagemAleatoria = MENSAGENS_TRANSICAO.get(random.nextInt(MENSAGENS_TRANSICAO.size()));
        JLabel mensagemLabel = new JLabel(mensagemAleatoria);
        mensagemLabel.setFont(new Font("Serif", Font.ITALIC, 22));
        mensagemLabel.setForeground(Color.WHITE);
        mensagemLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mensagemLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 25, 10));
        painelAnimacao.add(mensagemLabel, BorderLayout.SOUTH);
        setContentPane(painelAnimacao);
        revalidate();
        repaint();
        Timer timer = new Timer(7000, e -> {
            preencherTelaResumo();
            setContentPane(telaNiveis);
            revalidate();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void mostrarTelaReceitas() {
        JOptionPane.showMessageDialog(this, "Livro de Receitas em construção!");
    }

    private void criarTelas() {
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
        btnInvisivel.setOpaque(false); btnInvisivel.setContentAreaFilled(false); btnInvisivel.setBorderPainted(false);
        btnInvisivel.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            setContentPane(telaJogo);
            atualizarStatusUI();
            revalidate();
            repaint();
        });
        telaInicial.add(labelBotao);
        telaInicial.add(btnInvisivel);

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
        JButton btnComprar = new JButton();
        btnComprar.setBounds(250, 250, 380, 90);
        btnComprar.setOpaque(false); btnComprar.setContentAreaFilled(false); btnComprar.setBorderPainted(false);
        btnComprar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            mostrarTelaCompraIngredientes();
        });
        telaJogo.add(btnComprar);
        JButton btnPrecos = new JButton();
        btnPrecos.setBounds(250, 360, 380, 90);
        btnPrecos.setOpaque(false); btnPrecos.setContentAreaFilled(false); btnPrecos.setBorderPainted(false);
        btnPrecos.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            mostrarTelaPrecos();
        });
        telaJogo.add(btnPrecos);
        JButton btnMelhorias = new JButton();
        btnMelhorias.setBounds(250, 470, 380, 90);
        btnMelhorias.setOpaque(false); btnMelhorias.setContentAreaFilled(false); btnMelhorias.setBorderPainted(false);
        btnMelhorias.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            JOptionPane.showMessageDialog(this, "Tela de Melhorias.", "Melhorias", JOptionPane.INFORMATION_MESSAGE);
        });
        telaJogo.add(btnMelhorias);
        JButton btnIniciarDia = new JButton();
        btnIniciarDia.setBounds(250, 580, 380, 90);
        btnIniciarDia.setOpaque(false); btnIniciarDia.setContentAreaFilled(false); btnIniciarDia.setBorderPainted(false);
        btnIniciarDia.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIniciarDia.addActionListener(e -> {
            List<String> mensagens = jogo.iniciarDia();
            playSoundEffect("src/resources/click.wav");
            if (!mensagens.isEmpty()) {
                for (String msg : mensagens) {
                    JOptionPane.showMessageDialog(this, msg, "Novidades!", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            if (verificarGameOver()) {
                return;
            }
            mostrarTransicaoDeDia();
        });
        telaJogo.add(btnIniciarDia);

        JButton btnLivro = new JButton();
        btnLivro.setBounds(680, 240, 120, 120);
        btnLivro.setOpaque(false); btnLivro.setContentAreaFilled(false); btnLivro.setBorderPainted(false);
        btnLivro.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            mostrarTelaReceitas();
        });
        telaJogo.add(btnLivro);

        telaNiveis = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("src/resources/resumo.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        telaNiveis.setLayout(null);
        Color corTextoMarrom = new Color(0x57, 0x12, 0x07);
        lblResumoReceita = new JLabel("R$ 0.00");
        lblResumoReceita.setFont(new Font("Arial", Font.BOLD, 22));
        lblResumoReceita.setForeground(corTextoMarrom);
        lblResumoReceita.setBounds(380, 350, 400, 30);
        telaNiveis.add(lblResumoReceita);
        lblResumoGastos = new JLabel("R$ 0.00");
        lblResumoGastos.setFont(new Font("Arial", Font.BOLD, 22));
        lblResumoGastos.setForeground(Color.RED);
        lblResumoGastos.setBounds(380, 412, 400, 30);
        telaNiveis.add(lblResumoGastos);
        lblResumoAluguel = new JLabel("R$ 0.00");
        lblResumoAluguel.setFont(new Font("Arial", Font.BOLD, 22));
        lblResumoAluguel.setForeground(Color.RED);
        lblResumoAluguel.setBounds(380, 465, 400, 30);
        telaNiveis.add(lblResumoAluguel);
        lblResumoLucro = new JLabel("R$ 0.00");
        lblResumoLucro.setFont(new Font("Arial", Font.BOLD, 26));
        lblResumoLucro.setForeground(Color.GREEN);
        lblResumoLucro.setBounds(430, 515, 400, 30);
        telaNiveis.add(lblResumoLucro);
        JButton btnVoltar = new JButton("Continuar");
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 18));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setBackground(new Color(0x954535));
        btnVoltar.setBounds(400, 560, 200, 50);

        Border bordaExterna = BorderFactory.createLineBorder(Color.WHITE, 2, true);
        Border espacamentoInterno = BorderFactory.createEmptyBorder(10, 20, 10, 20);
        btnVoltar.setBorder(BorderFactory.createCompoundBorder(bordaExterna, espacamentoInterno));

        btnVoltar.setFocusPainted(false);
        btnVoltar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVoltar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            jogo.prepararProximoDia();
            setContentPane(telaJogo);
            atualizarStatusUI();
            revalidate();
            repaint();
        });
        telaNiveis.add(btnVoltar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}