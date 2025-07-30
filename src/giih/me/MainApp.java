package giih.me;

import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class MainApp extends JFrame {
    private Clip audioClip;
    private JPanel telaInicial;
    private JPanel telaJogo;
    private JPanel telaNiveis; // Painel para a tela após "Iniciar dia"

    public MainApp() {
        setTitle("Sushi Bar");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        playBackgroundMusic("src/resources/song.wav");

        criarTelas();

        setContentPane(telaInicial); // Começa com a tela inicial
        setVisible(true);
    }

    private void playBackgroundMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            audioClip = AudioSystem.getClip();
            audioClip.open(audioStream);
            audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            audioClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
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
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao carregar o efeito sonoro: " + e.getMessage());
        }
    }

    private void criarTelas() {
        // ----------- Tela Inicial -----------
        telaInicial = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = new ImageIcon("src/resources/tela.png").getImage();
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        telaInicial.setLayout(null);

        ImageIcon iconBotao = new ImageIcon("src/resources/btn.png");
        JLabel labelBotao = new JLabel(iconBotao);
        labelBotao.setBounds(300, 500, 360, 144);

        JButton btnInvisivel = new JButton();
        btnInvisivel.setBounds(300, 500, 360, 144);
        btnInvisivel.setOpaque(false);
        btnInvisivel.setContentAreaFilled(false);
        btnInvisivel.setBorderPainted(false);
        btnInvisivel.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            setContentPane(telaJogo);
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
                try {
                    Image bg = new ImageIcon("src/resources/menu.png").getImage();
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        telaJogo.setLayout(null);

        // 1. Botão "Comprar ingredientes"
        JButton btnComprar = new JButton();
        btnComprar.setBounds(250, 250, 380, 90);
        btnComprar.setOpaque(false);
        btnComprar.setContentAreaFilled(false);
        btnComprar.setBorderPainted(false);
        btnComprar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            JOptionPane.showMessageDialog(this, "Tela de Compra de Ingredientes.", "Ingredientes", JOptionPane.INFORMATION_MESSAGE);
        });
        telaJogo.add(btnComprar);

        // 2. Botão "Definir preços"
        JButton btnPrecos = new JButton();
        btnPrecos.setBounds(250, 360, 380, 90);
        btnPrecos.setOpaque(false);
        btnPrecos.setContentAreaFilled(false);
        btnPrecos.setBorderPainted(false);
        btnPrecos.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            JOptionPane.showMessageDialog(this, "Tela para Definir Preços.", "Preços", JOptionPane.INFORMATION_MESSAGE);
        });
        telaJogo.add(btnPrecos);

        // 3. Botão "Melhorias"
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

        // 4. Botão "Iniciar dia"
        JButton btnIniciarDia = new JButton();
        btnIniciarDia.setBounds(250, 580, 380, 90);
        btnIniciarDia.setOpaque(false);
        btnIniciarDia.setContentAreaFilled(false);
        btnIniciarDia.setBorderPainted(false);
        btnIniciarDia.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            setContentPane(telaNiveis);
            revalidate();
            repaint();
        });
        telaJogo.add(btnIniciarDia);

        // 5. Botão "Livro"
        JButton btnLivro = new JButton();
        btnLivro.setBounds(680, 240, 120, 120);
        btnLivro.setOpaque(false);
        btnLivro.setContentAreaFilled(false);
        btnLivro.setBorderPainted(false);
        btnLivro.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            JOptionPane.showMessageDialog(this, "Aqui você pode ver o livro de receitas!", "Livro de Receitas", JOptionPane.INFORMATION_MESSAGE);
        });
        telaJogo.add(btnLivro);

        // Tela final
        telaNiveis = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = new ImageIcon("src/resources/resumo.png").getImage();
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    g.setColor(Color.DARK_GRAY); // Cor de fallback
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        telaNiveis.setLayout(null);

        JButton btnVoltar = new JButton("Voltar ao Menu");
        btnVoltar.setBounds(400, 500, 150, 50);
        btnVoltar.addActionListener(e -> {
            playSoundEffect("src/resources/click.wav");
            setContentPane(telaJogo);
            revalidate();
            repaint();
        });
        telaNiveis.add(btnVoltar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}