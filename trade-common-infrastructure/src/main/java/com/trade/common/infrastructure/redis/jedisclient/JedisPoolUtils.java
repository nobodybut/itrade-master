package com.trade.common.infrastructure.redis.jedisclient;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.trade.common.infrastructure.util.logger.LogInfoUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Redis工具类（暂时只支持1个 Slave 订阅）
 */
public class JedisPoolUtils {

	// 日志记录
	protected static Logger s_logger = LoggerFactory.getLogger(JedisPoolUtils.class);

	// 定义 redis Key $JNR$ 特殊分隔符
	public final static String JNR_SEPARATOR = "$JNR$";
	private final static int JNR_SEPARATOR_LEN = JNR_SEPARATOR.length();

	// 线程池存储
	private static Map<Integer, List<JedisPool>> s_jedisPoolsMap_master = Maps.newHashMap();
	private static Map<Integer, List<JedisPool>> s_jedisPoolsMap_slave = Maps.newHashMap();

	/**
	 * 私有构造器.
	 */
	private JedisPoolUtils() {
	}

	/**
	 * 初始化 List<JedisPool>，暂时只支持一个 Slave 库的订阅
	 */
	static {
		List<RedisServerConfig> cacheServerConfigs = initCacheServerConfigs();
		for (RedisServerConfig cacheServerConfig : cacheServerConfigs) {
			// 计算 masterServers、slaveServers
			List<String> masterServers = Lists.newArrayList();
			List<String> slaveServers = Lists.newArrayList();

			Iterable<String> servers = Splitter.on("|").split(cacheServerConfig.getServerList());
			for (String server : servers) {
				String[] mss = StringUtils.split(server, "^");
				if (mss.length == 1) {
					masterServers.add(mss[0]);
				} else if (mss.length == 2) {
					masterServers.add(mss[0]);
					slaveServers.add(mss[1]);
				} else {
					s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName(), "数据库（conf_redisserver表）redis部分配置出错!"));
				}
			}

			// 添加 master pool
			s_jedisPoolsMap_master.put(cacheServerConfig.getId(), createJedisPools(cacheServerConfig.getMaxTotal(), cacheServerConfig.getMaxIdle(), cacheServerConfig.getMaxWaitMillis(), masterServers));

			// 添加 slave pool
			if (slaveServers.size() > 0) {
				s_jedisPoolsMap_slave.put(cacheServerConfig.getId(), createJedisPools(cacheServerConfig.getMaxTotal(), cacheServerConfig.getMaxIdle(), cacheServerConfig.getMaxWaitMillis(), slaveServers));
			}
		}
	}

	/**
	 * 初始化 redis 服务器列表
	 *
	 * @return
	 */
	private static List<RedisServerConfig> initCacheServerConfigs() {
		List<RedisServerConfig> result = Lists.newArrayList();


		return result;
	}

	/**
	 * 创建 List<JedisPool>
	 *
	 * @param maxTotal
	 * @param maxIdle
	 * @param maxWaitMillis
	 * @param servers
	 * @return
	 */
	private static List<JedisPool> createJedisPools(int maxTotal, int maxIdle, int maxWaitMillis, List<String> servers) {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(maxTotal);
		config.setMaxIdle(maxIdle);
		config.setMaxWaitMillis(maxWaitMillis);
		config.setTestOnBorrow(true);
		config.setTestOnReturn(true);
		config.setTestWhileIdle(true);

		List<JedisPool> result = new ArrayList<JedisPool>(servers.size());
		for (String server : servers) {
			String[] arrServer = StringUtils.split(server, ":");
			result.add(new JedisPool(config, arrServer[0], NumberUtils.toInt(arrServer[1]), maxWaitMillis, "j^dy@", Protocol.DEFAULT_DATABASE));
		}

		return result;
	}

	// ================================== Jedis 单个实例 ==================================

	/**
	 * 获取单个 Jedis 实例（Master）
	 *
	 * @param proxyType
	 * @param key
	 * @return
	 */
	public Jedis getMasterJedis(Integer proxyType, String key) {
		return getJedis(s_jedisPoolsMap_master, proxyType, key);
	}

	/**
	 * 获取单个 Jedis 实例（Slave）
	 *
	 * @param proxyType
	 * @param key
	 * @return
	 */
	public Jedis getSlaveJedis(Integer proxyType, String key) {
		return getJedis(s_jedisPoolsMap_slave, proxyType, key);
	}

	/**
	 * 根据类型获取单个 Jedis 实例
	 *
	 * @param jedisPoolsMap
	 * @param proxyType
	 * @param key
	 * @return
	 */
	private Jedis getJedis(Map<Integer, List<JedisPool>> jedisPoolsMap, Integer proxyType, String key) {
		Jedis jedis = null;

		List<JedisPool> pools = jedisPoolsMap.get(proxyType);
		int nodeIndex = calClusterNodeIndex(key, pools.size());

		try {
			jedis = pools.get(nodeIndex).getResource();
		} catch (Exception e) {
			String logData = String.format("get redis %s (nodeIndex=%s) failed!", proxyType, nodeIndex);
			s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName(), logData), e);

			if (jedis != null) {
				jedis.close();
			}
		}

		return jedis;
	}

	/**
	 * 计算集群节点索引位置
	 *
	 * @param key
	 * @param poolsSize
	 * @return
	 */
	private int calClusterNodeIndex(String key, int poolsSize) {
		int jnrIndex = key.indexOf(JNR_SEPARATOR);
		if (jnrIndex != -1) {
			return NumberUtils.toInt(key.substring(jnrIndex + JNR_SEPARATOR_LEN)) % poolsSize;
		} else {
			return Math.abs(Objects.hashCode(key)) % poolsSize;
		}
	}

	// ================================== Jedis 全部实例 ==================================

	/**
	 * 获取全部 Jedis 全部实例（Master）
	 *
	 * @param proxyType
	 * @return
	 */
	public List<Jedis> getMasterJedisList(Integer proxyType) {
		return getJedisList(s_jedisPoolsMap_master, proxyType);
	}

	/**
	 * 获取全部 Jedis 全部实例（Slave）
	 *
	 * @param proxyType
	 * @return
	 */
	public List<Jedis> getSlaveJedisList(Integer proxyType) {
		return getJedisList(s_jedisPoolsMap_slave, proxyType);
	}

	/**
	 * 根据类型获取全部 Jedis 全部实例
	 *
	 * @param jedisPoolsMap
	 * @param proxyType
	 * @return
	 */
	private List<Jedis> getJedisList(Map<Integer, List<JedisPool>> jedisPoolsMap, Integer proxyType) {
		List<Jedis> result = Lists.newArrayList();

		List<JedisPool> pools = jedisPoolsMap.get(proxyType);
		int size = pools.size();

		for (int i = 0; i < size; i++) {
			Jedis jedis = null;

			try {
				jedis = pools.get(i).getResource();
				result.add(jedis);
			} catch (Exception e) {
				String logData = String.format("get redislist %s failed!", proxyType);
				s_logger.error(String.format(LogInfoUtils.HAS_DATA_TMPL, Thread.currentThread().getStackTrace()[1].getMethodName(), logData), e);

				if (jedis != null) {
					jedis.close();
				}
			}
		}

		return result;
	}

	/**
	 * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到时才会装载，从而实现了延迟加载。
	 */
	private static class JedisPoolUtilsHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static JedisPoolUtils instance = new JedisPoolUtils();
	}

	/**
	 * 当getInstance方法第一次被调用的时候，它第一次读取 RedisUtilHolder.instance，导致RedisUtilHolder类得到初始化；<BR />
	 * 而这个类在装载并被初始化的时候，会初始化它的静 态域，从而创建RedisUtil的实例，由于是静态的域，因此只会在虚拟机装载类的时候初始化一次，并由虚拟机来保证它的线程安全性。<BR />
	 * 这个模式的优势在于，getInstance方法并没有被同步，并且只是执行一个域的访问，因此延迟初始化并没有增加任何访问成本。<BR />
	 */
	public static JedisPoolUtils getInstance() {
		return JedisPoolUtilsHolder.instance;
	}
}