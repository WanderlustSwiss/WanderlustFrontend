package eu.wise_iot.wanderlust.controllers;

public interface QueueCommand {
    void execute(FragmentHandler handler);
    void executeAfterSuccess(Object obj);
    boolean isExecutable();
}
