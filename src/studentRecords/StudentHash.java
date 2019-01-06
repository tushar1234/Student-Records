package studentRecords;

public class StudentHash {

	// constant for hashing
	final int HASH_TABLE_CAPACITY = 157;
	final int HASH_weight = 31;
	final int COMPRESSION_A = 3;
	final int COMPRESSION_B = 269;
	final int DOUBLE_HASH_CONST = 101;
	
	// structure to hold student records
	private class studentRecord{
		
		String stuId;
		float CGPA;

		studentRecord(String id, float marks) {
			stuId = id;
			CGPA = marks;
		}
	}
	
	// Record Table
	private studentRecord[] recordTable;
	private int tableSize = 0;
	
	public int getNoOfRecords() {
		return tableSize;
	}
	
	// init
	public void initialize() {
		recordTable = new studentRecord[HASH_TABLE_CAPACITY];
	}
	
	// check if input student id is valid according to required format YYYYAAADDDD
	private boolean isStudentIdValid(String studentId) {

		boolean isValid = false;

		// student id length check
		if (studentId.length() == 11) {

			// student id admission year check
			int admissionYear = Integer.parseInt(studentId.substring(0, 4));
			if (admissionYear >= 2008 && admissionYear <= 2017) {

				// student id course check
				String studentCourse = studentId.substring(4, 7);
				if (studentCourse == "CSE" || studentCourse == "MEC" || studentCourse == "ARC"
						|| studentCourse == "ECE") {
				}

				int rollNo = Integer.parseInt(studentId.substring(7, 11));
				if (rollNo >= 0 && rollNo <= 9999) {
					isValid = true;
				}
			}
		}

		if (!isValid) {
			System.out.println("Format of Student id is not valid");
		}

		return isValid;
	}
	
	// Compute the hash code map from string type to integer type
	private long computeHashCode(String studentId) {

		// copy the last character
		long hashCode = Long.parseLong(studentId.substring(studentId.length() - 1));
		long hashWeight = HASH_weight;

		for (int i = studentId.length() - 2; i >= 0; i--) {

			// Value of A is 10
			hashCode = hashCode + (Character.getNumericValue((studentId.charAt(i))) * hashWeight);
			hashWeight = hashWeight * HASH_weight;
		}
		//System.out.println("Hash code: " + hashCode);
		return hashCode;
	}
	
	// compute compression code map
	private int computeCompressionCode(long hashCode) {

		long val = (COMPRESSION_A * hashCode) + COMPRESSION_B;
		int comValue = (int) (val % HASH_TABLE_CAPACITY);
		//System.out.println("compression: " + comValue);
		return comValue;
	}
	
	// In case of collision find the next slot in table
	// Using double hashing technique to resolve collision 
	int getNextPossibleindex(long hashCode, int comCode, int j)
	{
		// Implementing q-k mod q
		// since the q-k can obtain negative mod value
		// so Implementing (((q-k)%q)+q)%q
		int secondHash = (int) (((DOUBLE_HASH_CONST - hashCode) % DOUBLE_HASH_CONST) + DOUBLE_HASH_CONST)
				% DOUBLE_HASH_CONST;

		// computing function f(j)
		int func = j * secondHash;
		int index = (comCode + func) % HASH_TABLE_CAPACITY;
		return index;
	}
	
	// In case of collision, find next empty slot
	private int resolveCollision(String studentId) {

		int index = -1;
		int j = 1;

		long hashCode = computeHashCode(studentId);
		int comCode = computeCompressionCode(hashCode);
		
		// In case of collision find next possible empty index.
		// Max no of trials to find next empty index is equal to size of array.
		while (j < HASH_TABLE_CAPACITY) {
			index = getNextPossibleindex(hashCode, comCode, j);
			//System.out.println("next possible index: " + index);
			if (recordTable[index] == null) {
				// System.out.println("empty index found at " + index);
				break;
			} else {
				// System.out.println(j + 1 + " time collision detected while inserting ");
			}

			// increase multiplier
			j++;
		}
		return index;
	}
	
	// compute hashId
	private int HashId(String studentId) {
		
		long hashCode = computeHashCode(studentId);
		return computeCompressionCode(hashCode);
	}
	
	// Insert record in hash table
	public void insert(String studentId, float CGPA) {

		if (tableSize < HASH_TABLE_CAPACITY) {

			if (isStudentIdValid(studentId)) {

				int hashId = HashId(studentId);
				//System.out.println("Hash id for " + studentId + " is " + hashId);

				// insert in table if there is no collision
				if (recordTable[hashId] == null) {
					recordTable[hashId] = new studentRecord(studentId, CGPA);
					tableSize++;
				} else {
					//System.out.println("collision detected while inserting " + studentId);
					
					// find next possible empty slot
					int newIndex = resolveCollision(studentId);
					if (newIndex == -1) {
						System.out.println("Insertion of " + studentId + "not possible");
					} else {
						recordTable[newIndex] = new studentRecord(studentId, CGPA);
						tableSize++;
					}
				}
			} else {
				System.out.println("provided student id is not valid, can not insert the record");
			}
		} else {
			System.out.println("Hash table is full");
		}
	}
	
	// Find in hash table the CGPA for provided studentId.
	// In case there is no entry return -1.0f as error. 
	public float getStudentCGPA(String studentId) {

		float CGPA = -1.0f;
		if (isStudentIdValid(studentId)) {

			int hash = HashId(studentId);
			
			if(studentId.equals(recordTable[hash].stuId)) {

				CGPA = recordTable[hash].CGPA;
			} else {

				// Find an element, maximum the size of table times, if not found then entry not
				// present.
				int trial = 1;
				long hashCode = computeHashCode(studentId);
				int comCode = computeCompressionCode(hashCode);
				// Max no of trails is equal to size of array
				while (trial < HASH_TABLE_CAPACITY) {

					int index = getNextPossibleindex(hashCode, comCode, trial);

					if(studentId.equals(recordTable[index].stuId)) {
						CGPA = recordTable[index].CGPA;
						break;
					}
					trial++;
				}
			}
		}

		return CGPA;
	}

	// destroy the table
	public void deinitialize() {

		for (int i = 0; i < HASH_TABLE_CAPACITY; i++) {
			if (recordTable[i] != null) {
				recordTable[i] = null;
			}
		}
		recordTable = null;
	}
	
}
