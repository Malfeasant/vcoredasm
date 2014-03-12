package us.malfeasant.vcoredasm;

public enum InstructionFormat {
	Vector80(10), Vector48(6), Scalar48(6), Scalar32(4), Scalar16(2);
	
	public final int totalBytes;
	InstructionFormat(int mb) {
		totalBytes = mb;
	}
	public static InstructionFormat identify(int instruction) {
		if ((instruction & 0xf800) == 0xf800) return Vector80;
		if ((instruction & 0xf800) == 0xf000) return Vector48;
		if ((instruction & 0xf000) == 0xe000) return Scalar48;
		if ((instruction & 0x8000) == 0x8000) return Scalar32;
		return Scalar16;
	}
}
