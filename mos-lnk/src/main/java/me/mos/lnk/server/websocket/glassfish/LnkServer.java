package me.mos.lnk.server.websocket.glassfish;

import java.util.concurrent.CountDownLatch;

import me.mos.lnk.etc.Profile;
import me.mos.lnk.server.AbstractServer;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 * @version 1.0
 * @since 2015年7月19日 上午8:48:00
 */
public class LnkServer extends AbstractServer {

	private int port = DEFAULT_PORT;
	
	private org.glassfish.tyrus.server.Server server;
	
	private Profile profile;
	
	private CountDownLatch latch;
	
	public LnkServer() {
		super();
		try {
			profile = Profile.newInstance();
			setPort(profile.getPort());
			log.error("Config LnkServer Success.");
		} catch (Exception e) {
			log.error("Create Server Profile from XML Error.", e);
		}
	}

	@Override
	protected void doStart() {
		try {
			latch = new CountDownLatch(1);
			server = new org.glassfish.tyrus.server.Server("localhost", port, "/lnk", ServerIoHandler.class);
			server.start();
			log.error("LnkServer[WS] started success on port {}.", port);
			latch.await();
		} catch (Throwable e) {
			log.error("LnkServer Starting Error.", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void doStop() {
		server.stop();
	}
	
	public void setPort(int port) {
		this.port = port;
	}
}