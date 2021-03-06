package me.mos.lnk.server.sock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.mos.lnk.channel.Channels;
import me.mos.lnk.packet.InPacket;
import me.mos.lnk.packet.OutPacket;
import me.mos.lnk.parser.PacketParser;
import me.mos.lnk.processor.ServerProcessor;
import me.mos.lnk.server.Handler;

/**
 * Lnk服务通道事件处理句柄.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 * 
 * @version 1.0.0
 * @since 2015年6月2日 上午12:08:50
 */
final class ServerIoHandler implements Runnable, Handler {

	private static final Logger log = LoggerFactory.getLogger(ServerIoHandler.class);

	private final BoundChannel channel;

	private final ServerProcessor processor;

	private final PacketParser parser;

	ServerIoHandler(BoundChannel channel, ServerProcessor processor, PacketParser parser) {
		super();
		this.channel = channel;
		this.processor = processor;
		this.parser = parser;
	}

	@Override
	public void run() {
		String packet = StringUtils.EMPTY;
		while (true) {
			try {
				if (!channel.isConnect()) {
					channel.close();
					Channels.offline(channel);
					break;
				}
				packet = channel.received();
				if (StringUtils.isBlank(packet)) {
					continue;
				}
				log.error("Original Incoming Packet : {}", packet);
				InPacket inPacket = parser.parse(packet);
				if (inPacket == null) {
					continue;
				}
				channel.setChannelId(inPacket.getMid());
				OutPacket outPacket = processor.process(channel, inPacket);
				if (outPacket == null) {
					continue;
				}
				channel.deliver(outPacket);
			} catch (Throwable e) {
			}
		}
	}
}