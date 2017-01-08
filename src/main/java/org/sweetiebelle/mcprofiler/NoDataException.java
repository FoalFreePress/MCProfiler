package org.sweetiebelle.mcprofiler;

/**
 * Throw to indicate either no {@link Account} was found, or if something is null when it shouldn't be.
 *
 */
public class NoDataException extends Exception {
    private static final long serialVersionUID = 3422799336114476332L;

    /**
     * Creates an Exception with "No Account found." as the reason.
     */
    public NoDataException() {
        super("No Account found.");
    }

    /**
     * Creates an Exception with the specified reason
     * @param string the reason
     */
    public NoDataException(final String string) {
        super(string);
    }
}
