package exception;

public class MovieNotFoundException extends DataAccessRuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MovieNotFoundException(String title) {
        super("找不到電影: " + title, null);
    }
}
