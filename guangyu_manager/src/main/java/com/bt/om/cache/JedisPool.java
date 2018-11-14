package com.bt.om.cache;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.apache.log4j.Logger;

import com.bt.om.util.NumberUtil;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * 
 * 
 * @author hl-tanyong
 * @version $Id: JedisPool.java, v 0.1 2015年9月18日 上午10:44:50 hl-tanyong Exp $
 */
public class JedisPool {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(JedisPool.class);

	private static ShardedJedisPool pool;

	public JedisPool(JedisPoolConfig jedisPoolConfig, String redisPoolList) {
		if (redisPoolList == null) {
			return;
		}
		String[] redisIPs = redisPoolList.split(",");

		if (redisIPs == null) {
			return;
		}

		ArrayList<JedisShardInfo> ppl = new ArrayList<JedisShardInfo>();
		for (String tmp : redisIPs) {
			String[] ips = tmp.split(":");
			if (ips.length == 2) {
				String ip = ips[0];
				int port = NumberUtil.parseInt(ips[1]);

				JedisShardInfo pool = new JedisShardInfo(ip, port);
				ppl.add(pool);
				logger.info("add Jedis Service:ip:" + ip + ",port:" + port);
			}
		}

		pool = new ShardedJedisPool((GenericObjectPoolConfig) jedisPoolConfig, ppl);
	}

	public ShardedJedisPool getPool() {
		return pool;
	}

	public ShardedJedis getResource() {
		return pool.getResource();
	}

	public void returnResource(ShardedJedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}

	public void returnBrokenResource(ShardedJedis jedis) {
		if (jedis != null) {
			pool.close();
		}
	}

	public void delete(String type, Object key) {
		byte[] cacheName = getCacheName(type, key);
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			jedis.del(cacheName);
		} catch (Exception e) {
			logger.error("delete cache error。");
		} finally {
			returnResource(jedis);
		}
	}

	/**
	 * 写入Cache
	 * 
	 * @param type
	 * @param key
	 * @param value
	 * @param seconds
	 */
	public void putInCache(String type, Object key, Object value, int seconds) {
		if (value != null) {
			byte[] cacheName = getCacheName(type, key);
			byte[] v = this.getSerializable(value);
			if (v != null) {
				ShardedJedis jedis = null;
				try {
					jedis = getResource();
					if (seconds < 1) {
						jedis.set(cacheName, v);
					} else {
						jedis.setex(cacheName, seconds, v);
					}
				} catch (Exception e) {
					logger.error("cache " + getCacheName(type, key) + " socket error。");
				} finally {
					returnResource(jedis);
				}
			}
		}
	}

	/**
	 * 无时限缓存
	 *
	 * @param type
	 * @param key
	 * @param value
	 */
	public void putNoTimeInCache(String type, Object key, Object value) {
		if (value != null) {
			putInCache(type, key, value, -1);
		}
	}

	public Object getFromCache(String type, Object key) {
		ShardedJedis jedis = null;
		try {
			jedis = getResource();
			byte[] v = jedis.get(getCacheName(type, key));
			if (null == v) {
				return null;
			}
			return this.getDeserialization(v);
		} catch (Exception e) {
			logger.debug("cache " + getCacheName(type, key) + " error。");
			return null;
		} finally {
			returnResource(jedis);
		}
	}

	private byte[] getCacheName(String type, Object key) {
		StringBuilder cacheName = new StringBuilder(type);
		if (key != null && key.toString().length() > 0) {
			cacheName.append("_").append(key);
		}
		return cacheName.toString().getBytes();
	}

	/**
	 * 序列化
	 *
	 * @param value
	 * @return
	 */
	private byte[] getSerializable(Object value) {
		ByteArrayOutputStream buffer = null;
		ObjectOutputStream oos = null;
		try {
			buffer = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(buffer);
			oos.writeObject(value);
			return buffer.toByteArray();
		} catch (IOException e) {
			logger.error("ERROR:", e);
		} finally {
			if (oos != null) {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 反序列化
	 *
	 * @param value
	 * @return
	 */
	private Object getDeserialization(byte[] value) {
		if (value == null) {
			return null;
		}
		try {
			ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(value));
			return ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("ERROR:", e);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error("ERROR:", e);
		}
		return null;
	}
}