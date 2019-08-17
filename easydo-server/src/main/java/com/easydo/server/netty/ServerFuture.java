package com.easydo.server.netty;

import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ServerFuture {

    private ChannelFuture channelFuture;

}
