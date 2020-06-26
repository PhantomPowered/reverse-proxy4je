package com.github.derrop.proxy.connection;

// TODO: replace with game profile
@Deprecated
public class LoginResult {

    private String id;
    private String name;
    private final Property[] properties;

    public LoginResult(String id, String name, Property[] properties) {
        this.id = id;
        this.name = name;
        this.properties = properties;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Property[] getProperties() {
        return this.properties;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return "LoginResult(id=" + this.getId() + ", name=" + this.getName() + ", properties=" + java.util.Arrays.deepToString(this.getProperties()) + ")";
    }

    public static class Property {

        private String name;
        private String value;
        private final String signature;

        public Property(String name, String value, String signature) {
            this.name = name;
            this.value = value;
            this.signature = signature;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public String getSignature() {
            return this.signature;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String toString() {
            return "LoginResult.Property(name=" + this.getName() + ", value=" + this.getValue() + ", signature=" + this.getSignature() + ")";
        }
    }
}
