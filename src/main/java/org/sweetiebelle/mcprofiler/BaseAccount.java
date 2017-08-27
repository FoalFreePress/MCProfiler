package org.sweetiebelle.mcprofiler;

import java.util.UUID;

/**
 * Interface for all other Accounts
 *
 */
public interface BaseAccount {

    /**
     * Returns the UUID of this alt
     * 
     * @return the uuid
     */
    public UUID getUUID();

    /**
     * Returns the IP of this alt.
     * 
     * @return the ip
     */
    public String getIP();

    /**
     * Safely makes a different type of BaseAccount from an existing one.
     * 
     * @param clazz
     *            The new type
     * @param alt
     *            the existing account
     * @return a new type of account, clazz with the data of alt. Or null if an error occurred
     * @throws UnsupportedOperationException
     *             if clazz is not a superclass of {@link BaseAccount}
     */
    public static <T> T switchType(final Class<T> clazz, final BaseAccount alt) {
        final Class<?>[] interfaces = clazz.getInterfaces();
        for (final Class<?> clz : interfaces)
            if (clz == BaseAccount.class)
                try {
                    return clazz.getConstructor(UUID.class, String.class).newInstance(alt.getUUID(), alt.getIP());
                } catch (final Exception e) {
                    e.printStackTrace();
                    return null;
                }
        throw new UnsupportedOperationException("Cannot convert from " + alt.getClass().getSimpleName() + " to " + clazz.getSimpleName());
    }
}
