package exception;

public class DAOException extends Exception {
    public DAOException() {}

    public DAOException(String msg) {
        super(msg);
    }

    public DAOException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }
}   