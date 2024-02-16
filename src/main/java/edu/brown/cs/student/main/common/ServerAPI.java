package edu.brown.cs.student.main.common;

import com.squareup.moshi.Moshi;

public class ServerAPI {

  public record ServerErrorResponse(String response_type, String message) {
    public ServerErrorResponse(String response_type, String message) {
      this.response_type = response_type;
      this.message = message;
    }

    String serialize() {
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(ServerErrorResponse.class).toJson(this);
    }
  }

  public static String GetServerErrorResponse(String response_type, String message) {
    return new ServerErrorResponse(response_type, message).serialize();
  }
}
