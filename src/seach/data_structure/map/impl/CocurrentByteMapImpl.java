package seach.data_structure.map.impl;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import seach.data_structure.map.ByteMap;

public class CocurrentByteMapImpl implements ByteMap{

	private static final byte[] BYTE_VALUE = {
			0x0001,
			0x0002,
			0x0004,
			0x0008,
			0x0010,
			0x0020,
			0x0040,
			-0x0080
	};

	private int size;
	
	private byte b;
	private byte[] biteMap;
	
	private ReadWriteLock lock;
	private ReadWriteLock[] locks;
	
	public CocurrentByteMapImpl() {
		this(8);
	}
	
	public CocurrentByteMapImpl(int size) {
		if(size <= 0) {
			throw new IllegalArgumentException("ByteMap size value should be positive");
		}
		this.size = size;
		if(size <= 8) {
			this.b = 0;
			this.lock = new ReentrantReadWriteLock();
		}else {
			int len = (size >> 3) + ((size & 7) > 0 ? 1 : 0);
			this.biteMap = new byte[len];
			this.locks = new ReadWriteLock[len];
			while(len-- > 0) {
				this.locks[len] = new ReentrantReadWriteLock(); 
			}
		}
	}
	
	public int getByte(int index) {
		if(index < 0 || index >= this.size) {
			throw new IllegalArgumentException("index out of bit map");
		}
		ReadWriteLock lock = (this.size <= 8) ? this.lock : this.locks[index >> 3];
		byte unit = (this.size <= 8) ? this.b : this.biteMap[index >> 3];
		int result = 0;
		try {
			lock.readLock().lock();
			result = unit & BYTE_VALUE[index & 7];
		} finally {
			lock.readLock().unlock();
		}	
		return result == 0 ? 0 : 1;
	}
	
	public void setByte(int index, int flag) {
		if(index < 0 || index >= this.size) {
			throw new IllegalArgumentException("index out of bit map");
		}
		if(flag != 0 && flag != 1) {
			throw new IllegalArgumentException("illegal flag argument, must be 1 or 0");
		}
		
		if(flag == this.getByte(index)) {
			return;
		}
		int offSet = index & 7;
		if(this.size <= 8) {
			ReadWriteLock lock = this.lock;
			try {
				lock.writeLock().lock();
				if(flag == 1) {
					this.b = (byte) (this.b | BYTE_VALUE[offSet]);
				}else {
					this.b = (byte) (this.b & (~BYTE_VALUE[offSet]));
				}
			} finally {
				lock.writeLock().unlock();
			}
			
		}else {
			int unitIndex = index >> 3;
			byte unit = this.biteMap[unitIndex];
			ReadWriteLock lock = this.locks[unitIndex];
			try {
				lock.writeLock().lock();
				if(flag == 1) {
					this.biteMap[unitIndex] = (byte) (unit | BYTE_VALUE[offSet]);
				}else {
					this.biteMap[unitIndex] = (byte) (unit & (~BYTE_VALUE[offSet]));
				}
			} finally {
				lock.writeLock().unlock();
			}
		}
	}
	
	public void setByte(int start, int end, int flag) {
		for(int i = start ; i <= end; ++i) {
			this.setByte(i, flag);
		}
	}
	
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}
	
	public int countOne() {
		return this.countOne(0, this.size - 1);
	}
	
	public int countOne(int start, int end) {
		int count = 0;
		for(int i = start; i <= end; i++) {
			count += this.getByte(i);
		}
		return count;
	}
	
	public ByteMap subMap(int start, int end) {
		ByteMap byteMap = new CocurrentByteMapImpl(end - start + 1);
		for(int i = start; i <= end; ++i) {
			if(this.getByte(i) != 0) {
				byteMap.setByte(i - start, 1);
			}
		}
		return byteMap;
	}
	
	public  ByteMap not() {
		ByteMap byteMap = new CocurrentByteMapImpl(this.size);
		for(int i = 0; i < this.size; ++i) {
			int flag = (this.getByte(i) == 0) ? 1 : 0;
			byteMap.setByte(i, flag);
		}
		return byteMap;
	}
	
	public ByteMap or(ByteMap byteMap) {
		int s1 = this.size;
		int s2 = byteMap.getSize();
		int orSize = s1 > s2 ? s1 : s2;
		ByteMap orMap = new CocurrentByteMapImpl(orSize);
		int i = 0;
		while(i < s1 && i < s2) {
			if(this.getByte(i) != 0 || byteMap.getByte(i) != 0) {
				orMap.setByte(i, 1);
			}
			++i;
		}
		while(i < s1) {
			if(this.getByte(i) != 0) {
				orMap.setByte(i, 1);
			}
			++i;
		}
		while(i < s2) {
			if(byteMap.getByte(i) != 0) {
				orMap.setByte(i, 1);
			}
			++i;
		}
		return orMap;
	}
	
	public ByteMap xor(ByteMap byteMap) {
		int s1 = this.size;
		int s2 = byteMap.getSize();
		int xorSize = s1 > s2 ? s1 : s2;
		ByteMap xorMap = new CocurrentByteMapImpl(xorSize);
		int i = 0;
		while(i < s1 && i < s2) {
			if(this.getByte(i) !=  byteMap.getByte(i)) {
				xorMap.setByte(i, 1);
			}
			++i;
		}
		while(i < s1) {
			if(this.getByte(i) != 0) {
				xorMap.setByte(i, 1);
			}
			++i;
		}
		while(i < s2) {
			if(byteMap.getByte(i) != 0) {
				xorMap.setByte(i, 1);
			}
			++i;
		}
		return xorMap;
	}
	
	public ByteMap and(ByteMap byteMap) {
		int s1 = this.size;
		int s2 = byteMap.getSize();
		int orSize = s1 > s2 ? s1 : s2;
		ByteMap andMap = new CocurrentByteMapImpl(orSize);
		int i = 0;
		while(i < s1 && i < s2) {
			if(this.getByte(i) != 0 && byteMap.getByte(i) != 0) {
				andMap.setByte(i, 1);
			}
			++i;
		}
		return andMap;
	}
	
	public ByteMap leftShift(int shiftStep) {
		ByteMap shiftMap = new CocurrentByteMapImpl(this.size);
		if(this.countOne() > 0 && shiftStep < this.size) {
			if(shiftStep < 0) {
				return this.rightShift((0 - shiftStep));
			}else {
				for(int i = shiftStep; i < this.size; ++i) {
					if(this.getByte(i) != 0) {
						shiftMap.setByte(i - shiftStep, 1);
					}
				}
			}
		}
		return shiftMap;
	}
	
	public ByteMap rightShift(int shiftStep) {
		ByteMap shiftMap = new CocurrentByteMapImpl(this.size);
		if(this.countOne() > 0 && shiftStep < this.size) {
			if(shiftStep < 0) {
				return this.leftShift((0 - shiftStep));
			}else {
				for(int i = this.size - shiftStep - 1; i >= 0; --i) {
					if(this.getByte(i) != 0) {
						shiftMap.setByte(i + shiftStep, 1);
					}
				}
			}
		}
		return shiftMap;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if(this.size <= 8) {
			for(int i = 0; i < 8; ++i) {
				if(i < 7) {
					try {
						sb.append(this.getByte(i) + ",");
					} catch (IllegalArgumentException e) {
						sb.append("0,");
					}
				}else {
					try {
						sb.append(this.getByte(i));
					} catch (IllegalArgumentException e) {
						sb.append("0");
					}
				}
			}
		}else {
			for(int i = 0; i < this.biteMap.length*8; ++i) {
				if((i&7) == 0) {
					sb.append("\n" + (i>>3) + ":");
				}else {
					sb.append(",");
				}
				try {
					sb.append(this.getByte(i));
				} catch (IllegalArgumentException e) {
					sb.append("0");
				}
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) {
		ByteMap byteMap1 = new CocurrentByteMapImpl(60);
		byteMap1.setByte(3, 5, 1);
		System.out.println("ByteMap1:" + byteMap1.toString());
		ByteMap byteMap2 = byteMap1.subMap(1, 11);
		System.out.println("byteMap1 sub map:" + byteMap2);
		System.out.println("or map:" + byteMap1.or(byteMap2));
		System.out.println("and map:" + byteMap1.and(byteMap2));
	}
}
