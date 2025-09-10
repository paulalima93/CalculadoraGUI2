import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
//        try (var conn = ConnectionFactory.getConnection()) {
//            System.out.println("Conexão bem sucedida!");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // Criação da janela
        JFrame frame = new JFrame("Calculadora");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 400);

        // Campo de texto
        JTextField textField = new JTextField();
        textField.setEditable(false);
        textField.setHorizontalAlignment(JTextField.RIGHT);
        textField.setPreferredSize(new Dimension(300, 50));
        frame.add(textField, BorderLayout.NORTH);

        // Painel de botões
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4));

        String[] botoes = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "C", "=", "+"
        };

//para cada texto que tenho, na lista de botoes
        for (String text : botoes) {
            //eu vou criar um botão novo com o texto
            JButton button = new JButton(text);
            panel.add(button);

            button.addActionListener(e -> {
                String comando = e.getActionCommand();

                if (comando.equals("C")) {
                    textField.setText("");
                } else if (comando.equals("=")) {
                    try {
                        String expressao = textField.getText(); //"789+545"
                        double resultado = calcular(expressao); //calcular("789+545") -> resultado = 1334.0
                        textField.setText(String.valueOf(resultado)); //"1334.0"

                        //Salvando no banco
                        CalculadoraDAO calcdao = new CalculadoraDAO();
                        calcdao.salvarOperacao(expressao, resultado);

                    } catch (Exception ex) {
                        textField.setText("Erro");
                    }
                } else {
                    textField.setText(textField.getText() + comando);
                }
            });
        }

        frame.add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static double calcular(String expressao) {
        // Remove espaços da expressão
        expressao = expressao.replaceAll("\\s+", "");

        // Listas para armazenar números e operadores
        List<Double> numeros = new ArrayList<>();
        List<Character> operadores = new ArrayList<>();

        String numeroBuffer = ""; // armazena os dígitos de cada número

        // Passo 1: separar números e operadores -> "789+545"
        for (int i = 0; i < expressao.length(); i++) {
            char c = expressao.charAt(i);

            // Se for dígito ou ponto, acumula no número
            if (Character.isDigit(c) || c == '.') {
                numeroBuffer += c; //545
            }

            // Se for operador
            else if (c == '+' || c == '-' || c == '*' || c == '/') {
                if (numeroBuffer.isEmpty()) {
                    throw new IllegalArgumentException("Número esperado antes do operador");
                    //numeroBuffer = "0";
                }

                // Converte o número acumulado e adiciona à lista
                numeros.add(Double.parseDouble(numeroBuffer));

                // Adiciona o operador à lista
                operadores.add(c);

                // Limpa o buffer para o próximo número
                numeroBuffer = "";
            }
            // Caractere inválido
            else {
                throw new IllegalArgumentException("Caractere inválido: " + c);
            }
        }

        // Adiciona o último número da expressão
        if (!numeroBuffer.isEmpty()) {
            numeros.add(Double.parseDouble(numeroBuffer));
        }

        // Passo 2: processar multiplicação e divisão primeiro
        for (int i = 0; i < operadores.size(); i++) {
            char op = operadores.get(i);
            if (op == '*' || op == '/') {
                double a = numeros.get(i);
                double b = numeros.get(i + 1);
                double res;

                switch (op) {
                    case '*':
                        res = a * b;
                        break;
                    case '/':
                        if (b == 0) throw new ArithmeticException("Divisão por zero");
                        res = a / b;
                        break;
                    default:
                        throw new IllegalStateException("Operador inesperado");
                }

                // Substitui o resultado na lista de números
                numeros.set(i, res);
                numeros.remove(i + 1);

                // Remove o operador que já foi usado
                operadores.remove(i);

                // Ajusta índice para não pular operador
                i--;
            }
        }

        // Passo 3: processar soma e subtração
        double resultado = numeros.get(0);
        for (int i = 0; i < operadores.size(); i++) {
            char op = operadores.get(i);
            double b = numeros.get(i + 1);

            switch (op) {
                case '+':
                    resultado += b;
                    break;
                case '-':
                    resultado -= b;
                    break;
                default:
                    throw new IllegalStateException("Operador inesperado");
            }
        }
        return resultado;
    }
}
