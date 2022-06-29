package wang.qrwells.net.server.handler.impl;


import wang.qrwells.net.server.handler.ChannelHandler;
import wang.qrwells.net.server.pipeline.Interface.MessageHandlerContext;
import wang.qrwells.net.server.pipeline.Pipeline;

public abstract class ChannelInitHandler implements ChannelHandler {
    public abstract void init(Pipeline pipeline);

    @Override
    public void onAdded(MessageHandlerContext mhc) {
        init(mhc.pipeline());
        mhc.pipeline().removeHandler(this);
    }
}
