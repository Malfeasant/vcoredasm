package us.malfeasant.vcoredasm;

public enum InstructionFormat {
	Vector80(8) {
		public String format(int op, byte[] bytes) {
			return String.format("%04x %08x %08x\n", op,
					((bytes[0] & 0xff) << 16) | (bytes[1] << 24) | (bytes[2] & 0xff) | ((bytes[3] & 0xff) << 8),
					((bytes[4] & 0xff) << 16) | (bytes[5] << 24) | (bytes[6] & 0xff) | ((bytes[7] & 0xff) << 8));
		}
	},
	Vector48(4) {
		public String format(int op, byte[] bytes) {
			return String.format("%04x %08x\n", op,
					((bytes[0] & 0xff) << 16) | (bytes[1] << 24) | (bytes[2] & 0xff) | ((bytes[3] & 0xff) << 8));
		}
	},
	Scalar48(4) {
		public String format(int op, byte[] bytes) {
			return String.format("%04x %08x\n", op,
					(bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8) | ((bytes[2] & 0xff) << 16) | (bytes[3] << 24));
		}
	},
	Scalar32(2) {
		public String format(int op, byte[] bytes) {
			return String.format("%04x %04x\n", op,
					(bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8));
		}
	},
	Scalar16(0) {
		public String format(int op, byte[] bytes) {
			return String.format("%04x\n", op);
		}
	};
	
	public final int moreBytes;
	InstructionFormat(int mb) {
		moreBytes = mb;
	}
	public static InstructionFormat identify(int instruction) {
		if ((instruction & 0x8000) == 0x0000) return Scalar16;
		if ((instruction & 0xf800) == 0xf800) return Vector80;
		if ((instruction & 0xf800) == 0xf000) return Vector48;
		if ((instruction & 0xf000) == 0xe000) return Scalar48;
		if ((instruction & 0x8000) == 0x8000) return Scalar32;
		throw new IllegalArgumentException("Polly shouldn't be!");	// above should cover all possibilities
	}
	public abstract String format(int op, byte[] bytes);
}
