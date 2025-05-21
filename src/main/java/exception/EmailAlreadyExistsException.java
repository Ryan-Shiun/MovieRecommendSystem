package exception;

public class EmailAlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException() {
        super("此電子郵件已被使用，請換一個唷！");
    }
}
