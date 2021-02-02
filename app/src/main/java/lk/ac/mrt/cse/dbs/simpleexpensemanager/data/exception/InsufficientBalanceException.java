package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception;

public class InsufficientBalanceException extends Exception{
    public InsufficientBalanceException(String detailMessage) {
        super(detailMessage);
    }

    public InsufficientBalanceException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
