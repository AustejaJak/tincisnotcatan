package edu.brown.cs.actions;

import com.google.errorprone.annotations.Keep;

/**
 * General form for reponse from Actions.
 *
 * @author anselvahle
 *
 */
public class ActionResponse {

  private boolean success;
  private String message;

  /**
   * Constructor for the class.
   * 
   * @param success
   *          Boolean stating whether or not the action succeeded.
   * @param message
   *          Message for the player about the action.
   * @param data
   *          Information specific to the action.
   */
  public ActionResponse(boolean success, String message, Object data) {
    super();
    this.success = success;
    this.message = message;
  }

  /**
   * Getter for success.
   * 
   * @return success.
   */
  public boolean getSuccess() {
    return success;
  }

  public String getMessage() {
    return message;
  }
}
