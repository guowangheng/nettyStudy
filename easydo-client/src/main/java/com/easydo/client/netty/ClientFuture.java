package com.easydo.client.netty;

import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ClientFuture {

    private ChannelFuture channelFuture;

}
