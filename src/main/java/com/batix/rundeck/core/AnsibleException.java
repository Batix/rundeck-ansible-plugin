package com.batix.rundeck.core;

import com.dtolabs.rundeck.core.execution.workflow.steps.FailureReason;
import com.dtolabs.rundeck.core.execution.workflow.steps.StepException;

public class AnsibleException extends StepException {

	private static final long serialVersionUID = 1L;

	public static enum AnsibleFailureReason implements FailureReason {
		  AnsibleNonZero, // Ansible process exited with non-zero value
		  AnsibleError, // Ansible not found etc.
		  StorageTierAccessError,
		  Interrupted,
		  IOFailure,
		  ParseArgumentsError,
		  Unknown
		}

    protected AnsibleFailureReason failureReason;

    public AnsibleException(String msg, AnsibleFailureReason reason) {
        super(msg,reason);
        this.failureReason = reason;
    }

    public AnsibleException(Throwable cause, AnsibleFailureReason reason) {
        super(cause,reason);
        this.failureReason = reason;
    }

    public AnsibleException(String msg, Throwable cause, AnsibleFailureReason reason) {
        super(msg, cause,reason);
        this.failureReason = reason;
    }

    public AnsibleFailureReason getFailureReason() {
        return failureReason;
    }
}

