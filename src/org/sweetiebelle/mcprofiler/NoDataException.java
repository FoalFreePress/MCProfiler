package org.sweetiebelle.mcprofiler;

/**
 * Throw to indicate either no {@link Account} was found, or if something is null when it shouldn't be.
 *
 */
class NoDataException extends Exception {
    private static final long serialVersionUID = 3422799336114476332L;

    /**
     * Creates an Exception with "No Account found." as the reason.
     */
    NoDataException() {
        super("No Account found.");
    }

    /**
     * Creates an Exception with the specified reason
     * @param string the reason
     */
    NoDataException(final String string) {
        super(string);
    }
}
