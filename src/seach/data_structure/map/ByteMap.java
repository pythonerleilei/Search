package seach.data_structure.map;

public interface ByteMap {

	/* get the value in the index of byte map
	 * @param index the index in byte map to get 
	 * @return 1 or 0 base the value in the index of byte map
	 */
	int getByte(int index);
	
	/* set the value in the index of byte map
	 * @param index the index in byte map to set
	 * @param flag the value setted in the index of byte map
	 */
	void setByte(int index, int flag);
	
	/* set the value in byte map from start to end
	 * @param start the start index in byte map
	 * @param end the end index in byte map
	 * @flag the value setted in byte map from start to end
	 */
	void setByte(int start, int end, int flag);
	
	/*
	 * get the size of byte map
	 */
	int getSize();
	
	/*
	 * set the size of byte map
	 * @param size to be setted
	 */
	void setSize(int size);
	
	/*
	 * count how many 1 in byte map
	 */
	int countOne();
	
	/*
	 * count how many i in byte map from start to end
	 */
	int countOne(int start, int end);
	
	/*
	 * get the sub map of byte map from start to end
	 */
	ByteMap subMap(int start, int end);
	
	/*
	 * flip the value in byte map 
	 */
	ByteMap not();
	
	/*
	 * or operation with another byte map
	 */
	ByteMap or(ByteMap byteMap);
	
	/*
	 * xor operation with another byte map
	 */
	ByteMap xor(ByteMap byteMap);
	
	/*
	 * and operation with another byte map
	 */
	ByteMap and(ByteMap byteMap);
	
	/*
	 * byte map left shift operation
	 */
	ByteMap leftShift(int shiftStep);
	
	/*
	 * byte map right shift operation
	 */
	ByteMap rightShift(int shiftStep);
}
