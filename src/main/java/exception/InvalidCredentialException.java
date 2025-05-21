package exception;

public class InvalidCredentialException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InvalidCredentialException() {
        super("帳號或密碼錯誤");
    }
}
