package org.yeauty.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicLong;

public final class ThroughputCounter extends GlobalTrafficShapingHandler {
    private static final Log logger = LogFactory.getLog(ThroughputCounter.class);

    private final ServerMetrics metrics;

    private final AtomicLong lastChecked = new AtomicLong();
    private final AtomicLong currentReads = new AtomicLong();
    private final AtomicLong currentWrites = new AtomicLong();

    private long lastReads;
    private long lastWrites;

    public ServerMetrics getMetrics() {
        return metrics;
    }
    public ThroughputCounter(ScheduledExecutorService executor, long checkInterval,
                             ServerMetrics metrics) {
        super(executor, checkInterval);
        this.metrics = metrics;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        currentReads.incrementAndGet();
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise)
            throws Exception {
        super.write(ctx, msg, promise);
        currentWrites.incrementAndGet();
    }

    @Override
    protected void doAccounting(TrafficCounter counter) {
        long currentTime = System.currentTimeMillis();
        long interval = currentTime - lastChecked.getAndSet(currentTime);
        if (interval == 0) {
            return;
        }
        this.lastReads = currentReads.getAndSet(0L);
        this.lastWrites = currentWrites.getAndSet(0L);

        long readsPerSec = (lastReads / interval) * 1000;
        long writesPerSec = (lastWrites / interval) * 1000;
        metrics.setLastReads(readsPerSec);
        metrics.setLastWrites(writesPerSec);

        TrafficCounter traffic = trafficCounter();
        long readThroughput = traffic.lastReadThroughput();
        long writeThroughput = traffic.lastWriteThroughput();
        metrics.setReadThroughput(readThroughput);
        metrics.setWriteThroughput(writeThroughput);
    }

    @Override
    public String toString() {
        TrafficCounter traffic = trafficCounter();
        final StringBuilder buf = new StringBuilder(512);
        long readThroughput = traffic.lastReadThroughput();
        buf.append("Read:  ").append(readThroughput / 1024L).append(" KB/sec, ");
        buf.append(lastReads).append(" msg/sec ");
        long writeThroughput = traffic.lastWriteThroughput();
        buf.append("Write: ").append(writeThroughput / 1024).append(" KB/sec, ");
        buf.append(lastWrites).append(" msg/sec");
        return buf.toString();
    }

}