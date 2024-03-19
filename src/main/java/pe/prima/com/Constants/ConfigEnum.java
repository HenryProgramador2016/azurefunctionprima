package pe.prima.com.Constants;

import lombok.Getter;

@Getter
public enum ConfigEnum {
    DANA_URL_FUNCTION("DANA_URL_FUNCTION"),DANA_CONVERSATION_ID("DANA_CONVERSATION_ID");

    private final String string;
    ConfigEnum(String string) {
        this.string = string;
    }
}
