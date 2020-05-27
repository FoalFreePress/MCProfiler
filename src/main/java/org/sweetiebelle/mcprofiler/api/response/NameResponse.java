package org.sweetiebelle.mcprofiler.api.response;

/**
 * The response class used to handle the data gotten.
 *
 */
public class NameResponse {

    /**
     * The time they changed to at. This is 0 if it is an original name.
     */
    public long changedToAt;
    /**
     * The name
     */
    public String name;

    public NameResponse(String name, long changedToAt) {
        this.name = name;
        this.changedToAt = changedToAt;
    }
}