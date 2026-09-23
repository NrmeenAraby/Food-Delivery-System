package Exceptions;

public class StockShortageException extends PlatformException{
    public StockShortageException(String message) {
        super(message);
    }
}
