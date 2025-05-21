package exception;

public class ReviewAlreadyExistsException extends RuntimeException {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReviewAlreadyExistsException() {
        super("此電影您已評論過囉！");
    }
}