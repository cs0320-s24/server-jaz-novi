package edu.brown.cs.student.main.query; //

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class QueryParser {

  public static Query parse(String input, String header, boolean headerFlag) {
    // Remove whitespace for simplicity
    input = input.replaceAll("\\s+", "");
    return parseExpression(input, header, headerFlag);
  }

  private static Query parseExpression(String input, String header, boolean headerFlag) {
    List<String> tokens = new ArrayList<>();
    StringBuilder token = new StringBuilder();

    // Simplistic tokenization
    for (char c : input.toCharArray()) {
      if (c == '(' || c == ')') {
        if (token.length() > 0) {
          tokens.add(token.toString());
          token = new StringBuilder();
        }
        tokens.add(Character.toString(c));
      } else if (c == ',') {
        if (token.length() > 0) {
          tokens.add(token.toString());
          token = new StringBuilder();
        }
        tokens.add(",");
      } else {
        token.append(c);
      }
    }
    if (token.length() > 0) {
      tokens.add(token.toString());
    }

    // Parse tokens into Query objects
    return buildQuery(tokens, header, headerFlag);
  }

  private static Query buildQuery(List<String> tokens, String header, boolean headerFlag) {
    Stack<Query> queryStack = new Stack<>();
    Stack<String> opStack = new Stack<>();
    boolean notFlag = false;
    for (String token : tokens) {
      switch (token) {
        case "and":
          notFlag = false;
        case "or":
          notFlag = false;
          opStack.push(token);
          break;
        case "not":
          notFlag = true;
          break;
        case "(":
          break;
        case ")":
          if (!opStack.isEmpty()) {
            String op = opStack.pop();
            List<Query> queries = new ArrayList<>();
            while (!queryStack.isEmpty()) {
              queries.add(
                  0, queryStack.pop()); // Reverse the order for correct left-to-right evaluation
            }
            Query compoundQuery =
                (op.equals("and"))
                    ? new AndQuery(queries.toArray(new Query[0]))
                    : new OrQuery(queries.toArray(new Query[0]));
            queryStack.push(compoundQuery);
          }
          break;
        case ",":
          break;
        default:
          // Assume the default case is a basic query in the form val,col
          String[] parts = token.split("_");

          Query basicQuery = null;
          if (parts.length == 2) {

            basicQuery = new BasicQuery(parts[1], parts[0], header, headerFlag, notFlag);
          }
          if (!opStack.isEmpty() && opStack.peek().equals("not")) {
            opStack.pop();
            basicQuery = new BasicQuery(parts[1], parts[0], header, headerFlag, true);
          }

          if (basicQuery != null) {
            queryStack.push(basicQuery);
          }

          break;
      }
    }

    return queryStack.isEmpty() ? null : queryStack.pop();
  }
}
