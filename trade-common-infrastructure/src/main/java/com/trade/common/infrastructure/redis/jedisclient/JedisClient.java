package com.trade.common.infrastructure.redis.jedisclient;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.trade.common.infrastructure.redis.cachekey.ICacheKey;
import com.trade.common.infrastructure.util.date.CustomDateUtils;
import com.trade.common.infrastructure.util.json.CustomJSONUtils;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JedisClient Redis命令参考：http://www.redisdoc.com/en/latest/
 *
 * @author Administrator
 */
@Slf4j
public abstract class JedisClient {

	private final static int SUCCESS = 1;

	/************************************* JedisClient *************************************/

	/**
	 * 将 byte[] 值 value 关联到 key 。 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 *
	 * @param key
	 * @param value
	 */
	public void set(ICacheKey key, byte[] value) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				int seconds = key.getExpirationTime();
				if (seconds > 0) {
					jds.setex(keyString.getBytes("UTF-8"), seconds, value);
				} else {
					jds.set(keyString.getBytes("UTF-8"), value);
				}
			}
		} catch (UnsupportedEncodingException e) {
			log.error("jedis set exception!", e);
		}
	}

	/**
	 * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。
	 *
	 * @param key
	 * @param value
	 */
	public void set(ICacheKey key, String value) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				int seconds = key.getExpirationTime();
				if (seconds > 0) {
					jds.setex(keyString, seconds, value);
				} else {
					jds.set(keyString, value);
				}
			}
		}
	}

	/**
	 * 将值 value 关联到 key，并将 key 的生存时间设为 seconds (以秒为单位)。 如果 key 已经存在， SETEX 命令将覆写旧值。
	 *
	 * @param key
	 * @param seconds
	 * @param value
	 */
	public void setex(ICacheKey key, int seconds, String value) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				jds.setex(keyString, seconds, value);
			}
		}
	}

	/**
	 * 将值 value 关联到 key，并将 key 的生存时间设为某个时间点。 如果 key 已经存在， SETEX 命令将覆写旧值。
	 *
	 * @param key
	 * @param expireAt
	 * @param value
	 */
	public void setex(ICacheKey key, LocalDateTime expireAt, String value) {
		set(key, value);
		expireAt(key, expireAt);
	}

	/**
	 * 将 key 的值设为 value ，当且仅当 key 不存在。 若给定的 key 已经存在，则 SETNX 不做任何动作。 SETNX 是『SET if Not eXists』(如果不存在，则 SET)的简写。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setnx(ICacheKey key, String value) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				boolean result = jds.setnx(keyString, value) == SUCCESS;

				if (result && key.getExpirationTime() > 0 && ttl(key) < 0) {
					expire(keyString, key.getExpirationTime());
				}

				return result;
			}
		}

		return false;
	}

	/**
	 * 返回 key 所关联的字符串值。 如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为 GET 只能用于处理字符串值。
	 *
	 * @param key
	 * @return
	 */
	public String get(ICacheKey key) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.get(keyString);
			}
		}
		return null;
	}

	/**
	 * 返回 key 所关联的byte[]值。 如果 key 不存在那么返回特殊值 nil
	 *
	 * @param key
	 * @return
	 */
	public byte[] getBytes(ICacheKey key) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.get(keyString.getBytes("UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			log.error("jedis set exception!", e);
		}

		return null;
	}

	/**
	 * 返回所有(一个或多个)给定 key 的值。 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。因此，该命令永不失败。
	 *
	 * @param keys
	 * @return
	 */
	public List<String> mget(List<ICacheKey> keys) {
		List<String> result = Lists.newArrayList();

		int size = keys.size();
		for (int i = 0; i < size; i++) {
			String value = get(keys.get(i));
			if (!Strings.isNullOrEmpty(value)) {
				result.add(value);
			}
		}

		return result;
	}

	/**
	 * 将给定 key 的值设为 value ，并返回 key 的旧值(old value)。 当 key 存在但不是字符串类型时，返回一个错误。
	 *
	 * @param key
	 * @param value
	 */
	public String getSet(ICacheKey key, String value) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.getSet(keyString, value);
			}
		}
		return "";
	}

	/**
	 * 检查给定 key 是否存在。
	 *
	 * @param key
	 * @return
	 */
	public boolean exists(ICacheKey key) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.exists(keyString);
			}
		}
		return false;
	}

	/**
	 * 删除给定的一个 key 。不存在的 key 会被忽略。
	 *
	 * @param key
	 * @return
	 */
	public boolean del(ICacheKey key) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.del(keyString) == SUCCESS;
			}
		}
		return false;
	}

	/**
	 * 删除给定的多个 key 。
	 *
	 * @param keys
	 * @return
	 */
	public boolean mdel(List<ICacheKey> keys) {
		if (keys.size() == 0) {
			return false;
		}

		for (ICacheKey key : keys) {
			if (!del(key)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * 将 key 中储存的数字值增一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 *
	 * @param key
	 * @return
	 */
	public long incr(ICacheKey key) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.incr(keyString);
			}
		}
		return 0;
	}

	/**
	 * 将 key 所储存的值加上增量 increment 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 INCRBY 命令。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 *
	 * @param key
	 * @param count
	 * @return
	 */
	public long incrBy(ICacheKey key, int count) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.incrBy(keyString, count);
			}
		}

		return 0;
	}

	/**
	 * 将 key 所储存的值减去减量 decrement 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 *
	 * @param key
	 * @param amount
	 * @return
	 */
	public long decrBy(ICacheKey key, long amount) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.decrBy(keyString, amount);
			}
		}

		return 0;
	}

	/**
	 * 将 key 所储存的值减去减量 decrement 。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECRBY 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 *
	 * @param key
	 * @param count
	 * @return
	 */
	public long decrBy(ICacheKey key, int count) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.decrBy(keyString, count);
			}
		}

		return 0;
	}

	/**
	 * 将 key 中储存的数字值减一。 如果 key 不存在，那么 key 的值会先被初始化为 0 ，然后再执行 DECR 操作。 如果值包含错误的类型，或字符串类型的值不能表示为数字，那么返回一个错误。 本操作的值限制在 64 位(bit)有符号数字表示之内。
	 *
	 * @param key
	 * @return
	 */
	public long decr(ICacheKey key) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.decr(keyString);
			}
		}
		return 0;
	}

	/**
	 * 如果 key 已经存在并且是一个字符串， APPEND 命令将 value 追加到 key 原来的值的末尾。 如果 key 不存在， APPEND 就简单地将给定 key 设为 value ，就像执行 SET key value 一样。
	 *
	 * @param key
	 * @param value
	 * @return
	 */
	public long append(ICacheKey key, String value) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.append(keyString, value);
			}
		}

		return 0;
	}

	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
	 *
	 * @param key
	 * @return
	 */
	public boolean expire(ICacheKey key) {
		return expire(key.toString(), key.getExpirationTime());
	}

	/**
	 * 为给定 key 设置生存时间，当 key 过期时(生存时间为 0 )，它会被自动删除。
	 *
	 * @param keyString
	 * @param seconds
	 * @return
	 */
	private boolean expire(String keyString, int seconds) {
		if (seconds > 0) {
			try (Jedis jds = getJedis(keyString)) {
				if (jds != null) {
					return jds.expire(keyString, seconds) == SUCCESS;
				}
			}
		}

		return false;
	}

	/**
	 * EXPIREAT 的作用和 EXPIRE 类似，都用于为 key 设置生存时间。 不同在于 EXPIREAT 命令接受的时间参数是 UNIX 时间戳(unix timestamp)。
	 *
	 * @param key
	 * @param expireAt
	 * @return
	 */
	public boolean expireAt(ICacheKey key, LocalDateTime expireAt) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.expireAt(keyString, CustomDateUtils.getDateTimeSecond(expireAt)) == SUCCESS;
			}
		}
		return false;
	}

	/**
	 * 以秒为单位，返回给定 key 的剩余生存时间(TTL, time to live)。
	 *
	 * @param key
	 * @return
	 */
	public long ttl(ICacheKey key) {
		String keyString = key.toString();

		try (Jedis jds = getJedis(keyString)) {
			if (jds != null) {
				return jds.ttl(keyString);
			}
		}
		return 0;
	}

	/**
	 * 查找所有符合给定模式 pattern 的 key（全部 redis 实例）。 KEYS * 匹配数据库中所有 key 。 KEYS h?llo 匹配 hello ， hallo 和 hxllo 等。 KEYS h*llo 匹配 hllo 和 heeeeello 等。 KEYS h[ae]llo 匹配 hello 和 hallo ，但不匹配 hillo 。 特殊符号用 \ 隔开
	 *
	 * @param pattern
	 * @return
	 */
	public List<String> keys(String pattern) {
		List<String> result = Lists.newArrayList();

		List<Jedis> jdsList = getJedisList();
		for (int i = 0; i < jdsList.size(); i++) {
			try (Jedis jedis = jdsList.get(i)) {
				result.addAll(keys(jedis, pattern));
			}
		}

		return result;
	}

	/**
	 * 查找所有符合给定模式 pattern 的 key（单个 redis 实例）
	 *
	 * @param jds
	 * @param pattern
	 * @return
	 */
	public Set<String> keys(Jedis jds, String pattern) {
		return jds.keys(pattern);
	}

	/**
	 * 清空当前数据库中的所有 key。 此命令从不失败。
	 */
	public void flushDB() {
		List<Jedis> jdsList = getJedisList();
		int size = jdsList.size();

		for (int i = 0; i < size; i++) {
			try (Jedis jedis = jdsList.get(i)) {
				jedis.flushDB();
			}
		}
	}

	/**
	 * 执行一个 AOF文件 重写操作。重写会创建一个当前 AOF 文件的体积优化版本。
	 */
	public void bgrewriteaof() {
		List<Jedis> jdsList = getJedisList();
		int size = jdsList.size();

		for (int i = 0; i < size; i++) {
			try (Jedis jedis = jdsList.get(i)) {
				jedis.bgrewriteaof();
			}
		}
	}

	/**
	 * 在后台异步(Asynchronously)保存当前数据库的数据到磁盘。
	 */
	public void bgsave() {
		List<Jedis> jdsList = getJedisList();
		int size = jdsList.size();

		for (int i = 0; i < size; i++) {
			try (Jedis jedis = jdsList.get(i)) {
				jedis.bgsave();
			}
		}
	}

	/**
	 * SAVE 命令执行一个同步保存操作，将当前 Redis 实例的所有数据快照(snapshot)以 RDB 文件的形式保存到硬盘。
	 */
	public void save() {
		List<Jedis> jdsList = getJedisList();
		int size = jdsList.size();

		for (int i = 0; i < size; i++) {
			try (Jedis jedis = jdsList.get(i)) {
				jedis.save();
			}
		}
	}

	/************************************* JedisClient.Generic *************************************/

	/**
	 * 将字符串值 value 关联到 key 。 如果 key 已经持有其他值， SET 就覆写旧值，无视类型。 对于某个原本带有生存时间（TTL）的键来说， 当 SET 命令成功在这个键上执行时， 这个键原有的 TTL 将被清除。（泛型版本）
	 *
	 * @param key
	 * @param value
	 */
	public <T> void set(ICacheKey key, T value) {
		set(key, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 返回 key 所关联的字符串值。 如果 key 不存在那么返回特殊值 nil 。 假如 key 储存的值不是字符串类型，返回一个错误，因为 GET 只能用于处理字符串值。（泛型版本）
	 *
	 * @param key
	 * @param clazz
	 * @return
	 */
	public <T> T get(ICacheKey key, Class<T> clazz) {
		return CustomJSONUtils.parseObject(get(key), clazz);
	}

	/**
	 * 返回所有(一个或多个)给定 key 的值。（泛型版本）
	 *
	 * @param keys
	 * @param clazz
	 * @return
	 */
	public <T> List<T> mget(List<ICacheKey> keys, Class<T> clazz) {
		List<T> result = Lists.newArrayList();

		for (ICacheKey key : keys) {
			T value = get(key, clazz);
			if (value != null) {
				result.add(value);
			}
		}

		return result;
	}

	/**
	 * 将值 value 关联到 key，并将 key 的生存时间设为 seconds (以秒为单位)。 如果 key 已经存在， SETEX 命令将覆写旧值。（泛型版本）
	 *
	 * @param key
	 * @param seconds
	 * @param value
	 */
	public <T> void setex(ICacheKey key, int seconds, T value) {
		setex(key, seconds, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 将值 value 关联到 key，并将 key 的生存时间设为某个时间点。 如果 key 已经存在， SETEX 命令将覆写旧值。（泛型版本）
	 *
	 * @param key
	 * @param expireAt
	 * @param value
	 */
	public <T> void setex(ICacheKey key, LocalDateTime expireAt, T value) {
		setex(key, expireAt, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 同时设置一个或多个 key-value 对。（泛型版本）
	 *
	 * @param values
	 */
	public <T> void mset(Map<ICacheKey, T> values) {
		if (values.size() > 0) {
			for (Map.Entry<ICacheKey, T> entry : values.entrySet()) {
				set(entry.getKey(), entry.getValue());
			}
		}
	}

	/************************************* JedisClient_Hash *************************************/

	/**
	 * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。 如果域 field 已经存在于哈希表中，旧值将被覆盖。
	 *
	 * @param hashId
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean hset(ICacheKey hashId, String field, String value) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {

			if (jds != null) {
				long returnVal = jds.hset(hashIdString, field, value);
				if (hashId.getExpirationTime() > 0 && ttl(hashId) < 0) {
					expire(hashIdString, hashId.getExpirationTime());
				}

				return (returnVal == 0 || returnVal == 1);
			}
		}

		return false;
	}

	/**
	 * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。 若域 field 已经存在，该操作无效。 如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
	 *
	 * @param hashId
	 * @param field
	 * @param value
	 * @return
	 */
	public boolean hsetnx(ICacheKey hashId, String field, String value) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				long returnVal = jds.hsetnx(hashIdString, field, value);

				if (hashId.getExpirationTime() > 0 && ttl(hashId) < 0) {
					expire(hashIdString, hashId.getExpirationTime());
				}

				return (returnVal == 0 || returnVal == 1);
			}
		}

		return false;
	}

	/**
	 * 返回哈希表 key 中给定域 field 的值。
	 *
	 * @param hashId
	 * @param field
	 * @return
	 */
	public String hget(ICacheKey hashId, String field) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hget(hashIdString, field);
			}
		}

		return "";
	}

	/**
	 * 返回哈希表 key 中所有域的值。
	 *
	 * @param hashId
	 * @return
	 */
	public List<String> hvals(ICacheKey hashId) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hvals(hashIdString);
			}
		}

		return Lists.newArrayList();
	}

	/**
	 * 返回哈希表 key 中所有域的值。
	 *
	 * @param hashIdString
	 * @return
	 */
	public List<String> hvals(String hashIdString) {
		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hvals(hashIdString);
			}
		}
		return Lists.newArrayList();
	}

	/**
	 * 返回哈希表 key 中，所有的域和值。 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
	 *
	 * @param hashId
	 * @return
	 */
	public Map<String, String> hgetAll(ICacheKey hashId) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hgetAll(hashIdString);
			}
		}
		return Maps.newHashMap();
	}

	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。 如果给定的域不存在于哈希表，那么返回一个 nil 值。 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
	 *
	 * @param hashId
	 * @param fields
	 * @return
	 */
	public List<String> hmget(ICacheKey hashId, String... fields) {
		List<String> result = Lists.newArrayList();

		if (fields.length > 0) {
			String hashIdString = hashId.toString();

			try (Jedis jds = getJedis(hashIdString)) {
				if (jds != null) {
					result = jds.hmget(hashIdString, fields).stream().filter(x -> !Strings.isNullOrEmpty(x)).collect(Collectors.toList());
				}
			}
		}

		return result;
	}

	/**
	 * 查看哈希表 key 中，给定域 field 是否存在。
	 *
	 * @param hashId
	 * @param field
	 * @return
	 */
	public boolean hexists(ICacheKey hashId, String field) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hexists(hashIdString, field);
			}
		}
		return false;
	}

	/**
	 * 删除哈希表 key 中的一个或多个指定域，不存在的域将被忽略。
	 *
	 * @param hashId
	 * @param field
	 * @return
	 */
	public boolean hdel(ICacheKey hashId, String field) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hdel(hashIdString, field) == SUCCESS;
			}
		}
		return false;
	}

	/**
	 * 为哈希表 key 中的域 field 的值加上增量 increment 。 增量也可以为负数，相当于对给定域进行减法操作。 如果 key 不存在，一个新的哈希表被创建并执行 HINCRBY 命令。 如果域 field 不存在，那么在执行命令前，域的值被初始化为 0 。 对一个储存字符串值的域 field 执行 HINCRBY 命令将造成一个错误。 本操作的值被限制在 64
	 * 位(bit)有符号数字表示之内。
	 *
	 * @param hashId
	 * @param field
	 * @param incrementBy
	 * @return
	 */
	public long hincrBy(ICacheKey hashId, String field, int incrementBy) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hincrBy(hashIdString, field, incrementBy);
			}
		}
		return 0;
	}

	/**
	 * 返回哈希表 key 中域的数量。
	 *
	 * @param hashId
	 * @return
	 */
	public long hlen(ICacheKey hashId) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hlen(hashIdString);
			}
		}

		return 0;
	}

	/**
	 * 返回哈希表 key 中的所有域。
	 *
	 * @param hashId
	 * @return
	 */
	public Set<String> hkeys(ICacheKey hashId) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hkeys(hashIdString);
			}
		}
		return Sets.newHashSet();
	}

	public ScanResult<Map.Entry<String, String>> hscan(ICacheKey hashId, String cursor) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				return jds.hscan(hashIdString, cursor);
			}
		}
		return null;
	}

	public ScanResult<Map.Entry<String, String>> hscan(ICacheKey hashId, String cursor, String pattern, int count) {
		String hashIdString = hashId.toString();

		try (Jedis jds = getJedis(hashIdString)) {
			if (jds != null) {
				ScanParams params = new ScanParams();
				params.match(pattern);
				params.count(count);
				return jds.hscan(hashIdString, cursor, params);
			}
		}
		return null;
	}

	/************************************* JedisClient_Hash.Generic *************************************/
	/**
	 * 将哈希表 key 中的域 field 的值设为 value 。 如果 key 不存在，一个新的哈希表被创建并进行 HSET 操作。 如果域 field 已经存在于哈希表中，旧值将被覆盖。
	 *
	 * @param hashId
	 * @param field
	 * @param value
	 * @return
	 */
	public <T> boolean hset(ICacheKey hashId, String field, T value) {
		return hset(hashId, field, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 将哈希表 key 中的域 field 的值设置为 value ，当且仅当域 field 不存在。 若域 field 已经存在，该操作无效。 如果 key 不存在，一个新哈希表被创建并执行 HSETNX 命令。
	 *
	 * @param hashId
	 * @param field
	 * @param value
	 * @return
	 */
	public <T> boolean hsetnx(ICacheKey hashId, String field, T value) {
		return hsetnx(hashId, field, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 返回哈希表 key 中给定域 field 的值。
	 *
	 * @param hashId
	 * @param field
	 * @param clazz
	 * @return
	 */
	public <T> T hget(ICacheKey hashId, String field, Class<T> clazz) {
		return CustomJSONUtils.parseObject(hget(hashId, field), clazz);
	}

	/**
	 * 返回哈希表 key 中所有域的值。
	 *
	 * @param hashId
	 * @param clazz
	 * @return
	 */
	public <T> List<T> hvals(ICacheKey hashId, Class<T> clazz) {
		List<T> result = Lists.newArrayList();

		List<String> valueStrings = hvals(hashId);
		for (int i = 0; i < valueStrings.size(); i++) {
			if (!Strings.isNullOrEmpty(valueStrings.get(i))) {
				result.add(CustomJSONUtils.parseObject(valueStrings.get(i), clazz));
			}
		}

		return result;
	}

	/**
	 * 返回哈希表 key 中所有域的值。
	 *
	 * @param hashIdString
	 * @param clazz
	 * @return
	 */
	public <T> List<T> hvals(String hashIdString, Class<T> clazz) {
		List<T> result = Lists.newArrayList();

		List<String> valueStrings = hvals(hashIdString);
		for (int i = 0; i < valueStrings.size(); i++) {
			if (!Strings.isNullOrEmpty(valueStrings.get(i))) {
				result.add(CustomJSONUtils.parseObject(valueStrings.get(i), clazz));
			}
		}

		return result;
	}

	/**
	 * 返回哈希表 key 中，所有的域和值。 在返回值里，紧跟每个域名(field name)之后是域的值(value)，所以返回值的长度是哈希表大小的两倍。
	 *
	 * @param hashId
	 * @param clazz
	 * @return
	 */
	public <T> Map<String, T> hgetAll(ICacheKey hashId, Class<T> clazz) {
		Map<String, T> result = Maps.newHashMap();

		Map<String, String> map = hgetAll(hashId);
		for (Map.Entry<String, String> kv : map.entrySet()) {
			result.put(kv.getKey(), CustomJSONUtils.parseObject(kv.getValue(), clazz));
		}

		return result;
	}

	/**
	 * 返回哈希表 key 中，一个或多个给定域的值。 如果给定的域不存在于哈希表，那么返回一个 nil 值。 因为不存在的 key 被当作一个空哈希表来处理，所以对一个不存在的 key 进行 HMGET 操作将返回一个只带有 nil 值的表。
	 *
	 * @param hashId
	 * @param clazz
	 * @param fields
	 * @return
	 */
	public <T> List<T> hmget(ICacheKey hashId, Class<T> clazz, String... fields) {
		List<T> result = Lists.newArrayList();

		if (fields.length > 0) {
			List<String> valueStrings = hmget(hashId, fields);
			valueStrings.stream().forEach(x -> result.add(CustomJSONUtils.parseObject(x, clazz)));
		}

		return result;
	}

	/************************************* JedisClient_List *************************************/
	/**
	 * 返回列表 key 的所有元素。
	 *
	 * @param listId
	 * @return
	 */
	public List<String> lrangeall(ICacheKey listId) {
		return lrange(listId, 0, -1);
	}

	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 *
	 * @param listId
	 * @param startingFrom
	 * @param endingAt
	 * @return
	 */
	public List<String> lrange(ICacheKey listId, int startingFrom, int endingAt) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				return jds.lrange(listIdString, startingFrom, endingAt);
			}
		}
		return Lists.newArrayList();
	}

	/**
	 * 将一个值 value 插入到列表 key 的表尾。 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。 如果 key
	 * 不存在，一个空列表会被创建并执行 RPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public void rpush(ICacheKey listId, String value) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				jds.rpush(listIdString, value);
				if (listId.getExpirationTime() > 0 && ttl(listId) < 0) {
					expire(listIdString, listId.getExpirationTime());
				}
			}
		}
	}

	/**
	 * 将一个值 value 插入到列表 key 的表尾。 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。 如果 key
	 * 不存在，一个空列表会被创建并执行 RPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public void rpushx(ICacheKey listId, String value) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				jds.rpushx(listIdString, value);
				if (listId.getExpirationTime() > 0 && ttl(listId) < 0) {
					expire(listIdString, listId.getExpirationTime());
				}
			}
		}
	}

	/**
	 * 将多个值 value 插入到列表 key 的表尾。
	 *
	 * @param listId
	 * @param values
	 */
	public void rpushlist(ICacheKey listId, List<String> values) {
		if (values.size() > 0) {
			for (String val : values) {
				rpush(listId, val);
			}
		}
	}

	/**
	 * 将一个值 value 插入到列表 key 的表头。 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。 如果
	 * key 不存在，一个空列表会被创建并执行 LPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public void lpush(ICacheKey listId, String value) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				jds.lpush(listIdString, value);
				if (listId.getExpirationTime() > 0 && ttl(listId) < 0) {
					expire(listIdString, listId.getExpirationTime());
				}
			}
		}
	}

	/**
	 * 将一个值 value 插入到列表 key 的表头。 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。 如果
	 * key 不存在，一个空列表会被创建并执行 LPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public void lpushx(ICacheKey listId, String value) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				jds.lpushx(listIdString, value);
				if (listId.getExpirationTime() > 0 && ttl(listId) < 0) {
					expire(listIdString, listId.getExpirationTime());
				}
			}
		}
	}

	/**
	 * 将多个值 value 插入到列表 key 的表头。
	 *
	 * @param listId
	 * @param values
	 */
	public void lpushlist(ICacheKey listId, List<String> values) {
		if (values.size() > 0) {
			for (String val : values) {
				lpush(listId, val);
			}
		}
	}

	/**
	 * 移除并返回列表 key 的头元素。
	 *
	 * @param listId
	 * @return
	 */
	public String lpop(ICacheKey listId) {
		String listIdString = listId.toString();
		LocalDateTime startTime = LocalDateTime.now();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				return jds.lpop(listIdString);
			}
		}

		return "";
	}

	/**
	 * 移除并返回列表 key 的头元素（block版本）
	 *
	 * @param listId
	 * @param timeout
	 * @return
	 */
	public String blpop(ICacheKey listId, int timeout) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				List<String> list = jds.blpop(timeout, listIdString, "");
				if (list != null && list.size() == 2) {
					return list.get(1);
				}
			}
		}

		return "";
	}

	/**
	 * 移除并返回列表 key 的尾元素
	 *
	 * @param listId
	 * @return
	 */
	public String rpop(ICacheKey listId) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				return jds.rpop(listIdString);
			}
		}

		return "";
	}

	/**
	 * 移除并返回列表 key 的尾元素（block版本）
	 *
	 * @param listId
	 * @param timeout
	 * @return
	 */
	public String brpop(ICacheKey listId, int timeout) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				List<String> list = jds.brpop(timeout, listIdString, "");
				if (list != null && list.size() == 2) {
					return list.get(1);
				}
			}
		}

		return "";
	}

	/**
	 * 移除列表中所有与参数 value 相等的元素。
	 *
	 * @param listId
	 * @param value
	 * @return
	 */
	public long lrem(ICacheKey listId, String value) {
		return lrem(listId, 0, value);
	}

	/**
	 * 根据参数 count 的值，移除列表中与参数 value 相等的元素。 count 的值可以是以下几种： count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。 count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。 count = 0 : 移除表中所有与 value 相等的值。
	 *
	 * @param listId
	 * @param noOfMatches
	 * @param value
	 * @return
	 */
	public long lrem(ICacheKey listId, int noOfMatches, String value) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				return jds.lrem(listIdString, noOfMatches, value);
			}
		}
		return 0;
	}

	/**
	 * 返回列表 key 中，下标为 index 的元素。 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 如果 key 不是列表类型，返回一个错误。
	 *
	 * @param listId
	 * @param listIndex
	 * @return
	 */
	public String lindex(ICacheKey listId, int listIndex) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				return jds.lindex(listIdString, listIndex);
			}
		}
		return "";
	}

	/**
	 * 将列表 key 下标为 index 的元素的值设置为 value 。 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
	 *
	 * @param listId
	 * @param listIndex
	 * @param value
	 */
	public void lset(ICacheKey listId, int listIndex, String value) {
		String listIdString = listId.toString();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				jds.lset(listIdString, listIndex, value);
				if (listId.getExpirationTime() > 0 && ttl(listId) < 0) {
					expire(listIdString, listId.getExpirationTime());
				}
			}
		}
	}

	/**
	 * 返回列表 key 的长度。 如果 key 不存在，则 key 被解释为一个空列表，返回 0 . 如果 key 不是列表类型，返回一个错误。
	 *
	 * @param listId
	 * @return
	 */
	public long llen(ICacheKey listId) {
		String listIdString = listId.toString();
		LocalDateTime startTime = LocalDateTime.now();

		try (Jedis jds = getJedis(listIdString)) {
			if (jds != null) {
				return jds.llen(listIdString);
			}
		}

		return 0;
	}

	/************************************* JedisClient_List.Generic *************************************/
	/**
	 * 返回列表 key 的所有元素。
	 *
	 * @param listId
	 * @param clazz
	 * @return
	 */
	public <T> List<T> lrangeall(ICacheKey listId, Class<T> clazz) {
		return lrange(listId, 0, -1, clazz);
	}

	/**
	 * 返回列表 key 中指定区间内的元素，区间以偏移量 start 和 stop 指定。 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 *
	 * @param listId
	 * @param startingFrom
	 * @param endingAt
	 * @param clazz
	 * @return
	 */
	public <T> List<T> lrange(ICacheKey listId, int startingFrom, int endingAt, Class<T> clazz) {
		return lrange(listId, startingFrom, endingAt).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toList());
	}

	/**
	 * 将一个值 value 插入到列表 key 的表尾。 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。 如果 key
	 * 不存在，一个空列表会被创建并执行 RPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public <T> void rpush(ICacheKey listId, T value) {
		rpush(listId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 将一个值 value 插入到列表 key 的表尾。 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表尾：比如对一个空列表 mylist 执行 RPUSH mylist a b c ，得出的结果列表为 a b c ，等同于执行命令 RPUSH mylist a 、 RPUSH mylist b 、 RPUSH mylist c 。 如果 key
	 * 不存在，一个空列表会被创建并执行 RPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public <T> void rpushx(ICacheKey listId, T value) {
		rpushx(listId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 将多个值 value 插入到列表 key 的表尾。
	 *
	 * @param listId
	 * @param values
	 */
	public <T> void rpushlistObj(ICacheKey listId, List<T> values) {
		if (values.size() > 0) {
			List<String> dataList = values.stream().map(x -> CustomJSONUtils.toJSONString(x)).collect(Collectors.toList());
			rpushlist(listId, dataList);
		}
	}

	/**
	 * 将一个值 value 插入到列表 key 的表头 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。 如果
	 * key 不存在，一个空列表会被创建并执行 LPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public <T> void lpush(ICacheKey listId, T value) {
		lpush(listId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 将一个值 value 插入到列表 key 的表头 如果有多个 value 值，那么各个 value 值按从左到右的顺序依次插入到表头： 比如说，对空列表 mylist 执行命令 LPUSH mylist a b c ，列表的值将是 c b a ，这等同于原子性地执行 LPUSH mylist a 、 LPUSH mylist b 和 LPUSH mylist c 三个命令。 如果
	 * key 不存在，一个空列表会被创建并执行 LPUSH 操作。 当 key 存在但不是列表类型时，返回一个错误。
	 *
	 * @param listId
	 * @param value
	 */
	public <T> void lpushx(ICacheKey listId, T value) {
		lpushx(listId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 将多个值 value 插入到列表 key 的表头
	 *
	 * @param listId
	 * @param values
	 */
	public <T> void lpushlistObj(ICacheKey listId, List<T> values) {
		if (values.size() > 0) {
			List<String> dataList = values.stream().map(x -> CustomJSONUtils.toJSONString(x)).collect(Collectors.toList());
			lpushlist(listId, dataList);
		}
	}

	/**
	 * 移除并返回列表 key 的头元素。
	 *
	 * @param listId
	 * @param clazz
	 * @return
	 */
	public <T> T lpop(ICacheKey listId, Class<T> clazz) {
		return CustomJSONUtils.parseObject(lpop(listId), clazz);
	}

	/**
	 * 移除并返回列表 key 的头元素（block版本）
	 *
	 * @param listId
	 * @param timeout
	 * @param clazz
	 * @return
	 */
	public <T> T blpop(ICacheKey listId, int timeout, Class<T> clazz) {
		return CustomJSONUtils.parseObject(blpop(listId, timeout), clazz);
	}

	/**
	 * 移除并返回列表 key 的尾元素
	 *
	 * @param listId
	 * @param clazz
	 * @return
	 */
	public <T> T rpop(ICacheKey listId, Class<T> clazz) {
		return CustomJSONUtils.parseObject(rpop(listId), clazz);
	}

	/**
	 * 移除并返回列表 key 的尾元素（block版本）
	 *
	 * @param listId
	 * @param timeout
	 * @param clazz
	 * @return
	 */
	public <T> T brpop(ICacheKey listId, int timeout, Class<T> clazz) {
		return CustomJSONUtils.parseObject(brpop(listId, timeout), clazz);
	}

	/**
	 * 移除列表中所有与参数 value 相等的元素。
	 *
	 * @param listId
	 * @param value
	 * @return
	 */
	public <T> long lrem(ICacheKey listId, T value) {
		return lrem(listId, 0, value);
	}

	/**
	 * 根据参数 count 的值，移除列表中与参数 value 相等的元素。 count 的值可以是以下几种： count > 0 : 从表头开始向表尾搜索，移除与 value 相等的元素，数量为 count 。 count < 0 : 从表尾开始向表头搜索，移除与 value 相等的元素，数量为 count 的绝对值。 count = 0 : 移除表中所有与 value 相等的值。
	 *
	 * @param listId
	 * @param noOfMatches
	 * @param value
	 * @return
	 */
	public <T> long lrem(ICacheKey listId, int noOfMatches, T value) {
		return lrem(listId, noOfMatches, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 返回列表 key 中，下标为 index 的元素。 下标(index)参数 start 和 stop 都以 0 为底，也就是说，以 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 如果 key 不是列表类型，返回一个错误。
	 *
	 * @param listId
	 * @param listIndex
	 * @param clazz
	 * @return
	 */
	public <T> T lindex(ICacheKey listId, int listIndex, Class<T> clazz) {
		return CustomJSONUtils.parseObject(lindex(listId, listIndex), clazz);
	}

	/**
	 * 将列表 key 下标为 index 的元素的值设置为 value 。 当 index 参数超出范围，或对一个空列表( key 不存在)进行 LSET 时，返回一个错误。
	 *
	 * @param listId
	 * @param listIndex
	 * @param value
	 */
	public <T> void lset(ICacheKey listId, int listIndex, T value) {
		lset(listId, listIndex, CustomJSONUtils.toJSONString(value));
	}

	/************************************* JedisClient_Set *************************************/

	/**
	 * 返回集合 key 中的所有成员。 不存在的 key 被视为空集合。
	 *
	 * @param setId
	 * @return
	 */
	public Set<String> smembers(ICacheKey setId) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.smembers(setIdString);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。 当 key 不是集合类型时，返回一个错误。 *
	 *
	 * @param setId
	 * @param item
	 */
	public void sadd(ICacheKey setId, String item) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				jds.sadd(setIdString, item);
				if (setId.getExpirationTime() > 0 && ttl(setId) < 0) {
					expire(setIdString, setId.getExpirationTime());
				}
			}
		}
	}

	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。 当 key 不是集合类型，返回一个错误。
	 *
	 * @param setId
	 * @param item
	 */
	public void srem(ICacheKey setId, String item) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				jds.srem(setIdString, item);
			}
		}
	}

	/**
	 * 移除并返回集合中的一个随机元素。 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
	 *
	 * @param setId
	 * @return
	 */
	public String spop(ICacheKey setId) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.spop(setIdString);
			}
		}

		return "";
	}

	/**
	 * 判断 member 元素是否集合 key 的成员。
	 *
	 * @param setId
	 * @param item
	 * @return
	 */
	public boolean sismember(ICacheKey setId, String item) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.sismember(setIdString, item);
			}
		}
		return false;
	}

	/**
	 * 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。
	 *
	 * @param setId
	 * @return
	 */
	public String srandmember(ICacheKey setId) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.srandmember(setIdString);
			}
		}
		return "";
	}

	/**
	 * 返回集合 key 的基数(集合中元素的数量)。
	 *
	 * @param setId
	 * @return
	 */
	public long scard(ICacheKey setId) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.scard(setIdString);
			}
		}
		return 0;
	}

	/************************************* JedisClient_Set.Generic *************************************/
	/**
	 * 返回集合 key 中的所有成员。 不存在的 key 被视为空集合。
	 *
	 * @param setId
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> smembers(ICacheKey setId, Class<T> clazz) {
		return smembers(setId).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 将一个或多个 member 元素加入到集合 key 当中，已经存在于集合的 member 元素将被忽略。 假如 key 不存在，则创建一个只包含 member 元素作成员的集合。 当 key 不是集合类型时，返回一个错误。
	 *
	 * @param setId
	 * @param item
	 */
	public <T> void sadd(ICacheKey setId, T item) {
		sadd(setId, CustomJSONUtils.toJSONString(item));
	}

	/**
	 * 移除集合 key 中的一个或多个 member 元素，不存在的 member 元素会被忽略。 当 key 不是集合类型，返回一个错误。
	 *
	 * @param setId
	 * @param item
	 */
	public <T> void srem(ICacheKey setId, T item) {
		srem(setId, CustomJSONUtils.toJSONString(item));
	}

	/**
	 * 移除并返回集合中的一个随机元素。 如果只想获取一个随机元素，但不想该元素从集合中被移除的话，可以使用 SRANDMEMBER 命令。
	 *
	 * @param setId
	 * @param clazz
	 * @return
	 */
	public <T> T spop(ICacheKey setId, Class<T> clazz) {
		return CustomJSONUtils.parseObject(spop(setId), clazz);
	}

	/**
	 * 如果命令执行时，只提供了 key 参数，那么返回集合中的一个随机元素。
	 *
	 * @param setId
	 * @param clazz
	 * @return
	 */
	public <T> T srandmember(ICacheKey setId, Class<T> clazz) {
		return CustomJSONUtils.parseObject(srandmember(setId), clazz);
	}

	/************************************* JedisClient_SortedSet *************************************/

	/**
	 * 将一个 member 元素加入到有序集 key 当中。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public boolean zadd(ICacheKey setId, String value) {
		return zadd(setId, getLexicalScore(value), value);
	}

	/**
	 * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当 key
	 * 存在但不是有序集类型时，返回一个错误。
	 *
	 * @param setId
	 * @param score
	 * @param value
	 * @return
	 */
	public boolean zadd(ICacheKey setId, double score, String value) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				boolean result = jds.zadd(setIdString, score, value) == SUCCESS;

				if (setId.getExpirationTime() > 0 && ttl(setId) < 0) {
					expire(setIdString, setId.getExpirationTime());
				}

				return result;
			}
		}
		return false;
	}

	/**
	 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。 当 key 存在但不是有序集类型时，返回一个错误。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public boolean zrem(ICacheKey setId, String value) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrem(setIdString, value) == SUCCESS;
			}
		}

		return false;
	}

	/**
	 * 返回某个元素是否在集合中。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public boolean exsits(ICacheKey setId, String value) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {

			if (jds != null) {
				return jds.zrank(setIdString, value) != -1;
			}
		}
		return false;
	}

	/**
	 * 为有序集 key 的成员 member 的 score 值加上增量 increment 。 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment
	 * member 等同于 ZADD key increment member 。 当 key 不是有序集类型时，返回一个错误。 score 值可以是整数值或双精度浮点数。
	 *
	 * @param setId
	 * @param score
	 * @param value
	 * @return
	 */
	public double zincrby(ICacheKey setId, double score, String value) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zincrby(setIdString, score, value);
			}
		}

		return 0;
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public long zrank(ICacheKey setId, String value) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrank(setIdString, value);
			}
		}
		return 0;
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public long zrevrank(ICacheKey setId, String value) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrevrank(setIdString, value);
			}
		}
		return 0;
	}

	/**
	 * 返回有序集 key 中的所有成员。
	 *
	 * @param setId
	 * @return
	 */
	public Set<String> zrangeall(ICacheKey setId) {
		return zrange(setId, 0, -1);
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递增(从小到大)来排序。 具有相同 score 值的成员按字典序(lexicographical order )来排列。
	 *
	 * @param setId
	 * @param fromRank
	 * @param toRank
	 * @return
	 */
	public Set<String> zrange(ICacheKey setId, int fromRank, int toRank) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrange(setIdString, fromRank, toRank);
			}
		}
		return new HashSet<String>();
	}

	/**
	 * 返回有序集 key 中的所有成员。
	 *
	 * @param setId
	 * @return
	 */
	public Set<String> zrevrangeall(ICacheKey setId) {
		return zrevrange(setId, 0, -1);
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。
	 *
	 * @param setId
	 * @param fromRank
	 * @param toRank
	 * @return
	 */
	public Set<String> zrevrange(ICacheKey setId, int fromRank, int toRank) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrevrange(setIdString, fromRank, toRank);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @return
	 */
	public Set<String> zrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrangeByScore(setIdString, fromStringScore, toStringScore);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT
	 * LIMIT offset, count )，注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @param skip
	 * @param take
	 * @return
	 */
	public Set<String> zrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore, int skip, int take) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrangeByScore(setIdString, fromStringScore, toStringScore, skip, take);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @return
	 */
	public Set<String> zrangeByScore(ICacheKey setId, double fromScore, double toScore) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrangeByScore(setIdString, fromScore, toScore);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT
	 * LIMIT offset, count )，注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @param skip
	 * @param take
	 * @return
	 */
	public Set<String> zrangeByScore(ICacheKey setId, double fromScore, double toScore, int skip, int take) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrangeByScore(setIdString, fromScore, toScore, skip, take);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @return
	 */
	public Set<String> zrevrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrevrangeByScore(setIdString, fromStringScore, toStringScore);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @param skip
	 * @param take
	 * @return
	 */
	public Set<String> zrevrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore, int skip, int take) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrevrangeByScore(setIdString, fromStringScore, toStringScore, skip, take);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @return
	 */
	public Set<String> zrevrangeByScore(ICacheKey setId, double fromScore, double toScore) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrevrangeByScore(setIdString, fromScore, toScore);
			}
		}
		return Sets.newHashSet();
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @param skip
	 * @param take
	 * @return
	 */
	public Set<String> zrevrangeByScore(ICacheKey setId, double fromScore, double toScore, int skip, int take) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zrevrangeByScore(setIdString, fromScore, toScore, skip, take);
			}
		}

		return Sets.newHashSet();
	}

	/**
	 * 移除有序集 key 中，指定排名(rank)区间内的所有成员。 区间分别以下标参数 start 和 stop 指出，包含 start 和 stop 在内。 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
	 *
	 * @param setId
	 * @param minRank
	 * @param maxRank
	 * @return
	 */
	public long zremrangeByRank(ICacheKey setId, int minRank, int maxRank) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zremrangeByRank(setIdString, minRank, maxRank);
			}
		}

		return 0;
	}

	/**
	 * 移除有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @return
	 */
	public long zremrangeByScore(ICacheKey setId, double fromScore, double toScore) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zremrangeByScore(setIdString, fromScore, toScore);
			}
		}
		return 0;
	}

	/**
	 * 返回有序集 key 的基数。
	 *
	 * @param setId
	 * @return
	 */
	public long zcard(ICacheKey setId) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zcard(setIdString);
			}
		}
		return 0;
	}

	/**
	 * 返回有序集 key 中，成员 member 的 score 值。 如果 member 元素不是有序集 key 的成员，或 key 不存在，返回 nil 。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public double zscore(ICacheKey setId, String value) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zscore(setIdString, value);
			}
		}
		return 0;
	}

	/**
	 * 返回有序集 key 中， score 值在 min 和 max 之间(默认包括 score 值等于 min 或 max )的成员的数量。
	 *
	 * @param setId
	 * @param min
	 * @param max
	 * @return
	 */
	public double zcount(ICacheKey setId, double min, double max) {
		String setIdString = setId.toString();

		try (Jedis jds = getJedis(setIdString)) {
			if (jds != null) {
				return jds.zcount(setIdString, min, max);
			}
		}
		return 0;
	}

	/************************************* 帮助方法 *************************************/

	private double getLexicalScore(String value) {
		if (Strings.isNullOrEmpty(value))
			return 0;

		double lexicalValue = 0;
		if (value.length() >= 1)
			lexicalValue += value.charAt(0) * (int) Math.pow(256, 3);

		if (value.length() >= 2)
			lexicalValue += value.charAt(1) * (int) Math.pow(256, 2);

		if (value.length() >= 3)
			lexicalValue += value.charAt(2) * (int) Math.pow(256, 1);

		if (value.length() >= 4)
			lexicalValue += value.charAt(3);

		return lexicalValue;
	}

	/************************************* JedisClient_SortedSet.Generic *************************************/

	/**
	 * 将一个 member 元素加入到有序集 key 当中。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public <T> boolean zadd(ICacheKey setId, T value) {
		return zadd(setId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中。 如果某个 member 已经是有序集的成员，那么更新这个 member 的 score 值，并通过重新插入这个 member 元素，来保证该 member 在正确的位置上。 score 值可以是整数值或双精度浮点数。 如果 key 不存在，则创建一个空的有序集并执行 ZADD 操作。 当 key
	 * 存在但不是有序集类型时，返回一个错误。
	 *
	 * @param setId
	 * @param score
	 * @param value
	 * @return
	 */
	public <T> boolean zadd(ICacheKey setId, double score, T value) {
		return zadd(setId, score, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 移除有序集 key 中的一个或多个成员，不存在的成员将被忽略。 当 key 存在但不是有序集类型时，返回一个错误。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public <T> boolean zrem(ICacheKey setId, T value) {
		return zrem(setId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 判断 member 元素是否集合 key 的成员。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public <T> boolean sismember(ICacheKey setId, T value) {
		return sismember(setId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 为有序集 key 的成员 member 的 score 值加上增量 increment 。 可以通过传递一个负数值 increment ，让 score 减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。 当 key 不存在，或 member 不是 key 的成员时， ZINCRBY key increment
	 * member 等同于 ZADD key increment member 。 当 key 不是有序集类型时，返回一个错误。 score 值可以是整数值或双精度浮点数。
	 *
	 * @param setId
	 * @param score
	 * @param value
	 * @return
	 */
	public <T> double zincrby(ICacheKey setId, double score, T value) {
		return zincrby(setId, score, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递增(从小到大)顺序排列。 排名以 0 为底，也就是说， score 值最小的成员排名为 0 。 使用 ZREVRANK 命令可以获得成员按 score 值递减(从大到小)排列的排名。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public <T> long zrank(ICacheKey setId, T value) {
		return zrank(setId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 返回有序集 key 中成员 member 的排名。其中有序集成员按 score 值递减(从大到小)排序。 排名以 0 为底，也就是说， score 值最大的成员排名为 0 。 使用 ZRANK 命令可以获得成员按 score 值递增(从小到大)排列的排名。
	 *
	 * @param setId
	 * @param value
	 * @return
	 */
	public <T> long zrevrank(ICacheKey setId, T value) {
		return zrevrank(setId, CustomJSONUtils.toJSONString(value));
	}

	/**
	 * 返回有序集 key 中的所有成员。
	 *
	 * @param setId
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrangeall(ICacheKey setId, Class<T> clazz) {
		return zrangeall(setId).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递增(从小到大)来排序。 具有相同 score 值的成员按字典序(lexicographical order )来排列。
	 *
	 * @param setId
	 * @param fromRank
	 * @param toRank
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrange(ICacheKey setId, int fromRank, int toRank, Class<T> clazz) {
		return zrange(setId, fromRank, toRank).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中的所有成员。
	 *
	 * @param setId
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrevrangeall(ICacheKey setId, Class<T> clazz) {
		return zrevrangeall(setId).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中，指定区间内的成员。 其中成员的位置按 score 值递减(从大到小)来排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order)排列。
	 *
	 * @param setId
	 * @param fromRank
	 * @param toRank
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrevrange(ICacheKey setId, int fromRank, int toRank, Class<T> clazz) {
		return zrevrange(setId, fromRank, toRank).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore, Class<T> clazz) {
		return zrangeByScore(setId, fromStringScore, toStringScore).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT
	 * LIMIT offset, count )，注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @param skip
	 * @param take
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore, int skip, int take, Class<T> clazz) {
		return zrangeByScore(setId, fromStringScore, toStringScore, skip, take).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrangeByScore(ICacheKey setId, double fromScore, double toScore, Class<T> clazz) {
		return zrangeByScore(setId, fromScore, toScore).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中，所有 score 值介于 min 和 max 之间(包括等于 min 或 max )的成员。有序集成员按 score 值递增(从小到大)次序排列。 具有相同 score 值的成员按字典序(lexicographical order)来排列(该属性是有序集提供的，不需要额外的计算)。 可选的 LIMIT 参数指定返回结果的数量及区间(就像SQL中的 SELECT
	 * LIMIT offset, count )，注意当 offset 很大时，定位 offset 的操作可能需要遍历整个有序集，此过程最坏复杂度为 O(N) 时间。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @param skip
	 * @param take
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrangeByScore(ICacheKey setId, double fromScore, double toScore, int skip, int take, Class<T> clazz) {
		return zrangeByScore(setId, fromScore, toScore, skip, take).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrevrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore, Class<T> clazz) {
		return zrevrangeByScore(setId, fromStringScore, toStringScore).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromStringScore
	 * @param toStringScore
	 * @param skip
	 * @param take
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrevrangeByScore(ICacheKey setId, String fromStringScore, String toStringScore, int skip, int take, Class<T> clazz) {
		return zrevrangeByScore(setId, fromStringScore, toStringScore, skip, take).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrevrangeByScore(ICacheKey setId, double fromScore, double toScore, Class<T> clazz) {
		return zrevrangeByScore(setId, fromScore, toScore).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	/**
	 * 返回有序集 key 中， score 值介于 max 和 min 之间(默认包括等于 max 或 min )的所有的成员。有序集成员按 score 值递减(从大到小)的次序排列。 具有相同 score 值的成员按字典序的逆序(reverse lexicographical order )排列。
	 *
	 * @param setId
	 * @param fromScore
	 * @param toScore
	 * @param skip
	 * @param take
	 * @param clazz
	 * @return
	 */
	public <T> Set<T> zrevrangeByScore(ICacheKey setId, double fromScore, double toScore, int skip, int take, Class<T> clazz) {
		return zrevrangeByScore(setId, fromScore, toScore, skip, take).stream().filter(x -> !Strings.isNullOrEmpty(x)).map(y -> CustomJSONUtils.parseObject(y, clazz)).collect(Collectors.toSet());
	}

	public void pub(String channel, String message) {
		Jedis jedis = getJedis("");
		Long pubId = jedis.publish(channel, message);
	}

	public void sub(String channel, JedisPubSub listener) {
		Jedis jedis = getJedis("");
		jedis.subscribe(listener, channel);
	}

	/************************************* abstract *************************************/
	/**
	 * 获取 Jedis 单个实例
	 *
	 * @param keyString
	 * @return
	 */
	private Jedis getJedis(String keyString) {
		if (isMasterServer()) {
			return JedisPoolUtils.getInstance().getMasterJedis(getProxyType(), keyString);
		} else {
			return JedisPoolUtils.getInstance().getSlaveJedis(getProxyType(), keyString);
		}
	}

	/**
	 * 释放 Jedis 单个实例
	 *
	 * @param jedis
	 */
	private void returnJedis(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	/**
	 * 获取 Jedis 全部实例
	 *
	 * @return
	 */
	public List<Jedis> getJedisList() {
		if (isMasterServer()) {
			return JedisPoolUtils.getInstance().getMasterJedisList(getProxyType());
		} else {
			return JedisPoolUtils.getInstance().getSlaveJedisList(getProxyType());
		}
	}

	/**
	 * 释放 Jedis 全部实例
	 *
	 * @param jedisList
	 */
	private void returnJedisList(List<Jedis> jedisList) {
		for (int i = 0; i < jedisList.size(); i++) {
			if (jedisList.get(i) != null) {
				jedisList.get(i).close();
			}
		}
	}

	/**
	 * 设置 Proxy 类型
	 *
	 * @return
	 */
	protected abstract Integer getProxyType();

	/**
	 * 设置是否为 Master 服务器
	 *
	 * @return
	 */
	protected abstract boolean isMasterServer();
}
