package org.yeauty.support;

import io.netty.handler.traffic.TrafficCounter;

public final class ServerMetrics {

    private long readThroughput;
    private long writeThroughput;
    private long lastReads;
    private long lastWrites;

    public ServerMetrics() {}

    public void setReadThroughput(long readThroughput) {
        this.readThroughput = readThroughput;
    }

    public void setWriteThroughput(long writeThroughput) {
        this.writeThroughput = writeThroughput;
    }

    public void setLastReads(long lastReads) {
        this.lastReads = lastReads;
    }

    public void setLastWrites(long lastWrites) {
        this.lastWrites = lastWrites;
    }

    public long getReadThroughput() {
        return readThroughput;
    }

    public long getWriteThroughput() {
        return writeThroughput;
    }

    public long getLastReads() {
        return lastReads;
    }

    public long getLastWrites() {
        return lastWrites;
    }

}