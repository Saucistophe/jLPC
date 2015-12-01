package org.saucistophe.lpc.exceptions;

/**
*/
public class WordNotFoundException extends Exception
{
    public WordNotFoundException(String message)
    {
        super(message);
    }

    public WordNotFoundException(Throwable throwable)
    {
        super(throwable);
    }

    public WordNotFoundException(String message, Throwable throwable)
    {
        super(message, throwable);
    }

    public WordNotFoundException()
    {
        super();
    }
}
