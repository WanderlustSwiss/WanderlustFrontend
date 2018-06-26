package eu.wise_iot.wanderlust.controllers;

/**
 * EventType which represents the Request Message
 * @author Tobias Rüegsegger, Alexander Weinbeck
 * @license GPL-3.0
 */
public enum EventType {

    OK(200), NOT_FOUND(404), CONFLICT(409), BAD_REQUEST(400), SERVER_ERROR(500), NETWORK_ERROR(0),

    PROGRESS_UPDATE(950), PROGRESS_NOTIFICATION(951),
    DOWNLOAD_FAILED(800), DOWNLOAD_ALREADY_DONE(801), DOWNLOAD_NO_SPACE(802), DOWNLOAD_OK(900);


    final int code;

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
            case 800:
                return DOWNLOAD_FAILED;
            case 801:
                return DOWNLOAD_ALREADY_DONE;
            case 802:
                return DOWNLOAD_NO_SPACE;
            case 900:
                return DOWNLOAD_OK;
            default:
                return NETWORK_ERROR;
        }
    }
}
