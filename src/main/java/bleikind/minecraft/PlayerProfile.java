package bleikind.minecraft;

import java.util.Set;

public class PlayerProfile {

    private String uuid;
    private String username;
    private Set<Property> properties;

    /**
     * Represents a property.
     */
    public static class Property {
        public String name;
        public String value;
        public String signature;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public String getSignature() {
            return signature;
        }
    }

    public PlayerProfile(String uuid, String username, Set<Property> properties) {
        this.uuid = uuid;
        this.username = username;
        this.properties = properties;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public Set<Property> getProperties() {
        return properties;
    }

}
