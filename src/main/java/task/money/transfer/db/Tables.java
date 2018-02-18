package task.money.transfer.db;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public enum Tables {

    ACCOUNTS("accounts"),
    CURRENCIES("currencies"),
    TRANSACTIONS("transactions");

    private final String name;

    Tables(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
