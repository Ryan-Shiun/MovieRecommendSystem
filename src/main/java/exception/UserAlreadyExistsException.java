package exception;

public class UserAlreadyExistsException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserAlreadyExistsException(String userName) {
        super("用戶已存在：" + userName);
    }
}