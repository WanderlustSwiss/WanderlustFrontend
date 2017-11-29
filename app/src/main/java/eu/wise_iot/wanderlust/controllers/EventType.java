package eu.wise_iot.wanderlust.controllers;

/*
 * EventType which represents the Request Message
 * @author Tobias Rüegsegger
 * @license MIT
 */
    public enum EventType {

    OK(200), NOT_FOUND(404), CONFLICT(409), BAD_REQUEST(400), SERVER_ERROR(500), NETWORK_ERROR(0);

    private int code;

    /**
     * Create EventType
     *
     * @param code
     */
    EventType(int code) {
        this.code = code;
    }

    /**
     * Get the enum from a specific code
     *
     * @param code
     */
    public static EventType getTypeByCode(int code) {
        switch (code) {
            case 200:
                return OK;
            case 404:
                return NOT_FOUND;
            case 409:
                return CONFLICT;
            case 400:
                return BAD_REQUEST;
            case 500:
                return SERVER_ERROR;
            default:
                return NETWORK_ERROR;
        }
    }
}
