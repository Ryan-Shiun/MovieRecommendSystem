package exception;

public class DataAccessRuntimeException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataAccessRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
