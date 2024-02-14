package edu.brown.cs.student.main;

import edu.brown.cs.student.main.common.utility;
import edu.brown.cs.student.main.creators.StringCreatorFromRow;
import edu.brown.cs.student.main.csv.CSVSearcher;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Scanner;

/** The Main class of our project. This is where execution begins. */
public final class Main {

  private String csvName;
  private String targetValue;
  private String colIdentifier;
  private boolean headerFlag;
  private boolean multiQueryFlag;
  private String queries;
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private Main(String[] args) {
    if (args.length >= 2) {
      this.csvName = args[0];
      // check if the file is in the appropriate directory
      if (!utility.isValidPath(this.csvName)) {
        System.err.println(
            "The requested csv file can not be accessed, please provide file in data directory");
        System.exit(0);
      }
      this.targetValue = args[1];
      if (args.length > 2) {
        // get the remaining args as column identifier
        this.colIdentifier = utility.arrayToString(Arrays.copyOfRange(args, 2, args.length));
      } else {
        this.colIdentifier = null;
      }
      // ask for user if the csv file has headers
      Scanner scanner = new Scanner(System.in);
      headerFlag = utility.checkYesNo("Does this csv file has headers? (yes/no): ", scanner);
      multiQueryFlag =
          utility.checkYesNo(
              "Please indicate if you would like to search for multiple values (yes/no): ",
              scanner);
      if (multiQueryFlag) {
        System.out.println(
            "Please add the entire queries, must include the query you just provided, use '_' to separate the value and column.");
        System.out.println("Begin with oerators such as 'and' or 'or' to combine the queries.");
        System.out.println("For example: and(or(val1_col1),...)");
        queries = scanner.nextLine();
      }
      scanner.close();

    } else {
      System.err.println("Please provide the csv file name, target value and column identifier");
      System.exit(0);
    }
  }

  private void run() {
    try {
      // create a searcher to search the csv file
      CSVSearcher searcher =
          new CSVSearcher(new FileReader(csvName), new StringCreatorFromRow() {}, headerFlag);
      if (multiQueryFlag) {
        searcher.searchMulti(queries);
      } else {
        searcher.search(targetValue, colIdentifier);
      }

    } catch (FileNotFoundException e) {
      System.err.println("Can't find the file, please provide a valid file name");
      System.exit(0);
    }
  }
}
