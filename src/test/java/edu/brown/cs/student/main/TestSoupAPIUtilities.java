// package edu.brown.cs.student.main;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
//
// import edu.brown.cs.student.main.soup.Soup;
// import edu.brown.cs.student.main.soup.SoupAPIUtilities;
// import java.io.IOException;
// import java.util.Arrays;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
//
/// **
// * Test deserializing soup recipes
// *
// * <p>Because we're using JUnit here, we needed to add JUnit to pom.xml.
// *
// * <p>In a real application, we'd want to test better---e.g., if it's part of our spec that
// * SoupHandler throws an IOException on invalid JSON, we'd want to test that.
// */
// public class TestSoupAPIUtilities {
//
//  @BeforeEach
//  public void setup() {
//    // No setup
//  }
//
//  @AfterEach
//  public void teardown() {
//    // No setup
//  }
//
//  @Test
//  public void testFrom_ValidIngredientsList() throws IOException {
//    String chickenNoodle =
//        "{\n"
//            + "    \"soupName\": \"chicken noodle\",\n"
//            + "    \"ingredients\": [\"chicken broth\", \"celery\", \"carrot\", \"onion\"],\n"
//            + "    \"isHot\": true\n"
//            + "  }";
//    Soup soup = SoupAPIUtilities.deserializeSoup(chickenNoodle);
//    // This might throw an IOException, but if so JUnit will mark the test as failed.
//    assertEquals(4, soup.getIngredients().size());
//  }
//
//  @Test
//  public void testTo_ValidSoup() throws IOException {
//    Soup potato = new Soup("potato", Arrays.asList("potato", "water"), true);
//    String json = SoupAPIUtilities.serializeSoup(potato);
//    // System.out.println(json);
//    // Don't try to parse the string yourself to test it.
//    // Instead, use a Json library to look at the info provided.
//    Soup result = SoupAPIUtilities.deserializeSoup(json);
//    // This will FAIL if we don't define equals in Soup
//    assertEquals(potato, result);
//    // If the above produces an exception, the JUnit test will fail.
//  }
// }
