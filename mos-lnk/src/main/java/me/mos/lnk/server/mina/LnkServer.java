package me.mos.lnk.server.mina;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.buffer.SimpleBufferAllocator;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import me.mos.lnk.channel.ChannelActiveMonitor;
import me.mos.lnk.etc.Profile;
import me.mos.lnk.executor.LnkExecutor;
import me.mos.lnk.parser.JsonPacketParser;
import me.mos.lnk.parser.PacketParser;
import me.mos.lnk.processor.BoundServerProcessor;
import me.mos.lnk.processor.ServerProcessor;
import me.mos.lnk.server.AbstractServer;
import me.mos.lnk.server.mina.codec.PacketProtocolCodecFilter;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年6月10日 下午4:15:22
 */
public class LnkServer extends AbstractServer {

	private int port = DEFAULT_PORT;
	
	/**
	 * <pre>
	 * -1 | 0 | nSec 
	 * -1表示使用OS缺省参数，0表示立即释放，nSec表示等待n秒后释放
	 * </pre>
	 */
	private int soLinger = DEFAULT_OS_SOLINGER;

	/**
	 * 输入连接指示（对连接的请求）的最大队列长度被设置为 backlog参数。如果队列满时收到连接指示，则拒绝该连接。
	 */
	private int backlog = DEFAULT_BACKLOG;
	
	/**
	 * 单位秒
	 */
	private int idleTime = DEFAULT_IDLETIME;
	
	private String charset = DEFAULT_CHARSET;
	
	private NioSocketAcceptor acceptor;

	private Profile profile;
	
	private ServerProcessor processor;
	
	private PacketParser parser;
	
	public LnkServer() {
		super();
		try {
			profile = Profile.newInstance();
			setPort(profile.getPort());
			setSoLinger(profile.getSoLinger());
			setBacklog(profile.getBacklog());
			setIdleTime(profile.getIdleTime());
			setCharset(profile.getCharset());
			setProcessor(new BoundServerProcessor());
			setParser(new JsonPacketParser());
			IoBuffer.setUseDirectBuffer(false);
			IoBuffer.setAllocator(new SimpleBufferAllocator());
			log.error("Config LnkServer Success.");
		} catch (Exception e) {
			log.error("Create Server Profile from XML Error.", e);
		}
	}

	@Override
	protected void doStart() {
		acceptor = new NioSocketAcceptor(Runtime.getRuntime().availableProcessors() * 2);
		acceptor.getFilterChain().addLast("exceutor", new ExecutorFilter(new LnkExecutor(profile)));
		acceptor.getFilterChain().addLast("mdc", new MdcInjectionFilter());
		acceptor.getFilterChain().addLast("codec", new PacketProtocolCodecFilter(Charset.forName(charset), parser));
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.setReuseAddress(true);
		acceptor.setBacklog(backlog);
		acceptor.getSessionConfig().setReuseAddress(true);
		acceptor.getSessionConfig().setReadBufferSize(10240);
		acceptor.getSessionConfig().setReceiveBufferSize(10240);
		acceptor.getSessionConfig().setSendBufferSize(10240);
		acceptor.getSessionConfig().setTcpNoDelay(true);
		acceptor.getSessionConfig().setSoLinger(soLinger);
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, idleTime);
		try {
			acceptor.setHandler(new ServerIoHandler(processor));
			acceptor.bind(new InetSocketAddress(port));
			new Thread(new ChannelActiveMonitor()).start();
			log.error("LnkServer[Mina] started success on port {}.", port);
		} catch (Throwable e) {
			log.error("LnkServer Starting Error.", e);
			throw new IllegalStateException(e);
		}
	}

	@Override
	protected void doStop() {
		acceptor.dispose();
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public void setSoLinger(int soLinger) {
		this.soLinger = soLinger;
	}
	
	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}
	
	public void setIdleTime(int idleTime) {
		this.idleTime = idleTime;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}
	
	public void setProcessor(ServerProcessor processor) {
		this.processor = processor;
	}
	
	public void setParser(PacketParser parser) {
		this.parser = parser;
	}
}