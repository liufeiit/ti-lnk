package me.mos.lnk.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.mos.lnk.groups.BoundGroupProvider;
import me.mos.lnk.groups.GroupProvider;
import me.mos.lnk.groups.join.BoundJoinGroupProvider;
import me.mos.lnk.groups.join.JoinGroupProvider;
import me.mos.lnk.groups.message.BoundGroupMessageProvider;
import me.mos.lnk.groups.message.GroupMessageProvider;
import me.mos.lnk.message.BoundMessageProvider;
import me.mos.lnk.message.MessageProvider;
import me.mos.lnk.packet.InPacket;
import me.mos.lnk.user.BoundUserProvider;
import me.mos.lnk.user.UserProvider;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 * 
 * @version 1.0.0
 * @since 2015年6月3日 下午4:31:36
 */
public abstract class AbstractPacketHandler<I extends InPacket> implements PacketHandler<I> {

	protected Logger log = LoggerFactory.getLogger(getClass());
	
	protected MessageProvider messageProvider;
	
	protected UserProvider userProvider;
    
    protected GroupProvider groupProvider;
    
    protected GroupMessageProvider groupMessageProvider;
    
    protected JoinGroupProvider joinGroupProvider;

	public AbstractPacketHandler() {
		super();
		messageProvider = BoundMessageProvider.getInstance();
		userProvider = BoundUserProvider.getInstance();
		groupProvider = BoundGroupProvider.getInstance();
		groupMessageProvider = BoundGroupMessageProvider.getInstance();
		joinGroupProvider = BoundJoinGroupProvider.getInstance();
	}
}