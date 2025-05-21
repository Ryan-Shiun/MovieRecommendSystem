package exception;

public class ReviewNotFoundException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 使用預設訊息，會顯示「找不到指定的影評」。
     */
    public ReviewNotFoundException() {
        super("查無評論！");
    }
}