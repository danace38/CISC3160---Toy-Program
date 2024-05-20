/*
 *  CISC 3160 -- Programming Languages
 *  Danil Ermolin
 *  Brooklyn College, CUNY
 *  Spring 2024
 *  Prof. Neng-Fa Zhou
 *  Toy Project
 */


import java.util.*; //importing utility classes for the program, specifically Map and Set
import java.util.regex.*; //importing utility for regular expression


public class Toy{

   private static Map<String, Integer> variables = new HashMap<>(); //holding variable names and integer values
   private static Set<String> initializedString = new HashSet<>(); //Storing and keeping track of initialized string variables 
   private static final Pattern identifierPattern = Pattern.compile("[a-zA-Z_][a-zA-Z_0-9]*"); //Matching valid variable names 

    public static void main(String[] args) {

      String [] input = { //Declaring string array, that includes expressions to be interpreted
         "x = 001;", 
         "x_2 = 0;",
         "x = 0; y = x; z = --- (x+y);",
         "x = 1; y = 2; z = --- (x+y) * (x + - y);",
      };

      for (int i = 0; i < input.length; i++){
         System.out.println("Input " + (i+1));
        
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

         if (!identifierPattern.matcher(identifier).matches())

      }
   }


 }