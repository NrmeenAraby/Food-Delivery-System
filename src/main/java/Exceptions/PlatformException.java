package Exceptions;

public class PlatformException extends RuntimeException{
    protected String message;
    public PlatformException(String message){
        this.message=message;
    }
}
