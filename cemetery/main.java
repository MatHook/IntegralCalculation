import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.List;

class ExpressionParser /* Parser by postfix */{
    private static String operators = "+-*/";
    private static String delimiters = "() " + operators;
    static boolean flag = true;
    private static boolean isDelimiter(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }
    private static boolean isOperator(String token) {
        if (token.equals("u-")) return true;
        for (int i = 0; i < operators.length(); i++) {
            if (token.charAt(0) == operators.charAt(i)) return true;
        }
        return false;
    }
    private static boolean isFunction(String token) {
        return token.equals("sqrt") || token.equals("cube") || token.equals("pow10");
    }
    private static int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals("*") || token.equals("/")) return 3;
        return 4;
    }
    static List<String> parse(String infix) {
        List<String> postfix = new ArrayList<String>();
        Deque<String> stack = new ArrayDeque<String>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
        String prev = "";
        String curr = "";
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) {
                System.out.println("Incorrect function.");
                flag = false;
                return postfix;
            }
            if (curr.equals(" ")) continue;
            if (isFunction(curr)) stack.push(curr);
            else if (isDelimiter(curr)) {
                if (curr.equals("(")) stack.push(curr);
                else if (curr.equals(")")) {
                    while (!stack.peek().equals("(")) {
                        postfix.add(stack.pop());
                        if (stack.isEmpty()) {
                            System.out.println("Incorrect '(' and ')'");
                            flag = false;
                            return postfix;
                        }
                    }
                    stack.pop();
                    if (!stack.isEmpty() && isFunction(stack.peek())) {
                        postfix.add(stack.pop());
                    }
                }
                else {
                    if (curr.equals("-") && (prev.equals("") || (isDelimiter(prev)  && !prev.equals(")")))) {
                        // Unar minus
                        curr = "u-";
                    }
                    else {
                        while (!stack.isEmpty() && (priority(curr) <= priority(stack.peek()))) {
                            postfix.add(stack.pop());
                        }

                    }
                    stack.push(curr);
                }

            }

            else {
                postfix.add(curr);
            }
            prev = curr;
        }

        while (!stack.isEmpty()) {
            if (isOperator(stack.peek())) postfix.add(stack.pop());
            else {
                System.out.println("Incorrect '(' and ')'");
                flag = false;
                return postfix;
            }
        }
        return postfix;
    }
}

class My_frame extends JFrame{
    private static Double calc(List<String> postfix) /*PostFix method to calculate in string */ {
        Deque<Double> stack = new ArrayDeque<Double>();
        for (String x : postfix) {
            switch (x) {
                case "sqrt":
                    stack.push(Math.sqrt(stack.pop()));
                    break;
                case "cube":
                    Double tmp = stack.pop();
                    stack.push(tmp * tmp * tmp);
                    break;
                case "pow10":
                    stack.push(Math.pow(10, stack.pop()));
                    break;
                case "+":
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "-": {
                    Double b = stack.pop(), a = stack.pop();
                    stack.push(a - b);
                    break;
                }
                case "*":
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "/": {
                    Double b = stack.pop(), a = stack.pop();
                    stack.push(a / b);
                    break;
                }
                case "u-":
                    stack.push(-stack.pop());
                    break;
                default:
                    stack.push(Double.valueOf(x));
                    break;
            }
        }
        return stack.pop();
    }
    private void read() throws IOException /* Read textfile method */{
        Scanner sc = new Scanner(new File(System.getProperty("user.home") + "/IntegralResult.txt"));
        String line;
        int i = 1;

        while(sc.hasNextLine()) {
            line = sc.nextLine();
            JOptionPane.showMessageDialog(null, line);
            i++;
        }
    }
    private My_frame() /* Main GUI */{
        super("Curse project");
        JFrame frame = new JFrame();
        Random rnd = new Random();
        String filepath = System.getProperty("user.home") + "/IntegralResult.txt";

        JLabel left_corner = new JLabel("Left corner of integral"); //Labels
        JLabel right_corner = new JLabel("Right corner of integral");
        JLabel text_integral = new JLabel("Input function: ");
        JLabel result_midrectpson = new JLabel("");
        JLabel result_simpson = new JLabel("");
        JLabel result_newkotes = new JLabel("");
        result_simpson.setBounds(10,100,600,25);
        result_midrectpson.setBounds(10,150,600,25);
        result_newkotes.setBounds(10, 200, 600,25);
        text_integral.setBounds(10,40, 200, 25);
        left_corner.setBounds(10,10,200,25);
        right_corner.setBounds(230,10,200,25);

        JTextField left_corner_text = new JTextField(10); //TextAreas
        JTextField right_corner_text = new JTextField(10);
        JTextField func_tfield = new JTextField(25);
        left_corner_text.setBounds(150,10,50,25);
        right_corner_text.setBounds(380, 10, 50,25);
        func_tfield.setBounds(110,40,320,25);

        JButton button_calc = new JButton("Integrate"); //Buttons
        button_calc.setBounds(5,70,300,25);
        JButton show_results = new JButton("Show past results");
        show_results.setBounds(450,500,150,25);

        button_calc.addActionListener(new ActionListener() { //Calculating
            @Override
            public void actionPerformed(ActionEvent e) {
                double var_left_corner_integral = Double.parseDouble(left_corner_text.getText());
                double var_right_corner_integral = Double.parseDouble(right_corner_text.getText());
                double var_mid_integral = (var_left_corner_integral + var_right_corner_integral)/2;
                double var_rand_integral = var_left_corner_integral + rnd.nextDouble()*(var_right_corner_integral - var_left_corner_integral);
                double c_0 = 5.0 / 288.0;
                double var_h_nkotes = (var_right_corner_integral-var_left_corner_integral)/ 5.0;
                double[] var_wits = new double[] {19.0, 75.0, 50.0, 50.0, 75.0, 19.0};
                double var_sum_nkotes = 0.0;
                String func_str = func_tfield.getText();
                String str_l = func_str.replaceAll("x", String.valueOf(var_left_corner_integral));
                String str_r = func_str.replaceAll("x", String.valueOf(var_right_corner_integral));
                String str_s = func_str.replaceAll("x", String.valueOf(var_mid_integral));
                String str_rand = func_str.replaceAll("x", String.valueOf(var_rand_integral));
                ExpressionParser ep = new ExpressionParser();
                List<String> expression_sim_left = ep.parse(str_l);
                List<String> expression_sim_right = ep.parse(str_r);
                List<String> expression_sim_middle = ep.parse(str_s);
                List<String> expression_nktotes = ep.parse(str_rand);
                boolean flag = ep.flag;

                if (flag) {
                    calc(expression_sim_left);
                    calc(expression_sim_right);
                    calc(expression_sim_middle);
                    calc(expression_nktotes);
                }
                for(int i = 0; i < 4; i++) {
                    var_sum_nkotes += var_wits[i]*calc(expression_nktotes);
                }
                result_simpson.setText("∫" + func_tfield.getText() + " result by Simpson method ≈ " + String.valueOf(((var_right_corner_integral-var_left_corner_integral)/6)
                        *(calc(expression_sim_left) + 4 * calc(expression_sim_middle) + calc(expression_sim_right)))
                        + ". Left corner: " + var_left_corner_integral + " and right corner: " + var_right_corner_integral + "\n");
                result_midrectpson.setText("∫" + func_tfield.getText() + " result by Middle Rectangles ≈ " + String.valueOf
                        (calc(expression_sim_middle)*(var_right_corner_integral-var_left_corner_integral))
                        + ". Left corner: " + var_left_corner_integral + " and right corner: " + var_right_corner_integral + "\n");
                result_newkotes.setText("∫" + func_tfield.getText() + " result by Nyuton-Kotes for 5 degree method ≈ "
                        + String.valueOf(c_0*var_h_nkotes*var_sum_nkotes)
                        + ". Left corner: " + var_left_corner_integral + " and right corner: " + var_right_corner_integral + "\n");
                try {
                    Files.write(Paths.get(filepath), result_midrectpson.getText().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    Files.write(Paths.get(filepath), result_simpson.getText().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                    Files.write(Paths.get(filepath), result_newkotes.getText().getBytes(), StandardOpenOption.APPEND, StandardOpenOption.CREATE);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null,"Can't write info");
                    e1.printStackTrace();
                }
            }
        });

        show_results.addActionListener(new ActionListener() { //Showing result in MessageDialog from text file
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    read();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "File not found");
                }
            }
        });

        frame.add(left_corner);
        frame.add(right_corner);
        frame.add(text_integral);
        frame.add(result_simpson);
        frame.add(result_midrectpson);
        frame.add(result_newkotes);
        frame.add(left_corner_text);
        frame.add(right_corner_text);
        frame.add(func_tfield);
        frame.add(button_calc);
        frame.add(show_results);
        frame.setSize(600, 600);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args){
        new My_frame();
    }
}