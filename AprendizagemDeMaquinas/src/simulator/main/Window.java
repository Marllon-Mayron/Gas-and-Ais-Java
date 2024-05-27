package simulator.main;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.ambiente.Ambiente;
import simulator.entities.SpecieAnimalia;
import simulator.entities.SpeciePlantae;
import simulator.entities.animalia.Coquito;
import simulator.enums.EatingPractices;
import simulator.graphics.ColorChart;
import simulator.utils.Utils;
public class Window extends Canvas implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;
    public static JFrame frame;
    private Thread thread;
    private boolean isRunning = true;
    public static final int windowSimulatorWidth = 240;
    public static final int windowSimulatorHeight = 240;
    public static final int SCALE = 3;
    public int x, y;
    private BufferedImage image;
    private Graphics g;
    public static Ambiente jungle = new Ambiente(30, 60);

    public static ColorChart colorChart;

    public Window() throws Exception {
        try {
            Utils.csvController.deleteAllFiles(Utils.csvController.resDir);
            System.out.println("Todos os arquivos foram apagados com sucesso.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        addKeyListener(this);
        setPreferredSize(new Dimension(windowSimulatorWidth * SCALE, windowSimulatorHeight * SCALE));
        image = new BufferedImage(windowSimulatorWidth, windowSimulatorHeight, BufferedImage.TYPE_INT_RGB);
        initFrame();
        startSimulator();
        
    }

    public void startSimulator() {
        jungle.generateWalls(10);
        jungle.generateFoods();
        for (int i = 0; i < jungle.getPopulationInitial(); i++) {
            int tempx = 0;
            int tempy = 0;
            do {
                tempx = Utils.random.nextInt(Utils.numGrid);
                tempy = Utils.random.nextInt(Utils.numGrid);
            } while (jungle.path[tempx][tempy]);

            Coquito coquito = new Coquito("Specie1", EatingPractices.HERBIVORE, "", tempx, tempy,
                    (windowSimulatorWidth / (Utils.numGrid * 2)), (windowSimulatorHeight / (Utils.numGrid * 2)), jungle, 15);
            jungle.getAnimals().add(coquito);
            jungle.path[tempx][tempy] = true;
        }
        Utils.csvController.csvCurrentDay(jungle, jungle.getAnimals());
        //ATUALIZAR GRÁFICO
        colorChart.updateChart();
    }

    public void initFrame() {
        frame = new JFrame("IA TRAINING");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(this, BorderLayout.CENTER);

        colorChart = new ColorChart("Distrubuição Genética", "CurrentDays-Specie1.csv", 20, 40);
        mainPanel.add(colorChart.getContentPane(), BorderLayout.EAST);

        frame.add(mainPanel);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        gd.setFullScreenWindow(frame);

        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws Exception {
        Window window = new Window();
        window.start();
    }

    public void tick() {
        jungle.tick();
        for (int i = 0; i < jungle.getAnimals().size(); i++) {
            SpecieAnimalia sa = jungle.getAnimals().get(i);
            sa.tick();
        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();

        if (bs == null) {
            this.createBufferStrategy(3);
            return;
        }

        g = image.getGraphics();
        g.setColor(new Color(0, 0, 0));
        g.fillRect(0, 0, windowSimulatorWidth * SCALE, windowSimulatorHeight * SCALE);

        for (int i = 0; i < Utils.numGrid; i++) {
            for (int j = 0; j < Utils.numGrid; j++) {
                if (jungle.path[i][j]) {
                    g.setColor(Color.GRAY);
                } else {
                    g.setColor(Color.DARK_GRAY);
                }

                g.fillRect((windowSimulatorWidth / Utils.numGrid) * i, (windowSimulatorHeight / Utils.numGrid) * j, (windowSimulatorWidth / (Utils.numGrid + 1)),
                        (windowSimulatorHeight / (Utils.numGrid + 1)));
            }
        }

        for (int i = 0; i < jungle.getPlants().size(); i++) {
            SpeciePlantae sp = jungle.getPlants().get(i);
            sp.render(g);
        }
        for (int i = 0; i < jungle.getAnimals().size(); i++) {
            SpecieAnimalia sa = jungle.getAnimals().get(i);
            sa.render(g);
        }

        g.setColor(Color.black);
        g.dispose();
        g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, windowSimulatorWidth * SCALE, windowSimulatorHeight * SCALE, null);

        for (int i = 0; i < jungle.getAnimals().size(); i++) {
            SpecieAnimalia sa = jungle.getAnimals().get(i);
            sa.renderSmall(g);
        }
        g.setColor(Color.white);
        g.drawString("Dias: " + jungle.getDays() + " (Proximo dia: " + (jungle.secDay - jungle.sec) + ")", 2, 12);
        g.drawString("Individuos Dia Anterior: " + jungle.yesterdayNumAnimals, 2, 28);
        g.drawString("Individuos: " + jungle.getAnimals().size(), 2, 44);

        bs.show();
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        int frames = 0;
        double timer = System.currentTimeMillis();
        requestFocus();
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_R) {
            Utils.showRangeVision = !Utils.showRangeVision;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
}