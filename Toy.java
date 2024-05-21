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
   private static final Pattern identifyVariable = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*"); //Matching valid variable names 

    public static void main(String[] args) {

      String [] input = { //Declaring string array, that includes expressions to be interpreted
         "x = 001;", 
         "x_2 = 0;",
         "x = 0; y = x; z = --- (x+y);",
         "x = 1; y = 2; z = --- (x+y) * (x + - y);",
      };

      for (int i = 0; i < input.length; i++){ //iterating through the input
         System.out.println("\nInput " + (i+1) + ": ");
         System.out.println();
         try{
            parse(input[i]); //calling parse method with current expression as parameter
            printVar();
         }catch (Exception exc){
            System.out.println("Error");
         }
      }
        
        
    }

    static class syntaxError extends Exception{ //throwing exception if there is a missing semicolon or invalid operator
      public syntaxError(String msg){
         super(msg);
      }
   }

   static class uninitializedVar extends Exception{ //throwing exception if there is uninitialized variable in expression
      public uninitializedVar(String msg){
         super(msg);
      }
   }

   
   private static void parse(String input) throws syntaxError, uninitializedVar{ // parse method to process each expression within input strings
      variables.clear();
      initializedString.clear();

      String[] assignments = input.split(";"); //splitting input string into individual assignments
      for (String assignment : assignments){ //iterating through each assignment
         if (assignment.trim().isEmpty()) continue;
            String[] parts = assignment.split("=");
            if (parts.length != 2) throw new syntaxError("Invalid syntax");

         String identifier = parts[0].trim();
         String expression = parts[1].trim();

         if (!identifyVariable.matcher(identifier).matches()) throw new syntaxError("Invalid identifier: " + identifier); //validate indentifier or throw error if it is invalid
         int value = evalExp(expression);
         variables.put(identifier, value);
         initializedString.add(identifier);
      }
   }

   private static int evalExp(String expression) throws syntaxError, uninitializedVar{ //evaluate expression method as a string argument
      expression = expression.replaceAll("\\s+", ""); //removing all whitespaces
      if (expression.isEmpty()) throw new syntaxError("Empty"); //if expression is empty, throw error

      Stack<Integer> values = new Stack<>(); //storing operand
      Stack<Character> oper = new Stack<>(); // storing operators

      for(int i = 0; i < expression.length(); i++){ //iterating through each expression
         char c = expression.charAt(i); // charcter

        if(Character.isDigit(c)){ //if character is digit, build a number by iterating through consecutive digits
         int num = 0;
         while (i < expression.length() && Character.isDigit(expression.charAt(i))){
            num = num * 10 + (expression.charAt(i) - '0');
            i++;
         }
         i--;
         values.push(num);
        }else if (Character.isLetter(c) || c == '_'){ //if charater is letter or underscore, build a variable name string and check if variable is initialized
         StringBuilder sb = new StringBuilder();
         while (i < expression.length() && (Character.isLetterOrDigit(expression.charAt(i)) || expression.charAt(i) == '_')) {
             sb.append(expression.charAt(i));
             i++;
         }
         i--;
         String identifier = sb.toString();
         if (!initializedString.contains(identifier)) throw new uninitializedVar("Uninitialized variable: " + identifier);
         values.push(variables.get(identifier));
        } else if (c == '(') { //if character is '(', push into oper stack 
         oper.push(c);
        }else if (c == ')'){
         while (oper.peek() != '('){
            values.push(operator(oper.pop(), values.pop(), values.pop()));
         }
         oper.pop();
        }else if (c == '+' || c == '-' || c == '*' || c == '/'){ //if character is a arithmetic operator, push into oper stack
         while (!oper.isEmpty() && precedence(c) <= precedence(oper.peek())){
            values.push(operator(oper.pop(), values.pop(), values.pop()));
         }
         oper.pop();
      }else if(c == '-' && (i == 0 || expression.charAt(i - 1) == '(')){ //if charater is '-' or after an opening parenthesis, push 0 into the values stack
         values.push(0);
         oper.push('-');
      }else {
         throw new syntaxError("Invalid: charter in expression: " + c);
      }
   }

      while (!oper.isEmpty()){
         values.push(operator(oper.pop(), values.pop(), values.pop())); //pushing result into values stack
      }

      return values.pop(); //pop remaining values from the value stack, which represents final result of expression evaluation 
   

   }

   private static int operator (char opr, int a, int b) throws syntaxError{ //operator method to perform arithemit operation
      switch (opr) {
         case '+': 
            return (a + b);
         case '-':
           return (a - b);
         case '*':
           return (a * b);
         case '/':
         if (b == 0) throw new syntaxError("Invalid: division by 0");
            return a/b;
         default:
             throw new syntaxError("Invalid: unknow operator " + opr);
            
      }
   }

   private static int precedence(char opr){ //precedence method to determine precedence of a given operator
      switch (opr) {
         case '+':
         case '-':
             return 1; //low precedence 
         case '*':
         case '/':
             return 2; //high precence
         case '(': 
             return 0; //lowest precedence 
         default:
            return -1; //if operator is not recognized, return -1
      }
   }

   private static void printVar(){ //print variable method to print final values encountered during parsing
      for (Map.Entry<String, Integer > entry: variables.entrySet()){
         System.out.println(entry.getKey() + " = " + entry.getValue());
      }
   }

 }