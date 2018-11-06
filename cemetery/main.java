import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

class ExpressionParser {
    private static String operators = "+-*/";
    private static String delimiters = "() " + operators;
    public static boolean flag = true;
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
        if (token.equals("sqrt") || token.equals("cube") || token.equals("pow10")) return true;
        return false;
    }

    private static int priority(String token) {
        if (token.equals("(")) return 1;
        if (token.equals("+") || token.equals("-")) return 2;
        if (token.equals("*") || token.equals("/")) return 3;
        return 4;
    }

    public static List<String> parse(String infix) {
        List<String> postfix = new ArrayList<String>();
        Deque<String> stack = new ArrayDeque<String>();
        StringTokenizer tokenizer = new StringTokenizer(infix, delimiters, true);
        String prev = "";
        String curr = "";
        while (tokenizer.hasMoreTokens()) {
            curr = tokenizer.nextToken();
            if (!tokenizer.hasMoreTokens() && isOperator(curr)) {
                System.out.println("Некорректное выражение.");
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
                            System.out.println("Скобки не согласованы.");
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
                        // унарный минус
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
                System.out.println("Скобки не согласованы.");
                flag = false;
                return postfix;
            }
        }
        return postfix;
    }
}

class My_frame extends JFrame{
    public static Double calc(List<String> postfix) {

        Deque<Double> stack = new ArrayDeque<Double>();
        for (String x : postfix) {
            if (x.equals("sqrt")) stack.push(Math.sqrt(stack.pop()));
            else if (x.equals("cube")) {
                Double tmp = stack.pop();
                stack.push(tmp * tmp * tmp);
            }
            else if (x.equals("pow10")) stack.push(Math.pow(10, stack.pop()));
            else if (x.equals("+")) stack.push(stack.pop() + stack.pop());
            else if (x.equals("-")) {
                Double b = stack.pop(), a = stack.pop();
                stack.push(a - b);
            }
            else if (x.equals("*")) stack.push(stack.pop() * stack.pop());
            else if (x.equals("/")) {
                Double b = stack.pop(), a = stack.pop();
                stack.push(a / b);
            }
            else if (x.equals("u-")) stack.push(-stack.pop());
            else stack.push(Double.valueOf(x));
        }
        return stack.pop();
    }
    My_frame(){
        super("Curse project");
        JFrame frame = new JFrame();


        //Labels
        JLabel left_corner = new JLabel("Left corner of integral");
        JLabel right_corner = new JLabel("Right corner of integral");
        JLabel text_integral = new JLabel("Input function: ");
        text_integral.setBounds(10,40, 200, 25);
        left_corner.setBounds(10,10,200,25);
        right_corner.setBounds(230,10,200,25);
        //TextAreas
        JTextField left_corner_text = new JTextField(10);
        JTextField right_corner_text = new JTextField(10);
        JTextField func_tfield = new JTextField(25);
        left_corner_text.setBounds(150,10,50,25);
        right_corner_text.setBounds(380, 10, 50,25);
        func_tfield.setBounds(110,40,320,25);
        left_corner_text.setToolTipText("Input left corner of integral:");
        right_corner_text.setToolTipText("Input right corner of integral:");
        //Values of corners of integral
        left_corner_text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double var_left_corner_integral = Double.parseDouble(left_corner_text.getText());
                JOptionPane.showMessageDialog(null, "Left corner is " + var_left_corner_integral);
            }
        });
        right_corner_text.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double var_right_corner_integral = Double.parseDouble(right_corner_text.getText());
                JOptionPane.showMessageDialog(null, "Right corner is " + var_right_corner_integral);
            }
        });
        func_tfield.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String func_str = func_tfield.getText();
                JOptionPane.showMessageDialog(null,"Your function: " + func_str);
            }
        });

        ExpressionParser ep = new ExpressionParser();
        List<String> expression = ep.parse(func_tfield.getText());
        boolean flag = ep.flag;
        if (flag) {
            calc(expression);
        }

        frame.add(left_corner);
        frame.add(right_corner);
        frame.add(text_integral);
        frame.add(left_corner_text);
        frame.add(right_corner_text);
        frame.add(func_tfield);
        frame.setSize(600, 600);
        frame.setLayout(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args){
        new My_frame();
    }
}