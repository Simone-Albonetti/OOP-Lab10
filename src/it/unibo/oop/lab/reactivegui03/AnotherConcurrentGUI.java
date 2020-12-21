package it.unibo.oop.lab.reactivegui03;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH_PERC = 0.2;
    private static final double HEIGHT_PERC = 0.1;
    private static final int TIME = 5;
    private final JLabel display = new JLabel();
    private final JButton stopB = new JButton("stop");
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final Timer timer = new Timer();
    private final Agent agent = new Agent();

    public AnotherConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH_PERC), (int) (screenSize.getHeight() * HEIGHT_PERC));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(stopB);
        panel.add(up);
        panel.add(down);
        this.getContentPane().add(panel);
        this.setVisible(true);
        new Thread(timer).start();
        new Thread(agent).start();

        stopB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
               agent.stopCounting();
            }
        });

        up.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
              agent.direction = true;
            }
        });

        down.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                agent.direction = false;
            }
        });
    }

    private class Timer implements Runnable {
        @Override
        public void run() {
            int timer = TIME;
            while (timer >= 0) {
                try {
                    Thread.sleep(1000);
                    timer--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            agent.stopCounting();
            }
        }
    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile int counter;
        private volatile boolean direction = true;
        @Override
        public void run() {
            while (!this.stop) {
                if (direction) {
                     try {
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(this.counter)));
                        this.counter++;
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    try {
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(Integer.toString(this.counter)));
                        this.counter--;
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace();
                    }

                }
            }
            down.setEnabled(false);
            up.setEnabled(false);
            stopB.setEnabled(false);
        }
        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }
    }
}
