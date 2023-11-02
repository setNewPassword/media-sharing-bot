package com.btard.service.enums;

public enum ServiceCommand {

    HELP("/help"),
    REGISTRATION("/registration"),
    CANCEL("/cancel"),
    START("/start");

    private final String value;

    ServiceCommand(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    public static ServiceCommand fromValue(String inputValue) {
        for (ServiceCommand command: ServiceCommand.values()) {
            if (command.value.equals(inputValue)) {
                return command;
            }
        }
        return null;
    }

}