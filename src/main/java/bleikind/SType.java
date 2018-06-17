package bleikind;

public enum SType {
    MINECRAFT_NET, SESSION_MINECRAFT_NET, ACCOUNT_MOJANG_NET, AUTH_MOJANG_COM, SKINS_MINECRAFT_NET, AUTHSERVER_MOJANG_COM, SESSIONSERVER_MOJANG_COM, API_MOJANG_COM, TEXTURES_MINECRAFT_NET, MOJANG_COM;

    public String toString() {
        return name().toLowerCase().replace("_", ".");
    }

}
