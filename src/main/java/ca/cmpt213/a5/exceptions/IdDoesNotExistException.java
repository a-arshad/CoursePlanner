package ca.cmpt213.a5.exceptions;


/**
 * Exception class that is called when a class of an invalid id is requested
 *
 * @author Ali Arshad
 */
public class IdDoesNotExistException extends Exception {
    public IdDoesNotExistException(String message) {
        super(message);
    }
}
