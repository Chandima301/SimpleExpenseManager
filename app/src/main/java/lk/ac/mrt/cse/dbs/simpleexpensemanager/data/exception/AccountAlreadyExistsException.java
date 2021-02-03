package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception;

public class AccountAlreadyExistsException extends Exception{
    public AccountAlreadyExistsException(String detailMessage) {
        super(detailMessage);
    }

    public AccountAlreadyExistsException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
