package com.itrade.common.infrastructure.util.collection;

import java.io.Serializable;

public class KeyValuePair<K, V> implements Serializable {

	private K key;
	private V value;

	public KeyValuePair(final K key, final V value) {
		this.key = key;
		this.value = value;
	}

	public K getKey() {
		return this.key;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return this.value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public boolean equals(Object o) {
		if (!(o instanceof KeyValuePair))
			return false;

		KeyValuePair<K, V> e = (KeyValuePair<K, V>) o;
		return eq(key, e.getKey()) && eq(value, e.getValue());
	}

	private boolean eq(Object o1, Object o2) {
		return o1 == null ? o2 == null : o1.equals(o2);
	}

	public int hashCode() {
		return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
	}

	@Override
	public String toString() {
		return key + "^" + value;
	}
}
