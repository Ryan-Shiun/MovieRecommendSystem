package exception;

public class UserNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNotFoundException(String userName) {
        super("找不到使用者 " + userName);
    }
}
