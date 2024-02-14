package edu.brown.cs.student.main.creators;

public class Star {

  private int starID;
  private String properName;
  private double x, y, z;

  public Star(int starID, String properName, double x, double y, double z) {
    this.starID = starID;
    this.properName = properName;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public int getStarID() {
    return starID;
  }

  public String getProperName() {
    return properName;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }

  public double getZ() {
    return z;
  }
}
