package exceptions;

public class SlotNotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public SlotNotFoundException(String message) {
        super(message);
    }
}
