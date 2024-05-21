/*
 *  CISC 3160 -- Programming Languages
 *  Danil Ermolin
 *  Brooklyn College, CUNY
 *  Spring 2024
 *  Prof. Neng-Fa Zhou
 *  Toy Project
 * 
 *  Toy Project overview: 
 *  ------------------
 *  The following defines a simple language, in which a program consists of assignments and each variable is assumed to be of the integer type. The interpreter for the language is written in Java. 
 * 
 *  The interpreter does the following: 
 *  1. Detects syntax errors
 *  2. Reports uninitialized variables
 *  3. Performs assignments if there is no error and prints out the values of all the variables after all the assignments are done.
 */


import java.util.*; //importing utility classes for the program, specifically Map and Set
import java.util.regex.*; //importing utility for regular expression


public class Toy{

   private static Map<String, Integer> variables = new HashMap<>(); //holding variable names and integer values
   private static Set<String> initializedString = new HashSet<>(); //Storing and keeping track of initialized string variables 
   private static final Pattern identifyPattern = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*"); //Matching valid variable names 

    public static void main(String[] args) {

      String [] input = { //Declaring string array, that includes expressions to be interpreted
         "x = 001;", 
         "x_2 = 0;",
         "x = 0; y = x; z = --- (x+y);",
         "x = 1; y = 2; z = --- (x+y) * (x + - y);",
      };

      for (int i = 0; i < input.length; i++){
         System.out.println("Input " + (i+1) + ": ");
         System.out.println();
         try{
            parse(input[i]);
            printVar();
         }catch (Exception exc){
            System.out.println("Error");
         }
      }
        
        
    }

    static class syntaxErrorExc extends Exception{
      public syntaxErrorExc(String msg){
         super(msg);
      }
   }

   static class variableExc extends Exception{
      public variableExc(String msg){
         super(msg);
      }
   }

   private static void parse(String input) throws syntaxErrorExc, variableExc{
      variables.clear();
      initializedString.clear();

      String[] assignments = input.split(";");
      for (String assignment : assignments){
         if (assignment.trim().isEmpty()) continue;
            String[] parts = assignment.split("=");
            if (parts.length != 2) throw new syntaxErrorExc("Invalid syntax");

         String identifier = parts[0].trim();
         String expression = parts[1].trim();

         if (!identifyPattern.matcher(identifier).matches()) throw new syntaxErrorExc("Invalid identifier: " + identifier);
         int value = evalExp(expression);
         variables.put(identifier, value);
         initializedString.add(identifier);
      }
   }

   private static int evalExp(String expression) throws syntaxErrorExc, variableExc{
      expression = expression.replaceAll("\\s+", "");
      if (expression.isEmpty()) throw new syntaxErrorExc("Empty");

      Stack<Integer> values = new Stack<>();
      Stack<Character> oper = new Stack<>();

      for(int i = 0; i < expression.length(); i++){
         char c = expression.charAt(i);

        if(Character.isDigit(c)){
         int num = 0;
         while (i < expression.length() && Character.isDigit(expression.charAt(i))){
            num = num * 10 + (expression.charAt(i) - '0');
            i++;
         }
         i--;
         values.push(num);
        }else if (Character.isLetter(c) || c == '_'){
         StringBuilder sb = new StringBuilder();
         while (i < expression.length() && (Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_')) {
             sb.append(expression.charAt(i));
             i++;
         }
         i--;
         String identifier = sb.toString();
         if (!initializedString.contains(identifier)) throw new variableExc("Uninitialized variable: " + identifier);
         values.push(variables.get(identifier));
        } else if (c == '(') {
         oper.push(c);
        }else if (c == ')'){
         while (oper.peek() != '('){
            values.push(operator(oper.pop(), values.pop(), values.pop()));
         }
         oper.pop();
        }else if (c == '+' || c == '-' || c == '*' || c == '/'){
         while (!oper.isEmpty() && precedence(c) <= precedence(oper.peek())){
            values.push(operator(oper.pop(), values.pop(), values.pop()));
         }
         oper.pop();
      }else if(c == '-' && (i == 0 || expression.charAt(i - 1) == '(')){
         values.push(0);
         oper.push('-');
      }else {
         throw new syntaxErrorExc("Invalid: charter in expression: " + c);
      }
   }

      while (!oper.isEmpty()){
         values.push(operator(oper.pop(), values.pop(), values.pop()));
      }

      return values.pop();
   

   }

   private static int operator (char opr, int a, int b) throws syntaxErrorExc{
      switch (opr) {
         case '+': 
         return (a + b);
         case '-':
         return (a - b);
         case '*':
         return (a * b);
         case '/':
         if (b == 0) throw new syntaxErrorExc("Invalid: division by 0");
         return a/b;
         default:
         throw new syntaxErrorExc("Invalid: unknow operator " + opr);
            
      }
   }

   private static int precedence(char opr){
      switch (opr) {
         case '+':
         case '-':
         return 1;
         case '*':
         case '/':
         return 2;
         case '(': 
         return 0;
         default:
         return -1;
      }
   }

   private static void printVar(){
      for (Map.Entry<String, Integer > entry: variables.entrySet()){
         System.out.println(entry.getKey() + " = " + entry.getValue());
      }
   }

 }