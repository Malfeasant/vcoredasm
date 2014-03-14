package us.malfeasant.vcoredasm;

public enum InstructionFormat {
	Vector80(8) {
		public String format(int op, byte[] bytes) {
			int arga = ((bytes[0] & 0xff) << 16) | (bytes[1] << 24) | (bytes[2] & 0xff) | ((bytes[3] & 0xff) << 8);
			int argb = ((bytes[4] & 0xff) << 16) | (bytes[5] << 24) | (bytes[6] & 0xff) | ((bytes[7] & 0xff) << 8);
			
			return String.format("%04x %08x, %08x", op, arga, argb);
		}
	},
	Vector48(4) {
		public String format(int op, byte[] bytes) {
			int arg = ((bytes[0] & 0xff) << 16) | (bytes[1] << 24) | (bytes[2] & 0xff) | ((bytes[3] & 0xff) << 8);
			
			return String.format("%04x %08x", op, arg);
		}
	},
	Scalar48(4) {
		public String format(int op, byte[] bytes) {
			int arg = (bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8) | ((bytes[2] & 0xff) << 16) | (bytes[3] << 24);
			
			return String.format("%04x %08x", op, arg);
		}
	},
	Scalar32(2) {
		public String format(int op, byte[] bytes) {
			int arg = (bytes[0] & 0xff) | ((bytes[1] & 0xff) << 8);
			return String.format("%04x %04x", op, arg);
		}
	},
	Scalar16(0) {
		public String format(int op, byte[] bytes) {
			if (op < 32) {
				switch (op) {
				case 0:
					return "bkpt";
				case 1:
					return "nop";
				case 2:
					return "wait";
				case 3:
					return "user";
				case 4:
					return "ei";
				case 5:
					return "di";
				case 6:
					return "cbclr";
				case 7:
					return "cbinc";
				case 8:
					return "cbchg";
				case 9:
					return "cbdec";
				case 10:
					return "rti";
				}
			}
			if ((op & 0xffe0) == 0x20) {
				return String.format("swi r%d", op & 0x1f);
			}
			if ((op & 0xffe0) == 0x40) {
				return String.format("b r%d", op & 0x1f);
			}
			if ((op & 0xffe0) == 0x60) {
				return String.format("bl r%d", op & 0x1f);
			}
			if ((op & 0xffe0) == 0x80) {
				return String.format("tbb r%d", op & 0x1f);
			}
			if ((op & 0xffe0) == 0xa0) {
				return String.format("tbs r%d", op & 0x1f);
			}
			if ((op & 0xffe0) == 0xe0) {
				return String.format("mov r%d, cpuid", op & 0x1f);
			}
			if ((op & 0xffc0) == 0x1c0) {
				return String.format("swi #%d", op & 0x1f);
			}
			if ((op & 0xfe80) == 0x200) {
				int rs = op & 0x60 >> 2;
				if (rs == 8) rs = 6;
				return String.format("pop r%d-r%d%s", rs, op & 0x1f, (op & 0x100) == 0 ? "" : ", pc");
			}
			if ((op & 0xfe80) == 0x280) {
				int rs = op & 0x60 >> 2;
				if (rs == 8) rs = 6;
				return String.format("push r%d-r%d%s", rs, op & 0x1f, (op & 0x100) == 0 ? "" : ", lr");
			}
			if ((op & 0xfe00) == 0x400) {
				return String.format("ld r%d, (sp)+%d", op & 0xf, (op & 0x1f0) >> 2);
			}
			if ((op & 0xfe00) == 0x600) {
				return String.format("st r%d, (sp)+%d", op & 0xf, (op & 0x1f0) >> 2);
			}
			return String.format("%04x", op);
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
