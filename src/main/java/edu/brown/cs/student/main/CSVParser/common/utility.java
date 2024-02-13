package edu.brown.cs.student.main.common;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.Pattern;

public class utility {
  private static final String ACCESS_DIRECTORY = "./data";

  public static boolean isValidPath(String requestedPath) {
    try {
      Path basePath = Paths.get(ACCESS_DIRECTORY).toAbsolutePath().normalize();
      Path resolvedPath = basePath.resolve(requestedPath).normalize();
      return resolvedPath.startsWith(basePath);
    } catch (InvalidPathException e) {
      return false;
    }
  }

  public static final Pattern regexSplitCSVRow =
      Pattern.compile(",(?=([^\"]*\"[^\"]*\")*(?![^\"]*\"))");

  public static String postprocess(String arg) {
    return arg.trim().replaceAll("^\"", "").replaceAll("\"$", "").replaceAll("\"\"", "\"");
  }

  public static boolean isNumeric(String targetCol) {
    try {
      Integer.parseInt(targetCol);
      // or Double.parseDouble(targetCol) for floating-point numbers
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public static String unifyString(String str) {
    return str.toLowerCase();
  }

  public static String arrayToString(String[] args) {
    StringBuilder sb = new StringBuilder();
    for (String arg : args) {
      sb.append(arg).append(" ");
    }
    return sb.toString().trim();
  }

  public static int findColIndex(String targetCol, boolean headerFlag, String headers) {
    int colIndex = 0;
    if (utility.isNumeric(targetCol)) {
      colIndex = Integer.parseInt(targetCol);
      if (colIndex >= headers.split(",").length) {
        System.err.println("The requested column does not exist in the csv file");
        return -1;
      }
    } else if (headerFlag) {
      colIndex = -1;
      String searchString = headers.toLowerCase();
      String[] splitArray = searchString.split(",");
      String targetString = targetCol.toLowerCase();
      for (int i = 0; i < splitArray.length; i++) {
        if (splitArray[i].trim().equals(targetString)) {
          colIndex = i;
          break;
        }
      }
      if (colIndex == -1) {
        System.err.println("The requested column does not exist in the csv file");
        return -1;
      }
    }
    return colIndex;
  }

  public static boolean checkYesNo(String question, Scanner scanner) {
    boolean resultFlag = false;
    //    Scanner scanner = new Scanner(System.in);
    while (true) {
      System.out.print(question);
      String currentInput = scanner.nextLine();
      if (currentInput.equalsIgnoreCase("yes") || currentInput.equalsIgnoreCase("no")) {
        resultFlag = currentInput.equalsIgnoreCase("yes");
        break;
      }
      System.out.println("Please provide a valid input (yes/no)");
    }
    //    scanner.close();
    return resultFlag;
  }
}
