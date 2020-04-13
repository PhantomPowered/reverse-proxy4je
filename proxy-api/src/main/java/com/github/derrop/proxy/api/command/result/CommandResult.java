package com.github.derrop.proxy.api.command.result;

public enum CommandResult {

    /**
     * The command was executed successfully
     */
    SUCCESS,

    /**
     * The command returned in code because of an error of the user
     */
    BREAK,

    /**
     * The command ended with no result
     */
    END,

    /**
     * The command execution has failed
     */
    FAILURE,

    /**
     * The command execution has failed because the given command is unknown (will print help message)
     */
    NOT_FOUND
}
