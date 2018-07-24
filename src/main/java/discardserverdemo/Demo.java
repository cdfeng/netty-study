package discardserverdemo;

import io.netty.channel.epoll.Epoll;

public class Demo {
    public static void main(String[] args) {
        Epoll.ensureAvailability();
    }
}
