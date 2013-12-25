
package com.easy.nettyPush.scheduler;


public class SchedulerKey {

    public enum Type {HEARBEAT_TIMEOUT,POLLING_DURATION, CLOSE_TIMEOUT};

    private final Type type;
    private final String sessionId;

    public SchedulerKey(Type type, String sessionId) {
        this.type = type;
        this.sessionId = sessionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((sessionId == null) ? 0 : sessionId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SchedulerKey other = (SchedulerKey) obj;
        if (sessionId == null) {
            if (other.sessionId != null)
                return false;
        } else if (!sessionId.equals(other.sessionId))
            return false;
        if (type != other.type)
            return false;
        return true;
    }

}
