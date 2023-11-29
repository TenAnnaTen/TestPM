import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            CalculatorFrame frame = new CalculatorFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

class CalculatorFrame extends JFrame {
    public CalculatorFrame() {
        setTitle("Калькулятор");
        CalculatorPanel panel = new CalculatorPanel();
        add(panel);
        pack();
        int width = 400;
        int height = 350;
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();
        setBounds(screenWidth / 2 - width / 2, screenHeight / 2 - height / 2, width, height);
    }
}

class CalculatorPanel extends JPanel {
    private JButton display;
    private JPanel mainPanel;
    private JPanel buttonsPanel;
    private JPanel additionalPanel;
    private BigDecimal result;
    private String lastCommand;
    private boolean start;

    public CalculatorPanel() {
        setLayout(new BorderLayout());
        result = BigDecimal.ZERO;
        lastCommand = "=";
        start = true;

        mainPanel = new JPanel(new BorderLayout());
        buttonsPanel = new JPanel(new GridLayout(4, 4));
        additionalPanel = new JPanel(new GridLayout(4, 1));

        display = new JButton("0");
        display.setEnabled(false);
        display.setFont(display.getFont().deriveFont(50f));
        mainPanel.add(display, BorderLayout.NORTH);

        ActionListener insert = new InsertAction();
        ActionListener command = new CommandAction();
        addButton("7", insert);
        addButton("8", insert);
        addButton("9", insert);
        addButton("÷", command);
        addButton("4", insert);
        addButton("5", insert);
        addButton("6", insert);
        addButton("*", command);
        addButton("1", insert);
        addButton("2", insert);
        addButton("3", insert);
        addButton("-", command);  // Изменено на "-"
        addButton("0", insert);
        addButton(".", insert);
        addButton("=", command);
        addButton("+", command);

        addButton("sqrt", command);
        addButton("pow", command);
        addButton("C", command);
        addButton("AC", command);

        mainPanel.add(buttonsPanel, BorderLayout.CENTER);
        mainPanel.add(additionalPanel, BorderLayout.EAST);
        add(mainPanel);
    }

    private void addButton(String label, ActionListener listener) {
        JButton button = new JButton(label);
        button.setFont(button.getFont().deriveFont(20f));
        button.addActionListener(listener);

        if (label.equals("sqrt") || label.equals("pow") || label.equals("C") || label.equals("AC")) {
            additionalPanel.add(button);
        } else {
            buttonsPanel.add(button);
        }
    }

    private class InsertAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String input = event.getActionCommand();
            if (start) {
                display.setText("");
                start = false;
            }
            display.setText(display.getText() + input);
        }
    }

    private class CommandAction implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            if (start) {
                if (command.equals("-")) {
                    display.setText(command);
                    start = false;
                } else if (command.equals("sqrt")) {
                    result = new BigDecimal(Math.sqrt(new BigDecimal(display.getText()).doubleValue()));
                    display.setText(result.stripTrailingZeros().toPlainString());
                } else if (command.equals("C")) {
                    clearLastEntry();
                } else if (command.equals("AC")) {
                    clearAll();
                } else {
                    lastCommand = command;
                }
            } else {
                calculate(new BigDecimal(display.getText()));
                lastCommand = command;
                start = true;
            }
        }

        private void calculate(BigDecimal x) {
            if (lastCommand.equals("+")) result = result.add(x);
            else if (lastCommand.equals("-")) result = result.subtract(x);
            else if (lastCommand.equals("*")) result = result.multiply(x);
            else if (lastCommand.equals("/")) result = result.divide(x, 10, BigDecimal.ROUND_HALF_UP);
            else if (lastCommand.equals("=")) result = x;
            else if (lastCommand.equals("pow")) result = new BigDecimal(Math.pow(result.doubleValue(), x.doubleValue()));

            if (result.compareTo(BigDecimal.ZERO) == 0) {
                result = BigDecimal.ZERO;
            }
            display.setText(result.stripTrailingZeros().toPlainString());
        }

        private void clearLastEntry() {
            if (!display.getText().isEmpty()) {
                display.setText(display.getText().substring(0, display.getText().length() - 1));
            }
        }

        private void clearAll() {
            result = BigDecimal.ZERO;
            display.setText("0");
            start = true;
        }
    }
}
